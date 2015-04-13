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

