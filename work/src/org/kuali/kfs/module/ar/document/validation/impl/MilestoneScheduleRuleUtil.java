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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.MilestoneSchedule;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Rules for the Milestone Schedule maintenance document.
 */
public class MilestoneScheduleRuleUtil {
    /**
     * Determines if a award has milestones defined
     *
     * @param milestoneSchedule to check the award for
     * @return true if the award has milestones defined
     */
    public static boolean checkIfMilestonesExists(MilestoneSchedule milestoneSchedule) {
        if (ObjectUtils.isNull(milestoneSchedule)) {
            return false;
        }

        MilestoneSchedule result = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(MilestoneSchedule.class, milestoneSchedule.getProposalNumber());

        // Make sure it exists and is not the same object
        return ObjectUtils.isNotNull(result) && !StringUtils.equals(milestoneSchedule.getObjectId(), result.getObjectId());
    }
}