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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * 
 * This class may be the longest class name evar.
 */
public class OrganizationAccountingDefaultMaintenanceDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    @Override
    public Set<String> getConditionallyHiddenSectionIds(BusinessObject businessObject) {
        Set<String> hiddenSectionIds = super.getConditionallyHiddenSectionIds(businessObject);
        
        //  consult the system param on whether to hide the receivable tab
        if (!showReceivableTab()) {
            hiddenSectionIds.add("OrganizationAccountingDefaultMaintenanceDocument-EditOrganizationAccountingPaymentDefaults");
        }

        //  consult the system param on whether to hide the writeoff tab
        if (showWriteoffTab()) {
            hiddenSectionIds.add("OrganizationAccountingDefaultMaintenanceDocument-EditOrganizationWriteoffAccountDefaults");
        }

        return hiddenSectionIds;
    }

    protected boolean showReceivableTab(){
        return ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD));
    }
    
    protected boolean showWriteoffTab(){
        return ArConstants.GLPE_WRITEOFF_GENERATION_METHOD_ORG_ACCT_DEFAULT.equals(SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_GENERATION_METHOD));
    }    

}
