package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.SQL.SQLData;
import de.unifrankfurt.dbis.config.DataSource;

import java.util.Collection;

public interface TaskInterface {
    String getName();

    String getSql();

    String serialize();

    Integer getScore();

    String getGroup();

    Collection<Integer> getOrder();

    boolean isStatic();

    SQLData execute(DataSource source);


}