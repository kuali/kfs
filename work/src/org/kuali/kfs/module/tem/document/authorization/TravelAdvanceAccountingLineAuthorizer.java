/*
 * Copyright 2013 The Kuali Foundation.
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
public class TravelAdvanceAccountingLineAuthorizer extends TEMAccountingLineAuthorizer {
    /**
     *
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#isGroupEditable(org.kuali.kfs.sys.document.AccountingDocument, java.util.List, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean isGroupEditable(AccountingDocument accountingDocument, List<? extends AccountingLineRenderingContext> accountingLineRenderingContexts, Person currentUser) {
        if (TemConstants.TravelStatusCodeKeys.AWAIT_FISCAL.equals(accountingDocument.getFinancialSystemDocumentHeader().getApplicationDocumentStatus())) {
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
        if (TemConstants.TravelStatusCodeKeys.AWAIT_FISCAL.equals(accountingDocument.getFinancialSystemDocumentHeader().getApplicationDocumentStatus())) {
            return super.renderNewLine(accountingDocument, accountingGroupProperty);
        }
        return false;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.document.authorization.TEMAccountingLineAuthorizer#determineEditPermissionOnLine(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, boolean, boolean)
     */
    @Override
    public boolean determineEditPermissionOnLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, boolean currentUserIsDocumentInitiator, boolean pageIsEditable) {
        if (TemConstants.TravelStatusCodeKeys.AWAIT_FISCAL.equals(accountingDocument.getFinancialSystemDocumentHeader().getApplicationDocumentStatus())) {
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
            return !(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE.equals(fieldName) || KFSPropertyConstants.ACCOUNT_NUMBER.equals(fieldName) || KFSPropertyConstants.AMOUNT.equals(fieldName)); //chart, account, and amount are not editable, everything else is
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