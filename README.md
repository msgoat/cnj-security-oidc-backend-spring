# cnj-security-oidc-backend-spring

Cloud native Java backend exposing REST endpoints protected
by [OpenID Connect](https://openid.net/developers/how-connect-works/)
security based on [Spring Security](https://docs.spring.io/spring-security/reference/index.html)
provided by Spring Boot.

## Synopsis

Please check [Maven POM](pom.xml) for details on how-to integrate `Spring Security`
into your application.

`Spring Security` adds a token based authentication mechanism to your application
which allows you to protect the REST endpoint exposed by your application. Each HTTP request received by your REST
endpoints is expected to carry a special HTTP header `Authorization` with a so-called bearer token
issued by an external identity provider. The token itself is a standardized JSON Web Token and will be verified
by the `Spring Security` feature to meet the following requirements:

* Is the JWT token signed by the given identity provider?
* Is the JWT token issued by the given identity provider?
* Does the authenticated user represented by the JWT token have the expected role to call the REST endpoint?

If any verification fails, the request is rejected with either HTTP status code `401` or `403`.

> __Attention__: Unfortunately, Spring Security does not fully support OpenID Connect out-of-the-box. Therefore,
> you will have to add a dependency to `group.msg.at.cloud.common:cnj-common-security-oidc-spring` to your POM file
> which adds full OpenID Connect support plus bearer token promotion to downstream services.

In order to activate the `Spring Security` properly you need to add a configuration bean 
annotated with `@Configuration` and `@EnableWebSecurity` to your application
(see class [WebSecurityConfiguration](src/main/java/group/msg/at/cloud/cloudtrain/WebSecurityConfiguration.java)):

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationConverter customConverter) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz.requestMatchers("/actuator/**").permitAll().anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(customConverter)));
        return http.build();
    }
}
```

The web security configuration requires all HTTP requests to be authenticated except HTTP requests to Spring Boot Actuator endpoints
and sets a custom `JwtAuthenticationConverter` on the OAuth2 resource server module which supports JWT tokens issued
by an OpenID Connect identity provider properly. The custom converter bean is created by the `cnj-common-security-oidc-spring` library. 

Additionally, values for at least two configuration parameters must be provided (see: [docker-compose.yml](docker-compose.yml)):

* `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI` specifies the endpoint of the identity provider which returns the public key set used to check the JWT tokens signature
* `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI` specifies the ID of the expected JWT token issuer

Within the application, standard Spring Security mechanisms like `@Secured` or `@PreAuthorize` annotations can be used
to secure access to protected resources.

> __Info__: Although `Spring Security` does not promote received JWT tokens to downstream services automatically, 
> the library `cnj-common-security-oidc-spring` will add a `RestTemplateCustomizer` to the application context
> which will add this missing capability to any `RestTemplate`-based REST client.

## Status

![Build status](https://codebuild.eu-west-1.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoiMHhKN1RNeFhveUkyamJNZVJaUU5mbjJCY0Z4WWVLeUhRbHBwL0Rtc0V4UWUzd0hGdFZlN0VzUmJZcTBIaGlwc3VuSm5nakp0NXBjdzhXSDNvTjk0UGlFPSIsIml2UGFyYW1ldGVyU3BlYyI6ImFwbmVXM3l5UldYYndjc2ciLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=main)

## Release Information

A changelog can be found in [changelog.md](changelog.md).

## Docker Pull Command

`docker pull docker.cloudtrain.aws.msgoat.eu/cloudtrain/cnj-security-oidc-backend-spring`

## HOW-TO build this application locally

If all prerequisites are met, just run the following Maven command in the project folder:

```shell 
mvn clean verify -P pre-commit-stage
```

Build results: a Docker image containing the showcase application.

## HOW-TO start and stop this showcase locally

In order to run the whole showcase locally, just run the following docker commands in the project folder:

```shell 
docker compose up -d
docker compose logs -f 
```

Press `Ctlr+c` to stop tailing the container logs and run the following docker command to stop the showcase:

```shell 
docker compose down
```

## HOW-TO demo this showcase

The showcase application will be accessible:
* locally via `http://localhost:38080`
* remotely via `https://train2023-dev.k8s.cloudtrain.aws.msgoat.eu/cloudtrain/cnj-security-oidc-backend-spring` (if the training cluster is up and running)

Send a simple GET request with a bearer token issued by your identity provider to endpoint URI `/api/v1/hello`
and you will get a personalized welcome message in JSON format:

```json
{ 
  "code":"hello",
  "id":"73127522-d6ca-4a9f-916c-790e3c8aea77",
  "text":"Dear <userName>, welcome to a cloud native Java application based on Spring Boot protected by OpenID Connect!"
}
```
