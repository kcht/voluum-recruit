package scenarios;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import com.codewise.entities.Campaign;
import com.codewise.voluum.AppProperties;
import com.codewise.voluum.VoluumClient;
import com.sun.deploy.util.StringUtils;

import static org.junit.Assert.assertEquals;

public class Scenario3
{
    VoluumClient voluumClient = new VoluumClient();

    @Test
    public void requestToPostbackIncreasesConversionsByOne() throws IOException, URISyntaxException, InterruptedException
    {
        String token = voluumClient.authenticate(AppProperties.USERNAME, AppProperties.PASSWORD);
        String existingCampaignId = AppProperties.EXISTING_CAMPAIGN_ID;

        Campaign campaign = voluumClient.getCampaign(token, existingCampaignId);
        int initialConversions = voluumClient.getNumberOfConversions(campaign.getId(), token);

        String location = voluumClient.visitCampaignURL(campaign.getUrl(), false);
        String[] split = StringUtils.splitString(location, "/");
        String locationRandomId = split[split.length - 1];

        voluumClient.performPostback(locationRandomId, token);
        Thread.sleep(30000);

        int finalConversions = voluumClient.getNumberOfConversions(existingCampaignId, token);

        assertEquals("Number of conversions was not increased by 1", initialConversions + 1, finalConversions);
    }
}
