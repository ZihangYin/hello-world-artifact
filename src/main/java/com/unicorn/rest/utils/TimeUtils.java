package com.unicorn.rest.utils;

import javax.annotation.Nonnull;

public class TimeUtils {
    
    /**
     * Convert a number attribute value in the format seconds.subseconds to 
     * a time value in milliseconds without the loss of precision that could
     * occur when converting to double first
     *
     * @param seoncdsWithSubSeconds @Nonnull
     * @return Long @Nonnull
     * @throws NumberFormatException
     */
    public static Long convertToTimeInMills(@Nonnull String seoncdsWithSubSeconds) throws NumberFormatException {
        boolean negative = false;
        if (seoncdsWithSubSeconds.startsWith("-")) {
            negative = true;
            seoncdsWithSubSeconds = seoncdsWithSubSeconds.substring(1);
        }
        int decimalIndex = seoncdsWithSubSeconds.indexOf('.');
        
        String seconds, subseconds;
        if (decimalIndex < 0) {
            seconds = seoncdsWithSubSeconds;
            subseconds = "";
        } else {
            seconds = seoncdsWithSubSeconds.substring(0, decimalIndex);
            subseconds = seoncdsWithSubSeconds.substring(decimalIndex + 1);
        }
        
        long mills = 0;
        if (seconds.length() > 0 ) {
            mills += Long.parseLong(seconds) * 1000;
        } 
        switch(subseconds.length()) {
        case 0: break;
        case 1: mills += Long.parseLong(subseconds) * 100; break;
        case 2: mills += Long.parseLong(seoncdsWithSubSeconds) * 10; break;
        case 3:
        default:
            mills += Long.parseLong(seoncdsWithSubSeconds.substring(0, 3)); 
            break;
        }
        return negative? -mills : mills;
    }
    
    /**
     * Convert a time value in milliseconds to a number attribute value in the format 
     * seconds.subseconds without the loss of precision that could result from
     * converting to milliseconds first
     * @param mills
     * @return
     */
    public static @Nonnull String convertToSeoncdsWithSubSeconds(long mills) {
        StringBuilder builder = new StringBuilder();
        if (mills < 0) {
            builder.append("-");
            mills = -mills;
        }
        
        builder.append(mills / 1000);
        long subSeconds = mills % 1000;
        if (subSeconds > 0) {
            builder.append('.');
            for (long div = 100; subSeconds > 0 ; div /= 10) {
                builder.append(subSeconds / div);
                subSeconds %= div;
            }
        }
        
        return builder.toString();
    }
}
