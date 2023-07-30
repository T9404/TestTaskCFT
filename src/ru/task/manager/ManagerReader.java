package ru.task.manager;

import ru.task.record.ValueParser;

public class ManagerReader {
    private final InputReader[] inputReaders;

    public ManagerReader(String[] inputFilesName, ValueParser valueParser) {
        this.inputReaders = new InputReader[inputFilesName.length];
        initInputReader(inputFilesName, valueParser);
    }

    private void initInputReader(String[] inputFilesName, ValueParser valueParser) {
        for (int i = 0; i < inputReaders.length; i++) {
            inputReaders[i] = new InputReader(inputFilesName[i], valueParser);
        }
    }

    public InputReader[] getInputReaders() {
        return inputReaders;
    }

    public InputReader getInputReader(int index) {
        return inputReaders[index];
    }

    public void closeReaders() {
        for (InputReader inputReader : inputReaders) {
            inputReader.closeBufferReader();
        }
    }
}
