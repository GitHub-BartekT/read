package pl.iseebugs.doread.domain.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(value = "auth")
public class AuthorizationProperties{
        private String secret;
        private long expirationRefreshTokenTime;
        private long expirationAccessTokenTime;
        private long expirationConfirmationTokenTime;
        private long expirationDeleteTokenTime;
}
