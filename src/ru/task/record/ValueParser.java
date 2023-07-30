package ru.task.record;

import ru.task.enums.Type;

import java.io.IOException;

public interface ValueParser {
    <T extends Comparable<T>> T parse(String line) throws IOException;
    Type type();
}
