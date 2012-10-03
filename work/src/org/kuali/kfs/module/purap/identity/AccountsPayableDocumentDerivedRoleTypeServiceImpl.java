/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;
import org.kuali.rice.krad.service.DocumentService;

public class AccountsPayableDocumentDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsPayableDocumentDerivedRoleTypeServiceImpl.class);

    protected static final String FISCAL_OFFICER_ROLE_NAME = "Fiscal Officer";
    protected static final String SUB_ACCOUNT_ROLE_NAME = "Sub-Account Reviewer";
    protected static final String ACCOUNTING_REVIEWER_ROLE_NAME = "Accounting Reviewer";

    /**
     * Overridden to check if the current user has document reviewer permission based on the data in the document.
     *
     * @see org.kuali.rice.kns.kim.role.RoleTypeServiceBase#(java.lang.String, java.util.List, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public boolean hasDerivedRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String,String> qualification) {

        String docId = qualification.get(KimConstants.AttributeConstants.DOCUMENT_NUMBER);

        List<String> roleIds = new ArrayList<String>();
        roleIds.add(KimApiServiceLocator.getRoleService().getRoleIdByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.KFS, FISCAL_OFFICER_ROLE_NAME));
        roleIds.add(KimApiServiceLocator.getRoleService().getRoleIdByNamespaceCodeAndName(PurapConstants.PURAP_NAMESPACE, SUB_ACCOUNT_ROLE_NAME));
        roleIds.add(KimApiServiceLocator.getRoleService().getRoleIdByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.KFS, ACCOUNTING_REVIEWER_ROLE_NAME    ));

        try {
            AccountsPayableDocument apDocument = (AccountsPayableDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
            for (Iterator iter = apDocument.getSourceAccountingLines().iterator(); iter.hasNext();) {
                SourceAccountingLine accountingLine = (SourceAccountingLine) iter.next();

                Map<String,String> roleQualifier = new HashMap<String,String>();
                roleQualifier.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, apDocument.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
                roleQualifier.put(KfsKimAttributes.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, apDocument.getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount().toString());
                roleQualifier.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, accountingLine.getChartOfAccountsCode());
                roleQualifier.put(KfsKimAttributes.ORGANIZATION_CODE, accountingLine.getAccount().getOrganizationCode());
                roleQualifier.put(KfsKimAttributes.ACCOUNT_NUMBER, accountingLine.getAccountNumber());
                roleQualifier.put(KfsKimAttributes.SUB_ACCOUNT_NUMBER, accountingLine.getSubAccountNumber());
                roleQualifier.put(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE, accountingLine.getOverrideCode());

                if (KimApiServiceLocator.getRoleService().principalHasRole(principalId, roleIds, roleQualifier)) {
                    return true;
                }

            }
        }
        catch (WorkflowException we) {
            LOG.error("Exception encountered when retrieving document number " + docId + ".", we);
            throw new RuntimeException("Exception encountered when retrieving document number " + docId + ".", we);
        }

        return false;
    }


}
