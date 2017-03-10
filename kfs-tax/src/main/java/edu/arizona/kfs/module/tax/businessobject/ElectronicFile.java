package edu.arizona.kfs.module.tax.businessobject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ElectronicFile {

    private TransmitterRecord transmitterRecord = new TransmitterRecord();

    private List<RecordGroup> recordGroups = new ArrayList<RecordGroup>();

    private EndRecord endRecord = new EndRecord();

    public RecordGroup addRecordGroup() {
        RecordGroup rg = new RecordGroup();
        recordGroups.add(rg);

        int cnt = 0;

        for (RecordGroup g : recordGroups) {
            cnt += g.getPayeeRecordsSize() + 2;
        }

        rg.getPayerRecord().setRecordNumberSequence(cnt + 1);

        return rg;
    }

    public TransmitterRecord getTransmitterRecord() {
        return transmitterRecord;
    }

    public List<ElectronicFileException> validateFile() {
        List<ElectronicFileException> errors = new ArrayList<ElectronicFileException>();

        errors.addAll(transmitterRecord.validateRecord());

        for (RecordGroup rg : recordGroups) {
            errors.addAll(rg.validateRecordGroup());
        }

        errors.addAll(endRecord.validateRecord());

        return errors;
    }

    public void updateTotals() {
        int payeeCnt = 0;
        int recordSize = 0;

        for (RecordGroup rg : recordGroups) {
            payeeCnt += rg.getPayeeRecordsSize();
            recordSize += rg.getRecordGroupSize();
        }

        transmitterRecord.setNumberPayees(new Integer(payeeCnt));

        endRecord.setPayerRecords(new Integer(recordGroups.size()));
        endRecord.setPayeeRecords(new Integer(payeeCnt));

        int recordSequnceTotal = recordSize + 2;

        endRecord.setRecordNumberSequence(new Integer(recordSequnceTotal));
    }

    public String getFileString() {
        StringBuffer sb = new StringBuffer();
        sb.append(transmitterRecord.getRecordString());

        for (RecordGroup rg : recordGroups) {
            sb.append(rg.getGroupString());
        }

        sb.append(endRecord.getRecordString());

        return sb.toString();
    }

    public void writeElectronicFile(String path) throws Exception {
        File file = new File(path);
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(getFileString().getBytes());
        fop.close();
    }
}
