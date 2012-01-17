/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.document.authorization;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;

/**
 * DocumentAuthorizer containing common, reusable document-level authorization code for financial (i.e. Transactional) documents
 */
public class AccountingDocumentAuthorizerBase extends FinancialSystemTransactionalDocumentAuthorizerBase {
    private static final Log LOG = LogFactory.getLog(AccountingDocumentAuthorizerBase.class);

    /**
     * Determines if the line is editable; if so, it adds the line to the editableAccounts map
     * @param line the line to determine editability of
     * @param currentUser the current session user to check permissions for
     * @param accountService the accountService
     * @return true if the line is editable, false otherwise 
     */
    protected boolean determineLineEditability(AccountingLine line, Person currentUser, AccountService accountService) {
        Account acct = accountService.getByPrimaryId(line.getChartOfAccountsCode(), line.getAccountNumber());
        if (acct == null) return true;
        return accountService.hasResponsibilityOnAccount(currentUser, acct);
    }
    
    @Override
    protected void addRoleQualification(Object dataObject, Map<String,String> attributes) {
        super.addRoleQualification(dataObject, attributes);
        Document document = (Document)dataObject;
        // add the document amount
        if ( ((AccountingDocument)document).getSourceTotal() != null && ((FinancialSystemDocumentHeader)document.getDocumentHeader()).getFinancialDocumentTotalAmount() != null ) {
            attributes.put(KfsKimAttributes.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, ((FinancialSystemDocumentHeader)document.getDocumentHeader()).getFinancialDocumentTotalAmount().toString());
        } else {
            attributes.put(KfsKimAttributes.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, "0" );
        }
    }
}

