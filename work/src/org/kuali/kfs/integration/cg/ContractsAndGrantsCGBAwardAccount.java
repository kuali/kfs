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
package org.kuali.kfs.integration.cg;

import java.sql.Date;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Integration interface for AwardAccount
 */
public interface ContractsAndGrantsCGBAwardAccount extends MutableInactivatable, ExternalizableBusinessObject {

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber
     */
    public Long getProposalNumber();

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode();

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     */
    public String getAccountNumber();

    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId
     */
    public String getPrincipalId();

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account
     */
    public Account getAccount();

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts();

    /**
     * This method returns the Award object associated with this AwardAccount.
     * 
     * @return The Award object associated with this AwardAccount.
     */

    // KFSMI-861 : Removing this method as it's not being referenced.
    // public ContractsAndGrantsAward getAward();

    /**
     * Gets the name of the project director for the associated award.
     * 
     * @return the name of the project director
     */
    public String getProjectDirectorName();


    /**
     * Gets the currentLastBilledDate attribute.
     * 
     * @return Returns the currentLastBilledDate.
     */
    public Date getCurrentLastBilledDate();

    /**
     * Gets the previousLastBilledDate attribute.
     * 
     * @return Returns the previousLastBilledDate.
     */
    public Date getPreviousLastBilledDate();

    /**
     * Gets the finalBilled attribute.
     * 
     * @return Returns the finalBilled.
     */
    public boolean isFinalBilled();


    /**
     * Gets the amountToDraw attribute.
     * 
     * @return Returns the amountToDraw.
     */
    public KualiDecimal getAmountToDraw();

    /**
     * Gets the locReviewIndicator attribute.
     * 
     * @return Returns the locReviewIndicator.
     */
    public boolean isLocReviewIndicator();

    public String getInvoiceDocumentStatus();
}
