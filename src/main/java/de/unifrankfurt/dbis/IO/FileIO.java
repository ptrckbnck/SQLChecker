package de.unifrankfurt.dbis.IO;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileIO {


    /**
     * returns Text from File at path as String.
     *
     * @param path
     * @return String
     * @throws IOException
     */
    private static String loadString(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    /**
     * load object from disk using gson.
     * @param path Path to serialized Object.
     * @param classOf Class of Object
     * @param <T> type of Object
     * @return deserialized object
     * @throws IOException IO
     */
    public static <T> T load(Path path, Class<T> classOf) throws IOException {
        return new Gson().fromJson(loadString(path), classOf);
    }

    /**
     * serialize object and save at path.
     * @param path
     * @param object
     * @throws IOException
     */
    public static void save(Path path, Object object) throws IOException {
        if (Files.exists(path)) {
            Files.move(path, path.getParent().resolve(path.getFileName() + ".backup"), StandardCopyOption.REPLACE_EXISTING);
        }
        Files.createFile(path);
        Files.write(path, (new Gson()).toJson(object).getBytes());
    }

    /**
     * Create File at path with given String text.
     * @param path
     * @param text
     * @throws IOException
     */
    public static void saveText(Path path, String text) throws IOException {
        if (Files.exists(path)) {
            Files.move(path, path.getParent().resolve(path.getFileName() + ".backup"), StandardCopyOption.REPLACE_EXISTING);
        }
        Files.createFile(path);
        Files.write(path, text.getBytes(StandardCharsets.UTF_8));
    }
}
