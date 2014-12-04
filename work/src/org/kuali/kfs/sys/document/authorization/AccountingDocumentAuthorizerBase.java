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

