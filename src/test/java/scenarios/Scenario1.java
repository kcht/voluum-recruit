package scenarios;

import com.codewise.entities.Campaign;
import com.codewise.voluum.AppProperties;
import com.codewise.voluum.VoluumClient;
import com.sun.deploy.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class Scenario1
{
    VoluumClient voluumClient = new VoluumClient();

    @Test
    public void newCampaignRedirectsToValidURL_E2E() throws Exception
    {
        String token = voluumClient.authenticate(AppProperties.USERNAME, AppProperties.PASSWORD);
        Campaign campaign = voluumClient.createCampaign(token);

        String location = voluumClient.visitCampaignURL(campaign.getUrl(), false);

        String[] split = StringUtils.splitString(location, "/");
        String locationRandomId = split[split.length - 1];
        String locationPrefix = StringUtils.splitString(location, locationRandomId)[0];
        Assert.assertEquals("Invalid length of random id", 24, locationRandomId.length());

        Assert.assertTrue("Non-letter characters in location random id", locationRandomId.matches("[a-zA-Z0-9]+"));
        Assert.assertTrue("Location prefix is different than expected", campaign.getDirectRedirectUrl().startsWith(locationPrefix));
    }
}
