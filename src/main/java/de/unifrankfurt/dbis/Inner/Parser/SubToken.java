package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.Inner.BaseBuilder;

public interface SubToken {
    /**
     * each SubToken should implement this method to check if SubToken can becreated of given rawToken.
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
     * @return Subtoken if no problem occured, null otherwise
     */
    static <T extends SubToken> T createOf(RawToken rawToken) {
        return null;
    }

    void build(BaseBuilder bb);
}
