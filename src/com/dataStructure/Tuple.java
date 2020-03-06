package com.dataStructure;

import java.util.Objects;

public class Tuple<T> {

    public T x;
    public T y;
    public T z;

    public Tuple(T x, T y, T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != Tuple.class) return false;

        Tuple other = (Tuple)o;
        return other.x.equals(this.x) && other.y.equals(this.y) && other.z.equals(this.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
