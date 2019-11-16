import com.codeforcommunity.auth.JWT.db.AuthDataBase;
import com.codeforcommunity.auth.JWT.tokens.AuthTokenGenerator;
import com.codeforcommunity.auth.exceptions.AuthException;
import com.codeforcommunity.processor.AuthProcessorImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
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
    public void testAuthenticateUser_tamperedSessionToken_fails() throws Exception { //todo handle all exceptions in method

        String untamperedToken = AuthTokenGenerator.builder().exp(Long.MAX_VALUE).username("luke").getSigned();

        String tamperedToken = untamperedToken.replaceFirst("e", "z");

        assertFalse(authProcessor.authenticateUser(tamperedToken));

    }

    @Test
    public void testNewAccessToken_validRefreshAndUnvoided_succeeds() {

        when(authDataBase.isValidRefresh(anyString())).thenReturn(true);

        try {
            String validToken = AuthTokenGenerator.builder().exp(Long.MAX_VALUE).getSigned();
            String newAccess = authProcessor.getNewAccessToken(validToken);
            assertEquals(newAccess.split("\\.").length, 3);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testNewAccessToken_validRefreshAndVoided_fails() {

        when(authDataBase.isValidRefresh(anyString())).thenReturn(false);

        try {
            String validToken = AuthTokenGenerator.builder().exp(Long.MAX_VALUE).getSigned();
            authProcessor.getNewAccessToken(validToken);
        } catch (Exception e) {
            assertTrue(e instanceof AuthException);
        }
    }

    @Test
    public void testNewAccessToken_expiredRefreshAndUnVoided_fails() {

        when(authDataBase.isValidRefresh(anyString())).thenReturn(true);

        try {
            String validToken = AuthTokenGenerator.builder().exp(0).getSigned();
            authProcessor.getNewAccessToken(validToken);
        } catch (Exception e) {
            assertTrue(e instanceof AuthException);
        }

    }

    @Test
    public void testNewAccessToken_tamperedRefreshAndUnVoided_fails() {

        when(authDataBase.isValidRefresh(anyString())).thenReturn(true);

        try {
            String validToken = AuthTokenGenerator.builder().exp(Long.MAX_VALUE).getSigned();
            String tamperedToken = validToken.replaceFirst("e", "z");
            authProcessor.getNewAccessToken(tamperedToken);
        } catch (Exception e) {
            assertTrue(e instanceof AuthException);
        }
    }

    @Test
    public void testInvalidateUserSession_success() {

        when(authDataBase.invalidateRefresh(anyString())).thenReturn(true);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        try {
            String validToken = AuthTokenGenerator.builder().exp(Long.MAX_VALUE).getSigned();
            authProcessor.invalidateUserSession(validToken);
            verify(authDataBase).invalidateRefresh(captor.capture());

            assertEquals(captor.getValue(), validToken.split("\\.")[2]);
        } catch (Exception e) {
            fail();
        }


    }

    @Test
    public void testInvalidateUserSession_fails() {

        when(authDataBase.invalidateRefresh(anyString())).thenReturn(false);

        try {
            String validToken = AuthTokenGenerator.builder().exp(Long.MAX_VALUE).getSigned();
            assertFalse(authProcessor.invalidateUserSession(validToken));
        } catch (Exception e) {
            fail();
        }

    }





}
