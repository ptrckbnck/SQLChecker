package de.unifrankfurt.dbis.Inner;

/**
 * status of evaluation of given task
 */
public enum EvalStatus {
    OKAY, ERROR, FAIL_SCHEMA_MISMATCH, FAIL_SQL_TYPE_MISMATCH, FAIL_DATATYPE_MISMATCH, FAIL_VALUE_MISMATCH
}
