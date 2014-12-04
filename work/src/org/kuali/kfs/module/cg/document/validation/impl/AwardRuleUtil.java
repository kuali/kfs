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
package org.kuali.kfs.module.cg.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Rules for the Award maintenance document.
 */
public class AwardRuleUtil {
    /**
     * Determines if a proposal has already been awarded
     * 
     * @param award the award to check the proposal for
     * @return true if the award's proposal has already been awarded
     */
    public static boolean isProposalAwarded(Award award) {
        if (ObjectUtils.isNull(award)) {
            return false;
        }
        Award result = null;
        if(ObjectUtils.isNotNull(award.getProposalNumber())) {
            result = (Award) SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(Award.class, award.getProposalNumber());
        }

        // Make sure it exists and is not the same object
        return ObjectUtils.isNotNull(result) && !StringUtils.equals(award.getObjectId(), result.getObjectId());
    }


    /**
     * Per KULCG-315 - Proposals should not be designated as inactive. This functionality is not yet implemented and this rule
     * should not be applied at this time. I'm leaving this code here in case the functionality gets added down the road.
     */
    // /**
    // * determines if a proposal is inactive
    // *
    // * @param award the award to check the proposal for
    // * @return true if the award's proposal has already been set to inactive
    // */
    // public static boolean isProposalInactive(Award award) {
    // if (ObjectUtils.isNull(award)) {
    // return false;
    // }
    //
    // Long proposalNumber = award.getProposalNumber();
    // Map<String, Object> awardPrimaryKeys = new HashMap<String, Object>();
    // awardPrimaryKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
    // Award result = (Award) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Award.class, awardPrimaryKeys);
    //
    // boolean inactive = ObjectUtils.isNotNull(result) && !result.isActive();
    //
    // return inactive;
    // }
}
