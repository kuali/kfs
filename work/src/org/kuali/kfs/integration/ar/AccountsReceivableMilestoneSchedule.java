/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.integration.ar;

import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.integration.ar.businessobject.Milestone;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

public interface AccountsReceivableMilestoneSchedule extends ExternalizableBusinessObject {

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber.
     */
    public Long getProposalNumber();

    /**
     * Gets the milestoneScheduleInquiryTitle attribute.
     *
     * @return Returns the milestoneScheduleInquiryTitle.
     */
    public String getMilestoneScheduleInquiryTitle();

    /**
     * Gets the award attribute.
     *
     * @return Returns the award.
     */
    public ContractsAndGrantsCGBAward getAward();

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
