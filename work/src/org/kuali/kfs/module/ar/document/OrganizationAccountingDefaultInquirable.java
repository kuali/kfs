/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.datadictionary.InquirySectionDefinition;
import org.kuali.rice.kns.inquiry.InquiryRestrictions;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.kns.web.ui.SectionBridge;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;

public class OrganizationAccountingDefaultInquirable extends KfsInquirableImpl {
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationAccountingDefaultInquirable.class);
    
    /**
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getSections(org.kuali.rice.krad.bo.BusinessObject)
     * KRAD Conversion: Maintainable customizes adding/removing sections
     * 
     * Uses data dictionary services
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Section> getSections(BusinessObject bo) {

        List<Section> sections = new ArrayList<Section>();
        if (getBusinessObjectClass() == null) {
            LOG.error("Business object class not set in inquirable.");
            throw new RuntimeException("Business object class not set in inquirable.");
        }
        
        InquiryRestrictions inquiryRestrictions = SpringContext.getBean(BusinessObjectAuthorizationService.class).getInquiryRestrictions(bo, GlobalVariables.getUserSession().getPerson());
        
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean showReceivableInfo = ArConstants.OrganizationAccountingOptionsConstants.SHOW_EDIT_PAYMENTS_DEFAULTS_TAB.equals(receivableOffsetOption);        

        Collection inquirySections = SpringContext.getBean(BusinessObjectDictionaryService.class).getInquirySections(getBusinessObjectClass());
        for (Iterator iter = inquirySections.iterator(); iter.hasNext();) {

            InquirySectionDefinition inquirySection = (InquirySectionDefinition) iter.next();
            Section section = SectionBridge.toSection(this, inquirySection, bo, inquiryRestrictions);
            
            if (inquirySection.getTitle().equals(ArConstants.ORGANIZATION_RECEIVABLE_ACCOUNT_DEFAULTS)){
                if( showReceivableInfo ){
                    sections.add(section);
                }
            } else {
                sections.add(section);  
            }
        }

        return sections;
    }    

}
