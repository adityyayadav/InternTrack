package com.internpilot.users;

import com.internpilot.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/v1/me
     * Returns the authenticated user's profile.
     * Auto-creates the profile if this is the first login.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileDto>> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt) {
        Profile profile = userService.getOrCreateProfile(jwt);
        return ResponseEntity.ok(ApiResponse.ok(ProfileDto.from(profile)));
    }
}
