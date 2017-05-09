package com.github.florent37.materialviewpager.sample.utility;

import com.github.florent37.materialviewpager.sample.other.Colors;

import java.util.Random;

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
}
