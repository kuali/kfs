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
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.CoinDetail;
import org.kuali.kfs.fp.businessobject.CurrencyDetail;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.CashReceiptFamilyBase;
import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.FinancialSystemWorkflowHelperService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation for the cash receipt document that verifies that the cash drawer is open at approval.
 */
public class CashReceiptCashDrawerOpenValidation extends GenericValidation {
    private CashReceiptFamilyBase cashReceiptDocumentForValidation;
    private CashReceiptService cashReceiptService;
    private CashDrawerService cashDrawerService;

    /**
     * Makes sure that the cash drawer for the verification unit associated with this CR doc is
     * open. If it's not, the the rule fails.
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {

        CashDrawer cd = getCashDrawerService().getByCampusCode(getCashReceiptDocumentForValidation().getCampusLocationCode());
        if (cd == null) {
            throw new IllegalStateException("There is no cash drawer associated with unitName '" + getCashReceiptDocumentForValidation().getCampusLocationCode() + "' from cash receipt " + getCashReceiptDocumentForValidation().getDocumentNumber());
        }
        WorkflowDocument workflowDocument = getCashReceiptDocumentForValidation().getDocumentHeader().getWorkflowDocument();
        boolean isAdhocApproval = SpringContext.getBean(FinancialSystemWorkflowHelperService.class).isAdhocApprovalRequestedForPrincipal(workflowDocument, GlobalVariables.getUserSession().getPrincipalId());

        if (cd.isClosed() && !isAdhocApproval) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.CashReceipt.MSG_CASH_DRAWER_CLOSED_VERIFICATION_NOT_ALLOWED, cd.getCampusCode());
            return false;
        }

        //check whether the change request is valid
        // Since these details are always initialized to default 0, they are never null, so we should check if they are empty instead.
        CashReceiptDocument crDoc = (CashReceiptDocument)cashReceiptDocumentForValidation;
        if (crDoc.isConfirmedChangeRequested()) {
            return checkChangeRequestIsValid();
        }
        return true;
    }

    /**
     * This method checks whether the change request in the cash receipt is valid, i.e. for each currency/coin denomination,
     * the change requested must not be greater than the sum of cash drawer + currency/coin from the cash receipt itself.
     * @return Returns true if the request is valid.
     */
    public boolean checkChangeRequestIsValid() {
        // we should use the confirmed amounts for this validation
        CashReceiptDocument crDoc = (CashReceiptDocument)cashReceiptDocumentForValidation;
        CurrencyDetail confirmedCurrency = crDoc.getConfirmedCurrencyDetail();
        CoinDetail confirmedCoin = crDoc.getConfirmedCoinDetail();
        CurrencyDetail confirmedChangeCurrency = crDoc.getConfirmedChangeCurrencyDetail();
        CoinDetail confirmedChangeCoin = crDoc.getConfirmedChangeCoinDetail();

        // create CurrencyDetail and CoinDetail from the cash drawer Currency and Coin amount, respectively
        CashDrawer cashDrawer = getCashDrawerService().getByCampusCode(getCashReceiptDocumentForValidation().getCampusLocationCode());
        CurrencyDetail drawerCurrency = new CurrencyDetail(cashDrawer);
        CoinDetail drawerCoin = new CoinDetail(cashDrawer);

        /* Note:
         * The original logic use coin count for comparison; but this won't work anymore, since we now include roll count into the amount.
         * It's more straight-forward to compare the amounts directly: The change request is valid if the following formula satisfy:
         * drawerCurrency + confirmedCurrency - changeCurrency >= 0
         * drawerCoin + confirmedCoin - changeCoin >= 0
         */
        drawerCurrency.add(confirmedCurrency);
        drawerCurrency.subtract(confirmedChangeCurrency);
        drawerCoin.add(confirmedCoin);
        drawerCoin.subtract(confirmedChangeCoin);

        // if the final result contains any negative amount, throw error
        if (drawerCurrency.hasNegativeAmount() || drawerCoin.hasNegativeAmount()) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.CashReceipt.ERROR_CHANGE_REQUEST, "");
            return false;
        }

        return true;
    }

    /**
     * Gets the cashReceiptDocumentForValidation attribute.
     * @return Returns the cashReceiptDocumentForValidation.
     */
    public CashReceiptFamilyBase getCashReceiptDocumentForValidation() {
        return cashReceiptDocumentForValidation;
    }

    /**
     * Sets the cashReceiptDocumentForValidation attribute value.
     * @param cashReceiptDocumentForValidation The cashReceiptDocumentForValidation to set.
     */
    public void setCashReceiptDocumentForValidation(CashReceiptFamilyBase cashReceiptFamilyDocumentForValidation) {
        this.cashReceiptDocumentForValidation = cashReceiptFamilyDocumentForValidation;
    }

    /**
     * Gets the cashDrawerService attribute.
     * @return Returns the cashDrawerService.
     */
    public CashDrawerService getCashDrawerService() {
        return cashDrawerService;
    }

    /**
     * Sets the cashDrawerService attribute value.
     * @param cashDrawerService The cashDrawerService to set.
     */
    public void setCashDrawerService(CashDrawerService cashDrawerService) {
        this.cashDrawerService = cashDrawerService;
    }

    /**
     * Gets the cashReceiptService attribute.
     * @return Returns the cashReceiptService.
     */
    public CashReceiptService getCashReceiptService() {
        return cashReceiptService;
    }

    /**
     * Sets the cashReceiptService attribute value.
     * @param cashReceiptService The cashReceiptService to set.
     */
    public void setCashReceiptService(CashReceiptService cashReceiptService) {
        this.cashReceiptService = cashReceiptService;
    }
}
