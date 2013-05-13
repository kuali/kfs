/*
 * Copyright 2005-2011 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;


/**
 * @author tdurkin
 */
public class PaymentRequestInitializationValidationErrors implements Serializable {

//    public List errorMessages = new ArrayList();
    private List expiredAccounts = new ArrayList();
    private List closedAccounts = new ArrayList();
    private Integer purchaseOrderNumberToUse;
    public boolean canAutoClosePO = false;

    private List<PREQCreationFailure> PREQCreationFailures = new ArrayList<PREQCreationFailure>();

    public void addPREQCreationFailure(String rejectReasonCode,
                                       String extraDescription){

        PREQCreationFailure rejectReason = new PREQCreationFailure();
        rejectReason.setRejectReasonCode(rejectReasonCode);
        rejectReason.setExtraDescription(extraDescription);
        PREQCreationFailures.add(rejectReason);
    }

    public PREQCreationFailure[] getPREQCreationFailures(){
        if (PREQCreationFailures.size() > 0){
            PREQCreationFailure[] rejectReasons = new PREQCreationFailure[this.PREQCreationFailures.size()];
            return this.PREQCreationFailures.toArray(rejectReasons);
        }else {
            return null;
        }
    }

    public void addExpiredAccount(Account expiredAccount) {
        expiredAccounts.add(new AccountContinuation(expiredAccount));
    }

    public void addClosedAccount(Account closedAccount) {
        closedAccounts.add(new AccountContinuation(closedAccount));
    }

    public void addExpiredAccount(AccountContinuation expiredAccount) {
        expiredAccounts.add(expiredAccount);
    }

    public void addClosedAccount(AccountContinuation closedAccount) {
        closedAccounts.add(closedAccount);
    }

    public boolean isClosedAccountsValid() {
        return this.isListMembersValid(closedAccounts);
    }

    public boolean isExpiredAccountsValid() {
        return this.isListMembersValid(expiredAccounts);
    }

    public boolean isListMembersValid(List l) {
        for (Iterator i = l.iterator(); i.hasNext();) {
            AccountContinuation acctCont = (AccountContinuation) i.next();
            if (acctCont.getReplacementAccountValid() == null || acctCont.getReplacementAccountValid().equals(Boolean.FALSE)) {
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

    public boolean isCanAutoClosePO() {
        return canAutoClosePO;
    }

    public void setCanAutoClosePO(boolean canAutoClosePO) {
        this.canAutoClosePO = canAutoClosePO;
    }

    public class AccountContinuation implements Serializable {

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

    public final class PREQCreationFailure {

        private String rejectReasonCode;
        private String extraDescription;

        public String getExtraDescription() {
            return extraDescription;
        }
        public void setExtraDescription(String extraDescription) {
            this.extraDescription = extraDescription;
        }
        public String getRejectReasonCode() {
            return rejectReasonCode;
        }
        public void setRejectReasonCode(String rejectReasonCode) {
            this.rejectReasonCode = rejectReasonCode;
        }

    }
}
