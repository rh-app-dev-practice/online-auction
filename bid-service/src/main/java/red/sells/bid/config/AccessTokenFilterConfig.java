package red.sells.bid.config;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Filter to capture the Keycloak AccessToken, pull out attributes, and add them to the ServletRequest.
 * Attributes will then be available via @RequestAttribute.
 *
 * userId is pulled out so there can be a common GUID for users rather than depend on email or username. Also will
 * be conveniently available if REST API calls need to be made to Keycloak.
 */
@Configuration
public class AccessTokenFilterConfig {

    public class AccessTokenFilter extends GenericFilterBean {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

            if (!(request instanceof HttpServletRequest)) {
                throw new RuntimeException("Expecting a HTTP request");
            }

            RefreshableKeycloakSecurityContext context = (RefreshableKeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());

            if (context != null) {
                AccessToken accessToken = context.getToken();
                Object userId = accessToken.getOtherClaims().get("user_id");

                if (userId != null) {
                    // Must add a Mapper for the userId for the Access token. In Keycloak console, go to:
                    // Clients >> online-auction >> Mappers >> user_id
                    request.setAttribute("userId", userId.toString());
                }
            }

            chain.doFilter(request, response);
        }
    }

    @Bean
    public AccessTokenFilter authFilter() {
        return new AccessTokenFilter();
    }
}
