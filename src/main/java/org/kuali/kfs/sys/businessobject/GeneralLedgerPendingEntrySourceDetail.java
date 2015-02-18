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

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

/**
 * A collection of methods, specifying data that would be necessary to create a GeneralLedgerPendingEntry
 */
public interface GeneralLedgerPendingEntrySourceDetail extends PersistableBusinessObject {
    /**
     * @return Returns the chartOfAccountsCode.
     */
    public abstract String getChartOfAccountsCode();
    /**
     * @return Returns the accountNumber.
     */
    public abstract String getAccountNumber();
    /**
     * @return Returns the account.
     */
    public abstract Account getAccount();
    /**
     * @return Returns the documentNumber.
     */
    public abstract String getDocumentNumber();
    /**
     * @return Returns the financialObjectCode.
     */
    public abstract String getFinancialObjectCode();
    /**
     * @return Returns the objectCode.
     */
    public abstract ObjectCode getObjectCode();
    /**
     * @return Returns the organizationReferenceId.
     */
    public abstract String getOrganizationReferenceId();
    /**
     * @return Returns the projectCode.
     */
    public abstract String getProjectCode();
    /**
     * @return Returns the referenceNumber.
     */
    public abstract String getReferenceNumber();
    /**
     * @return Returns the referenceTypeCode.
     */
    public abstract String getReferenceTypeCode();
    /**
     * @return Returns the referenceOriginCode.
     */
    public abstract String getReferenceOriginCode();
    /**
     * @return Returns the subAccountNumber.
     */
    public abstract String getSubAccountNumber();
    /**
     * @return Returns the financialSubObjectCode.
     */
    public abstract String getFinancialSubObjectCode();
    /**
     * @return Returns the financialDocumentLineDescription.
     */
    public abstract String getFinancialDocumentLineDescription();
    /**
     * @return Returns the amount.
     */
    public abstract KualiDecimal getAmount();
    /**
     * @return Returns the postingYear.
     */
    public abstract Integer getPostingYear();
    /**
     * @return Returns the balanceTypeCode.
     */
    public abstract String getBalanceTypeCode();
}
