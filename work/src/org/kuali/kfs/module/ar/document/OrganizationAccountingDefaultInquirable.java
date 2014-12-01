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
