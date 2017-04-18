package edu.arizona.kfs.module.tax.businessobject;

import java.util.ArrayList;
import java.util.List;

import edu.arizona.kfs.module.tax.util.RecordUtil;

/**
 * The structure of the EndRecord is as follows:
 * 1: recordType (Character)
 * 2-9: payeeRecords (right justified Integer)
 * 50-57: payeeRecords (right justified Integer)
 * 500-507: recordNumberSequence (right justified Integer)
 */
public class EndRecord implements Record {
    private static Character recordType = new Character('F');
    private Integer payerRecords;
    private Integer payeeRecords;
    private Integer recordNumberSequence;

    public Integer getPayerRecords() {
        return payerRecords;
    }

    public void setPayerRecords(Integer payerRecords) {
        this.payerRecords = payerRecords;
    }

    public Integer getPayeeRecords() {
        return payeeRecords;
    }

    public void setPayeeRecords(Integer payeeRecords) {
        this.payeeRecords = payeeRecords;
    }

    public Integer getRecordNumberSequence() {
        return recordNumberSequence;
    }

    public void setRecordNumberSequence(Integer recordNumberSequence) {
        this.recordNumberSequence = recordNumberSequence;
    }

    @Override
    public List<ElectronicFileException> validateRecord() {
        List<ElectronicFileException> errors = new ArrayList<ElectronicFileException>();

        if (payerRecords == null) {
            errors.add(new ElectronicFileException(ElectronicFileException.END_RECORD, "Payer Records", payerRecords, "Payer records is a required integer value."));
        }
        if (payeeRecords == null) {
            errors.add(new ElectronicFileException(ElectronicFileException.END_RECORD, "Payee Records", payeeRecords, "Payee records is a required integer value."));
        }
        if (recordNumberSequence == null) {
            errors.add(new ElectronicFileException(ElectronicFileException.END_RECORD, "Record Number Sequence", recordNumberSequence, "Record number sequence is a required integer value."));
        }

        if (getRecordString().length() != 750) {
            errors.add(new ElectronicFileException(ElectronicFileException.END_RECORD, "Record Length", new Integer(getRecordString().length()), "Record length must 750 characters."));
        }

        return errors;
    }

    @Override
    public String getRecordString() {
        StringBuffer buf = new StringBuffer();

        buf.append(recordType);
        buf.append(RecordUtil.rightJustifyInteger(payerRecords, 8));
        buf.append(RecordUtil.getZeroes(21));
        buf.append(RecordUtil.getBlanks(19));
        buf.append(RecordUtil.rightJustifyInteger(payeeRecords, 8));
        buf.append(RecordUtil.getBlanks(442));
        buf.append(RecordUtil.rightJustifyInteger(recordNumberSequence, 8));
        buf.append(RecordUtil.getBlanks(241));
        buf.append("\r\n");

        return buf.toString();
    }
}
