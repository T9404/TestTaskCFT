package ru.task.fileprocessing;

import ru.task.enums.Type;
import ru.task.manager.ManagerReader;
import ru.task.manager.Writer;
import ru.task.record.ValueParser;
import ru.task.record.GenericComparator;

public class Merger {
    private ManagerReader managerReaders;
    private Writer writer;
    private int minIndex;
    private int prevMinIndex;
    private int countNullReaders;
    private Object[] previousValuesPerFiles;
    private Object[] valuesPerFiles;

    public Merger(String[] inputFilesName, ValueParser valueParser, String outputFileName, String outputFilePath) {
        initialize(inputFilesName, valueParser, outputFileName, outputFilePath);
    }

    public Merger(String[] inputFilesName, ValueParser valueParser, String outputFileName) {
        initialize(inputFilesName, valueParser, outputFileName, null);
    }

    private void initialize(String[] inputFilesName, ValueParser valueParser, String outputFileName, String outputFilePath) {
        managerReaders = new ManagerReader(inputFilesName, valueParser);
        initWriter(outputFileName, outputFilePath);
        initArrays(valueParser.type(), inputFilesName.length);
    }

    private void initWriter(String outputFileName, String outputFilePath) {
        if (outputFilePath == null) {
            writer = new Writer(outputFileName);
        } else {
            writer = new Writer(outputFilePath, outputFileName);
        }
    }

    private void initArrays(Type type, int countFiles) {
        switch (type) {
            case STRING -> initByString(countFiles);
            case INTEGER -> initByInteger(countFiles);
        }
    }

    private void initByString(int countFiles) {
        valuesPerFiles = new String[countFiles];
        previousValuesPerFiles = new String[countFiles];
    }

    private void initByInteger(int countFiles) {
        valuesPerFiles = new Integer[countFiles];
        previousValuesPerFiles = new Integer[countFiles];
    }

    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> void merge(GenericComparator<T> comparator) {
        T previousValue = null;
        do {
            findMinValue(comparator, previousValue);
            if (valuesPerFiles[minIndex] == null) {
                break;
            } else if (previousValue == null || comparator.isRightOrder(previousValue, (T) valuesPerFiles[minIndex])) {
                writeValue((T) valuesPerFiles[minIndex]);
                previousValue = (T) valuesPerFiles[minIndex];
                resetStatistic();
            }
        } while (countNullReaders != managerReaders.getInputReaders().length);
    }

    private <T extends Comparable<T>> void findMinValue(GenericComparator<T> comparator, T previousValue) {
        for (int i = 0; i < managerReaders.getInputReaders().length; i++) {
            findAvailableValue(i, comparator, previousValue);
            if (isElementNull(i)) {
                countNullReaders++;
                continue;
            }
            updateMinIndex(comparator, i);
        }
    }

    private boolean isElementNull(int index) {
        return valuesPerFiles[index] == null;
    }

    private <T extends Comparable<T>> void writeValue(T value) {
        writer.writeFile(value);
        writer.writeFile("\n");
    }

    private void resetStatistic() {
        countNullReaders = 0;
        previousValuesPerFiles[minIndex] = valuesPerFiles[minIndex];
        prevMinIndex = minIndex;
        minIndex = 0;
    }

    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> void findAvailableValue(int i, GenericComparator<T> comparator, T previousWritten) {
        readValue(i);
        T currentValue = (T) valuesPerFiles[i], previousFileValue = (T) previousValuesPerFiles[i];
        if (isExists(previousFileValue) && isExists(currentValue) && (isInWrongOrder(previousFileValue, currentValue, comparator))
                && (isInWrongOrder(previousWritten, currentValue, comparator))) {
            valuesPerFiles[i] = findNextValue(i, comparator);
        }
    }

    private <T extends Comparable<T>> boolean isExists(T value) {
        return value != null;
    }

    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> void updateMinIndex(GenericComparator<T> comparator, int currentIndex) {
        T currentValue = (T) valuesPerFiles[currentIndex], minValue = (T) valuesPerFiles[minIndex];
        if (isNotExists(minValue) || !isInWrongOrder(currentValue, minValue, comparator)) {
            minIndex = currentIndex;
        }
    }

    private <T extends Comparable<T>> boolean isNotExists(T value) {
        return value == null;
    }

    private void readValue(int index) {
        if (isElementNull(index) || prevMinIndex == index) {
            valuesPerFiles[index] = managerReaders.getInputReader(index).readFile();
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> T findNextValue(int index, GenericComparator<T> comparator) {
        T previousValue = (T) previousValuesPerFiles[index], currentValue = managerReaders.getInputReader(index).readFile();
        while (hasMoreToRead(currentValue) && isInWrongOrder(previousValue, currentValue, comparator)) {
            currentValue = managerReaders.getInputReader(index).readFile();
        }
        return currentValue;
    }

    private <T extends Comparable<T>> boolean isInWrongOrder(T previousValue, T value, GenericComparator<T> comparator) {
        return !comparator.isRightOrder(previousValue, value);
    }

    private <T extends Comparable<T>> boolean hasMoreToRead(T value) {
        return value != null;
    }

    public void closeBufferReaders() {
        managerReaders.closeReaders();
    }

    public void closeWriter() {
        writer.closeFile();
    }
}
