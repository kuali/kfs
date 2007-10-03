/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.cg.maintenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.datadictionary.InquirySectionDefinition;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.ui.Section;
import org.kuali.core.web.ui.SectionBridge;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.inquiry.KfsInquirableImpl;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.cg.CGConstants;

/**
 * Used for wiring up {@link Proposal} for inquiries.
 */
public class ProposalInquirable extends KfsInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProposalInquirable.class);

    private transient static String centralPreAwardWorkgroupName;
    private transient static String centralPostAwardWorkgroupName;
    private transient static String centralReviewWorkgroupName;

    /**
     * @see org.kuali.core.inquiry.KualiInquirableImpl#getSections(org.kuali.core.bo.BusinessObject)
     */
    public List<Section> getSections(BusinessObject bo) {

        initStatics();

        List<Section> sections = new ArrayList<Section>();
        if (getBusinessObjectClass() == null) {
            LOG.error("Business object class not set in inquirable.");
            throw new RuntimeException("Business object class not set in inquirable.");
        }

        Collection inquirySections = SpringContext.getBean(BusinessObjectDictionaryService.class).getInquirySections(getBusinessObjectClass());
        for (Iterator iter = inquirySections.iterator(); iter.hasNext();) {

            UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();
            InquirySectionDefinition inquirySection = (InquirySectionDefinition) iter.next();
            Section section = SectionBridge.toSection(this, inquirySection, bo);
            if (inquirySection.getTitle().equals("Research Risks")) {
                if (user.isMember(centralPreAwardWorkgroupName) || user.isMember(centralPostAwardWorkgroupName)) {
                    sections.add(section);
                }
            }
            else {
                sections.add(section);
            }
        }

        return sections;
    }

    /**
     * A non-static way to initialize the static attributes. Doing it statically would cause problems with the {@link SpringContext}.
     * So doing it non-statically helps by allowing the {@link SpringContext} time to load.
     */
    private void initStatics() {
        // get the group name that we need here
        if (centralPreAwardWorkgroupName == null) {
            centralPreAwardWorkgroupName = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.CONTRACTS_AND_GRANTS_DOCUMENT.class, CGConstants.PRE_AWARD_GROUP);
        }
        if (centralPostAwardWorkgroupName == null) {
            centralPostAwardWorkgroupName = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.CONTRACTS_AND_GRANTS_DOCUMENT.class, CGConstants.POST_AWARD_GROUP);
        }
    }


}
