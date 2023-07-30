package ru.task.arguments;

import ru.task.enums.Order;
import ru.task.enums.Type;

import java.util.Arrays;
import java.util.Scanner;

public class LaunchParameters {
    private String[] args;
    private Order order;
    private Type type;
    private String outputNameFile;
    private String[] inputNameFiles;
    private String[] nameFilesToSort;
    private int countLineSplitFile;

    public LaunchParameters(String[] args) {
        this.args = args;
        init();
    }

    public int getCountLineSplitFile() {
        return countLineSplitFile;
    }

    public String[] getNameFilesToSort() {
        return nameFilesToSort;
    }

    public Order getOrder() {
        return order;
    }

    public Type getType() {
        return type;
    }

    public String getOutputNameFile() {
        return outputNameFile;
    }

    public String[] getInputNameFiles() {
        return inputNameFiles;
    }

    private void init() {
        promptForOutputFileAndType();
        parseOrder();
        parseType();
        parseOutputFileName();
        parseInputFileNames();
        parseFilesToSort();
    }

    private void promptForOutputFileAndType() {
        while (args.length < 1) {
            System.out.println("Необходимо указать имя выходного файла и тип данных.");
            String input = new Scanner(System.in).nextLine();
            args = input.split(" ");
        }
    }

    private void parseOrder() {
        if (Arrays.asList(args).contains("-d")) {
            order = Order.DESCENDING;
            return;
        }
        order = Order.ASCENDING;
    }

    private void parseType() {
        String inputType = getCorrectType();
        type = isStringType(inputType) ? Type.STRING : Type.INTEGER;
    }

    private String getCorrectType() {
        String input = getArgsType();
        while (!isStringType(input) && !isIntegerType(input)) {
            System.out.println("Укажите тип данных. Используйте '-s' для строк и '-i' для чисел.");
            input = getUserInput();
        }
        return input;
    }

    private String getArgsType() {
        return String.valueOf(Arrays.stream(args)
                .filter(s -> s.contains("-s") || s.contains("-i"))
                .findFirst());
    }

    private String getUserInput() {
        return new Scanner(System.in).nextLine();
    }

    private boolean isStringType(String input) {
        return input.contains("-s");
    }

    private boolean isIntegerType(String input) {
        return input.contains("-i");
    }

    private void parseOutputFileName() {
        outputNameFile = Arrays.stream(args)
                .filter(s -> s.contains(".txt"))
                .findFirst()
                .orElseGet(this::getOutputFileName);
    }

    private String getOutputFileName() {
        System.out.println("Укажите имя выходного файла.");
        return getFileName();
    }

    private String getFileName() {
        String input = getUserInput();
        while (!input.contains(".txt")) {
            System.out.println("Имя выходного файла должно содержать расширение '.txt'");
            input = getUserInput();
        }
        return input;
    }

    private void parseInputFileNames() {
        int inputFilesStartIndex = Arrays.asList(args).indexOf(outputNameFile) + 1;
        int countInputFiles = getInputFilesEndIndex() - inputFilesStartIndex;
        getNameInputFiles(inputFilesStartIndex, countInputFiles);
    }

    private void getNameInputFiles(int inputFilesStartIndex, int countInputFiles) {
        if ((countInputFiles < 1)) {
            System.out.println("Необходимо указать хотя бы один входной файл.");
            getNameInputFiles();
        } else {
            copyNameInputFiles(inputFilesStartIndex, countInputFiles);
        }
    }

    private void getNameInputFiles() {
        String userInput = getUserInput();
        while (userInput.isBlank() || !userInput.contains(".txt")) {
            System.out.println("Необходимо указать хотя бы один входной файл.");
            userInput = getUserInput();
        }
        inputNameFiles = userInput.split(" ");
    }

    private void copyNameInputFiles(int inputFilesStartIndex, int countInputFiles) {
        inputNameFiles = new String[countInputFiles];
        System.arraycopy(args, inputFilesStartIndex, inputNameFiles, 0, countInputFiles);
    }

    private int getInputFilesEndIndex() {
        if (Arrays.asList(args).contains("sorted")) {
            return Arrays.asList(args).indexOf("sorted");
        } else {
            return args.length;
        }
    }

    private void parseFilesToSort() {
        if (hasNoKeyPhrase()) {
            return;
        }
        getFilesToSort();
    }

    private boolean hasNoKeyPhrase() {
        if (!Arrays.asList(args).contains("sorted")) {
            nameFilesToSort = new String[0];
            return true;
        }
        return false;
    }

    private void getFilesToSort() {
        int sortedFilesStartIndex = Arrays.asList(args).indexOf("sorted");
        getCountLineSplitFile(++sortedFilesStartIndex);
        int numSortedFiles = args.length - ++sortedFilesStartIndex;
        copyNameFilesToSort(sortedFilesStartIndex, numSortedFiles);
    }

    private void getCountLineSplitFile(int index) {
        if (isNumber(args[index])) {
            countLineSplitFile = Integer.parseInt(args[index]);
        } else {
            System.out.println("Необходимо указать размер chunk для сортировки.");
            countLineSplitFile = getUserInputCountLine();
        }
    }

    private int getUserInputCountLine() {
        String userInput = new Scanner(System.in).nextLine();
        while (!isNumber(userInput)) {
            System.out.println("Укажите число: ");
            userInput = new Scanner(System.in).nextLine();
        }
        return Integer.parseInt(userInput);
    }

    private boolean isNumber(String input) {
        return input.matches("\\d+");
    }

    private void copyNameFilesToSort(int sortedFilesStartIndex, int numSortedFiles) {
        nameFilesToSort = new String[numSortedFiles];
        System.arraycopy(args, sortedFilesStartIndex, nameFilesToSort, 0, numSortedFiles);
    }
}
