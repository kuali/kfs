/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceItemCode;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class is used to ensure that default values are set accordingly if blank
 */
public class CustomerInvoiceItemCodePreRule extends PromptBeforeValidationBase { 

    @Override
    public boolean doPrompts(Document document) {

        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        CustomerInvoiceItemCode invoiceItemCode = (CustomerInvoiceItemCode) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
    
        Person user = GlobalVariables.getUserSession().getPerson();
        ChartOrgHolder chartOrg = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(user, ArConstants.AR_NAMESPACE_CODE);
        
        invoiceItemCode.setChartOfAccountsCode( chartOrg.getChartOfAccountsCode() );
        invoiceItemCode.setOrganizationCode( chartOrg.getOrganizationCode() );
        
        OrganizationAccountingDefault orgAccDefault = new OrganizationAccountingDefault();
        invoiceItemCode.setChartOfAccounts(orgAccDefault.getChartOfAccounts());
        
        
        
        return true;
    }
}

