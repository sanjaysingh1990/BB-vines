package com.raj.moh.sanju.vines.utility;

/**
 * Created by NEERAJ on 5/13/2017.
 */

public class SingleTonAds {
    private static SingleTonAds singleton = new SingleTonAds();
    public synchronized static SingleTonAds getInstance() {
        return singleton;
    }
    private int totalcount=0;
    public void incrementCount()
    {
        totalcount++;
    }

    public int getTotalCount()
    {
        return  totalcount;
    }

}
