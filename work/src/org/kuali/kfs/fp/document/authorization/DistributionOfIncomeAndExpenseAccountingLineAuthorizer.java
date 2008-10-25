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
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.rice.kim.bo.Person;
import org.kuali.kfs.sys.document.AccountingDocument;

/**
 * Authorizer which deals with financial processing document issues, specifically sales tax lines on documents
 * This class utilizes the new accountingLine model.
 */
public class DistributionOfIncomeAndExpenseAccountingLineAuthorizer extends FinancialProcessingAccountingLineAuthorizer {

    /**
     * This method determines if the current accounting line is editable based upon if electronic claims
     * exists on the DI document.  This method returns a boolean value.
          * 
     * @param document
     *
     * Note: the following parameters are not required in the method but are here for the interface.
     *
     * @param accountingLine
     * @param currentUser
     * @return true if the line is editable, false otherwise 
     */
    @Override
    protected boolean isAccountingLineEditable(AccountingDocument document, AccountingLine accountingLine, Person currentUser) {
        boolean isEditable = super.isAccountingLineEditable(document, accountingLine, currentUser);

        if (isEditable) {
            DistributionOfIncomeAndExpenseDocument diDoc = (DistributionOfIncomeAndExpenseDocument) document;
    
            List<ElectronicPaymentClaim> epcs = diDoc.getElectronicPaymentClaims();
    
            if (epcs == null) {
                diDoc.refreshReferenceObject("electronicPaymentClaims");
                epcs = diDoc.getElectronicPaymentClaims();
            }
    
            if (epcs != null && epcs.size() > 0) {
                return false;
            }
        }

        return isEditable;
    }
}

