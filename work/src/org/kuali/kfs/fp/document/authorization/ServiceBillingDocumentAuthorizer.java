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
package org.kuali.kfs.fp.document.authorization;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * The customized document authorizer for the Service Billing document
 */
public class ServiceBillingDocumentAuthorizer extends AccountingDocumentAuthorizerBase {
    protected static String serviceBillingDocumentTypeName;

    /**
     * Overridden to only allow error correction and copy actions if the current user has Modify Accounting Document permission on every accounting line on the document
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase#getDocumentActions(org.kuali.rice.krad.document.Document, org.kuali.rice.kim.api.identity.Person, java.util.Set)
     */
    @Override
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActionsFromPresentationController) {
        Set<String> documentActions = super.getDocumentActions(document, user, documentActionsFromPresentationController);
        
        boolean canCopyOrErrorCorrect = (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_COPY) || documentActions.contains(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT)) ? canModifyAllSourceAccountingLines(document, user) : true;
        
        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_COPY)) {
            if (!canCopyOrErrorCorrect) {
                documentActions.remove(KRADConstants.KUALI_ACTION_CAN_COPY);
            }
        }
        if (documentActions.contains(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT)) {
            if (!canCopyOrErrorCorrect) {
                documentActions.remove(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT);
            }
        }
        return documentActions;
    }

    /**
     * Determines if the given user has permission to modify all accounting lines on the document
     * @param document the document with source accounting lines to check
     * @param user the user to check
     * @return true if the user can modify all the accounting lines, false otherwise
     */
    protected boolean canModifyAllSourceAccountingLines(Document document, Person user) {
        for (Object accountingLineAsObject : ((AccountingDocument)document).getSourceAccountingLines()) {
            if (!canModifyAccountingLine(document, ((AccountingLine)accountingLineAsObject), user)) return false;
        }
        return true;
    }
    
    /**
     * Determines if the given user can modify the given accounting line, which is a source line on the given document
     * @param document a document with source accounting lines
     * @param accountingLine the accounting line to check the modifyability of
     * @param user the user being checked
     * @return true if the user can modify the given accounting line, false otherwise
     */
    public boolean canModifyAccountingLine(Document document, AccountingLine accountingLine, Person user) {
        return isAuthorizedByTemplate(document, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.PermissionTemplate.MODIFY_ACCOUNTING_LINES.name, user.getPrincipalId(), buildPermissionDetails(document), buildRoleQualifiers(accountingLine));
    }
    
    /**
     * Builds the permission details map for permission check
     * @param document the document, which is used to find the real document type name
     * @return a Map of permissionDetail values
     */
    protected Map<String, String> buildPermissionDetails(Document document) {
        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, getDocumentTypeName(document)); // document type name
        permissionDetails.put(KfsKimAttributes.ROUTE_NODE_NAME, DocumentAuthorizerBase.PRE_ROUTING_ROUTE_NAME); // route node = PreRoute
        permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, "sourceAccountingLines"); // property = sourceAccountingLines
        return permissionDetails;
    }
    
    /**
     * Looks up in the data dictionary the document type name
     * @param document the document to find a document type name for
     * @return the document type name
     */
    protected String getDocumentTypeName(Document document) {
        if (serviceBillingDocumentTypeName == null) {
            serviceBillingDocumentTypeName = getDataDictionaryService().getDocumentTypeNameByClass(document.getClass());
        }
        return serviceBillingDocumentTypeName;
    }
    
    /**
     * Builds a map of role qualifiers, each containing the chart and account of the given accounting line
     * @param accountingLine the accounting line to build role qualifiers for
     * @return the Map of role qualifiers
     */
    protected Map<String, String> buildRoleQualifiers(AccountingLine accountingLine) {
        Map<String, String> roleQualifiers = new HashMap<String, String>();
        roleQualifiers.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, accountingLine.getChartOfAccountsCode());
        roleQualifiers.put(KfsKimAttributes.ACCOUNT_NUMBER, accountingLine.getAccountNumber());
        return roleQualifiers;
    }
}
