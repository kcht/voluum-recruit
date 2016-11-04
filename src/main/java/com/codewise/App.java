package com.codewise;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class App
{

    public static String username;
    public static String password;
    public static String authServiceUrl;
    public static String coreServiceUrl;
    public static String reportServiceUrl;
    public static String postBackUrlPrefix;
    public static String existingCampaignUrl;


    public static void init()
    {

        Properties prop = new Properties();
        InputStream input = null;

        try
        {

            input = new FileInputStream("app.properties");

            prop.load(input);

            username = prop.getProperty("username");
            password = prop.getProperty("password");
            authServiceUrl = prop.getProperty("authServiceURL");
            coreServiceUrl = prop.getProperty("coreServiceURL");
            reportServiceUrl = prop.getProperty("reportServiceURL");
            existingCampaignUrl = prop.getProperty("exisitingCampaignId");
            postBackUrlPrefix = prop.getProperty("existingCampaignUrl");

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

    }
}
