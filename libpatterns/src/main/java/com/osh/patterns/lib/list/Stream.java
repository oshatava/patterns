package com.osh.patterns.lib.list;

import com.osh.patterns.lib.Base;
import com.osh.patterns.lib.function.Filter;
import com.osh.patterns.lib.function.Mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Oleg Shatava on 03.05.18.
 */

public class Stream<T> extends Base<List<T>> {

    protected Stream(List<T> value) {
        super(value);
    }

    public static <T> Stream<T> with(T... value) {
        List<T> temp = empty();
        Collections.addAll(temp, value);
        return new Stream<>(temp);
    }

    public static <T> Stream<T> with(Collection<T> value) {
        List<T> temp = empty();
        temp.addAll(value);
        return new Stream<>(temp);
    }

    private static <T> List<T> empty() {
        return new ArrayList<>();
    }

    public Stream<T> append(T... s) {
        Collections.addAll(getValue(), s);
        return this;
    }

    public Stream<T> append(Collection<T> s) {
        getValue().addAll(s);
        return this;
    }

    public Stream<T> insert(Collection<T> s) {
        return with(s).append(getValue());
    }

    public Stream<T> insert(T... s) {
        return with(s).append(getValue());
    }

    public <R> Stream<R> map(Mapper<R, T> mapper) {
        List<R> newItems = empty();
        for (T t : getValue())
            newItems.add(mapper.map(t));
        return with(newItems);
    }

    public Stream<T> filter(Filter<T> filter) {
        List<T> newItems = empty();
        for (T t : getValue()) {
            if (filter.match(t)) {
                newItems.add(t);
            }
        }
        return with(newItems);
    }

    public Stream<T> sort(Comparator<T> comparator) {
        Collections.sort(getValue(), comparator);
        return this;
    }

}
