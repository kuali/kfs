/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.UpdateAccountingLineEvent;
import org.kuali.kfs.sys.service.FinancialSystemWorkflowHelperService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * A validation that checks whether the given accounting line is accessible to the given user or not
 */
public class PurchaseOrderAmendmentAccountingLineAccessibleValidation extends PurchasingAccountsPayableAccountingLineAccessibleValidation {

    protected PurapService purapService;

    /**
     * Validates that the given accounting line is accessible for editing by the current user.
     * <strong>This method expects a document as the first parameter and an accounting line as the second</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {

        if( purapService.isDocumentStoppedInRouteNode((PurchasingAccountsPayableDocument)event.getDocument(), "New Unordered Items") ){
            //DO NOTHING: do not check that user owns acct lines; at this level, they can edit all accounts on PO amendment
            return true;

        } else if (SpringContext.getBean(FinancialSystemWorkflowHelperService.class).isAdhocApprovalRequestedForPrincipal(event.getDocument().getDocumentHeader().getWorkflowDocument(), GlobalVariables.getUserSession().getPrincipalId())) {
            return true;
        } else if (onlyAmountChanged(((UpdateAccountingLineEvent) event).getAccountingLine(), ((UpdateAccountingLineEvent) event).getUpdatedAccountingLine())) {
            //  if only amount is changed, return true...
            //  This condition is added later which will return true only if amount is changed, it won't affect any other functionality of amending the PO document.
            return true;
        } else {
            boolean result = false;
            boolean setDummyAccountIdentifier = false;

            if (needsDummyAccountIdentifier()) {
                ((PurApAccountingLine)getAccountingLineForValidation()).setAccountIdentifier(Integer.MAX_VALUE);  // avoid conflicts with any accouting identifier on any other accounting lines in the doc because, you know, you never know...
                setDummyAccountIdentifier = true;
            }

            result = super.validate(event);

            if (setDummyAccountIdentifier) {
                ((PurApAccountingLine)getAccountingLineForValidation()).setAccountIdentifier(null);
            }

            return result;
        }
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    /**
     * Checks to see if the object code is the only difference between the original accounting line and the updated accounting line.
     *
     * @param accountingLine
     * @param updatedAccountingLine
     * @return true if only the object code has changed on the accounting line, false otherwise
     */
    private boolean onlyAmountChanged(AccountingLine accountingLine, AccountingLine updatedAccountingLine) {
        // no changes, return false
        if (accountingLine.isLike(updatedAccountingLine)) {
            return false;
        }

        // copy the updatedAccountLine so we can set the object code on the copy of the updated accounting line
        // to be the original value for comparison purposes
        AccountingLine updatedLine = null;
        if (updatedAccountingLine.isSourceAccountingLine()) {
            updatedLine = new SourceAccountingLine();
        } else {
            updatedLine = new TargetAccountingLine();
        }

        updatedLine.copyFrom(updatedAccountingLine);
        updatedLine.setAmount(accountingLine.getAmount());

        boolean isLike = false;
        Map thisValues = accountingLine.getValuesMap();
        Map otherValues = updatedLine.getValuesMap();
        isLike = thisValues.equals(otherValues);

        //  if both are not same, check for document number, as document number gets omitted in values map for updated line
        if (!isLike) {
            int countInequalities = 0;

            for (Iterator i = otherValues.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();

                Object thisValue = thisValues.get(key);
                Object otherValue = otherValues.get(key);
                //  ignore if document number and sequence number found, as document number is omitted from one map and sequence number might change so ignore considering them
                if (!org.apache.commons.lang.ObjectUtils.equals(key, KFSPropertyConstants.DOCUMENT_NUMBER) && !org.apache.commons.lang.ObjectUtils.equals(key, KFSPropertyConstants.SEQUENCE_NUMBER) && !org.apache.commons.lang.ObjectUtils.equals(thisValue, otherValue)) {
                    countInequalities++;
                }
            }
            isLike = (countInequalities == 0);
        }

        // if they're the same, the only change was the amount
        return isLike;
    }
}

