/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.service;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Services to support the ContractsGrantsLetterOfCreditReviewDocument
 */
public interface ContractsGrantsLetterOfCreditReviewDocumentService {

    /**
     * This method retrieves the amount to draw for the award accounts
     *
     * @param awardAccounts
     * @param award
     */
    public void setAwardAccountToDraw(List<ContractsAndGrantsBillingAwardAccount> awardAccounts, ContractsAndGrantsBillingAward award);

    /**
     * This method retrieves the amount available to draw for the award accounts
     *
     * @param awardTotalAmount
     * @param awardAccounts
     * @return
     */
    public KualiDecimal getAmountAvailableToDraw(KualiDecimal awardTotalAmount, List<ContractsAndGrantsBillingAwardAccount> awardAccounts);

    /**
     * This method calculates the claim on cash balance for every award account.
     *
     * @param awardAccount
     * @param awardBeginningDate
     * @return
     */
    public KualiDecimal getClaimOnCashforAwardAccount(ContractsAndGrantsBillingAwardAccount awardAccount, java.sql.Date awardBeginningDate);

    /**
     * To create a generic method to retrieve all active awards based on the criteria passed.
     *
     * @param criteria
     * @return
     */
    public List<ContractsAndGrantsBillingAward> getActiveAwardsByCriteria(Map<String, Object> criteria);
}
