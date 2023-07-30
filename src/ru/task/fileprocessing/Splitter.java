package ru.task.fileprocessing;

import ru.task.config.FilePaths;
import ru.task.enums.Type;
import ru.task.manager.InputReader;
import ru.task.record.ValueParser;
import ru.task.manager.Writer;
import ru.task.record.GenericComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Splitter {
    private final InputReader inputReader;
    private Object[] values;
    private final int limitCountLine;
    private final List<String> outputFileName;
    private final ValueParser valueParser;

    public Splitter(int limitCountLine, String fileName, ValueParser valueParser) {
        this.limitCountLine = limitCountLine;
        this.valueParser = valueParser;
        inputReader = new InputReader(fileName, valueParser);
        configureValues();
        outputFileName = new ArrayList<>();
    }

    private void configureValues() {
        if (valueParser.type() == Type.INTEGER) {
            values = new Integer[limitCountLine];
        } else {
            values = new String[limitCountLine];
        }
    }

    public <T extends Comparable<T>> void split(GenericComparator<T> comparator) {
        try {
            splitData(comparator);
            inputReader.closeBufferReader();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private <T extends Comparable<T>> void splitData(GenericComparator<T> comparator) {
        int currentLine = 0, currentChunk = 1;

        while (true) {
            if (currentLine == limitCountLine) {
                fixData(comparator, currentChunk++);
                currentLine = 0;
            }
            T parsedValue = inputReader.readFile();
            if (parsedValue == null) {
                break;
            }
            values[currentLine++] = parsedValue;
        }

        if (currentLine > 0) {
            fixData(comparator, currentChunk);
        }
    }

    private <T extends Comparable<T>> void fixData(GenericComparator<T> comparator, int currentChunk) {
        sortValues(comparator, values.length);
        saveDataToFile(currentChunk);
        configureValues();
    }

    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> void sortValues(GenericComparator<T> comparator, int lengthArray) {
        T[] temp = (T[]) Arrays.copyOf(values, lengthArray);
        Sorter sorter = new Sorter(temp, comparator);
        values = sorter.getArray();
    }

    private void saveDataToFile(int currentChunk) {
        String tempFileName = "temp" + currentChunk + ".txt";
        outputFileName.add(tempFileName);
        saveValuesToTemp(tempFileName);
    }

    private void saveValuesToTemp(String tempFileName) {
        try {
            Writer writer = new Writer(FilePaths.INPUT_FILE_PATH, tempFileName);
            writeData(writer);
            writer.closeFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void writeData(Writer writer) {
        for (Object value : values) {
            writeValue(writer, value);
        }
    }

    private static void writeValue(Writer writer, Object value) {
        if (value != null) {
            writer.writeFile(value.toString());
            writer.writeFile("\n");
        }
    }

    public List<String> getOutputFilesName() {
        return outputFileName;
    }
}
