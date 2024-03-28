package group.msg.at.cloud.cloudtrain.core.boundary;

import group.msg.at.cloud.cloudtrain.core.entity.Message;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

/**
 * Simple demo boundary to show how the authenticated user can be retrieved from the current Spring Security Context.
 * <p>
 *
 * @author michael.theis@msg.group
 * @version 1.0
 * @TODO: enable security on method level
 * </p>
 */
@Service
@Secured("CLOUDTRAIN_USER")
public class HelloWorld {

    private static final String WELCOME_MESSAGE_TEMPLATE = "Dear \"%s\", welcome to a cloud native Java application based on Spring Boot protected by OpenID Connect!";

    /**
     * Returns a welcome message to the currently authenticated user.
     */
    public Message getWelcomeMessage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserId = authentication != null ? authentication.getName() : "anonymous";
        Message result = new Message(UUID.randomUUID());
        result.setCode("hello");
        result.setText("Welcome to Cloud Native Java with Spring Boot!");
        result.setUser(authenticatedUserId);
        result.setLocale(Locale.ENGLISH);
        return result;
    }
}
