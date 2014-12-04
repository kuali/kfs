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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Accounting line authorizer for Requisition document which allows adding accounting lines at specified nodes
 */
public class VendorCreditMemoAccountingLineAuthorizer extends PurapAccountingLineAuthorizer {
    private static final String INITIATOR_NODE = "Initiator";
    private static final String CONTENT_REVIEW_NODE = "Organization";

    /**
     * Allow new lines to be rendered at Initiator node
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#renderNewLine(org.kuali.kfs.sys.document.AccountingDocument, java.lang.String)
     */
    @Override
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty) {
        WorkflowDocument workflowDocument = ((PurchasingAccountsPayableDocument)accountingDocument).getFinancialSystemDocumentHeader().getWorkflowDocument();

        Set<String> currentNodeNames = workflowDocument.getCurrentNodeNames();
        if (CollectionUtils.isNotEmpty(currentNodeNames) && (currentNodeNames.equals(VendorCreditMemoAccountingLineAuthorizer.INITIATOR_NODE) || currentNodeNames.equals(VendorCreditMemoAccountingLineAuthorizer.CONTENT_REVIEW_NODE))) {
            return true;
        }
        return super.renderNewLine(accountingDocument, accountingGroupProperty);
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getUnviewableBlocks(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, boolean, org.kuali.rice.kim.bo.Person)
     */
    @Override
    public Set<String> getUnviewableBlocks(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser) {
        Set<String> unviewableBlocks = super.getUnviewableBlocks(accountingDocument, accountingLine, newLine, currentUser);
        unviewableBlocks.remove(KFSPropertyConstants.PERCENT);
        unviewableBlocks.remove(KFSPropertyConstants.AMOUNT);

        return unviewableBlocks;
    }

}
