package sample.patterns.com.osh.sampleapppatterns.utils;

import com.osh.patterns.lib.handlers.data.ErrorConsumer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Oleg Shatava on 01.05.18.
 */

public class Log {

    private static ErrorConsumer errorConsumer = t -> d(t.toString());

    private Log() {
        throw new IllegalStateException("Not intended for create new instance");
    }

    public static ErrorConsumer onError() {
        return errorConsumer;
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
