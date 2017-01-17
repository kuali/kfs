package edu.arizona.kfs.module.tax.businessobject;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.type.KualiDecimal;

public class RecordGroup {
    private PayerRecord payerRecord = new PayerRecord();
    private List<PayeeRecord> payeeRecords = new ArrayList<PayeeRecord>();
    private EndPayerRecord endPayerRecord = new EndPayerRecord();

    public PayeeRecord addPayeeRecord() {
        PayeeRecord pr = new PayeeRecord();
        payeeRecords.add(pr);
        return pr;
    }

    public int getRecordGroupSize() {
        return payeeRecords.size() + 2;
    }

    public int getPayeeRecordsSize() {
        return payeeRecords.size();
    }

    public PayerRecord getPayerRecord() {
        return payerRecord;
    }

    public List<ElectronicFileException> validateRecordGroup() {
        List<ElectronicFileException> errors = new ArrayList<ElectronicFileException>();

        errors.addAll(payerRecord.validateRecord());

        for (PayeeRecord pr : payeeRecords) {
            errors.addAll(pr.validateRecord());
        }

        errors.addAll(endPayerRecord.validateRecord());

        return errors;
    }

    public void updateTotals() {
        int payeeCnt = 0;

        double[] totals = new double[14];

        for (PayeeRecord pr : payeeRecords) {
            pr.setRecordNumberSequence(new Integer(payerRecord.getRecordNumberSequence().intValue() + payeeCnt + 1));
            payeeCnt++;

            for (int i = 0; i < 14; ++i) {
                totals[i] = pr.getPaymentAmount("" + (1 + 1)).doubleValue();
            }
        }

        endPayerRecord.setPayeeRecords(new Integer(payeeCnt));

        for (int i = 0; i < 14; ++i) {
            endPayerRecord.setTotalControl(i, new KualiDecimal(totals[i]));
        }

        endPayerRecord.setRecordNumberSequence(new Integer(payerRecord.getRecordNumberSequence().intValue() + payeeCnt + 1));
    }

    public String getGroupString() {
        StringBuffer sb = new StringBuffer();
        sb.append(payerRecord.getRecordString());

        for (PayeeRecord pr : payeeRecords) {
            sb.append(pr.getRecordString());
        }

        sb.append(endPayerRecord.getRecordString());

        return sb.toString();
    }
}
