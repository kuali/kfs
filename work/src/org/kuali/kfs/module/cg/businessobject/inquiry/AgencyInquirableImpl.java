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
package org.kuali.kfs.module.cg.businessobject.inquiry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
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
 * Used for wiring up {@link Agency} for inquiries.
 */
public class AgencyInquirableImpl extends KfsInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AgencyInquirableImpl.class);

    /**
     * Don't process Contracts & Grants Billing (CGB) related sections if CGB is disabled.
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

        InquiryRestrictions inquiryRestrictions = KNSServiceLocator.getBusinessObjectAuthorizationService()
                .getInquiryRestrictions(businessObject, GlobalVariables.getUserSession().getPerson());

        Collection<InquirySectionDefinition> inquirySections = getBusinessObjectDictionaryService().getInquirySections(
                getBusinessObjectClass());
        Collection<?> sectionIdsToIgnore = getSectionIdsToIgnore();

        for (Iterator<InquirySectionDefinition> iter = inquirySections.iterator(); iter.hasNext();) {
            InquirySectionDefinition inquirySection = iter.next();
            String sectionId = inquirySection.getId();
            if (!inquiryRestrictions.isHiddenSectionId(sectionId) && !sectionIdsToIgnore.contains(sectionId)) {
                Section section = SectionBridge.toSection(this, inquirySection, businessObject, inquiryRestrictions);
                sections.add(section);
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
        if (!SpringContext.getBean(AccountsReceivableModuleBillingService.class).isContractsGrantsBillingEnhancementActive()) {
            return SpringContext.getBean(ContractsAndGrantsBillingService.class).getAgencyContractsGrantsBillingSectionIds();
        } else {
            return CollectionUtils.EMPTY_COLLECTION;
        }
    }

}

