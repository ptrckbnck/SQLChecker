package de.unifrankfurt.dbis.IO;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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
        return Files.readString(path, StandardCharsets.UTF_8);
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
        try {
            return new Gson().fromJson(loadString(path), classOf);
        } catch (JsonSyntaxException e) {
            return null;
        }

    }


    /**
     * Create File at path with given String text.
     * @param path
     * @param text
     * @throws IOException
     */
    public static void saveText(Path path, String text) throws IOException {
        path = path.toAbsolutePath();
        if (Files.exists(path)) {
            Files.move(path, path.getParent().resolve(path.getFileName() + ".backup"), StandardCopyOption.REPLACE_EXISTING);
        }
        Files.createFile(path);
        Files.write(path, text.getBytes(StandardCharsets.UTF_8));
    }
}
