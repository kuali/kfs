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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Verifies that the total of advance accounting lines truly does equal the travel advance amount
 */
public class TravelAdvanceAccountingTotalsValidation extends GenericValidation {
    protected DebitDeterminerService debitDeterminerService;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        final TravelAuthorizationDocument documentForValidation = (TravelAuthorizationDocument)event.getDocument();
        boolean success = true;

        if (!ObjectUtils.isNull(documentForValidation.getTravelAdvance()) && documentForValidation.getTravelAdvance().isAtLeastPartiallyFilledIn()) {
            // find the total for the accounting lines
            final KualiDecimal accountingLineTotal = calculateAdvanceAccountingLineTotal(documentForValidation);
            if (!accountingLineTotal.equals(documentForValidation.getTravelAdvance().getTravelAdvanceRequested())) {
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.ADVANCE_ACCOUNTING_LINES+"[0]."+KFSPropertyConstants.AMOUNT, TemKeyConstants.ERROR_TA_ADVANCE_ACCOUNTING_LINES_ADVANCE_AMOUNT_REQUESTED_NOT_EQUAL, documentForValidation.getTravelAdvance().getTravelAdvanceRequested().toString(), accountingLineTotal.toString());
                success = false;
            }
        }

        return success;
    }

    /**
     * Using the debit determination service, calculates the total of the advance accounting lines
     * @param document the travel authorization or child document to calculate the amount of
     * @return the total of the accounting lines
     */
    protected KualiDecimal calculateAdvanceAccountingLineTotal(TravelAuthorizationDocument document) {
        KualiDecimal accountingLineTotal = KualiDecimal.ZERO;
        for (TemSourceAccountingLine advanceAccountingLine : document.getAdvanceAccountingLines()) {
            if (document.isDebit(advanceAccountingLine)) {
                accountingLineTotal = accountingLineTotal.add(advanceAccountingLine.getAmount()); // negate the debit, because the accounting lines are offsetting the actual advance
            }
            else {
                accountingLineTotal = accountingLineTotal.subtract(advanceAccountingLine.getAmount());
            }
        }
        return accountingLineTotal;
    }

    /**
     * @return the injected implementation of the DebitDeterminerService
     */
    public DebitDeterminerService getDebitDeterminerService() {
        return debitDeterminerService;
    }

    /**
     * Injects an implementation of the DebitDeterminerService.  A totally awesome implementation!
     * @param debitDeterminerService the awesome implementation of the DebitDeterminerService to inject
     */
    public void setDebitDeterminerService(DebitDeterminerService debitDeterminerService) {
        this.debitDeterminerService = debitDeterminerService;
    }

}
