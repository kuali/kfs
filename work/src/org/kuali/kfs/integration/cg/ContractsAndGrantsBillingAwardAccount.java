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

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Integration interface for AwardAccount
 */
public interface ContractsAndGrantsBillingAwardAccount extends ContractsAndGrantsAccountAwardInformation{



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
     * Gets the finalBilledIndicator attribute.
     *
     * @return Returns the finalBilledIndicator.
     */
    public boolean isFinalBilledIndicator();


    /**
     * Gets the amountToDraw attribute.
     *
     * @return Returns the amountToDraw.
     */
    public KualiDecimal getAmountToDraw();

    /**
     * Gets the letterOfCreditReviewIndicator attribute.
     *
     * @return Returns the letterOfCreditReviewIndicator.
     */
    public boolean isLetterOfCreditReviewIndicator();

    public String getInvoiceDocumentStatus();
}
