package net.polarizedions.polarizedbot.util;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

public class Pair<A, B> {
    public A one;
    public B two;

    @Contract(pure = true)
    public Pair(A one, B two) {
        this.one = one;
        this.two = two;
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Pair<?, ?> pair = (Pair<?, ?>)o;
        return Objects.equals(one, pair.one) &&
                Objects.equals(two, pair.two);
    }

    @Override
    public int hashCode() {
        return Objects.hash(one, two);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "one=" + one +
                ", two=" + two +
                '}';
    }
}
