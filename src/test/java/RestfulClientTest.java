import com.codewise.exceptions.InvalidResponseCodeException;
import com.codewise.voluum.RestfulClient;

import matchers.matchers.InvalidResponseErrorCodeMatcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

public class RestfulClientTest
{
    private static final String username = "sdit.recruit1@codewise.com";
    private static final String password = "";

    private static final String invalidUsername = "INVALID";
    private static final String invalidPassword = "INVALID";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void authenticateSuccessfully() throws InvalidResponseCodeException, IOException {
        String token = RestfulClient.authenticate(username, password);
        Assert.assertEquals(token.length(), 32);
    }

    @Test
    public void badCredentialAuthenticationError() throws InvalidResponseCodeException, IOException {
        expectedException.expect(InvalidResponseCodeException.class);
        expectedException.expect(InvalidResponseErrorCodeMatcher.hasResponseCode(401));

        RestfulClient.authenticate(invalidUsername, invalidPassword);
    }
}
