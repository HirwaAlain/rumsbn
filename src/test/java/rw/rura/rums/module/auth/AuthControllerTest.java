package rw.rura.rums.module.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import rw.rura.rums.enums.UserDepartment;
import rw.rura.rums.enums.UserRole;
import rw.rura.rums.module.auth.dto.LoginRequest;
import rw.rura.rums.module.auth.dto.LoginResponse;
import rw.rura.rums.module.auth.dto.RefreshRequest;
import rw.rura.rums.module.auth.service.AuthService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    // -------------------------------------------------------------------------
    // POST /api/auth/login — success
    // -------------------------------------------------------------------------

    @Test
    void login_withValidCredentials_returns200AndTokens() throws Exception {
        LoginResponse.AuthUserInfo userInfo = new LoginResponse.AuthUserInfo(
                UUID.fromString("a0000000-0000-0000-0000-000000000001"),
                "System Administrator",
                UserRole.ADMIN,
                UserDepartment.ICT
        );
        LoginResponse loginResponse = new LoginResponse("mock-access-token", "mock-refresh-token", 28800, userInfo);

        when(authService.login(any(LoginRequest.class), any())).thenReturn(loginResponse);

        LoginRequest request = new LoginRequest("admin@rura.rw", "Admin@1234!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("mock-refresh-token"))
                .andExpect(jsonPath("$.data.expiresIn").value(28800))
                .andExpect(jsonPath("$.data.user.role").value("admin"));
    }

    // -------------------------------------------------------------------------
    // POST /api/auth/login — wrong password
    // -------------------------------------------------------------------------

    @Test
    void login_withWrongPassword_returns401() throws Exception {
        when(authService.login(any(LoginRequest.class), any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        LoginRequest request = new LoginRequest("admin@rura.rw", "WrongPass!99");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    // -------------------------------------------------------------------------
    // POST /api/auth/refresh — returns new token pair
    // -------------------------------------------------------------------------

    @Test
    void refresh_withValidRefreshToken_returns200AndNewTokens() throws Exception {
        LoginResponse.AuthUserInfo userInfo = new LoginResponse.AuthUserInfo(
                UUID.fromString("a0000000-0000-0000-0000-000000000001"),
                "System Administrator",
                UserRole.ADMIN,
                UserDepartment.ICT
        );
        LoginResponse newTokens = new LoginResponse("new-access-token", "new-refresh-token", 28800, userInfo);

        when(authService.refresh(any())).thenReturn(newTokens);

        RefreshRequest request = new RefreshRequest("old-refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"));
    }

    // -------------------------------------------------------------------------
    // GET /api/licenses — no Bearer token → 401
    // -------------------------------------------------------------------------

    @Test
    void accessProtectedEndpoint_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/licenses")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
