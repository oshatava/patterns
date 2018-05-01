package com.osh.patterns.lib.list;

import com.osh.patterns.lib.handlers.Mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Oleg Shatava on 30.04.18.
 */

public class CollectionUtils {


    public static <T> List<T> distinct(List<T> in, Compare<T> comparator) {
        List<T> out = new ArrayList<T>();
        for (T t : in) {
            if (!exist(out, t, comparator)) {
                out.add(t);
            }
        }
        return out;
    }


    public static <T> boolean exist(List<T> in, T item, Compare<T> comparator) {
        List<T> out = new ArrayList<T>();
        for (T t : in) {
            if (comparator.isEqual(t, item))
                return true;
        }

        return false;
    }

    public static <ClassTo, ClassFrom> List<ClassTo> map(Iterable<ClassFrom> in, Mapper<ClassTo, ClassFrom> mapper) {
        List<ClassTo> ret = new ArrayList<ClassTo>();
        if (mapper != null && in != null) {
            for (ClassFrom from : in) {
                ret.add(mapper.map(from));
            }
        }
        return ret;
    }

    public static <ClassTo, ClassFrom> List<ClassTo> mapI(Iterable<ClassFrom> in, MapperIndexed<ClassTo, ClassFrom> mapper) {
        List<ClassTo> ret = new ArrayList<ClassTo>();
        if (mapper != null && in != null) {
            int index = 0;
            for (ClassFrom from : in) {
                ret.add(mapper.map(index, from));
                index++;
            }
        }
        return ret;
    }

    public static <T> T first(List<T> in) {
        if (in == null) return null;
        if (in.size() == 0) return null;
        return in.get(0);
    }

    public static <T> T last(List<T> in) {
        if (in == null) return null;
        if (in.size() == 0) return null;
        return in.get(in.size() - 1);
    }

    public static <ClassTo, ClassFrom> List<ClassTo> map(List<ClassTo> out,
                                                         Iterable<ClassFrom> in,
                                                         Mapper<ClassTo, ClassFrom> mapper) {
        if (mapper != null && in != null) {
            for (ClassFrom from : in) {
                out.add(mapper.map(from));
            }
        }
        return out;
    }

    public static int compare(long lhs, long rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }

    public static int compareReverse(long lhs, long rhs) {
        return rhs < lhs ? -1 : (rhs == lhs ? 0 : 1);
    }


    public static <T> List<T> findAll(Iterable<T> items, Match<T> match) {
        List<T> ret = new ArrayList<T>();
        if (items == null) return ret;
        if (match == null) return ret;
        for (T item : items)
            if (match.isMatch(item)) {
                ret.add(item);
            }
        return ret;
    }

    public static <T> void forEach(Iterable<T> items, OnEach<T> onEach) {
        if (items == null) return;
        if (onEach == null) return;
        for (T item : items)
            onEach.onEach(item);
    }

    public static <T> List<T> toList(T... ts) {
        List<T> ret = new ArrayList<T>();
        for (T r : ts)
            ret.add(r);
        return ret;
    }

    public static <T, P> List<T> aggregate(List<P> in, Mapper<List<T>, P> aggregator) {
        List<T> ret = new ArrayList<T>();
        for (P p : in)
            ret.addAll(aggregator.map(p));
        return ret;
    }

    public static <T> List<T> clone(List<T> in) {
        List ret = new ArrayList();
        if (in != null) {
            ret.addAll(in);
        }
        return ret;
    }

    public static <T> List<T> addAll(List<T> in, T... s) {
        Collections.addAll(in, s);
        return in;
    }

    public static List<Integer> addAll(List<Integer> in, int[] s) {
        for (int i : s)
            in.add(new Integer(i));
        return in;
    }

    public static <T> boolean exists(List<T> in, final T t) {
        List<T> ret = findAll(in, new Match<T>() {
            @Override
            public boolean isMatch(T item) {
                return item.equals(t);
            }
        });
        if (ret != null)
            if (ret.size() > 0)
                return true;
        return false;
    }

    public static boolean ends(final String url, List<String> suffixes) {
        List<String> ret = findAll(suffixes, new Match<String>() {
            @Override
            public boolean isMatch(String item) {
                return url.endsWith(item);
            }
        });
        if (ret != null)
            if (ret.size() > 0)
                return true;
        return false;
    }

    public static <T> Builder<T> with(List<T> list) {
        return new Builder<T>(list);
    }

    public static Builder<Integer> with(int[] list) {
        List<Integer> r = new ArrayList<>();
        return new Builder<>(CollectionUtils.addAll(r, list));
    }

    public interface MapperIndexed<ClassTo, ClassFrom> {
        ClassTo map(int index, ClassFrom from);
    }

    public interface OnEach<T> {
        void onEach(T item);
    }

    public interface Match<T> {
        boolean isMatch(T item);
    }

    public interface Compare<T> {
        boolean isEqual(T l, T r);
    }

    public static class Builder<T> {
        private List<T> list;

        private Builder(List<T> list) {
            this.list = list;
        }

        public <ClassTo> Builder<ClassTo> map(Mapper<ClassTo, T> mapper) {
            return new Builder<ClassTo>(CollectionUtils.map(list, mapper));
        }

        public <ClassTo> Builder<ClassTo> mapI(MapperIndexed<ClassTo, T> mapper) {
            return new Builder<ClassTo>(CollectionUtils.mapI(list, mapper));
        }


        public <ClassFrom> Builder<T> aggregate(List<ClassFrom> in, Mapper<List<T>, ClassFrom> aggregator) {
            for (ClassFrom p : in)
                list.addAll(aggregator.map(p));
            return this;
        }

        public Builder<T> addAll(T... s) {
            Collections.addAll(list, s);
            return this;
        }

        public Builder<T> insert(T... s) {
            int i = 0;
            for (T l : s) {
                list.add(i++, l);
            }
            return this;
        }

        public Builder<T> clone() {
            list = CollectionUtils.clone(list);
            return this;
        }

        public Builder<T> findAll(Match<T> match) {
            list = CollectionUtils.findAll(list, match);
            return this;
        }

        public Builder<T> forEach(OnEach<T> onEach) {
            CollectionUtils.forEach(list, onEach);
            return this;
        }

        public T first() {
            return CollectionUtils.first(list);
        }

        public T findFirst(Match<T> match) {
            for (T t : list) {
                if (match.isMatch(t))
                    return t;
            }
            return null;
        }

        public T last() {
            return CollectionUtils.last(list);
        }

        public List<T> list() {
            return list;
        }


        public boolean contains(T i) {
            return list.contains(i);
        }

        public boolean isEmpty() {
            return list.size() == 0;
        }
    }
}
