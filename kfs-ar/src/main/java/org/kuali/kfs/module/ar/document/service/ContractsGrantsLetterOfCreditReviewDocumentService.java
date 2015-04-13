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
package org.kuali.kfs.module.ar.document.service;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Services to support the ContractsGrantsLetterOfCreditReviewDocument
 */
public interface ContractsGrantsLetterOfCreditReviewDocumentService {

    /**
     * Calculates the amount to draw for the award accounts, placing the amounts in a Map keyed by chart of accounts code-account number
     * @param award the award to find amounts to draw for
     * @param awardAccounts the award accounts to find amounts to draw for
     */
    public Map<String, KualiDecimal> calculateAwardAccountAmountsToDraw(ContractsAndGrantsBillingAward award, List<ContractsAndGrantsBillingAwardAccount> awardAccounts);

    /**
     * Generates a key, which will be used in the map returned by calculateAwardAccountAmountsToDraw, for the given award account
     * @param awardAccount an award account to generate a key for
     * @return the key
     */
    public String getAwardAccountKey(ContractsAndGrantsBillingAwardAccount awardAccount);

    /**
     * This method retrieves the amount available to draw for the award accounts
     *
     * @param awardTotalAmount
     * @param awardAccounts
     * @return
     */
    public KualiDecimal getAmountAvailableToDraw(KualiDecimal awardTotalAmount, List<ContractsAndGrantsBillingAwardAccount> awardAccounts);

    /**
     * To create a generic method to retrieve all active awards based on the criteria passed.
     *
     * @param criteria
     * @return
     */
    public List<ContractsAndGrantsBillingAward> getActiveAwardsByCriteria(Map<String, Object> criteria);

    /**
     * Generates contracts grants invoices for the given letter of credit review document
     * @param locReviewDoc the contracts grants letter of credit review document to generate contracts grants invoices for
     */
    public void generateContractsGrantsInvoiceDocuments(ContractsGrantsLetterOfCreditReviewDocument locReviewDoc);
}
