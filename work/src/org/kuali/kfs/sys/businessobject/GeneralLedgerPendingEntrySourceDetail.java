/*
 * Copyright 2008 The Kuali Foundation
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
