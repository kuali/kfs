/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.financial.web.struts.form;

import static org.kuali.Constants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE;
import static org.kuali.Constants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE;
import static org.kuali.Constants.AuxiliaryVoucher.RECODE_DOC_TYPE;

import javax.servlet.http.HttpServletRequest;

import org.kuali.Constants;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;

/**
 * Struts form so <code>{@link AuxiliaryVoucherDocument}</code> can be accessed and modified through UI.
 * 
 * 
 */
public class AuxiliaryVoucherForm extends VoucherForm {
    private String originalVoucherType = Constants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE; // keep this in sync with the default
                                                                                            // value set in the document business
                                                                                            // object

    public AuxiliaryVoucherForm() {
        super();
        setDocument(new AuxiliaryVoucherDocument());
    }

    /**
     * Overrides the parent to call super.populate and then to call the two methods that are specific to loading the two select
     * lists on the page. In addition, this also makes sure that the credit and debit amounts are filled in for situations where
     * validation errors occur and the page reposts.
     * 
     * @see org.kuali.core.web.struts.pojo.PojoForm#populate(javax.servlet.http.HttpServletRequest)
     */
    public void populate(HttpServletRequest request) {
        // populate the drop downs
        super.populate(request);
        populateReversalDateForRendering();
    }

    /**
     * @return Returns the serviceBillingDocument.
     */
    public AuxiliaryVoucherDocument getAuxiliaryVoucherDocument() {
        return (AuxiliaryVoucherDocument) getDocument();
    }

    /**
     * @param serviceBillingDocument The serviceBillingDocument to set.
     */
    public void setAuxiliaryVoucherDocument(AuxiliaryVoucherDocument auxiliaryVoucherDocument) {
        setDocument(auxiliaryVoucherDocument);
    }

    /**
     * Handles special case display rules for displaying Reversal Date at UI layer
     */
    public void populateReversalDateForRendering() {
        java.sql.Date today = SpringServiceLocator.getDateTimeService().getCurrentSqlDateMidnight();

        if (getAuxiliaryVoucherDocument().getTypeCode().equals(ACCRUAL_DOC_TYPE) && (getAuxiliaryVoucherDocument().getReversalDate() == null || getAuxiliaryVoucherDocument().getReversalDate().before(today))) {
            getAuxiliaryVoucherDocument().setReversalDate(today);
        }
        else if (getAuxiliaryVoucherDocument().getTypeCode().equals(ADJUSTMENT_DOC_TYPE)) {
            getAuxiliaryVoucherDocument().setReversalDate(null);
        }
        else if (getAuxiliaryVoucherDocument().getTypeCode().equals(RECODE_DOC_TYPE)) {
            getAuxiliaryVoucherDocument().setReversalDate(new java.sql.Date(getDocument().getDocumentHeader().getWorkflowDocument().getCreateDate().getTime()));
        }
    }

    /**
     * This method returns the reversal date in the format MMM d, yyyy.
     * 
     * @return String
     */
    public String getFormattedReversalDate() {
        return formatReversalDate(getAuxiliaryVoucherDocument().getReversalDate());
    }

    /**
     * @return String
     */
    public String getOriginalVoucherType() {
        return originalVoucherType;
    }

    /**
     * @param originalVoucherType
     */
    public void setOriginalVoucherType(String originalVoucherType) {
        this.originalVoucherType = originalVoucherType;
    }

    /**
     * Returns a formatted auxiliary voucher type: <Voucher Type Name> (<Voucher Type Code>)
     * 
     * @return
     */
    public String getFormattedAuxiliaryVoucherType() {
        String voucherTypeCode = getAuxiliaryVoucherDocument().getTypeCode();
        String formattedVoucherType = new String();

        if (Constants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE.equals(voucherTypeCode)) {
            formattedVoucherType = Constants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE_NAME;
        }
        else if (Constants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE.equals(voucherTypeCode)) {
            formattedVoucherType = Constants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE_NAME;
        }
        else if (Constants.AuxiliaryVoucher.RECODE_DOC_TYPE.equals(voucherTypeCode)) {
            formattedVoucherType = Constants.AuxiliaryVoucher.RECODE_DOC_TYPE_NAME;
        }
        else {
            throw new IllegalStateException("Invalid auxiliary voucher type code: " + voucherTypeCode);
        }

        return formattedVoucherType + " (" + voucherTypeCode + ")";
    }
}
