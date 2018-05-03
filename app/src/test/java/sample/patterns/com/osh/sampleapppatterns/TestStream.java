package sample.patterns.com.osh.sampleapppatterns;

import com.osh.patterns.lib.function.data.Maybe;
import com.osh.patterns.lib.list.Stream;

import org.junit.Test;

import java.util.List;

import sample.patterns.com.osh.sampleapppatterns.utils.Log;

/**
 * Created by Oleg Shatava on 30.04.18.
 */

public class TestStream {

    @Test
    public void testStreamForArray() {

        Integer[] v = new Integer[]{1, 3, 6, 0, 17};

        Maybe<List<String>> value = Stream.with(v)
                .map(i -> (i * 10))
                .sort(Integer::compareTo)
                .filter(i -> i > 3)
                .map(i -> "I:" + i)
                .get();

        Log.d(value.get().toString());
    }


}
