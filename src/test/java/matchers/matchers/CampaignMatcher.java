package matchers.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;


public class CampaignMatcher extends TypeSafeMatcher<CampaignMatcher>
{
    @Override protected boolean matchesSafely(CampaignMatcher campaignMatcher)
    {
        return false;
    }

    public void describeTo(Description description)
    {

    }
}
