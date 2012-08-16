/*
 * Copyright 2012 The Kuali Foundation.
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
