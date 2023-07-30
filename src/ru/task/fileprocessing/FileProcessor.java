package ru.task.fileprocessing;

import ru.task.arguments.LaunchParameters;
import ru.task.config.FilePaths;
import ru.task.enums.Order;
import ru.task.enums.Type;
import ru.task.record.GenericComparator;
import ru.task.record.IntegerParser;
import ru.task.record.StringParser;
import ru.task.record.ValueParser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileProcessor {
    private final LaunchParameters launchParameters;
    private String[] inputFilesName;
    private String outputFileName;
    private Type type;
    private Order order;
    private ValueParser valueParser;
    private GenericComparator<?> genericComparator;
    private String[] sortedFiles;
    private Splitter splitter;

    public FileProcessor(String[] args) {
        launchParameters = new LaunchParameters(args);
        initializeParameters();
    }

    private void initializeParameters() {
        inputFilesName = launchParameters.getInputNameFiles();
        outputFileName = launchParameters.getOutputNameFile();
        type = launchParameters.getType();
        order = launchParameters.getOrder();
        sortedFiles = launchParameters.getNameFilesToSort();
        initValueParser();
        initGenericComparator();
    }

    private void initValueParser() {
        if (type == Type.INTEGER) {
            valueParser = new IntegerParser(type);
        } else {
            valueParser = new StringParser(type);
        }
    }

    private void initGenericComparator() {
        if (type == Type.INTEGER) {
            genericComparator = new GenericComparator<Integer>(order);
        } else {
            genericComparator = new GenericComparator<String>(order);
        }
    }

    public void start() {
        try {
            sortFilesIfNotEmpty();
            merge();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Программа завершена.");
        }
    }

    private void merge() {
        Merger merger = new Merger(inputFilesName, valueParser, outputFileName);
        merger.merge(genericComparator);
        merger.closeWriter();
        merger.closeBufferReaders();
    }

    private void sortFilesIfNotEmpty() {
        try {
            sortFileBySplitCount();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void sortFileBySplitCount() {
        if (sortedFiles.length > 0) {
            sortFiles(launchParameters.getCountLineSplitFile());
        }
    }

    private void sortFiles(int countSplitLine) {
        for (String fileName: sortedFiles) {
            splitter = new Splitter(countSplitLine, fileName, valueParser);
            splitter.split(genericComparator);
            mergeTempFiles(fileName);
            deleteTempFiles(splitter.getOutputFilesName());
        }
    }

    private void mergeTempFiles(String fileName) {
        Merger mergerFile = new Merger(getFileNames(), valueParser, fileName, FilePaths.INPUT_FILE_PATH);
        mergerFile.merge(genericComparator);
        mergerFile.closeWriter();
        mergerFile.closeBufferReaders();
    }

    private static void deleteTempFiles(List<String> inputFilesName) {
        inputFilesName.forEach(FileProcessor::deleteTempFile);
    }

    private static void deleteTempFile(String fileName) {
        try {
            Files.delete(Path.of(FilePaths.INPUT_FILE_PATH + fileName));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String[] getFileNames() {
        List<String> outputFiles = splitter.getOutputFilesName();
        return outputFiles.toArray(String[]::new);
    }
}
