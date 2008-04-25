/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.ar.maintenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.datadictionary.InquirySectionDefinition;
import org.kuali.core.inquiry.KualiInquirableImpl;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.ui.Section;
import org.kuali.core.web.ui.SectionBridge;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.inquiry.KfsInquirableImpl;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.OrganizationAccountingDefault;
import org.kuali.module.ar.document.CustomerInvoiceDocument;

public class OrganizationAccountingDefaultInquirable extends KfsInquirableImpl {
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationAccountingDefaultInquirable.class);
    
    /**
     * @see org.kuali.core.inquiry.KualiInquirableImpl#getSections(org.kuali.core.bo.BusinessObject)
     */
    public List<Section> getSections(BusinessObject bo) {

        List<Section> sections = new ArrayList<Section>();
        if (getBusinessObjectClass() == null) {
            LOG.error("Business object class not set in inquirable.");
            throw new RuntimeException("Business object class not set in inquirable.");
        }
        
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean showReceivableInfo = ArConstants.OrganizationAccountingOptionsConstants.SHOW_EDIT_PAYMENTS_DEFAULTS_TAB.equals(receivableOffsetOption);        

        Collection inquirySections = SpringContext.getBean(BusinessObjectDictionaryService.class).getInquirySections(getBusinessObjectClass());
        for (Iterator iter = inquirySections.iterator(); iter.hasNext();) {

            InquirySectionDefinition inquirySection = (InquirySectionDefinition) iter.next();
            Section section = SectionBridge.toSection(this, inquirySection, bo);
            
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
