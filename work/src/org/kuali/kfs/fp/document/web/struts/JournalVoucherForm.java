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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.financial.document.JournalVoucherDocument;

/**
 * This class is the Struts specific form object that works in conjunction with the pojo utilities to build the UI for the Journal
 * Voucher Document. This class is unique in that it leverages a helper data structure called the VoucherAccountingLineHelper
 * because the Journal Voucher, under certain conditions, presents the user with a debit and credit column for amount entry. In
 * addition, this form class must keep track of the changes between the old and new balance type selection so that the corresponding
 * action class and make decisions based upon the differences. New accounting lines use specific credit and debit amount fields b/c
 * the new line is explicitly known; however, already existing accounting lines need to exist within a list with ordering that
 * matches the accounting lines source list.
 */
public class JournalVoucherForm extends VoucherForm {
    private List balanceTypes;
    private String originalBalanceType;
    private BalanceTyp selectedBalanceType;

    /**
     * Constructs a JournalVoucherForm instance.
     */
    public JournalVoucherForm() {
        super();
        setDocument(new JournalVoucherDocument());
        selectedBalanceType = new BalanceTyp(KFSConstants.BALANCE_TYPE_ACTUAL);
        originalBalanceType = "";
    }

    /**
     * Overrides the parent to call super.populate and then to call the two methods that are specific to loading the two select
     * lists on the page. In addition, this also makes sure that the credit and debit amounts are filled in for situations where
     * validation errors occur and the page reposts.
     * 
     * @see org.kuali.core.web.struts.pojo.PojoForm#populate(javax.servlet.http.HttpServletRequest)
     */
    public void populate(HttpServletRequest request) {
        super.populate(request);
        populateBalanceTypeListForRendering();
    }

    /**
     * Override the parent, to push the chosen accounting period and balance type down into the source accounting line object. In
     * addition, check the balance type to see if it's the "External Encumbrance" balance and alter the encumbrance update code on
     * the accounting line appropriately.
     * 
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#populateSourceAccountingLine(org.kuali.core.bo.SourceAccountingLine)
     */
    public void populateSourceAccountingLine(SourceAccountingLine sourceLine) {
        super.populateSourceAccountingLine(sourceLine);
        populateSourceAccountingLineEncumbranceCode(sourceLine);
    }

    /**
     * Sets the encumbrance code of the line based on the balance type.
     * 
     * @param sourceLine - line to set code on
     */
    protected void populateSourceAccountingLineEncumbranceCode(SourceAccountingLine sourceLine) {
        BalanceTyp selectedBalanceType = getSelectedBalanceType();
        if (selectedBalanceType != null && StringUtils.isNotBlank(selectedBalanceType.getCode())) {
            sourceLine.setBalanceTyp(selectedBalanceType);
            sourceLine.setBalanceTypeCode(selectedBalanceType.getCode());

            // set the encumbrance update code appropriately
            if (KFSConstants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE.equals(selectedBalanceType.getCode())) {
                sourceLine.setEncumbranceUpdateCode(KFSConstants.JOURNAL_VOUCHER_ENCUMBRANCE_UPDATE_CODE_BALANCE_TYPE_EXTERNAL_ENCUMBRANCE);
            }
            else {
                sourceLine.setEncumbranceUpdateCode(null);
            }
        }
        else {
            // it's the first time in, the form will be empty the first time in
            // set up default selection value
            selectedBalanceType = SpringContext.getBean(BalanceTypService.class).getBalanceTypByCode(KFSConstants.BALANCE_TYPE_ACTUAL); 
            setSelectedBalanceType(selectedBalanceType);
            setOriginalBalanceType(selectedBalanceType.getCode());

            sourceLine.setEncumbranceUpdateCode(null);
        }
    }

    /**
     * This method retrieves the list of valid balance types to display.
     * 
     * @return List
     */
    public List getBalanceTypes() {
        return balanceTypes;
    }

    /**
     * This method sets the selected balance type.
     * 
     * @return BalanceTyp
     */
    public BalanceTyp getSelectedBalanceType() {
        return selectedBalanceType;
    }

    /**
     * This method retrieves the selected balance type.
     * 
     * @param selectedBalanceType
     */
    public void setSelectedBalanceType(BalanceTyp selectedBalanceType) {
        this.selectedBalanceType = selectedBalanceType;
    }

    /**
     * This method sets the list of valid balance types to display.
     * 
     * @param balanceTypes
     */
    public void setBalanceTypes(List balanceTypes) {
        this.balanceTypes = balanceTypes;
    }

    /**
     * This method returns the journal voucher document associated with this form.
     * 
     * @return Returns the journalVoucherDocument.
     */
    public JournalVoucherDocument getJournalVoucherDocument() {
        return (JournalVoucherDocument) getTransactionalDocument();
    }

    /**
     * This method sets the journal voucher document associated with this form.
     * 
     * @param journalVoucherDocument The journalVoucherDocument to set.
     */
    public void setJournalVoucherDocument(JournalVoucherDocument journalVoucherDocument) {
        setDocument(journalVoucherDocument);
    }

    /**
     * This method retrieves the originalBalanceType attribute.
     * 
     * @return String
     */
    public String getOriginalBalanceType() {
        return originalBalanceType;
    }

    /**
     * This method sets the originalBalanceType attribute.
     * 
     * @param changedBalanceType
     */
    public void setOriginalBalanceType(String changedBalanceType) {
        this.originalBalanceType = changedBalanceType;
    }

    /**
     * This method retrieves all of the balance types in the system and prepares them to be rendered in a dropdown UI component.
     */
    private void populateBalanceTypeListForRendering() {
        // grab the list of valid balance types
        ArrayList balanceTypes = new ArrayList(SpringContext.getBean(BalanceTypService.class).getAllBalanceTyps());

        // set into the form for rendering
        this.setBalanceTypes(balanceTypes);

        String selectedBalanceTypeCode = getSelectedBalanceType().getCode();
        if (StringUtils.isBlank(selectedBalanceTypeCode)) {
            selectedBalanceTypeCode = KFSConstants.BALANCE_TYPE_ACTUAL;
        }
        
        setSelectedBalanceType(getPopulatedBalanceTypeInstance(selectedBalanceTypeCode));
        getJournalVoucherDocument().setBalanceTypeCode(selectedBalanceTypeCode);
    }

    /**
     * This method will fully populate a balance type given the passed in code, by calling the business object service that
     * retrieves the rest of the instances' information.
     * 
     * @param balanceTypeCode
     * @return BalanceTyp
     */
    protected BalanceTyp getPopulatedBalanceTypeInstance(String balanceTypeCode) {
        // now we have to get the code and the name of the original and new balance types
        BalanceTypService bts = SpringContext.getBean(BalanceTypService.class);
        return bts.getBalanceTypByCode(balanceTypeCode);
    }

    /**
     * If the balance type is an offset generation balance type, then the user is able to enter the amount as either a debit or a
     * credit, otherwise, they only need to deal with the amount field in this case we always need to update the underlying bo so
     * that the debit/credit code along with the amount, is properly set.
     */
    protected void populateCreditAndDebitAmounts() {
        if (isSelectedBalanceTypeFinancialOffsetGenerationIndicator()) {
            super.populateCreditAndDebitAmounts();
        }
    }

    /**
     * This is a convenience helper method that is used several times throughout this action class to determine if the selected
     * balance type contained within the form instance is a financial offset generation balance type or not.
     * 
     * @return boolean True if it is an offset generation balance type, false otherwise.
     */
    private boolean isSelectedBalanceTypeFinancialOffsetGenerationIndicator() {
        return getPopulatedBalanceTypeInstance(getSelectedBalanceType().getCode()).isFinancialOffsetGenerationIndicator();
    }
}