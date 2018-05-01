package sample.patterns.com.osh.sampleapppatterns.utils;

import com.osh.patterns.lib.handlers.data.ErrorHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Oleg Shatava on 01.05.18.
 */

public class Log {

    private static ErrorHandler errorHandler = t -> d(t.toString());

    public static ErrorHandler onError() {
        return errorHandler;
    }

    public static void d(String log) {
        java.util.Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm::ss.SSS");
        String format = formatter.format(date);
        String out = "#" + Thread.currentThread().getName()
                + " - " + format
                + " : " + log;
        System.out.println(out);
    }

}
