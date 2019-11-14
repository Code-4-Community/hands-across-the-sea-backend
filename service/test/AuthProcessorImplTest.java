import com.codeforcommunity.auth.JWT.db.AuthDataBase;
import com.codeforcommunity.processor.AuthProcessorImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
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
            boolean test = true;
        } catch (Exception e) {
            fail();
        }
    }



}
