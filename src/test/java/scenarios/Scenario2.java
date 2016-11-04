package scenarios;

import java.io.IOException;
import java.net.URISyntaxException;

import com.codewise.App;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.codewise.entities.Campaign;
import com.codewise.voluum.VoluumClient;

public class Scenario2
{
    VoluumClient voluumClient = new VoluumClient();

    @BeforeClass
    public static void setup(){
        App.init();
    }
    @Test
    public void visitsToCampaignIncreaseVisitsByOne_E2E() throws IOException, URISyntaxException, InterruptedException
    {
        String token = voluumClient.authenticate(App.username, App.password);

        Campaign campaign = voluumClient.getCampaign(token, App.existingCampaignUrl);
        int initialVisits = voluumClient.getNumberOfVisits(campaign.getId(), token);

        voluumClient.visitCampaignURL(campaign.getUrl(), true);
        Thread.sleep(10000);
        int finalVisits = voluumClient.getNumberOfVisits(campaign.getId(), token);

        Assert.assertEquals("Number of visits was not increased by 1", initialVisits + 1, finalVisits);

    }
}
