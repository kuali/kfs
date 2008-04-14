/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import static org.kuali.kfs.KFSConstants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE;
import static org.kuali.kfs.KFSConstants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE;
import static org.kuali.kfs.KFSConstants.AuxiliaryVoucher.RECODE_DOC_TYPE;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.kuali.core.document.Document;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;
import org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRule;
import org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants;
import org.kuali.module.financial.service.UniversityDateService;

/**
 * Struts form so <code>{@link AuxiliaryVoucherDocument}</code> can be accessed and modified through UI.
 */
public class AuxiliaryVoucherForm extends VoucherForm {
    private String originalVoucherType = KFSConstants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE; // keep this in sync with the default

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
        java.sql.Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();

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

        if (KFSConstants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE.equals(voucherTypeCode)) {
            formattedVoucherType = KFSConstants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE_NAME;
        }
        else if (KFSConstants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE.equals(voucherTypeCode)) {
            formattedVoucherType = KFSConstants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE_NAME;
        }
        else if (KFSConstants.AuxiliaryVoucher.RECODE_DOC_TYPE.equals(voucherTypeCode)) {
            formattedVoucherType = KFSConstants.AuxiliaryVoucher.RECODE_DOC_TYPE_NAME;
        }
        else {
            throw new IllegalStateException("Invalid auxiliary voucher type code: " + voucherTypeCode);
        }

        return formattedVoucherType + " (" + voucherTypeCode + ")";
    }

    /**
     * This method generates a proper list of valid accounting periods that the user can select from.
     * 
     * @see org.kuali.module.financial.web.struts.form.VoucherForm#populateAccountingPeriodListForRendering()
     */
    @Override
    public void populateAccountingPeriodListForRendering() {
        // grab the list of valid accounting periods
        ArrayList accountingPeriods = new ArrayList(SpringContext.getBean(AccountingPeriodService.class).getOpenAccountingPeriods());
        // now, validate further, based on the rules from AuxiliaryVoucherDocumentRule
        ArrayList filteredAccountingPeriods = new ArrayList();
        filteredAccountingPeriods.addAll(CollectionUtils.select(accountingPeriods, new OpenAuxiliaryVoucherPredicate(this.getDocument())));
        // if our auxiliary voucher doc contains an accounting period already, make sure the collection has it too!
        if (this.getDocument() instanceof AuxiliaryVoucherDocument) {
            AuxiliaryVoucherDocument avDoc = (AuxiliaryVoucherDocument) this.getDocument();
            if (avDoc != null && avDoc.getAccountingPeriod() != null && !filteredAccountingPeriods.contains(avDoc.getAccountingPeriod())) {
                // this is most likely going to happen because the approver is trying
                // to approve a document after the grace period of an accounting period
                // or a fiscal year has switched over when the document was first created;
                // as such, it's probably a good bet that the doc's accounting period
                // belongs at the top of the list
                filteredAccountingPeriods.add(0, avDoc.getAccountingPeriod());
            }
        }
        // set into the form for rendering
        setAccountingPeriods(filteredAccountingPeriods);
        // set the chosen accounting period into the form
        populateSelectedVoucherAccountingPeriod();
    }

    private class OpenAuxiliaryVoucherPredicate implements Predicate {
        private ParameterService parameterService;
        private UniversityDateService dateService;
        private AccountingPeriodService acctPeriodService;
        private Document auxiliaryVoucherDocument;
        private AccountingPeriod currPeriod;
        private ParameterEvaluator evaluator;
        private java.sql.Date currentDate;
        private Integer currentFiscalYear;

        public OpenAuxiliaryVoucherPredicate(Document doc) {
            this.parameterService = SpringContext.getBean(ParameterService.class);
            this.dateService = SpringContext.getBean(UniversityDateService.class);
            this.acctPeriodService = SpringContext.getBean(AccountingPeriodService.class);
            this.auxiliaryVoucherDocument = doc;
            this.currPeriod = acctPeriodService.getByDate(new java.sql.Date(new java.util.GregorianCalendar().getTimeInMillis()));
            this.currentDate = new java.sql.Date(new java.util.Date().getTime());
            this.evaluator = SpringContext.getBean(ParameterService.class).getParameterEvaluator(AuxiliaryVoucherDocument.class, AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_PERIOD_CODES, "");
            this.currentFiscalYear = dateService.getCurrentFiscalYear();
        }

        public boolean evaluate(Object o) {
            boolean result = false;
            if (o instanceof AccountingPeriod) {
                AccountingPeriod period = (AccountingPeriod) o;

                evaluator.setConstrainedValue(period.getUniversityFiscalPeriodCode());
                result = evaluator.evaluationSucceeds();
                if (result) {
                    result = (period.getUniversityFiscalYear().equals( currentFiscalYear ));
                    if (result) {
                        // did this accounting period end before now?
                        result = acctPeriodService.compareAccountingPeriodsByDate(period, currPeriod) >= 0;
                        if (!result) {
                            // if yes, are we still in the grace period?
                            result = AuxiliaryVoucherDocumentRule.calculateIfWithinGracePeriod(currentDate, period);
                        }
                    }
                    else {
                        // are we in current in the grace period of an ending accounting period of the previous fiscal year?
                        result = AuxiliaryVoucherDocumentRule.calculateIfWithinGracePeriod(currentDate, period) && AuxiliaryVoucherDocumentRule.isEndOfPreviousFiscalYear(period);
                    }
                }
            }
            return result;
        }
    }

}
