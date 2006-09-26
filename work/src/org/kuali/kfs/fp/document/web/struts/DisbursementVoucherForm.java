/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.web.struts.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.format.SimpleBooleanFormatter;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.financial.bo.DisbursementVoucherNonEmployeeExpense;
import org.kuali.module.financial.bo.DisbursementVoucherPreConferenceRegistrant;
import org.kuali.module.financial.bo.TravelPerDiem;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.rules.DisbursementVoucherDocumentRule;
import org.kuali.module.financial.rules.DisbursementVoucherRuleConstants;

/**
 * This class is the action form for the Disbursement Voucher.
 * 
 * @author Kuali Financial Transactions Team ()
 */
public class DisbursementVoucherForm extends KualiTransactionalDocumentFormBase {
    private static final long serialVersionUID = 1L;

    private DisbursementVoucherNonEmployeeExpense newNonEmployeeExpenseLine;
    private DisbursementVoucherNonEmployeeExpense newPrePaidNonEmployeeExpenseLine;
    private DisbursementVoucherPreConferenceRegistrant newPreConferenceRegistrantLine;

    private String wireChargeMessage;

    public DisbursementVoucherForm() {
        super();
        setFormatterType("canPrintCoverSheet", SimpleBooleanFormatter.class);
        setDocument(new DisbursementVoucherDocument());
    }

    /**
     * @return Returns the newNonEmployeeExpenseLine.
     */
    public DisbursementVoucherNonEmployeeExpense getNewNonEmployeeExpenseLine() {
        return newNonEmployeeExpenseLine;
    }

    /**
     * @param newNonEmployeeExpenseLine The newNonEmployeeExpenseLine to set.
     */
    public void setNewNonEmployeeExpenseLine(DisbursementVoucherNonEmployeeExpense newNonEmployeeExpenseLine) {
        this.newNonEmployeeExpenseLine = newNonEmployeeExpenseLine;
    }

    /**
     * @return Returns the newPreConferenceRegistrantLine.
     */
    public DisbursementVoucherPreConferenceRegistrant getNewPreConferenceRegistrantLine() {
        return newPreConferenceRegistrantLine;
    }

    /**
     * @param newPreConferenceRegistrantLine The newPreConferenceRegistrantLine to set.
     */
    public void setNewPreConferenceRegistrantLine(DisbursementVoucherPreConferenceRegistrant newPreConferenceRegistrantLine) {
        this.newPreConferenceRegistrantLine = newPreConferenceRegistrantLine;
    }

    /**
     * @return Returns the newPrePaidNonEmployeeExpenseLine.
     */
    public DisbursementVoucherNonEmployeeExpense getNewPrePaidNonEmployeeExpenseLine() {
        return newPrePaidNonEmployeeExpenseLine;
    }

    /**
     * @param newPrePaidNonEmployeeExpenseLine The newPrePaidNonEmployeeExpenseLine to set.
     */
    public void setNewPrePaidNonEmployeeExpenseLine(DisbursementVoucherNonEmployeeExpense newPrePaidNonEmployeeExpenseLine) {
        this.newPrePaidNonEmployeeExpenseLine = newPrePaidNonEmployeeExpenseLine;
    }

    /**
     * @return Returns the wireChargeMessage.
     */
    public String getWireChargeMessage() {
        return wireChargeMessage;
    }

    /**
     * @param wireChargeMessage The wireChargeMessage to set.
     */
    public void setWireChargeMessage(String wireChargeMessage) {
        this.wireChargeMessage = wireChargeMessage;
    }

    /**
     * 
     * determines if the DV document is in a state that allows printing of the cover sheet
     * 
     * @return
     */
    public boolean getCanPrintCoverSheet() {
        DisbursementVoucherDocumentRule documentRule = new DisbursementVoucherDocumentRule();

        return documentRule.isCoverSheetPrintable(getDocument());
    }

    /**
     * Returns list of available travel expense type codes for rendering per diem link page.
     * 
     * @return
     */
    public List getTravelPerDiemCategoryCodes() {
        Map criteria = new HashMap();
        criteria.put("fiscalYear", SpringServiceLocator.getDateTimeService().getCurrentFiscalYear());

        return (List) SpringServiceLocator.getKeyValuesService().findMatching(TravelPerDiem.class, criteria);
    }

    /**
     * Returns the per diem link message from the parameters table.
     * 
     * @return
     */
    public String getTravelPerDiemLinkPageMessage() {
        return SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(DisbursementVoucherRuleConstants.DV_DOCUMENT_PARAMETERS_GROUP_NM, DisbursementVoucherRuleConstants.TRAVEL_PER_DIEM_MESSAGE_PARM_NM);
    }

}