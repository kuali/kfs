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
package org.kuali.kfs.sys.document.authorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.workflow.KualiWorkflowUtils.RouteLevelNames;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * DocumentAuthorizer containing common, reusable document-level authorization code for financial (i.e. Transactional) documents
 */
public class AccountingDocumentAuthorizerBase extends FinancialSystemTransactionalDocumentAuthorizerBase implements AccountingDocumentAuthorizer {
    private static Log LOG = LogFactory.getLog(AccountingDocumentAuthorizerBase.class);
    
    /**
     * Determines if the line is editable; if so, it adds the line to the editableAccounts map
     * @param line the line to determine editability of
     * @param currentUser the current session user to check permissions for
     * @param accountService the accountService
     * @return true if the line is editable, false otherwise 
     */
    private boolean determineLineEditability(AccountingLine line, Person currentUser, AccountService accountService) {
        Account acct = accountService.getByPrimaryId(line.getChartOfAccountsCode(), line.getAccountNumber());
        if (ObjectUtils.isNull(acct)) return true;
        if (accountService.hasResponsibilityOnAccount(currentUser, acct)) return true;
        return false;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizer#getEditableAccounts(org.kuali.rice.kns.document.TransactionalDocument, org.kuali.rice.kim.bo.Person)
     */
    public Map getEditableAccounts(TransactionalDocument document, Person user) {

        Map editableAccounts = new HashMap();
        AccountingDocument acctDoc = (AccountingDocument) document;
        AccountService accountService = SpringContext.getBean(AccountService.class);

        // for every source accounting line, decide if account should be in map
        for (Object acctLineAsObj: acctDoc.getSourceAccountingLines()) {
            if (determineLineEditability((AccountingLine)acctLineAsObj, user, accountService)) {
                editableAccounts.put(((AccountingLine)acctLineAsObj).getAccountKey(), "TRUE");
            }
        }

        // for every target accounting line, decide if account should be in map
        for (Object acctLineAsObj: acctDoc.getTargetAccountingLines()) {
            AccountingLine acctLine = (AccountingLine)acctLineAsObj;
            if (determineLineEditability((AccountingLine)acctLineAsObj, user, accountService)) {
                editableAccounts.put(((AccountingLine)acctLineAsObj).getAccountKey(), "TRUE");
            }
        }

        return editableAccounts;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizer#getEditableAccounts(java.util.List,
     *      org.kuali.module.chart.bo.KfsUser)
     */
    public Map getEditableAccounts(List<AccountingLine> lines, Person user) {
        Map editableAccounts = new HashMap();
        AccountService accountService = SpringContext.getBean(AccountService.class);
        
        for (AccountingLine line: lines) {
            if (determineLineEditability(line, user, accountService)) {
                editableAccounts.put(line.getAccountKey(), "TRUE");
            }
        }

        return editableAccounts;
    }
    
    @Override
    protected void addRoleQualification(BusinessObject businessObject, Map<String,String> attributes) {
        super.addRoleQualification(businessObject, attributes);
        Document document = (Document)businessObject;
        // add the document amount
        if ( ((AccountingDocument)document).getSourceTotal() != null && ((FinancialSystemDocumentHeader)document.getDocumentHeader()).getFinancialDocumentTotalAmount() != null ) {
            attributes.put(KfsKimAttributes.DOCUMENT_AMOUNT, ((FinancialSystemDocumentHeader)document.getDocumentHeader()).getFinancialDocumentTotalAmount().toString());
        } else {
            attributes.put(KfsKimAttributes.DOCUMENT_AMOUNT, "0" );
        }
    }
}

