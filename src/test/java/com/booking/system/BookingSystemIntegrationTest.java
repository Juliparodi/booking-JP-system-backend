package com.booking.system;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingSystemIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    // Helper to generate a token using our dev auth controller
    private String obtainToken(String username, String role) throws Exception {
        String requestBody = """
                {
                    "username": "%s",
                    "role": "%s"
                }
                """.formatted(username, role);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return jsonMapper.readTree(response).get("accessToken").asString();
    }

    @Test
    @DisplayName("Verify complete booking flow with OAuth2 JWT, database persistence and overlap checks")
    void fullBookingIntegrationTest() throws Exception {
        // 1. Generate Auth Tokens
        String adminToken = obtainToken(UUID.randomUUID().toString(), "ADMIN");
        UUID clientUserUuid = UUID.randomUUID();
        String clientToken = obtainToken(clientUserUuid.toString(), "CLIENT");
        String rogueClientToken = obtainToken(UUID.randomUUID().toString(), "CLIENT");

        // 2. Register Resource as ADMIN (FR-004)
        String registerResourceJson = """
                {
                    "name": "Luxury Coworking Desk A",
                    "type": "DESK"
                }
                """;

        MvcResult resourceResult = mockMvc.perform(post("/api/v1/resources")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerResourceJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resourceId").exists())
                .andReturn();

        String resourceId = jsonMapper.readTree(resourceResult.getResponse().getContentAsString()).get("resourceId").asString();

        // 3. Verify CLIENT is Forbidden from registering resources
        mockMvc.perform(post("/api/v1/resources")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerResourceJson))
                .andExpect(status().isForbidden());

        // 4. Query Available Resources - Expect our newly registered resource to be available
        LocalDateTime start = LocalDateTime.now().plusDays(5).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(2);

        mockMvc.perform(get("/api/v1/resources/available")
                        .header("Authorization", "Bearer " + clientToken)
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].resourceId", hasItem(resourceId)));

        // 5. Create Booking as CLIENT (FR-001)
        String createBookingJson = """
                {
                    "resourceId": "%s",
                    "start": "%s",
                    "end": "%s"
                }
                """.formatted(resourceId, start, end);

        MvcResult bookingResult = mockMvc.perform(post("/api/v1/bookings")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBookingJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").exists())
                .andReturn();

        String bookingId = jsonMapper.readTree(bookingResult.getResponse().getContentAsString()).get("bookingId").asString();

        // 6. Attempt Overlapping Booking - Expect Conflict 409
        String overlappingBookingJson = """
                {
                    "resourceId": "%s",
                    "start": "%s",
                    "end": "%s"
                }
                """.formatted(resourceId, start.plusHours(1), end.plusHours(1)); // Overlaps by 1 hour

        mockMvc.perform(post("/api/v1/bookings")
                        .header("Authorization", "Bearer " + rogueClientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(overlappingBookingJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title", is("Booking Schedule Conflict")));

        // 7. Verify security: another client cannot read this booking
        mockMvc.perform(get("/api/v1/bookings/" + bookingId)
                        .header("Authorization", "Bearer " + rogueClientToken))
                .andExpect(status().isForbidden());

        // 8. List bookings by user (FR-003)
        mockMvc.perform(get("/api/v1/bookings")
                        .header("Authorization", "Bearer " + clientToken)
                        .param("userId", clientUserUuid.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bookingId", is(bookingId)))
                .andExpect(jsonPath("$[0].resourceName", is("Luxury Coworking Desk A")))
                .andExpect(jsonPath("$[0].status", is("CREATED")));

        // 9. Cancel booking as CLIENT (FR-002)
        mockMvc.perform(post("/api/v1/bookings/" + bookingId + "/cancel")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isNoContent());

        // 10. List bookings again to verify status transitioned to CANCELLED in DB
        mockMvc.perform(get("/api/v1/bookings")
                        .header("Authorization", "Bearer " + clientToken)
                        .param("userId", clientUserUuid.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is("CANCELLED")))
                .andExpect(jsonPath("$[0].cancelledAt", notNullValue()));
    }
}
