import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.codeforcommunity.processor.AuthDataBaseImpl;
import com.codeforcommunity.dto.NewSessionRequest;
import com.codeforcommunity.dto.RefreshSessionRequest;
import com.codeforcommunity.dto.RefreshSessionResponse;
import com.codeforcommunity.dto.SessionResponse;
import com.codeforcommunity.auth.IAuthDatabase;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.processor.AuthProcessorImpl;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthorizationProcessorImplTest {

    private static Algorithm algorithm = Algorithm.HMAC256("secretKey");
    private IAuthDatabase mockedDatabase = Mockito.mock(AuthDataBaseImpl.class);
    private final AuthProcessorImpl authorizationProcessor = new AuthProcessorImpl(mockedDatabase);
    private long lotsOfMilliseconds = 100000000L;

    private String validTestToken = JWT.create().withExpiresAt(Date.from(Instant.now().plusMillis(lotsOfMilliseconds)))
            .withClaim("username", "luke").withIssuer("c4c")
            .sign(algorithm);
    private String expiredTestToken = JWT.create().withExpiresAt(Date.from(Instant.now().minusMillis(lotsOfMilliseconds))).sign(algorithm);
    private String tamperedTestToken = JWT.create().withExpiresAt(Date.from(Instant.now().plusMillis(lotsOfMilliseconds))).sign(algorithm)
            .replaceFirst("e", "z");

    @Test
    public void isAuthorized_validToken_succeeds() {
        assertTrue(authorizationProcessor.isAuthorized(validTestToken));
    }

    @Test
    public void isAuthorized_expiredToken_fails() {
        assertFalse(authorizationProcessor.isAuthorized(expiredTestToken));
    }

    @Test
    public void isAuthorized_tamperedToken_fails() {
        assertFalse(authorizationProcessor.isAuthorized(tamperedTestToken));
    }

    @Test
    public void getSession_validRequest_getsValidResponse() {

        NewSessionRequest request = new NewSessionRequest() {{
            setUsername("luke");
        }};

        JWTVerifier verifier = JWT.require(algorithm).withClaim("username", "luke")
                .build();

        try {
            SessionResponse response = authorizationProcessor.getSession(request);
            verifier.verify(response.getAccessToken());
            verifier.verify(response.getRefreshToken());
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void getSession_invalidRequest_fails() throws Exception {

        NewSessionRequest request = new NewSessionRequest(); //don't add a username claim and expect verification to fail

        JWTVerifier verifier = JWT.require(algorithm).withClaim("username", "luke")
                .build();

        try {
            SessionResponse response = authorizationProcessor.getSession(request);
            verifier.verify(response.getAccessToken());
            verifier.verify(response.getRefreshToken());
        } catch (JWTVerificationException e) {
            //pass test
        }
    }

    @Test
    public void endSession_signaturePassedToDatabase() {

        String hashToAddToDatabase = validTestToken.split("\\.")[2];

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        authorizationProcessor.endSession(validTestToken);

        verify(mockedDatabase).invalidateRefresh(captor.capture());

        assertEquals(hashToAddToDatabase, captor.getValue());

    }

    @Test
    public void endSession_nullPassed_throwsException() {
        try {
            authorizationProcessor.endSession(null);
        } catch (Exception e) {
            assert e instanceof NullPointerException;
        }
    }

    @Test
    public void refreshSession_validToken_succeeds() throws Exception {

        when(mockedDatabase.isValidRefresh(anyString())).thenReturn(true);

        RefreshSessionRequest request = new RefreshSessionRequest() {{
            setRefreshToken(validTestToken);
        }};

        RefreshSessionResponse response = authorizationProcessor.refreshSession(request);

        try {

            JWTVerifier verifier = JWT.require(algorithm).withClaim("username", "luke")
                    .build();

            verifier.verify(response.getFreshAccessToken());

        } catch (JWTVerificationException exception) {
            fail();
        }

    }

    @Test(expected = AuthException.class)
    public void refreshSession_voidedToken_returnsNull() throws Exception {

        when(mockedDatabase.isValidRefresh(anyString())).thenReturn(false);

        RefreshSessionRequest request = new RefreshSessionRequest() {{
            setRefreshToken(validTestToken);
        }};

        authorizationProcessor.refreshSession(request);

    }

    @Test(expected = AuthException.class)
    public void refreshSession_expiredToken_returnsNull() throws Exception {

        when(mockedDatabase.isValidRefresh(anyString())).thenReturn(true);

        RefreshSessionRequest request = new RefreshSessionRequest() {{
            setRefreshToken(expiredTestToken);
        }};

        authorizationProcessor.refreshSession(request);

    }

    @Test(expected = AuthException.class)
    public void refreshSession_tamperedToken_returnsNull() throws Exception {

        when(mockedDatabase.isValidRefresh(anyString())).thenReturn(true);

        RefreshSessionRequest request = new RefreshSessionRequest() {{
            setRefreshToken(tamperedTestToken);
        }};

        authorizationProcessor.refreshSession(request);

    }
}
