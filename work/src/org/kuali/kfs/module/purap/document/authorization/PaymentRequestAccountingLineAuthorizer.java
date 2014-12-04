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

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Accounting line authorizer for Requisition document which allows adding accounting lines at specified nodes
 */
public class PaymentRequestAccountingLineAuthorizer extends PurapAccountingLineAuthorizer {
    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getUnviewableBlocks(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, boolean, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public Set<String> getUnviewableBlocks(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser) {
        Set<String> unviewableBlocks = super.getUnviewableBlocks(accountingDocument, accountingLine, newLine, currentUser);

        // if Account Distribution Method equals Sequential, show both percent and amount, otherwise
        // show just percent or amount based on logic in super.getUnviewableBlocks (whether full entry mode is complete)
        PurchasingAccountsPayableDocumentBase purApDocument = (PurchasingAccountsPayableDocumentBase) accountingDocument;

        if (PurapConstants.AccountDistributionMethodCodes.SEQUENTIAL_CODE.equalsIgnoreCase(purApDocument.getAccountDistributionMethod())) {
            unviewableBlocks.remove(KFSPropertyConstants.PERCENT);
            unviewableBlocks.remove(KFSPropertyConstants.AMOUNT);
        }

        return unviewableBlocks;
    }
}
