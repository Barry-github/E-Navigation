package cn.ehanghai.routecalc.common.utils;

public class Triple<A, B,C>  {

    public final A first;

    public final B second;

    public final C Third;

    public Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        Third = third;
    }

    @Override
    public String toString() {
        return "Triple{" +
                "first=" + first +
                ", second=" + second +
                ", Third=" + Third +
                '}';
    }
}
