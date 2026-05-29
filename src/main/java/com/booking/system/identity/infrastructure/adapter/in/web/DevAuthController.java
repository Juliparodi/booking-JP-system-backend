package com.booking.system.identity.infrastructure.adapter.in.web;

import com.booking.system.identity.application.DevJwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Profile({"dev", "test"})
public class DevAuthController {

    private final DevJwtService devJwtService;

    public DevAuthController(DevJwtService devJwtService) {
        this.devJwtService = devJwtService;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> generateDevToken(@Valid @RequestBody TokenRequest request) {
        String token = devJwtService.generateToken(request.username(), request.role());
        return ResponseEntity.ok(new TokenResponse(token, "Bearer"));
    }

    public record TokenRequest(
            @NotBlank(message = "Username is required") String username,
            @NotBlank(message = "Role is required")
            @Pattern(regexp = "ADMIN|CLIENT", message = "Role must be either ADMIN or CLIENT")
            String role
    ) {}

    public record TokenResponse(
            String accessToken,
            String tokenType
    ) {}
}
