package edu.arizona.kfs.module.tax.businessobject;

import edu.arizona.kfs.sys.KFSConstants;

public class ElectronicFileException {

    private String tin;
    private String name;
    private String recordType;
    private String field;
    private String exception;
    private Object value;

    public static final String PAYEE = "PAYEE";
    public static final String PAYER = "PAYER";
    public static final String END_PAYER = "END PAYER";
    public static final String END_RECORD = "END RECORD";
    public static final String TRANSMITTER = "TRANSMITTER";

    public ElectronicFileException() {
    }

    public ElectronicFileException(String recordType, String field, Object value, String exception) {
        this(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING, recordType, field, value, exception);
    }

    public ElectronicFileException(String tin, String name, String recordType, String field, Object value, String exception) {
        this.tin = tin;
        this.name = name;
        this.recordType = recordType;
        this.field = field;
        this.value = value;
        this.exception = exception;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String toCsvString() {
        return tin + "," + name + "," + recordType + "," + field + "," + value + "," + exception;
    }

    @Override
    public String toString() {
        return name + " (" + tin + ") [" + recordType + "," + field + "=" + value + "] Exception : " + exception;
    }
}
