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
package org.kuali.module.ar.rules;

import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.bo.ChartOrgHolder;
import org.kuali.kfs.bo.FinancialSystemUser;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.FinancialSystemUserService;
import org.kuali.module.ar.bo.CustomerInvoiceItemCode;
import org.kuali.module.ar.bo.OrganizationAccountingDefault;

/**
 * This class is used to ensure that default values are set accordingly if blank
 */
public class CustomerInvoiceItemCodePreRule extends PreRulesContinuationBase { 

    @Override
    public boolean doRules(Document document) {

        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        CustomerInvoiceItemCode invoiceItemCode = (CustomerInvoiceItemCode) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
    
        FinancialSystemUser user = GlobalVariables.getUserSession().getFinancialSystemUser();
        ChartOrgHolder chartOrg = SpringContext.getBean(FinancialSystemUserService.class).getOrganizationByModuleId("ar");
        
        invoiceItemCode.setChartOfAccountsCode( chartOrg.getChartOfAccountsCode() );
        invoiceItemCode.setOrganizationCode( chartOrg.getOrganizationCode() );
        
        OrganizationAccountingDefault orgAccDefault = new OrganizationAccountingDefault();
        invoiceItemCode.setChartOfAccounts(orgAccDefault.getChartOfAccounts());
        
        
        
        return true;
    }
}
