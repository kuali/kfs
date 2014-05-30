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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.service.ContractsAndGrantsBillingService;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.InquirySectionDefinition;
import org.kuali.rice.kns.inquiry.InquiryRestrictions;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.kns.web.ui.SectionBridge;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Used for wiring up {@link Award} for inquiries.
 */
public class AwardInquirableImpl extends KfsInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AwardInquirableImpl.class);

    /**
     * Only show the Schedule link if CGB is enabled and for the appropriate Billing Frequency
     * (Milestone Schedule for Milestone billing, Predetermined Billing Schedule for PDBS billing, or none).
     *
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getSections(org.kuali.rice.krad.bo.BusinessObject)
     *
     * KRAD Conversion: Inquirable performs conditional display/hiding of the sections on the inquiry
     * But all field/section definitions are in data dictionary for bo Asset.
     */
    @Override
    public List<Section> getSections(BusinessObject businessObject) {
        List<Section> sections = new ArrayList<Section>();
        if (getBusinessObjectClass() == null) {
            LOG.error("Business object class not set in inquirable.");
            throw new RuntimeException("Business object class not set in inquirable.");
        }

        Award award = (Award) businessObject;

        InquiryRestrictions inquiryRestrictions = KNSServiceLocator.getBusinessObjectAuthorizationService()
                .getInquiryRestrictions(businessObject, GlobalVariables.getUserSession().getPerson());

        Collection<InquirySectionDefinition> inquirySections = getBusinessObjectDictionaryService().getInquirySections(
                getBusinessObjectClass());
        for (Iterator<InquirySectionDefinition> iter = inquirySections.iterator(); iter.hasNext();) {
            InquirySectionDefinition inquirySection = iter.next();
            String sectionId = inquirySection.getId();
            if (!inquiryRestrictions.isHiddenSectionId(sectionId) && !getSectionIdsToIgnore().contains(sectionId)) {
                if (StringUtils.equals(sectionId, CGConstants.SectionId.AWARD_PREDETERMINED_BILLING_SCHEDULE_SECTION_ID)) {
                    if (StringUtils.equals(award.getPreferredBillingFrequency(), CGPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) &&
                        SpringContext.getBean(AccountsReceivableModuleService.class).hasPredeterminedBillingSchedule(award.getProposalNumber())) {
                            Section section = SectionBridge.toSection(this, inquirySection, businessObject, inquiryRestrictions);
                            sections.add(section);
                    }
                } else if (StringUtils.equals(sectionId, CGConstants.SectionId.AWARD_MILESTONE_SCHEDULE_SECTION_ID)) {
                    if (StringUtils.equals(award.getPreferredBillingFrequency(), CGPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE) &&
                        SpringContext.getBean(AccountsReceivableModuleService.class).hasMilestoneSchedule(award.getProposalNumber())) {
                            Section section = SectionBridge.toSection(this, inquirySection, businessObject, inquiryRestrictions);
                            sections.add(section);
                    }
                } else {
                    Section section = SectionBridge.toSection(this, inquirySection, businessObject, inquiryRestrictions);
                    sections.add(section);
                }
            }
        }

        return sections;
    }

    /**
     * If the Contracts & Grants Billing (CGB) enhancement is disabled, we don't want to
     * process sections only related to CGB.
     *
     * @return Collection of section ids to ignore
     */
    protected Collection<?> getSectionIdsToIgnore() {
        if (!SpringContext.getBean(AccountsReceivableModuleService.class).isContractsGrantsBillingEnhancementActive()) {
            return SpringContext.getBean(ContractsAndGrantsBillingService.class).getAwardContractsGrantsBillingSectionIds();
        } else {
            return CollectionUtils.EMPTY_COLLECTION;
        }
    }

}

