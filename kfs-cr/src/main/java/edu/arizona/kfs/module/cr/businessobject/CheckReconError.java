package edu.arizona.kfs.module.cr.businessobject;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * CheckReconError
 * 
 * Represents an error when importing the list of Checks from PDP and the Posting of Bank Transactions mod.
 */
public class CheckReconError extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = -6546459558179867362L;

    private String bankAcctNum;
    private String checkNum;
    private String checkDate;
    private String amount;
    private String message;

    public String getBankAcctNum() {
        return bankAcctNum;
    }

    public void setBankAcctNum(String bankAcctNum) {
        this.bankAcctNum = bankAcctNum;
    }

    public String getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(String checkNum) {
        this.checkNum = checkNum;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
