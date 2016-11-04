import com.codewise.exceptions.InvalidResponseCodeException;
import com.codewise.voluum.AppProperties;
import com.codewise.voluum.ApplicationProperties;
import com.codewise.voluum.RestfulClient;

import com.codewise.voluum.VoluumClient;
import matchers.matchers.InvalidResponseErrorCodeMatcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

public class AuthenticationTest
{
    private static final String invalidUsername = "INVALID";
    private static final String invalidPassword = "INVALID";

    VoluumClient voluumClient = new VoluumClient();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void authenticateSuccessfully() throws InvalidResponseCodeException, IOException {
        String token = voluumClient.authenticate(AppProperties.USERNAME, AppProperties.PASSWORD);
        Assert.assertEquals(token.length(), 32);
    }

    @Test
    public void badCredentialAuthenticationError() throws InvalidResponseCodeException, IOException {
        expectedException.expect(InvalidResponseCodeException.class);
        expectedException.expect(InvalidResponseErrorCodeMatcher.hasResponseCode(401));

        voluumClient.authenticate(invalidUsername, invalidPassword);
    }
}
