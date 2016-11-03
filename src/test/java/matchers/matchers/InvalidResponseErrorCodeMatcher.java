package matchers.matchers;

import com.codewise.exceptions.InvalidResponseCodeException;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;


public class InvalidResponseErrorCodeMatcher extends TypeSafeMatcher<InvalidResponseCodeException>
{
    private int actualErrorCode;
    private final int expectedErrorCode;

    private InvalidResponseErrorCodeMatcher(int expectedErrorCode){
        this.expectedErrorCode = expectedErrorCode;
    }

    @Override
    protected boolean matchesSafely(final InvalidResponseCodeException e){
        actualErrorCode = e.getErrorCode();
        return (actualErrorCode == expectedErrorCode);
    }

    public void describeTo(Description description)
    {
        description.appendValue(actualErrorCode).appendText(" was found instead of expected: ").appendValue(expectedErrorCode);
    }

    public static InvalidResponseErrorCodeMatcher hasResponseCode(int errorCode){
        return new InvalidResponseErrorCodeMatcher(errorCode);
    }

}
