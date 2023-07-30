package ru.task.record;

import ru.task.enums.Type;

public record IntegerParser(Type type) implements ValueParser {

    @Override
    public <T extends Comparable<T>> T parse(String line) throws NumberFormatException {
        return parseInteger(line);
    }

    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> T parseInteger(String line) throws NumberFormatException {
        String integerRegex = "(-)*[0-9]+";
        if (line.matches(integerRegex)) {
            return (T) Integer.valueOf(line);
        }
        throw new NumberFormatException("Error parsing integer value: " + line);
    }
}
