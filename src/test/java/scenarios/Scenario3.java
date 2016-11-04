package scenarios;

import java.io.IOException;
import java.net.URISyntaxException;

import com.codewise.App;
import org.junit.BeforeClass;
import org.junit.Test;

import com.codewise.entities.Campaign;
import com.codewise.voluum.VoluumClient;
import com.sun.deploy.util.StringUtils;

import static org.junit.Assert.assertEquals;

public class Scenario3
{
    VoluumClient voluumClient = new VoluumClient();

    @BeforeClass
    public static void setup(){
        App.init();
    }
    @Test
    public void requestToPostbackIncreasesConversionsByOne() throws IOException, URISyntaxException, InterruptedException
    {
        String token = voluumClient.authenticate(App.username, App.password);

        Campaign campaign = voluumClient.getCampaign(token, App.existingCampaignUrl);
        int initialConversions = voluumClient.getNumberOfConversions(campaign.getId(), token);

        String location = voluumClient.visitCampaignURL(campaign.getUrl(), false);
        String[] split = StringUtils.splitString(location, "/");
        String locationRandomId = split[split.length - 1];

        voluumClient.performPostback(locationRandomId, token);
        Thread.sleep(30000);

        int finalConversions = voluumClient.getNumberOfConversions(App.existingCampaignUrl, token);

        assertEquals("Number of conversions was not increased by 1", initialConversions + 1, finalConversions);
    }
}
