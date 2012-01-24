/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.cg.businessobject.authorization;

import java.util.Set;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.rice.kns.inquiry.InquiryAuthorizerBase;

public class ProposalInquiryAuthorizer extends InquiryAuthorizerBase{

    /**
     * @see org.kuali.rice.kns.inquiry.InquiryAuthorizerBase#getSecurePotentiallyHiddenSectionIds()
     */
    @Override
    public Set<String> getSecurePotentiallyHiddenSectionIds() {
        Set<String> hiddenSectionIds = super.getSecurePotentiallyHiddenSectionIds();       
        hiddenSectionIds.add(CGConstants.SectionId.PROPOSAL_RESEARCH_RISKS);

        return hiddenSectionIds;
    }   
}
