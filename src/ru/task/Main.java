package ru.task;

import ru.task.fileprocessing.FileProcessor;

public class Main {

    public static void main(String[] args) {
        printAvailableMemoryInJVM();
        FileProcessor fileProcessor = new FileProcessor(args);
        fileProcessor.start();
    }

    private static void printAvailableMemoryInJVM() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long maxMemoryInMB = maxMemory / (1024 * 1024);
        System.out.println("Доступно памяти в JVM: " + maxMemoryInMB + " MB");
    }
}
