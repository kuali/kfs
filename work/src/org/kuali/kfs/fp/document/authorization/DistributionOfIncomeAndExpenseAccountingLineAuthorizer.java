/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.authorization;

import java.util.List;

import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineViewField;

/**
 * Authorizer which deals with financial processing document issues, specifically sales tax lines on documents This class utilizes
 * the new accountingLine model.
 */
public class DistributionOfIncomeAndExpenseAccountingLineAuthorizer extends FinancialProcessingAccountingLineAuthorizer {

    /**
     * This method determines if the current accounting line is editable based upon if electronic claims exists on the DI document.
     * 
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineFieldModifyability(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.document.web.AccountingLineViewField, java.util.Map)
     */
    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName) {
        final boolean canModify = super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName);
        if (canModify && accountingLine.isSourceAccountingLine()) {
            DistributionOfIncomeAndExpenseDocument diDoc = (DistributionOfIncomeAndExpenseDocument) accountingDocument;

            List<ElectronicPaymentClaim> epcs = diDoc.getElectronicPaymentClaims();

            if (epcs == null) {
                diDoc.refreshReferenceObject(KFSPropertyConstants.ELECTRONIC_PAYMENT_CLAIMS);
                epcs = diDoc.getElectronicPaymentClaims();
            }

            if (epcs != null && epcs.size() > 0) {
                return false; // we can't modify no matter what...
            }
        }
        
        return canModify;
    }

}
