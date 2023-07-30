package ru.task.manager;

import ru.task.record.ValueParser;
import ru.task.config.FilePaths;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class InputReader extends PathHandlerImpl {
    private final Path filePath;
    private final BufferedReader reader;
    private final ValueParser valueParser;

    public InputReader(String fileName, ValueParser valueParser) {
        this(FilePaths.INPUT_FILE_PATH, fileName, valueParser);
    }

    public InputReader(String filePath, String fileName, ValueParser valueParser) {
        this.filePath = normalizePath(filePath + fileName);
        this.valueParser = valueParser;
        reader = openFile();
    }

    private BufferedReader openFile() {
        try {
            return new BufferedReader(new FileReader(filePath.toFile()));
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден: " + filePath);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public <T extends Comparable<T>> T readFile() {
        try {
            String line = getReadLineWithoutSpace();
            return getParsedValue(line);
        } catch (Exception e) {
            System.out.println(e.getMessage() + " " + filePath);
        }
        return null;
    }

    private String getReadLineWithoutSpace() throws IOException {
        String line = reader.readLine();
        try {
            getParsedValue(line);
        } catch (Exception e) {
            line = getLine(line);
        }
        return line;
    }

    private String getLine(String line) throws IOException {
        while (line != null) {
            line = reader.readLine();
            if (isGoodLine(line)) {
                break;
            }
        }
        return line;
    }

    private boolean isGoodLine(String line) {
        try {
            getParsedValue(line);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage() + " " + filePath);
        }
        return false;
    }

    private <T extends Comparable<T>> T getParsedValue(String line) throws IOException {
        return (line != null) ? valueParser.parse(line) : null;
    }

    public void closeBufferReader() {
        try {
            reader.close();
        } catch (Exception e) {
            System.out.println(e.getMessage() + " " + filePath);
        }
    }
}
