package de.unifrankfurt.dbis.Inner;

import de.unifrankfurt.dbis.Inner.Parser.BaseType;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;

public class BaseInfo {
    protected final Integer id;
    protected final Path path;
    protected final String name;
    protected final Charset charset;
    protected final List<Student> authors;
    protected final Boolean valid;
    protected final Base base;
    protected final BaseType baseType;
    protected final String error;

    public BaseInfo(Integer id, Path path, String name, Charset charset, List<Student> authors, Boolean valid, Base base, BaseType baseType, String error) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.charset = charset;
        this.authors = authors;
        this.valid = valid;
        this.base = base;
        this.baseType = baseType;
        this.error = error;
    }

    public static BaseInfo of(Base base, Integer id, Path root) {
        return new BaseInfo(id, root.relativize(base.getPath()), base.getName(), base.getCharset(), base.getAuthors(), true, base, base.getType(), "");
    }

    public Path getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Charset getCharset() {
        return charset;
    }

    public List<Student> getAuthors() {
        return authors;
    }

    public Base getBase() {
        return base;
    }

    public Boolean getValid() {
        return valid;
    }

    public Integer getId() {
        return id;
    }

    public String getError() {
        return error;
    }

    public BaseType getBaseType() {
        return baseType;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BaseInfo.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("path=" + path)
                .add("name='" + name + "'")
                .add("charset=" + charset)
                .add("authors=" + authors)
                .add("valid=" + valid)
                .add("base=" + base.getClass().getName() + "@" + base.hashCode())
                .add("baseType=" + baseType)
                .add("error='" + error + "'")
                .toString();
    }

}




