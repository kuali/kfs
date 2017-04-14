package edu.arizona.kfs.module.cr.businessobject;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import edu.arizona.kfs.module.cr.CrConstants;

/**
 * Check Reconciliation Report Line
 */
public class CheckReconciliationReportLine extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 4880645454407318068L;

    private String checkNumber;
    private String payeeId;
    private String payeeTypeCode;
    private String payeeName;
    private String bankAccountNumber;
    private String checkDate;
    private String checkMonth;
    private String status;
    private Double amount;

    public CheckReconciliationReportLine() {
    }

    public CheckReconciliationReportLine(CheckReconciliation cr) {
        checkNumber = cr.getCheckNumber().toString();
        payeeId = cr.getPayeeId();
        payeeTypeCode = cr.getPayeeTypeCode();
        payeeName = cr.getPayeeName();
        bankAccountNumber = cr.getBankAccountNumber();
        checkDate = CrConstants.MM_DD_YYYY.format(cr.getCheckDate());
        checkMonth = CrConstants.MMM_YYYY.format(cr.getCheckDate());
        status = cr.getStatus();
        amount = cr.getAmount().doubleValue();
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public String getPayeeTypeCode() {
        return payeeTypeCode;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public String getCheckMonth() {
        return checkMonth;
    }

    public String getStatus() {
        return status;
    }

    public Double getAmount() {
        return amount;
    }

}
