package matchers.matchers;

import com.codewise.exceptions.InvalidResponseCodeException;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;



public class InvalidResponseMessageMatcher extends TypeSafeMatcher<InvalidResponseCodeException>
{
    private String actualCause;
    private final String expectedCause;

    private InvalidResponseMessageMatcher( String expectedCause){

        this.expectedCause = expectedCause;
    }

    @Override
    protected boolean matchesSafely(final InvalidResponseCodeException e){
        actualCause = e.getMessage();
        return actualCause.equals(expectedCause);


    }

    public void describeTo(Description description)
    {
        description.appendValue(actualCause).appendText(" was found instead of expected: ").appendValue(expectedCause);
    }

    public static InvalidResponseMessageMatcher hasStatusMessage(String message){
        return new InvalidResponseMessageMatcher(message);
    }
}
