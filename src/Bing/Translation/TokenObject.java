package Bing.Translation;

import java.util.Date;

/**
 * Object to store the token information from the Bing API
 * Created by ngrande on 4/10/15.
 */
public class TokenObject {
    private String accessToken;
    private String tokenType;
    private String scope;
    private String expiresIn;
    private Date reqDate;

    public TokenObject(String accessToken, String tokenType, String scope, String expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.scope = scope;
        this.expiresIn = expiresIn;
        reqDate = new Date();
    }

    public Date getReqDate() {
        return reqDate;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Boolean isExpired() {
        long diff = (new Date()).getTime() - reqDate.getTime();
        long diffSeconds = diff / 1000 % 60;
        return diffSeconds >= Integer.valueOf(expiresIn);
    }

    public String getAuthorizationHeader() {
        // mind the gap between acccess_token and bearer;
        return "Bearer " + accessToken;
    }
}
