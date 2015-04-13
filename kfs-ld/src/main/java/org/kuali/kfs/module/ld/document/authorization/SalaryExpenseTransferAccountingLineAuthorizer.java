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
package org.kuali.kfs.module.ld.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.util.GlobalVariables;

public class SalaryExpenseTransferAccountingLineAuthorizer extends LaborExpenseTransferAccountingLineAuthorizer {
    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editablePage) {
        if (accountingLine.isTargetAccountingLine()) {
            SalaryExpenseTransferDocument expenseTransferDocument = (SalaryExpenseTransferDocument) accountingDocument;
            WorkflowDocument workflowDocument = expenseTransferDocument.getDocumentHeader().getWorkflowDocument();
            
            // decide if the object code field should be read-only or not based on the user's permissions to edit the field.
            if(KFSPropertyConstants.FINANCIAL_OBJECT_CODE.equals(fieldName)) {
                return this.hasEditPermissionOnObjectCode(expenseTransferDocument, workflowDocument);
            }
        }
        
        return super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName, editablePage);
    }
    
    // check if user is allowed to edit the object code.
    protected boolean hasEditPermissionOnObjectCode(SalaryExpenseTransferDocument expenseTransferDocument, WorkflowDocument workflowDocument) {
        String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
        DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(expenseTransferDocument);
        String templateName = KFSConstants.PermissionTemplate.MODIFY_ACCOUNTING_LINES.name;
    
        Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
        additionalPermissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, workflowDocument.getDocumentTypeName());
        additionalPermissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, "targetAccountingLines.financialObjectCode");
    
        return documentAuthorizer.isAuthorizedByTemplate(expenseTransferDocument, KFSConstants.ParameterNamespaces.KFS, templateName, principalId, additionalPermissionDetails, null);
    }
}
