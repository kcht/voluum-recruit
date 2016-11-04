package scenarios;

import com.codewise.entities.Campaign;
import com.codewise.entities.CampaignReportValue;
import com.codewise.exceptions.InvalidResponseCodeException;
import com.codewise.voluum.AppProperties;
import com.codewise.voluum.ApplicationProperties;
import com.codewise.voluum.RestfulClient;
import com.codewise.voluum.VoluumClient;
import com.sun.beans.decoder.ValueObject;
import com.sun.deploy.util.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.codewise.entities.CampaignReportValue.*;

/**
 * Created by kchachlo on 2016-11-02.
 */
public class Scenario1
{
//    private static final String username = "sdit.recruit2@codewise.com";
//    private static final String password = "r8uJ4@5qQpC%";

    VoluumClient voluumClient = new VoluumClient();

    @BeforeClass
    public static void setUp(){
    }
    @Test
    public void newCampaignRedirectsToValidURL_E2E() throws IOException, InvalidResponseCodeException, Exception{
        String token = voluumClient.authenticate(AppProperties.USERNAME, AppProperties.PASSWORD);
        Campaign campaign = voluumClient.createCampaign(token);

        String location = voluumClient.visitCampaignURL(campaign.getUrl(), false);

        String [] split = StringUtils.splitString(location,"/");
        String locationRandomId = split[split.length-1];
        String locationPrefix = StringUtils.splitString(location, locationRandomId)[0];
        Assert.assertEquals("Invalid length of random id", 24, locationRandomId.length());

        Assert.assertTrue("Non-letter characters in location random id", locationRandomId.matches("[a-zA-Z0-9]+"));
        Assert.assertTrue("Location prefix is different than expected", campaign.getDirectRedirectUrl().startsWith(locationPrefix));
    }

    @Test
    public void visitsToCampaignIncreaseVisitsByOne_E2E() throws IOException, URISyntaxException, InterruptedException
    {
        String token = voluumClient.authenticate(AppProperties.USERNAME, AppProperties.PASSWORD);
        String existingCampaignId = "1d6b4915-1633-4be7-ba14-d54f7c559de3";

        Campaign campaign = voluumClient.getCampaign(token, existingCampaignId);
        int initialNumberOfVisits =voluumClient.getNumberOfVisits( campaign.getId(), token);

        voluumClient.visitCampaignURL(campaign.getUrl(), true);
        Thread.sleep(10000);
        int finalNumberOfVisits = voluumClient.getNumberOfVisits( campaign.getId(), token);

        Assert.assertEquals("Number of visits was not increased by 1", initialNumberOfVisits+1, finalNumberOfVisits);

    }

    @Test
    public void requestToPostbackIncreasesConversionsByOne() throws IOException, URISyntaxException, InterruptedException
    {

        String token = voluumClient.authenticate(AppProperties.USERNAME, AppProperties.PASSWORD);
        String existingCampaignId = "1d6b4915-1633-4be7-ba14-d54f7c559de3";

        Campaign campaign = voluumClient.getCampaign(token, existingCampaignId);
     int initialNumberOfConversions =voluumClient.getNumberOfConversions(campaign.getId(), token);

        String location = voluumClient.visitCampaignURL(campaign.getUrl(), false);
        String [] split = StringUtils.splitString(location,"/");
        String locationRandomId = split[split.length-1];

        voluumClient.performPostback(locationRandomId, token);
        Thread.sleep(30000);

        int finalNumberOfConversions = voluumClient.getNumberOfConversions(existingCampaignId, token);

        Assert.assertEquals("Number of conversions was not increased by 1", initialNumberOfConversions+1, finalNumberOfConversions);


    }
}
