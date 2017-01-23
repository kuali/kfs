package edu.arizona.kfs.module.tax.businessobject;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.type.KualiDecimal;

import edu.arizona.kfs.module.tax.util.RecordUtil;

/**
 * The structure of the EndPayerRecord is as follows:
 * 1: recordType (Character)
 * 2-9: payeeRecords (right justified Integer)
 * 16-267: totalControl (Array of formatted KualiDecimals)
 * 500-507: recordNumberSequence (right justified Integer)
 */
public class EndPayerRecord implements Record {
    private static Character recordType = new Character('C');
    private Integer payeeRecords;
    private KualiDecimal[] totalControl = new KualiDecimal[14];
    private Integer recordNumberSequence;

    public Integer getPayeeRecords() {
        return payeeRecords;
    }

    public void setPayeeRecords(Integer payeeRecords) {
        this.payeeRecords = payeeRecords;
    }

    public void setTotalControl(int indx, KualiDecimal amount) {
        totalControl[indx] = amount;
    }

    public Integer getRecordNumberSequence() {
        return recordNumberSequence;
    }

    public void setRecordNumberSequence(Integer recordNumberSequence) {
        this.recordNumberSequence = recordNumberSequence;
    }

    @Override
    public String getRecordString() {
        StringBuffer buf = new StringBuffer();

        buf.append(recordType);
        buf.append(RecordUtil.rightJustifyInteger(payeeRecords, 8));
        buf.append(RecordUtil.getBlanks(6));

        for (int i = 0; i < totalControl.length; ++i) {
            buf.append(RecordUtil.formatKualiDecimal(totalControl[i], 18));
        }

        buf.append(RecordUtil.getBlanks(232));
        buf.append(RecordUtil.rightJustifyInteger(recordNumberSequence, 8));
        buf.append(RecordUtil.getBlanks(241));
        buf.append("\r\n");

        return buf.toString();
    }

    @Override
    public List<ElectronicFileException> validateRecord() {
        List<ElectronicFileException> errors = new ArrayList<ElectronicFileException>();

        for (int i = 0; i < totalControl.length; ++i) {
            if (totalControl[i] == null) {
                errors.add(new ElectronicFileException(ElectronicFileException.END_PAYER, "Total Control " + (i + 1), totalControl[i], "Total Control " + (i + 1) + " is a required decimal value."));
            }
        }

        if (payeeRecords == null) {
            errors.add(new ElectronicFileException(ElectronicFileException.END_PAYER, "Payee Records", payeeRecords, "Payee Records is a required integer value."));
        }

        if (getRecordString().length() != 750) {
            errors.add(new ElectronicFileException(ElectronicFileException.END_PAYER, "Record Length", new Integer(getRecordString().length()), "Record length must 750 characters."));
        }

        return errors;
    }

}
