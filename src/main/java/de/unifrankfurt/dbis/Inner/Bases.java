package de.unifrankfurt.dbis.Inner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Bases {

    private static BaseInfo infoFromPath(Path path, Path root, Integer id) {
        Base base;
        try {
            base = Base.fromPath(path);
            return BaseInfo.of(base, id, root);
        } catch (IOException e) {
            return new BaseInfo(id, root.relativize(path), null, null, null, false, null, null, e.getMessage());
        }
    }

    public static List<BaseInfo> loadBaseInfos(Path root) throws IOException {
        int depth = 0;
        if (Files.isDirectory(root)) {
            depth = 2;
        }
        List<Path> paths = Files.walk(root, depth)
                .filter(x -> !Files.isDirectory(x))
                .collect(Collectors.toList());

        return IntStream.range(0, paths.size())
                .mapToObj(x -> Bases.infoFromPath(paths.get(x), root, x))
                .collect(Collectors.toList());
    }


}
