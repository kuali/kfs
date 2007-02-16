/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.bo;

import java.io.Serializable;
import java.util.LinkedHashMap;

import org.kuali.core.bo.TransientBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;

/**
 * This class holds respnsibility information pertaining to an account specifically whether or not the responsibility is of type
 * Fiscal Officer, Primary Delegate of Fiscal Officer, or Secondary Delegate of Fiscal Officer, and if it is one of the delegated
 * responsibility types, then there is additional information about the responsibility such as dollar range and document type
 */
public class AccountResponsibility extends TransientBusinessObjectBase implements Serializable {

    private static final long serialVersionUID = 3101634819409319387L;
    public static final int FISCAL_OFFICER_RESPONSIBILITY = 1;
    public static final int DELEGATED_RESPONSIBILITY = 2;

    private Account account;
    private int accountResponsibilityType;
    private KualiDecimal minimumDollarAmount;
    private KualiDecimal maximumDollarAmount;
    private String documentTypeId;

    public AccountResponsibility() {
    }

    /**
     * Constructor that allows us to create and pop on same line in other files
     * 
     * @param accountResponsibilityType
     * @param minimumDollarAmount
     * @param maximumDollarAmount
     * @param documentTypeId
     */
    public AccountResponsibility(int accountResponsibilityType, KualiDecimal minimumDollarAmount, KualiDecimal maximumDollarAmount, String documentTypeId, Account account) {
        if (!isValidResponsibilityType(accountResponsibilityType)) {
            throw new IllegalArgumentException("invalid accountResponsibilityType passed in");
        }
        this.accountResponsibilityType = accountResponsibilityType;
        this.minimumDollarAmount = minimumDollarAmount;
        this.maximumDollarAmount = maximumDollarAmount;
        this.documentTypeId = documentTypeId;
        this.account = account;
    }

    private boolean isValidResponsibilityType(int type) {
        return type == FISCAL_OFFICER_RESPONSIBILITY || type == DELEGATED_RESPONSIBILITY;
    }


    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getAccountResponsibilityType() {
        return accountResponsibilityType;
    }

    public void setAccountResponsibilityType(int accountResponsibilityType) {
        this.accountResponsibilityType = accountResponsibilityType;
    }

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public KualiDecimal getMaximumDollarAmount() {
        return maximumDollarAmount;
    }

    public void setMaximumDollarAmount(KualiDecimal maximumDollarAmount) {
        this.maximumDollarAmount = maximumDollarAmount;
    }

    public KualiDecimal getMinimumDollarAmount() {
        return minimumDollarAmount;
    }

    public void setMinimumDollarAmount(KualiDecimal minimumDollarAmount) {
        this.minimumDollarAmount = minimumDollarAmount;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (account != null) {
          m.put("accountNumber", this.account.getAccountNumber());
        }
        m.put("accountResponsibilityType", this.accountResponsibilityType);
        m.put("minimumDollarAmount", this.minimumDollarAmount);
        m.put("maximumDollarAmount", this.maximumDollarAmount);
        m.put("documentTypeId", this.documentTypeId);
        return m;
    }
    
    

}