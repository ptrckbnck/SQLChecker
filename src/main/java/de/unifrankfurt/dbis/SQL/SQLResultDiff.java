package de.unifrankfurt.dbis.SQL;

public interface SQLResultDiff {

    String getMessage();

    Boolean isOk();

    String getMinimalMessage();
}
