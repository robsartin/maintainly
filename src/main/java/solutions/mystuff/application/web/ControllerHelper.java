package solutions.mystuff.application.web;

import java.security.Principal;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.port.in.UserResolver;
import org.slf4j.MDC;
import org.springframework.security.oauth2.client
        .authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user
        .OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class ControllerHelper {

    private static final String MDC_ORG_ID =
            "organizationId";
    private static final int MAX_PAGE_SIZE = 50;

    private final UserResolver userResolver;

    public ControllerHelper(UserResolver userResolver) {
        this.userResolver = userResolver;
    }

    AppUser resolveUser(Principal principal) {
        return userResolver.resolveOrCreate(
                extractUsername(principal));
    }

    private String extractUsername(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken
                oauth) {
            OAuth2User user = oauth.getPrincipal();
            String email = user.getAttribute("email");
            if (email != null && !email.isBlank()) {
                return email;
            }
        }
        return principal.getName();
    }

    void addUserAttrs(AppUser user, Model model) {
        model.addAttribute("username",
                user.getUsername());
        model.addAttribute("organization",
                user.getOrganization());
        model.addAttribute("currentUser", user);
    }

    void setOrgMdc(AppUser user) {
        if (user.hasOrganization()) {
            MDC.put(MDC_ORG_ID,
                    user.getOrganization().getId()
                            .toString());
        }
    }

    void clearOrgMdc() {
        MDC.remove(MDC_ORG_ID);
    }

    int clampSize(int size) {
        return Math.max(1, Math.min(size, MAX_PAGE_SIZE));
    }
}
