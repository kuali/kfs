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
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Authorizer for accounting lines associated with the travel advance
 */
public class TravelAdvanceAccountingLineAuthorizer extends TEMAccountingLineAuthorizer {
    protected static volatile ParameterService parameterService;

    /**
     *
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#isGroupEditable(org.kuali.kfs.sys.document.AccountingDocument, java.util.List, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean isGroupEditable(AccountingDocument accountingDocument, List<? extends AccountingLineRenderingContext> accountingLineRenderingContexts, Person currentUser) {
        // do the doc numbers of the accounting lines match the doc number of the given document?  If not, these accounting lines
        // are from a different document; they are not editable
        if (areLinesFromAnotherDocument(accountingDocument, accountingLineRenderingContexts)) {
            return false;
        }
        if (((TravelAuthorizationDocument)accountingDocument).allParametersForAdvanceSet()) {
            return false;
        }
        return super.isGroupEditable(accountingDocument, accountingLineRenderingContexts, currentUser);
    }

    /**
     *
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#renderNewLine(org.kuali.kfs.sys.document.AccountingDocument, java.lang.String)
     */
    @Override
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty) {
        if (((TravelAuthorizationDocument)accountingDocument).allParametersForAdvanceSet() && accountingGroupProperty.startsWith(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.ADVANCE_ACCOUNTING_LINES)) {
            return false;
        }
        return super.renderNewLine(accountingDocument, accountingGroupProperty);
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.document.authorization.TEMAccountingLineAuthorizer#determineEditPermissionOnLine(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, boolean, boolean)
     */
    @Override
    public boolean determineEditPermissionOnLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, boolean currentUserIsDocumentInitiator, boolean pageIsEditable) {
        if (accountingLine.getDocumentNumber() != null && !accountingLine.getDocumentNumber().equals(accountingDocument.getDocumentNumber())) {
            return false;
        }
        if (((TravelAuthorizationDocument)accountingDocument).allParametersForAdvanceSet()) {
            return false;
        }
        return super.determineEditPermissionOnLine(accountingDocument, accountingLine, accountingLineCollectionProperty, currentUserIsDocumentInitiator, pageIsEditable);
    }

    /**
     * Determines if the accounting lines being asked to render are from another accounting document
     * @param accountingDocument the current accounting document being rendered
     * @param accountingLineRenderingContexts the accounting lines which are being rendered
     * @return true if the accounting lines are from another document, false otherwise
     */
    protected boolean areLinesFromAnotherDocument(AccountingDocument accountingDocument, List<? extends AccountingLineRenderingContext> accountingLineRenderingContexts) {
        return accountingLineRenderingContexts != null && !accountingLineRenderingContexts.isEmpty() && accountingLineRenderingContexts.get(0).getAccountingLine().getDocumentNumber() != null && !accountingLineRenderingContexts.get(0).getAccountingLine().getDocumentNumber().equals(accountingDocument.getDocumentNumber());
    }

    /**
     * @return the default implementation of the ParameterService
     */
    public ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
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