/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.fp.document.authorization;

import java.util.List;

import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Authorizer which deals with financial processing document issues, specifically sales tax lines on documents This class utilizes
 * the new accountingLine model.
 */
public class DistributionOfIncomeAndExpenseAccountingLineAuthorizer extends CapitalAccountingLinesAuthorizerBase {

    /**
     * This method determines if the current accounting line is editable based upon if electronic claims exists on the DI document.
     *
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineFieldModifyability(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.document.web.AccountingLineViewField, java.util.Map)
     */
    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editablePage) {
        final boolean canModify = super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName, editablePage);
        if (canModify && accountingLine.isSourceAccountingLine()) {
            return !hasElectronicPaymentClaims(accountingDocument);
        }

        return canModify;
    }

    /**
     * Don't render a new line if this is the source group and there's electronic payment claims
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#renderNewLine(org.kuali.kfs.sys.document.AccountingDocument, java.lang.String)
     */
    @Override
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty) {
        final boolean shouldRender = super.renderNewLine(accountingDocument, accountingGroupProperty);
        if (shouldRender && accountingGroupProperty.contains("source")) {
            return !hasElectronicPaymentClaims(accountingDocument);
        }
        return shouldRender;
    }

    /**
     * There's no edit permission on lines in the source group on documents claiming electronic payments
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineEditPermissionOnLine(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     */
    @Override
    public boolean determineEditPermissionOnLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, boolean currentUserIsDocumentInitiator, boolean pageIsEditable) {
        final boolean hasEditPermOnLine = super.determineEditPermissionOnLine(accountingDocument, accountingLine, accountingLineCollectionProperty, currentUserIsDocumentInitiator, pageIsEditable);
        if (hasEditPermOnLine && accountingLineCollectionProperty.contains("source")) {
            return !hasElectronicPaymentClaims(accountingDocument);
        }
        return hasEditPermOnLine;
    }

    /**
     * Determines if the DI document has electronic payment claims associated with it
     * @param accountingDocument a DI document
     * @return true if there are electronic payment claims, false otherwise
     */
    protected boolean hasElectronicPaymentClaims(AccountingDocument accountingDocument) {
        DistributionOfIncomeAndExpenseDocument diDoc = (DistributionOfIncomeAndExpenseDocument) accountingDocument;

        List<ElectronicPaymentClaim> epcs = diDoc.getElectronicPaymentClaims();

        if (epcs == null) {
            diDoc.refreshReferenceObject(KFSPropertyConstants.ELECTRONIC_PAYMENT_CLAIMS);
            epcs = diDoc.getElectronicPaymentClaims();
        }

        return (!ObjectUtils.isNull(epcs) && epcs.size() > 0);
    }

}
