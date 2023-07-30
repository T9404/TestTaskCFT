package ru.task.record;

import ru.task.enums.Type;

import java.io.IOException;

public record StringParser(Type type) implements ValueParser {

    @Override
    public <T extends Comparable<T>> T parse(String line) throws IOException {
        return parseString(line);
    }

    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> T parseString(String line) throws IOException {
        if (line.matches("\\s+") || line.isEmpty()) {
            throw new IOException("Error parsing string value: " + line);
        }
        return (T) line;
    }
}
