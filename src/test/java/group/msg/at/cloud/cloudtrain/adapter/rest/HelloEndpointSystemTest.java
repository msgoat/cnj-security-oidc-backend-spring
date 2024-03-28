package group.msg.at.cloud.cloudtrain.adapter.rest;

import group.msg.at.cloud.cloudtrain.core.entity.Message;
import group.msg.at.cloud.common.test.rest.RestAssuredSystemTestFixture;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * System test that verifies that the REST endpoint works as expected.
 * <p>
 * Assumes that a remote server hosting the REST endpoint is up and running.
 * </p>
 */
public class HelloEndpointSystemTest {

    private static final RestAssuredSystemTestFixture fixture = new RestAssuredSystemTestFixture();

    @BeforeAll
    public static void onBeforeClass() {
        fixture.onBefore();
    }

    @AfterAll
    public static void onAfterClass() {
        fixture.onAfter();
    }

    @Test
    void testGetWelcomeMessage() {
        Message welcomeMessage = given().auth().oauth2(fixture.getToken())
                .get("api/v1/hello")
                .then().assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Message.class);
        assertThat(welcomeMessage).as("GET must return non-null response").isNotNull();
        assertThat(welcomeMessage.getId()).as("message.id must not be empty").isNotNull();
        assertThat(welcomeMessage.getCode()).as("message.code must not be empty").isNotNull();
        assertThat(welcomeMessage.getText()).as("message.text must not be empty").isNotNull();
        assertThat(welcomeMessage.getText()).as("message.text must start with expected text").isEqualTo("Welcome to Cloud Native Java with Spring Boot!");
        assertThat(welcomeMessage.getUser()).as("message.user must not be empty").isNotNull();
        assertThat(welcomeMessage.getUser()).as("message.user must match expected user").isEqualTo("cnj-tester");
        assertThat(welcomeMessage.getLocale()).as("message.locale must not be empty").isNotNull();
    }
}
