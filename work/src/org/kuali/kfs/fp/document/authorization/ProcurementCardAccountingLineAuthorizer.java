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
package org.kuali.kfs.fp.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.RouteLevelNames;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.KRADConstants;

public class ProcurementCardAccountingLineAuthorizer extends CapitalAccountingLinesAuthorizerBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardAccountingLineAuthorizer.class);

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getKimHappyPropertyNameForField(java.lang.String)
     */
    @Override
    protected String getKimHappyPropertyNameForField(String convertedName) {
        String name = stripDocumentPrefixFromName(convertedName);
        name = name.replaceAll("\\[\\d+\\]", StringUtils.EMPTY);
        name = name.replaceFirst("(.)*transactionEntries\\.", StringUtils.EMPTY);

        return name;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getDeleteLineMethod(org.kuali.kfs.sys.businessobject.AccountingLine,
     *      java.lang.String, java.lang.Integer)
     */
    @Override
    protected String getDeleteLineMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        final String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        String lineIndex = this.getLineContainerIndex(accountingLineProperty);
        String lineContainer = this.getLineContainer(accountingLineProperty) + ".";

        return KRADConstants.DELETE_METHOD + infix + "Line." + lineContainer + "line" + accountingLineIndex + ".anchoraccounting" + infix + "Anchor";
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getAddMethod(org.kuali.kfs.sys.businessobject.AccountingLine,
     *      java.lang.String, java.lang.Integer)
     */
    @Override
    protected String getAddMethod(AccountingLine accountingLine, String accountingLineProperty) {
        Integer lineIndex = ((ProcurementCardTargetAccountingLine) accountingLine).getTransactionContainerIndex();
        String lineIndexString = lineIndex.toString();

        String infix = getActionInfixForNewAccountingLine(accountingLine, accountingLineProperty);

        return KFSConstants.INSERT_METHOD + infix + "Line" + ".transactionEntries[" + lineIndex + "]";
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getBalanceInquiryMethod(org.kuali.kfs.sys.businessobject.AccountingLine,
     *      java.lang.String, java.lang.Integer)
     */
    @Override
    protected String getBalanceInquiryMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        String lineContainer = this.getLineContainer(accountingLineProperty) + ".";

        return KFSConstants.PERFORMANCE_BALANCE_INQUIRY_FOR_METHOD + infix + "Line." + lineContainer + "line" + accountingLineIndex + ".anchoraccounting" + infix + "existingLineLineAnchor" + accountingLineIndex;
    }

    protected String getLineContainer(String accountingLineProperty) {
        String lineContainer = stripDocumentPrefixFromName(accountingLineProperty);
        return StringUtils.substringBeforeLast(lineContainer, ".");
    }

    protected String getLineContainerIndex(String accountingLineProperty) {
        String lineContainer = this.getLineContainer(accountingLineProperty);
        return StringUtils.substringBetween(lineContainer, "[", "]");
    }

    /**
     * Overrides to handle the special case for PCDO routed to AccountFullEdit node, at which point the FO (and the delegates) should
     * be able to add new accounts or change any existing account to any other account even if s/he is not the FO of the new account.
     * Note: This method is called by hasEditPermissionOnField and hasEditPermissionOnAccountingLine. The best place to override with
     * the above logic would have been these two methods; but both of them are definded as final for some reason, so we put the logic here.
     *
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineEditPermissionByFieldName(AccountingDocument,AccountingLine,String,Person)
     */
    @Override
    protected boolean determineEditPermissionByFieldName(AccountingDocument accountingDocument, AccountingLine accountingLine, String fieldName, Person currentUser) {
        // 1. If this method is called, we know it's a PCDO document here.
        // 2. Check that the document is at AccountFullEdit route node
        if (accountingDocument.getDocumentHeader() != null &&
                accountingDocument.getDocumentHeader().getWorkflowDocument() != null) {
            Set<String> currentNodes = accountingDocument.getDocumentHeader().getWorkflowDocument().getCurrentNodeNames();
            if (currentNodes != null && currentNodes.contains(RouteLevelNames.ACCOUNT_REVIEW_FULL_EDIT)) {
                // 3. Check that the current user has the permission to edit the document, which means in this case he can edit the accounting line
                if (getDocumentAuthorizer(accountingDocument).canEdit(accountingDocument, currentUser)) {
                    // if above conditions satisfy, then we can skip further validation on permission checking,
                    // since any user that can edit the accounting lines will be able to add/change it to any other account
                    return true;
                }
            }
        }

        return super.determineEditPermissionByFieldName(accountingDocument, accountingLine, fieldName, currentUser);
    }

}
