package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.Inner.BaseBuilder;

/**
 * \/*%%<Typ>%%<Kopf>%%*\/
 * <Body>
 */
public interface ParseToken {


    /**
     * each SubToken should implement this method to check if SubToken can created of given rawToken.
     *
     * @param rawToken RawToken
     * @return true if SubToken can be created of rawToken, else false
     */
    static boolean check(RawToken rawToken) {
        return false;
    }

    /**
     * creates SubToken from given rawToken.
     *
     * @param rawToken
     * @param <T>
     * @return Subtoken if no problem occurred, null otherwise
     */
    static <T extends ParseToken> T createOf(RawToken rawToken) {
        return null;
    }

    void build(BaseBuilder bb);
}
