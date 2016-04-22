package edu.arizona.kfs.sys.businessobject;

import java.sql.Timestamp;

import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kim.api.identity.Person;

/**
 * This business object class was created to store information needed by batch jobs.
 *
 * @author Adam Kost kosta@email.arizona.edu
 */
public class BatchFileUploads extends TimestampedBusinessObjectBase {

    private static final long serialVersionUID = 441180109879781184L;

    private KualiInteger id;
    private String batchFileName;
    private Timestamp batchDate;
    private KualiInteger transactionCount;
    private KualiDecimal batchTotalAmount;
    private String submiterUserId;
    private Timestamp fileProcessTimestamp;
    private String batchName;

    private CustomerProfile customerProfile;
    private Person submiterUser;

    public BatchFileUploads() {
        super();
    }

    public KualiInteger getId() {
        return id;
    }

    public void setId(KualiInteger id) {
        this.id = id;
    }

    public String getBatchFileName() {
        return batchFileName;
    }

    public void setBatchFileName(String batchFileName) {
        this.batchFileName = batchFileName;
    }

    public Timestamp getBatchDate() {
        return batchDate;
    }

    public void setBatchDate(Timestamp timestamp) {
        this.batchDate = timestamp;
    }

    public KualiInteger getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(KualiInteger lineNum) {
        this.transactionCount = lineNum;
    }

    public KualiDecimal getBatchTotalAmount() {
        return batchTotalAmount;
    }

    public void setBatchTotalAmount(KualiDecimal batchTotalAmount) {
        this.batchTotalAmount = batchTotalAmount;
    }

    public String getSubmiterUserId() {
        return submiterUserId;
    }

    public void setSubmiterUserId(String submiterUserId) {
        this.submiterUserId = submiterUserId;
    }

    public Timestamp getFileProcessTimestamp() {
        return fileProcessTimestamp;
    }

    public void setFileProcessTimestamp(Timestamp fileProcessTimestamp) {
        this.fileProcessTimestamp = fileProcessTimestamp;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public CustomerProfile getCustomerProfile() {
        return customerProfile;
    }

    public void setCustomerProfile(CustomerProfile customerProfile) {
        this.customerProfile = customerProfile;
    }

    public Person getSubmiterUser() {
        return submiterUser;
    }

    public void setSubmiterUser(Person submiterUser) {
        this.submiterUser = submiterUser;
    }
}