package com.yangrd.springcloud.example;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * OAuthController
 *
 * @author yangrd
 * @date 2019/09/26
 */
@Controller
// 必须配置
@SessionAttributes("authorizationRequest")
public class Oauth2Controller {

    @RequestMapping("/oauth/approvale/confirm_access")
    public String getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) throws Exception {
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
        model.put("scopes",authorizationRequest.getScope());
        model.put("clientId", authorizationRequest.getClientId());
        return "oauth_approval";
    }

    @RequestMapping({ "/oauth/approvale/error" })
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
}
