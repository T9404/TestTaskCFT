package ru.task.manager;

import ru.task.config.FilePaths;

import java.io.BufferedWriter;
import java.nio.file.Path;
import java.io.FileWriter;

public class Writer extends PathHandlerImpl {
    private final BufferedWriter bufferedWriter;
    private final Path outputFilePath;

    public Writer(String outputFileName) {
        this(FilePaths.OUTPUT_FILE_PATH, outputFileName);
    }

    public Writer(String outputFilePath, String outputFileName) {
        this.outputFilePath = normalizePath(outputFilePath + outputFileName);
        bufferedWriter = createFile();
    }

    public <T extends Comparable<T>> void writeFile(T value) {
        try {
            bufferedWriter.write(value.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private BufferedWriter createFile() {
        try {
            return new BufferedWriter(new FileWriter(outputFilePath.toFile()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void closeFile() {
        try {
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
