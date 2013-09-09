/*
 * Copyright 2011 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.integration.ar;

import java.sql.Date;

import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Integration interface for Milestone
 */
public interface AccountsReceivableMilestone extends ExternalizableBusinessObject {


    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber.
     */
    public Long getProposalNumber();

    /**
     * Gets the milestoneNumber attribute.
     *
     * @return Returns the milestoneNumber.
     */
    public Long getMilestoneNumber();

    /**
     * Gets the milestoneDescription attribute.
     *
     * @return Returns the milestoneDescription.
     */
    public String getMilestoneDescription();

    /**
     * Gets the milestoneAmount attribute.
     *
     * @return Returns the milestoneAmount.
     */
    public KualiDecimal getMilestoneAmount();

    /**
     * Gets the isItBilled attribute.
     *
     * @return Returns the isItBilled.
     */
    public String getIsItBilled();

    /**
     * Gets the milestoneActualCompletionDate attribute.
     *
     * @return Returns the milestoneActualCompletionDate.
     */
    public Date getMilestoneActualCompletionDate();

    /**
     * Gets the milestoneExpectedCompletionDate attribute.
     *
     * @return Returns the milestoneExpectedCompletionDate.
     */
    public Date getMilestoneExpectedCompletionDate();

    /**
     * Gets the milestoneIdentifier attribute.
     *
     * @return Returns the milestoneIdentifier.
     */
    public Long getMilestoneIdentifier();

    /**
     * Gets the award attribute.
     *
     * @return Returns the award.
     */
    public ContractsAndGrantsCGBAward getAward();


}
