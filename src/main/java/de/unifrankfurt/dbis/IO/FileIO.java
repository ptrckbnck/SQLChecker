package de.unifrankfurt.dbis.IO;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileIO {


    private static String loadString(Path path) throws IOException {
         return new String(Files.readAllBytes(path));
    }

    public static <T> T load(Path path, Class<T> classOf) throws IOException {
        return new Gson().fromJson(loadString(path), classOf);
    }

    public static void save(Path path, Object object) throws IOException {
        if (Files.exists(path)) {
            Files.move(path, path.getParent().resolve(path.getFileName() + ".backup"), StandardCopyOption.REPLACE_EXISTING);
        }
        Files.createFile(path);
        Files.write(path,(new Gson()).toJson(object).getBytes());
    }
}
