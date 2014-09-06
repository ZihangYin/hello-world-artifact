package com.unicorn.rest.repository.model;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.joda.time.DateTime;

import com.unicorn.rest.utils.TimeUtils;
import com.unicorn.rest.utils.UUIDGenerator;

@EqualsAndHashCode
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationToken {

    public enum AuthenticationTokenType {
        ACCESS_TOKEN("bearer"),
        REQUEST_TOKEN("authorization_code"),
        REFRESH_TOKEN("bearer_refresh");

        private String tokenType;
        private AuthenticationTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        @Override
        public String toString() {
            return tokenType;
        }
    }
    
    // TODO: In future, we might consider using Optional<T> for optional parameters
    @Getter @Nonnull private final String token;
    @Getter @Nonnull private final AuthenticationTokenType tokenType;
    @Getter @Nonnull private final DateTime issuedAt;
    @Getter @Nonnull private final DateTime expireAt;
    @Getter private final Long userId;
    @Getter private final Long clientId;
    @Getter private final ByteBuffer clinetSecretProof;
    @Getter private final List<String> scope;
    @Getter private final URI redirectUri;
    @Getter private final String state;
    @Getter private final String refreshToken;

    public static AuthenticationTokenBuilder generateTokenBuilder() {
        return new AuthenticationTokenBuilder().token(UUIDGenerator.randomUUID().toString());
    }
    
    public static AuthenticationToken generateAccessTokenForUser(@Nonnull Long userId) {
        return generateTokenBuilder().tokenType(AuthenticationTokenType.ACCESS_TOKEN).userId(userId).build();
    }

    public static AuthenticationTokenBuilder buildTokenBuilder(@Nonnull String token) {
        return new AuthenticationTokenBuilder().token(token);
    }
    
    public static AuthenticationToken updateTokenValue(AuthenticationToken currentAuthenticationToken) {
        return new AuthenticationToken(UUIDGenerator.randomUUID().toString(), currentAuthenticationToken.getTokenType(),
                currentAuthenticationToken.getIssuedAt(), currentAuthenticationToken.getExpireAt(), currentAuthenticationToken.getUserId(),
                currentAuthenticationToken.getClientId(), currentAuthenticationToken.getClinetSecretProof(), currentAuthenticationToken.getScope(),
                currentAuthenticationToken.getRedirectUri(), currentAuthenticationToken.getState(), currentAuthenticationToken.getRefreshToken());
    }

    public static class AuthenticationTokenBuilder {

        private static final int DEFAULT_EXPIRATION_IN_HOURS = 24 * 60;

        private String token;
        private AuthenticationTokenType tokenType;
        private DateTime issuedAt = TimeUtils.getDateTimeNowInUTC();
        private int expiredInMinutes = DEFAULT_EXPIRATION_IN_HOURS;
        private DateTime expiredAt;
        private Long userId;
        private Long clientId;
        private ByteBuffer clinetSecretProof;
        private List<String> scope;
        private URI redirectUri;
        private String state;
        private String refreshToken;

        public AuthenticationTokenBuilder() {}

        private AuthenticationTokenBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthenticationTokenBuilder tokenType(AuthenticationTokenType tokenType) {
            this.tokenType = tokenType;
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
        
        public AuthenticationTokenBuilder expiredAt(DateTime expiredAt) {
            this.expiredAt = expiredAt;
            return this;
        }

        public AuthenticationTokenBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public AuthenticationTokenBuilder clientId(Long clientId) {
            this.clientId = clientId;
            return this;
        }

        public AuthenticationTokenBuilder clinetSecretProof(ByteBuffer clinetSecretProof) {
            this.clinetSecretProof = clinetSecretProof;
            return this;
        }

        public AuthenticationTokenBuilder scope(List<String> scope) {
            this.scope = scope;
            return this;
        }

        public AuthenticationTokenBuilder redirectUri(URI redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public AuthenticationTokenBuilder state(String state) {
            this.state = state;
            return this;
        }
        
        public AuthenticationTokenBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public AuthenticationToken build() {
            if (token == null || tokenType == null) {
                throw new IllegalArgumentException("Failed while attempting to build authentication token due to missing required parameters");
            }
            return new AuthenticationToken(token, tokenType, issuedAt, expiredAt == null ? issuedAt.plusMinutes(expiredInMinutes) : expiredAt, 
                    userId, clientId, clinetSecretProof, scope, redirectUri, state, refreshToken);
        }
    }
}
