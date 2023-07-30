package ru.task.fileprocessing;

import ru.task.record.GenericComparator;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Sorter {
    private final Object[] array;

    public <T extends Comparable<T>> Sorter(T[]  array, GenericComparator<T> comparator) {
        this.array = array;
        mergeSortArray(array, comparator);
    }

    public Object[] getArray() {
        return array;
    }

    public <T extends Comparable<T>> void mergeSortArray(T[] array, GenericComparator<T> comparator) {
        try {
            mergeSort(array, 0, array.length - 1, comparator);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static <T extends Comparable<T>> void mergeSort(T[] array, int low, int high, GenericComparator<T> comparator) {
        if (low < high) {
            int mid = (low + high) / 2;

            ExecutorService executorService = Executors.newFixedThreadPool(2);
            executorService.submit(() -> mergeSort(array, low, mid, comparator));
            executorService.submit(() -> mergeSort(array, mid + 1, high, comparator));
            executorService.shutdown();
            handleExecutor(executorService);
            merge(array, low, mid, high, comparator);
        }
    }

    private static void handleExecutor(ExecutorService executorService) {
        try {
            if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                throw new RuntimeException("Timeout: ExecutorService не завершила работу в течение ожидаемого времени.");
            }
        } catch (InterruptedException e) {
            System.out.println("Поток прерван.");
        }
    }

    private static <T extends Comparable<T>> void merge(T[] array, int low, int mid, int high, GenericComparator<T> comparator) {
        int leftSize = mid - low + 1, rightSize = high - mid;
        T[] mergedArray = Arrays.copyOfRange(array, low, high + 1);
        int leftIndex = 0, mergedIndex = low, rightIndex = mid - low + 1;

        while ((leftIndex < leftSize) && (rightIndex < leftSize + rightSize)) {
            if (comparator.compare(mergedArray[leftIndex], mergedArray[rightIndex]) < 0) {
                array[mergedIndex++] = mergedArray[leftIndex++];
            } else {
                array[mergedIndex++] = mergedArray[rightIndex++];
            }
        }

        while (leftIndex < leftSize) {
            array[mergedIndex++] = mergedArray[leftIndex++];
        }

        while (rightIndex < leftSize + rightSize) {
            array[mergedIndex++] = mergedArray[rightIndex++];
        }
    }
}
