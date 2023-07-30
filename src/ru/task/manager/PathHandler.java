package ru.task.manager;

import java.nio.file.Path;

public interface PathHandler {
    Path normalizePath(String filePath);
}
