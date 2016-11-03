package com.codewise.entities;

/**
 * Created by kchachlo on 2016-11-01.
 */
public class Campaign
{
    private String directRedirectUrl;
    private String url;
    private String id;
    private String name;

    public String getName()
    {
        return name;
    }

    public Campaign withName(String name)
    {
        this.name = name;
        return this;
    }

    public String getId()
    {
        return id;
    }

    public Campaign withId(String id)
    {
        this.id = id;
        return this;
    }

    public String getUrl()
    {
        return url;
    }

    public Campaign withUrl(String url)
    {
        this.url = url;
        return this;
    }


    public String getDirectRedirectUrl()
    {
        return directRedirectUrl;
    }

    public Campaign withDirectRedirectUrl(String directRedirectUrl)
    {
        this.directRedirectUrl = directRedirectUrl;
        return this;
    }
}
