/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.businessobject;

import java.io.Serializable;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
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
