package utils;

import Exceptions.FileUtilityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    // Private constructor because this is a utility class with static methods. No point of initializing.
    private FileUtils() {}

    /**
     * Creates a file
     *
     * @param location full path to create the file
     * @throws FileUtilityException if an error occurs while extracting the file
     */
//    public static void createFile(String location) throws FileUtilityException {
//        try {
//            Files.createFile(Paths.get(location));
//        } catch (IOException e) {
//            String msg = "Error in creating file at: " + location;
//            log.error(msg, e);
//            throw new FileUtilityException(msg, e);
//        }
//    }

    /**
     * Creates a directory
     *
     * @param path path of the directory to create
     * @throws FileUtilityException if an error occurs while creating the directory
     */
//    public static void createDirectory(String path) throws FileUtilityException {
//        try {
//            Files.createDirectories(Paths.get(path));
//        } catch (IOException e) {
//            String msg = "Error in creating directory at: " + path;
//            log.error(msg, e);
//            throw new FileUtilityException(msg, e);
//        }
//    }

    /**
     * Writes the string content to a file
     *
     * @param path    path of the file to be written.
     * @param content Content to be written.
     * @throws FileUtilityException if an error occurs while writing to file
     */
//    public static void writeToFile(String path, String content) throws FileUtilityException {
//        try {
//            Files.write(Paths.get(path), content.getBytes(StandardCharsets.UTF_8),
//                    StandardOpenOption.CREATE,
//                    StandardOpenOption.TRUNCATE_EXISTING);
//        } catch (IOException e) {
//            String msg = "I/O error while writing to file at: " + path;
//            log.error(msg, e);
//            throw new FileUtilityException(msg, e);
//        }
//    }

    /**
     * Read contents of a file as text
     *
     * @param path full path of the file to read
     * @return text content of the file
     * @throws FileUtilityException if an error occurs while reading the file
     */
    public static String readFileContentAsText(String path) throws FileUtilityException {
//        try {
//            return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            String msg = "Error while reading file " + path;
//            log.error(msg, e);
//            throw new FileUtilityException(msg, e);
//        }
        return null;
    }

    /**
     * Delete a given file
     *
     * @param path Path to the file to be deleted
     * @throws FileUtilityException if unable to delete the file
     */
//    public static void deleteFile(String path) throws FileUtilityException {
//        try {
//            Files.delete(Paths.get(path));
//        } catch (IOException e) {
//            String errorMsg = "Error while deleting file : " + path;
//            log.error(errorMsg, e);
//            throw new FileUtilityException(errorMsg, e);
//        }
//    }

    /**
     * Deletes a directory recursively.
     *
     * @param directory directory to delete
     * @throws FileUtilityException in case deletion is unsuccessful or {@code directory} does not exist or is not a directory
     *
     */
    public static void deleteDirectory(final File directory) throws FileUtilityException {
        if (!directory.exists()) {
            return;
        }
        if (!directory.delete()) {
            final String message = "Unable to delete directory " + directory + ".";
            throw new FileUtilityException(message);
        }
    }
}
