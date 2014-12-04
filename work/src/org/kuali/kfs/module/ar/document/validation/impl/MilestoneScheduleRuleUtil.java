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
