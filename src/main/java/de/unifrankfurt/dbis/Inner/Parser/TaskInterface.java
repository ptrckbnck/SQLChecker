package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.SQL.SQLData;
import de.unifrankfurt.dbis.config.DataSource;

public interface TaskInterface {
    String getName();

    String getSql();

    String serialize();

    Integer getScore();

    String getGroup();

    boolean isStatic();

    SQLData execute(DataSource source);
}