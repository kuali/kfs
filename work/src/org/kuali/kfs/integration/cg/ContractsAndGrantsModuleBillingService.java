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
package org.kuali.kfs.integration.cg;

import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Module service specific to those methods needed to support Contracts & Grants Billing.
 * If Contracts & Grants Billing is not used at your institution, there is no need to implement this service; simply continue to use
 * the provided NoOp implementation of the service.
 */
public interface ContractsAndGrantsModuleBillingService {

    /**
     * This method would return list of business object - in this case Awards for CG Invoice functionality in AR.
     *
     * @param fieldValues
     * @param unbounded
     * @return
     */
    public List<? extends ContractsAndGrantsAward> lookupAwards(Map<String, String> fieldValues, boolean unbounded);

    /**
     * Compares the Proposal Number passed in with that in the Award object.  If they are the same, it returns the
     * original object.  Otherwise, it retrieves the Award based on the proposalNumber and returns it.
     *
     * @param proposalNumber
     * @param currentAward
     * @return
     */
    public ContractsAndGrantsBillingAward updateAwardIfNecessary(Long proposalNumber, ContractsAndGrantsBillingAward currentAward);

    /**
     * This method sets last Billed Date to award Account.
     *
     * @param criteria
     * @param invoiceReversal
     * @param lastBilledDate
     */
    public void setLastBilledDateToAwardAccount(Map<String, Object> criteria, boolean invoiceReversal, java.sql.Date lastBilledDate);

    /**
     * This method sets last billed Date to Award
     *
     * @param proposalNumber
     * @param lastBilledDate
     */
    public void setLastBilledDateToAward(Long proposalNumber, java.sql.Date lastBilledDate);

    /**
     * This method sets value of LOC Creation Type to Award
     *
     * @param proposalNumber
     * @param locCreationType
     */
    public void setLOCCreationTypeToAward(Long proposalNumber, String locCreationType);

    /**
     * This method sets amount to draw to award Account.
     *
     * @param criteria
     * @param amountToDraw
     */
    public void setAmountToDrawToAwardAccount(Map<String, Object> criteria, KualiDecimal amountToDraw);

    /**
     * This method sets loc review indicator to award Account.
     *
     * @param criteria
     * @param locReviewIndicator
     */
    public void setLOCReviewIndicatorToAwardAccount(Map<String, Object> criteria, boolean locReviewIndicator);

    /**
     * This method sets final billed to award Account.
     *
     * @param criteria
     * @param finalBilled
     */
    public void setFinalBilledToAwardAccount(Map<String, Object> criteria, boolean finalBilled);

    /**
     * This method sets final billed and last billed date to Award Account.
     *
     * @param mapKey
     * @param finalBilled
     * @param invoiceReversal
     * @param lastBilledDate
     */
    public void setFinalBilledAndLastBilledDateToAwardAccount(Map<String, Object> mapKey, boolean finalBilled, boolean invoiceReversal, java.sql.Date lastBilledDate);

}
