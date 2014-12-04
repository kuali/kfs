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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.List;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Authorizer for accounting lines associated with the travel advance
 */
public class TravelAdvanceAccountingLineAuthorizer extends TemAccountingLineAuthorizer {
    /**
     *
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#isGroupEditable(org.kuali.kfs.sys.document.AccountingDocument, java.util.List, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean isGroupEditable(AccountingDocument accountingDocument, List<? extends AccountingLineRenderingContext> accountingLineRenderingContexts, Person currentUser) {
        if (!((TravelAuthorizationDocument)accountingDocument).allParametersForAdvanceAccountingLinesSet() && TemConstants.TravelStatusCodeKeys.AWAIT_FISCAL.equals(accountingDocument.getFinancialSystemDocumentHeader().getApplicationDocumentStatus())) {
            return super.isGroupEditable(accountingDocument, accountingLineRenderingContexts, currentUser);
        }
        return false;
    }

    /**
     *
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#renderNewLine(org.kuali.kfs.sys.document.AccountingDocument, java.lang.String)
     */
    @Override
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty) {
        if (!((TravelAuthorizationDocument)accountingDocument).allParametersForAdvanceAccountingLinesSet() && TemConstants.TravelStatusCodeKeys.AWAIT_FISCAL.equals(accountingDocument.getFinancialSystemDocumentHeader().getApplicationDocumentStatus())) {
            return super.renderNewLine(accountingDocument, accountingGroupProperty);
        }
        return false;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.document.authorization.TemAccountingLineAuthorizer#determineEditPermissionOnLine(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, boolean, boolean)
     */
    @Override
    public boolean determineEditPermissionOnLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, boolean currentUserIsDocumentInitiator, boolean pageIsEditable) {
        if (!((TravelAuthorizationDocument)accountingDocument).allParametersForAdvanceAccountingLinesSet() && TemConstants.TravelStatusCodeKeys.AWAIT_FISCAL.equals(accountingDocument.getFinancialSystemDocumentHeader().getApplicationDocumentStatus())) {
            return super.determineEditPermissionOnLine(accountingDocument, accountingLine, accountingLineCollectionProperty, currentUserIsDocumentInitiator, pageIsEditable);
        }
        return false;
    }

    /**
     * If the parameters for the advance are set, don't allow editing of chart, account, or object code
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineEditPermissionOnField(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editablePage) {
        if (KFSPropertyConstants.FINANCIAL_OBJECT_CODE.equals(fieldName)) {
            return false; // you can never change the object code
        }
        if (((TravelAuthorizationDocument)accountingDocument).allParametersForAdvanceAccountingLinesSet()) {
            return false;
        }
        // parameters aren't set - so only fiscal officer should check
        if (TemConstants.TravelStatusCodeKeys.AWAIT_FISCAL.equals(accountingDocument.getFinancialSystemDocumentHeader().getApplicationDocumentStatus())) {
            return super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName, editablePage);
        }
        return false; // we're not at fiscal officer note...so, no editing
    }

    /**
     * Returns "advanceAccounting", the "infix" of the group
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getActionInfixForNewAccountingLine(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     */
    @Override
    protected String getActionInfixForNewAccountingLine(AccountingLine accountingLine, String accountingLinePropertyName) {
        if (TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_TYPE_CODE.equals(accountingLine.getFinancialDocumentLineTypeCode())) {
            return TemPropertyConstants.NEW_ADVANCE_ACCOUNTING_LINE_GROUP_NAME;
        }
        return super.getActionInfixForNewAccountingLine(accountingLine, accountingLinePropertyName);
    }

    /**
     * Returns "advanceAccoutingLines", the "infix" of the existing lines
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getActionInfixForExtantAccountingLine(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     */
    @Override
    protected String getActionInfixForExtantAccountingLine(AccountingLine accountingLine, String accountingLinePropertyName) {
        if (TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_TYPE_CODE.equals(accountingLine.getFinancialDocumentLineTypeCode())) {
            return TemPropertyConstants.NEW_ADVANCE_ACCOUNTING_LINE_GROUP_NAME;
        }
        return super.getActionInfixForExtantAccountingLine(accountingLine, accountingLinePropertyName);
    }

}
