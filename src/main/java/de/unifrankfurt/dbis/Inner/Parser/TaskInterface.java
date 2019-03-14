package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.SQL.SQLData;
import de.unifrankfurt.dbis.config.DataSource;

public interface TaskInterface {
    String getName();

    String getSQL();

    String serialize();

    boolean isStatic();

    SQLData execute(DataSource source);
}