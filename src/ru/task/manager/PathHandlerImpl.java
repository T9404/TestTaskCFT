package ru.task.manager;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathHandlerImpl implements PathHandler {

    @Override
    public Path normalizePath(String filePath) {
        try {
            return Paths.get(filePath);
        } catch (InvalidPathException e) {
            System.out.println("Недопустимый путь к файлу: " + filePath);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
