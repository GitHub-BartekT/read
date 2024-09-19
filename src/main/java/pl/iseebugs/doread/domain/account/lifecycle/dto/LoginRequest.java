package pl.iseebugs.doread.domain.account.lifecycle.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class LoginRequest {
    private String email;
    private String password;
}