/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.cg.businessobject.inquiry;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.service.ContractsGrantsBillingService;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Used for wiring up {@link Award} for inquiries.
 */
public class AwardInquirableImpl extends KfsInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AwardInquirableImpl.class);

    /**
     * Only show the Schedule link for the appropriate Billing Frequency (Milestone Schedule for Milestone billing,
     * Predetermined Billing Schedule for PDBS billing, or none.
     *
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getSections(org.kuali.rice.krad.bo.BusinessObject)
     *
     * KRAD Conversion: Inquirable performs conditional display/hiding of the sections on the inquiry
     * But all field/section definitions are in data dictionary for bo Asset.
     */
    @Override
    public List<Section> getSections(BusinessObject businessObject) {
        List<Section> sectionsToReturn = new ArrayList<Section>();
        List<Section> sections = super.getSections(businessObject);

        Award award = (Award) businessObject;

        // sectionsToReturn is hoky but it looks like that section.setHidden doesn't work on inquirable.
        if (sections != null) {
            for (Section section : sections) {
                String sectionId = section.getSectionId();
                if (StringUtils.equals(sectionId, CGPropertyConstants.BILLING_SCHEDULE_SECTION)) {
                    if (isContractsGrantsBillingEnhancementsActive() &&
                        StringUtils.equals(award.getPreferredBillingFrequency(), CGPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) &&
                        SpringContext.getBean(AccountsReceivableModuleService.class).hasPredeterminedBillingSchedule(award.getProposalNumber())) {
                            sectionsToReturn.add(section);
                    }
                } else {
                    sectionsToReturn.add(section);
                }
            }
        }

        return sectionsToReturn;
    }

    /**
     * Checks ENABLE_CG_BILLING_ENHANCEMENTS_IND parameter to determine
     * if enhancements are active.
     *
     * @return true if Contracts and Grants Billing enhancements are enabled
     */
    private boolean isContractsGrantsBillingEnhancementsActive() {
        return SpringContext.getBean(ContractsGrantsBillingService.class).isContractsGrantsBillingEnhancementsActive();
    }
}

