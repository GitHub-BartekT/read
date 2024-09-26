package pl.iseebugs.doread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.iseebugs.doread.domain.account.AppProperties;
import pl.iseebugs.doread.domain.email.EmailProperties;
import pl.iseebugs.doread.domain.security.AuthorizationProperties;

@SpringBootApplication
@EnableConfigurationProperties({EmailProperties.class, AuthorizationProperties.class, AppProperties.class})
public class DoReadApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoReadApplication.class, args);
	}
}
