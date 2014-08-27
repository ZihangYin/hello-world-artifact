package com.unicorn.rest.repository.model;

import java.util.List;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.glassfish.jersey.server.Uri;
import org.joda.time.DateTime;

import com.unicorn.rest.utils.UUIDGenerator;

@EqualsAndHashCode
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationToken {

    public enum AuthenticationTokenType {
        ACCESS_TOKEN("Bearer"),
        REQUEST_TOKEN("authorization_code"),
        REFRESH_TOKEN("Bearer_Refresh");

        private String tokenType;
        private AuthenticationTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        @Override
        public String toString() {
            return tokenType;
        }
    }
    
    @Getter @Nonnull private final String token;
    @Getter @Nonnull private final AuthenticationTokenType tokenType;
    @Getter private final String refreshToken;
    @Getter @Nonnull private final DateTime issuedAt;
    @Getter @Nonnull private final DateTime expireAt;
    @Getter private final String userId;
    @Getter private final String clientId;
    @Getter private final String clinetSecretProof;
    @Getter private final List<String> scope;
    @Getter private final Uri redirectUri;
    @Getter private final String state;

    public static AuthenticationTokenBuilder generateToken() {
        return new AuthenticationTokenBuilder().token(UUIDGenerator.randomUUID().toString());
    }

    public static AuthenticationTokenBuilder buildToken(@Nonnull String token) {
        return new AuthenticationTokenBuilder().token(token);
    }

    public static AuthenticationToken updateTokenValue(AuthenticationToken currentAuthenticationToken) {
        return new AuthenticationToken(UUIDGenerator.randomUUID().toString(), currentAuthenticationToken.getTokenType(), currentAuthenticationToken.getRefreshToken(),
                currentAuthenticationToken.getIssuedAt(), currentAuthenticationToken.getExpireAt(), currentAuthenticationToken.getUserId(),
                currentAuthenticationToken.getClientId(), currentAuthenticationToken.getClinetSecretProof(), currentAuthenticationToken.getScope(),
                currentAuthenticationToken.getRedirectUri(), currentAuthenticationToken.getState());
    }

    public static class AuthenticationTokenBuilder {

        private static final int DEFAULT_EXPIRATION_IN_HOURS = 24 * 60;

        private String token;
        private AuthenticationTokenType tokenType;
        private String refreshToken;
        private DateTime issuedAt = new DateTime();
        private int expiredInMinutes = DEFAULT_EXPIRATION_IN_HOURS;
        private String userId;
        private String clientId;
        private String clinetSecretProof;
        private List<String> scope;
        private Uri redirectUri;
        private String state;

        public AuthenticationTokenBuilder() {}

        private AuthenticationTokenBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthenticationTokenBuilder tokenType(AuthenticationTokenType tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public AuthenticationTokenBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public AuthenticationTokenBuilder issuedAt(DateTime issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }

        public AuthenticationTokenBuilder expiredInMinutes(int expiredInMinutes) {
            this.expiredInMinutes = expiredInMinutes;
            return this;
        }

        public AuthenticationTokenBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public AuthenticationTokenBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public AuthenticationTokenBuilder clinetSecretProof(String clinetSecretProof) {
            this.clinetSecretProof = clinetSecretProof;
            return this;
        }

        public AuthenticationTokenBuilder scope(List<String> scope) {
            this.scope = scope;
            return this;
        }

        public AuthenticationTokenBuilder redirectUri(Uri redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public AuthenticationTokenBuilder state(String state) {
            this.state = state;
            return this;
        }

        public AuthenticationToken build() {
            if (token == null || tokenType == null) {
                throw new IllegalArgumentException("Failed while attempting to build authentication token due to missing required parameters");
            }
            return new AuthenticationToken(token, tokenType, refreshToken, issuedAt, issuedAt.plusMinutes(expiredInMinutes), 
                    userId, clientId, clinetSecretProof, scope, redirectUri, state);
        }
    }
}
