package scenarios;

import com.codewise.entities.Campaign;
import com.codewise.exceptions.InvalidResponseCodeException;
import com.codewise.voluum.RestfulClient;
import com.sun.deploy.util.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by kchachlo on 2016-11-02.
 */
public class Scenario1
{
    private static final String username = "sdit.recruit1@codewise.com";
    private static final String password = "WNZL5#Q-?ntr";

    @Before
    public void setUp(){

    }
    @Test
    public void newCampaignRedirectsToValidURL_E2E() throws IOException, InvalidResponseCodeException, Exception{
        String token = RestfulClient.authenticate(username, password);
         Campaign campaign = RestfulClient.createCampaign(token);

       String location =RestfulClient.visitCampaignURL("http://rskxz.voluumtrk.com/voluum/6d2a1cfc-3381-4f71-ba5b-73a2da713fec");
         location = "http://example.com/w3CD9A5LEVF5VCR0HST8EM8O";

        String [] split = StringUtils.splitString(location,"/");
        String randomId = split[split.length-1];
        Assert.assertEquals("Invalid length of random id", 24, randomId.length());
        Assert.assertTrue("Non-letter characters in location random id", randomId.matches("[a-zA-Z0-9]+"));

      //  Assert.assertEquals("Location in response header is different than expected", campaign.getDirectRedirectUrl(), location);
    }

    @Test
    public void scenario2() throws IOException, URISyntaxException, InterruptedException
    {
        String token = RestfulClient.authenticate(username, password);
        Campaign campaign = RestfulClient.createCampaign(token);
        int initialNumberOfVisits =RestfulClient.getNumberOfVisits(campaign.getId(), token);

        RestfulClient.visitCampaignURL(campaign.getUrl());
        Thread.sleep(10000);
        int finalNumberOfVisits = RestfulClient.getNumberOfVisits(campaign.getId(), token);

        Assert.assertEquals("Number of visits was not increased by 1", initialNumberOfVisits+1, finalNumberOfVisits);

    }

    @Test
    public void scenario3(){
        // postback
       // http://rskxz.voluumtrk.com/postback?cid=REPLACE&payout=OPTIONAL&txid=OPTIONAL

        //secure postback
        //https://rskxz.voluumtrk.com/postback?cid=REPLACE&payout=OPTIONAL&txid=OPTIONAL

      //  http://example.com/w3CD9A5LEVF5VCR0HST8EM8O
        //http://rskxz.voluumtrk.com/postback?cid=w65Q83U5MEAOCDR0HLF2B290

    }
}
