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
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.authorization.DisbursementVoucherDocumentAuthorizer;
import org.kuali.kfs.fp.document.service.DisbursementVoucherWorkGroupService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineGroupTotalsUnchangedValidation;
import org.kuali.rice.kns.service.DocumentAuthorizationService;
import org.kuali.rice.kns.util.GlobalVariables;

public class DisbursementVoucherAccountingLineTotalsValidation extends AccountingLineGroupTotalsUnchangedValidation {
    private DisbursementVoucherWorkGroupService disbursementVoucherWorkGroupService = SpringContext.getBean(DisbursementVoucherWorkGroupService.class);

    /**
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineGroupTotalsUnchangedValidation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) event.getDocument();
        FinancialSystemUser financialSystemUser = GlobalVariables.getUserSession().getFinancialSystemUser();
        
        DocumentAuthorizationService documentAuthorizer = SpringContext.getBean(DocumentAuthorizationService.class);
        DisbursementVoucherDocumentAuthorizer dvAuthorizer = (DisbursementVoucherDocumentAuthorizer) documentAuthorizer.getDocumentAuthorizer(dvDocument);
        
        // amounts can only decrease
        if (dvAuthorizer.isSpecialRouting(dvDocument, financialSystemUser) && this.isUserInDisbursementVouchWorkGroups(financialSystemUser)) {

            // users in foreign or wire workgroup can increase or decrease amounts because of currency conversion
            if (this.isUserNotInForeignDraftAndWireTransferWorkGroups(financialSystemUser)) {
                DisbursementVoucherDocument persistedDocument = (DisbursementVoucherDocument) retrievePersistedDocument(dvDocument);
                if (persistedDocument == null) {
                    handleNonExistentDocumentWhenApproving(dvDocument);
                    return true;
                }
                
                // check total cannot decrease
                if (persistedDocument.getDisbVchrCheckTotalAmount().isLessThan(dvDocument.getDisbVchrCheckTotalAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.DISB_VCHR_CHECK_TOTAL_AMOUNT, KFSKeyConstants.ERROR_DV_CHECK_TOTAL_CHANGE);
                    return false;
                }
            }
        }
        
        return true;
    }
    
    // determine whether the current user is a member of the specified work groups
    private boolean isUserInDisbursementVouchWorkGroups(FinancialSystemUser financialSystemUser) {
        boolean isInWorkGroups = true;
        isInWorkGroups = isInWorkGroups || disbursementVoucherWorkGroupService.isUserInFRNGroup(financialSystemUser);
        isInWorkGroups = isInWorkGroups || disbursementVoucherWorkGroupService.isUserInTaxGroup(financialSystemUser);
        isInWorkGroups = isInWorkGroups || disbursementVoucherWorkGroupService.isUserInTravelGroup(financialSystemUser);
        isInWorkGroups = isInWorkGroups || disbursementVoucherWorkGroupService.isUserInWireGroup(financialSystemUser);
        
        return isInWorkGroups;
    }
    
    // determine whether the current user is a member of neither foreign draft nor wire transfer work groups
    private boolean isUserNotInForeignDraftAndWireTransferWorkGroups(FinancialSystemUser financialSystemUser) {
        boolean isNotInWorkGroups = true;
        isNotInWorkGroups = isNotInWorkGroups && !disbursementVoucherWorkGroupService.isUserInFRNGroup(financialSystemUser);
        isNotInWorkGroups = isNotInWorkGroups && !disbursementVoucherWorkGroupService.isUserInWireGroup(financialSystemUser);
        
        return isNotInWorkGroups;
    }

    /**
     * Sets the disbursementVoucherWorkGroupService attribute value.
     * @param disbursementVoucherWorkGroupService The disbursementVoucherWorkGroupService to set.
     */
    public void setDisbursementVoucherWorkGroupService(DisbursementVoucherWorkGroupService disbursementVoucherWorkGroupService) {
        this.disbursementVoucherWorkGroupService = disbursementVoucherWorkGroupService;
    }
}
