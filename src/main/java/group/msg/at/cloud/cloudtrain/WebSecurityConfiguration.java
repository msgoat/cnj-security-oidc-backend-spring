package group.msg.at.cloud.cloudtrain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security OAuth2 web security configuration.
 *
 * @author Michael Theis (msg group)
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    /**
     * Defines the security rules applied to different request URIs and sets up JWT based resource server authentication.
     * <p>
     * All requests to the actuator endpoints are permitted to all; any other request requires JWT based authentication.
     * </p>
     *
     * @param http
     * @throws Exception
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz.requestMatchers("/actuator/**").permitAll().anyRequest().authenticated())
                .oauth2ResourceServer().jwt();
        return http.build();
    }
}
