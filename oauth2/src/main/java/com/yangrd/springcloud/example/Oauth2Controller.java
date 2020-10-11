package com.yangrd.springcloud.example;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

/**
 * OAuthController
 *
 * @author yangrd
 * @date 2019/09/26
 */
@Controller
@SessionAttributes("authorizationRequest")
public class Oauth2Controller {

    @RequestMapping("/oauth/approval/confirm_access")
    public String getAccessConfirmation(Map<String, Object> model) {
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
        model.put("scopes",authorizationRequest.getScope());
        model.put("clientId", authorizationRequest.getClientId());
        return "oauth_approval";
    }

    @RequestMapping({ "/oauth/approval/error" })
    public String handleError(Map<String, Object> model, HttpServletRequest request) {
        Object error = request.getAttribute("error");
        String errorSummary;
        if (error instanceof OAuth2Exception) {
            OAuth2Exception oauthError = (OAuth2Exception) error;
            errorSummary = HtmlUtils.htmlEscape(oauthError.getSummary());
        } else {
            errorSummary = "Unknown error";
        }
        model.put("errorSummary", errorSummary);
        return "oauth_error";
    }

    @GetMapping("/")
    public String index(Principal principal, Map<String,Object> model){
        model.put("username", principal.getName());
        return "index";
    }
}
