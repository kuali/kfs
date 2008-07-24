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
package org.kuali.kfs.module.ar.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.web.ui.Section;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;

public class OrganizationAccountingDefaultMaintainableImpl extends KualiMaintainableImpl {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationAccountingDefault.class);
    
    @Override
    @SuppressWarnings("unchecked")
    public List getSections(Maintainable oldMaintainable) {

        List<Section> sections = super.getSections(oldMaintainable);
        List<Section> updatedSections = new ArrayList<Section>();

        for (Iterator iter = sections.iterator(); iter.hasNext();) {

            Section section = (Section) iter.next();
            
            if( isReceivableTab(section.getSectionTitle()) ){
                if( showReceivableTab() ){
                    updatedSections.add(section);
                }
            } else if( isWriteoffTab(section.getSectionTitle()) ){
                if( showWriteoffTab() ){
                    updatedSections.add(section);
                }
            } else {
                updatedSections.add(section);
            }
        }
        return updatedSections;
    }
    
    protected boolean isReceivableTab(String sectionTitle){
        return ArConstants.OrganizationAccountingOptionsConstants.ORG_ACCT_DEFAULT_RECEIVABLE_TAB_NAME.equals( sectionTitle );
    }
    
    protected boolean isWriteoffTab(String sectionTitle){
        return ArConstants.OrganizationAccountingOptionsConstants.ORG_ACCT_DEFAULT_WRITEOFF_TAB_NAME.equals( sectionTitle );        
    }   
    
    protected boolean showReceivableTab(){
        return ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD));
    }
    
    protected boolean showWriteoffTab(){
        return ArConstants.GLPE_WRITEOFF_GENERATION_METHOD_ORG_ACCT_DEFAULT.equals(SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_GENERATION_METHOD));
    }    
}
