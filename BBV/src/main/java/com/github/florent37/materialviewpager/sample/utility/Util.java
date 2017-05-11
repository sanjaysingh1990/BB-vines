package com.github.florent37.materialviewpager.sample.utility;

import android.util.Log;

import com.github.florent37.materialviewpager.sample.other.Colors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by android on 9/5/17.
 */

public class Util {
    private static Util singleton = new Util();
    public synchronized static Util getInstance() {
        return singleton;
    }


    private Util()
    {

    }
    public int getRadomNumber()
    {
        Colors[]cards= Colors.values();
        int min = 0;
        int max =cards.length-1;
        ;

        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

    public String coolFormat(double n, int iteration) {
         char[] c = new char[]{'K', 'M', 'B', 'T'};

        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : coolFormat(d, iteration+1));

    }

    public String getDateTime(String startTime) {
        String data = "";
        /*
         ********************** DATE AND TIME FORMATTING EXCPETION ******************
         */
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat.parse(startTime);
            DateFormat date = new SimpleDateFormat("EEE dd");
            DateFormat date2 = new SimpleDateFormat("EEE dd hh:mm a");
            DateFormat time = new SimpleDateFormat("hh:mm a");
            data= String.valueOf(date2.format(myDate));
        }
        catch (Exception ex)
        {
            Log.e("error",ex.getMessage());
        }

        return data;
    }
}
