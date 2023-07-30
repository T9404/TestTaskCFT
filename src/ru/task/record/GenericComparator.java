package ru.task.record;

import ru.task.enums.Order;

import java.util.Comparator;

public record GenericComparator<T extends Comparable<T>>(Order order) implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
        if (o1 == null) {
            return (o2 == null) ? 0 : -1;
        } else if (o2 == null) {
            return 1;
        } else {
            int result = o1.compareTo(o2);
            return order == Order.ASCENDING ? result : -result;
        }
    }

    public boolean isRightOrder(T first, T second) {
        return compare(first, second) <= 0;
    }
}
