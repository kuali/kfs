/*
 * Created on Apr 14, 2005
 *
 */
package org.kuali.kfs.module.purap.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;


/**
 * @author tdurkin
 *
 */
public class PaymentRequestInitializationValidationErrors implements Serializable {

  public List errorMessages = new ArrayList();
  public List expiredAccounts = new ArrayList();
  public List closedAccounts = new ArrayList();
  private Integer purchaseOrderNumberToUse;
  public boolean canAutoClosePO = false;

  public void addExpiredAccount(Account expiredAccount) {
    expiredAccounts.add(new AccountContinuation(expiredAccount));
  }
  public void addClosedAccount(Account closedAccount) {
    closedAccounts.add(new AccountContinuation(closedAccount));
  }
  
  public boolean isClosedAccountsValid() {
    return this.isListMembersValid(closedAccounts);
  }
  
  public boolean isExpiredAccountsValid() {
    return this.isListMembersValid(expiredAccounts);
  }

  public boolean isListMembersValid(List l) {
    for (Iterator i = l.iterator(); i.hasNext(); ) {
      AccountContinuation acctCont = (AccountContinuation)i.next();
      if (acctCont.getReplacementAccountValid() == null || 
          acctCont.getReplacementAccountValid().equals(Boolean.FALSE)) {
        return false;
      }
    }
    return true;
  }
  
  public Integer getPurchaseOrderNumberToUse() {
    return purchaseOrderNumberToUse;
  }
  public void setPurchaseOrderNumberToUse(Integer purchaseOrderNumberToUse) {
    this.purchaseOrderNumberToUse = purchaseOrderNumberToUse;
  }
  /**
   * @return Returns the canAutoClosePO.
   */
  public boolean isCanAutoClosePO() {
    return canAutoClosePO;
  }
  /**
   * @param canAutoClosePO The canAutoClosePO to set.
   */
  public void setCanAutoClosePO(boolean canAutoClosePO) {
    this.canAutoClosePO = canAutoClosePO;
  }
  public class AccountContinuation implements Serializable{
    private String accountFinancialChartOfAccountsCode;
    private String accountAccountNumber;
    private String replacementFinancialChartOfAccountsCode;
    private String replacementAccountNumber;
    private Boolean replacementAccountValid;
    
    public AccountContinuation(Account account) {
      this.accountFinancialChartOfAccountsCode = account.getChartOfAccountsCode();
      this.accountAccountNumber = account.getAccountNumber();
      this.replacementFinancialChartOfAccountsCode = account.getContinuationFinChrtOfAcctCd();
      this.replacementAccountNumber = account.getContinuationAccountNumber();
    }
    
    public void setAccountFinancialChartOfAccountsCode(String accountFinancialChartOfAccountsCode) {
      this.accountFinancialChartOfAccountsCode = accountFinancialChartOfAccountsCode;
    }
    public String getAccountFinancialChartOfAccountsCode() {
      return accountFinancialChartOfAccountsCode;
    }
    public void setAccountAccountNumber(String accountAccountNumber) {
      this.accountAccountNumber = accountAccountNumber;
    }
    public String getAccountAccountNumber() {
      return accountAccountNumber;
    }
    public void setReplacementFinancialChartOfAccountsCode(String continuationFinancialChartOfAccountsCode) {
      this.replacementFinancialChartOfAccountsCode = continuationFinancialChartOfAccountsCode;
    }
    public String getReplacementFinancialChartOfAccountsCode() {
      return replacementFinancialChartOfAccountsCode;
    }
    public void setReplacementAccountNumber(String continuationAccountNumber) {
      this.replacementAccountNumber = continuationAccountNumber;
    }
    public String getReplacementAccountNumber() {
      return replacementAccountNumber;
    }
    public Boolean getReplacementAccountValid() {
      return replacementAccountValid;
    }
    public void setReplacementAccountValid(Boolean replacementAccountValid) {
      this.replacementAccountValid = replacementAccountValid;
    }
  }
}