import com.codeforcommunity.auth.JWT.db.AuthDataBase;
import com.codeforcommunity.auth.JWT.tokens.AuthTokenGenerator;
import com.codeforcommunity.processor.AuthProcessorImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Instant;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AuthProcessorImplTest {

    AuthDataBase authDataBase = Mockito.mock(AuthDataBase.class);
    AuthProcessorImpl authProcessor = new AuthProcessorImpl(authDataBase);
    String testJsonLoginCredentials;

    @Before
    public void init() {
        testJsonLoginCredentials = "{ \"username\" : \"luke\", \"password\" : \"lukeIsCool\" }";
        when(authDataBase.isValidUser(anyString(), anyString())).thenReturn(true);
        when(authDataBase.newUser(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        when(authDataBase.recordNewRefreshToken(anyString(), anyString())).thenReturn(true);
        when(authDataBase.invalidateRefresh(anyString())).thenReturn(true);
        when(authDataBase.isValidRefresh(anyString())).thenReturn(true);
    }

    @Test
    public void testGetNewUserSession() {

        try {
            String[] tokenReturn = authProcessor.getNewUserSession(testJsonLoginCredentials);
            assertEquals(tokenReturn.length, 2);
            assertEquals(tokenReturn[0].split("\\.").length, 3);
            assertEquals(tokenReturn[1].split("\\.").length, 3);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAuthenticateUser_validSessionToken_succeeds() throws Exception {

        String validToken = AuthTokenGenerator.builder().exp(Long.MAX_VALUE).getSigned();

        assertTrue(authProcessor.authenticateUser(validToken));

    }

    @Test
    public void testAuthenticateUser_expiredSessionToken_fails() throws Exception {

        String expiredToken = AuthTokenGenerator.builder().exp(0).getSigned();

        assertFalse(authProcessor.authenticateUser(expiredToken));
    }

    @Test
    public void testAuthenticateUser_tamperedSessionToken_fails() throws Exception {

        String untamperedToken = AuthTokenGenerator.builder().exp(Long.MAX_VALUE).username("luke").getSigned();

        String tamperedToken = untamperedToken.replaceFirst("e", "z");

        assertFalse(authProcessor.authenticateUser(tamperedToken));

    }
}
