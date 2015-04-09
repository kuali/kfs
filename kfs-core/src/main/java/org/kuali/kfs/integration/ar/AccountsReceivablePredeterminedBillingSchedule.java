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
package org.kuali.kfs.integration.ar;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

public interface AccountsReceivablePredeterminedBillingSchedule extends ExternalizableBusinessObject {

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber.
     */
    public Long getProposalNumber();

    /**
     * Gets the predeterminedBillingScheduleInquiryTitle attribute.
     *
     * @return Returns the predeterminedBillingScheduleInquiryTitle.
     */
    public String getPredeterminedBillingScheduleInquiryTitle();

    /**
     * Gets the award attribute.
     *
     * @return Returns the award.
     */
    public ContractsAndGrantsBillingAward getAward();

    /**
     * Gets the totalAmountScheduled attribute.
     *
     * @return Returns the totalAmountScheduled.
     */
    public KualiDecimal getTotalAmountScheduled();

    /**
     * Gets the totalAmountRemaining attribute.
     *
     * @return Returns the totalAmountRemaining.
     */
    public KualiDecimal getTotalAmountRemaining();
}
