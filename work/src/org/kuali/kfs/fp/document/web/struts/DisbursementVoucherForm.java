/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.fp.document.web.struts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeExpense;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPreConferenceRegistrant;
import org.kuali.kfs.fp.businessobject.TravelPerDiem;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherCoverSheetService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.PaymentSourceHelperService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.core.web.format.SimpleBooleanFormatter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.krad.service.KeyValuesService;
import org.kuali.rice.krad.util.KRADConstants;
/**
 * This class is the action form for the Disbursement Voucher.
 */
public class DisbursementVoucherForm extends KualiAccountingDocumentFormBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherForm.class);

    protected static final long serialVersionUID = 1L;

    protected String payeeIdNumber;
    protected String vendorHeaderGeneratedIdentifier = StringUtils.EMPTY;
    protected String vendorDetailAssignedIdentifier = StringUtils.EMPTY;
    protected String vendorAddressGeneratedIdentifier;

    protected String tempPayeeIdNumber;
    protected String tempVendorHeaderGeneratedIdentifier = StringUtils.EMPTY;
    protected String tempVendorDetailAssignedIdentifier = StringUtils.EMPTY;
    protected String tempVendorAddressGeneratedIdentifier;
    protected String oldPayeeType = StringUtils.EMPTY;

    protected boolean hasMultipleAddresses = false;

    protected DisbursementVoucherNonEmployeeExpense newNonEmployeeExpenseLine;
    protected DisbursementVoucherNonEmployeeExpense newPrePaidNonEmployeeExpenseLine;
    protected DisbursementVoucherPreConferenceRegistrant newPreConferenceRegistrantLine;
    protected String wireChargeMessage;

    protected boolean canExport = false;

    /**
     * Constructs a DisbursementVoucherForm.java.
     */
    public DisbursementVoucherForm() {
        super();
        setFormatterType("canPrintCoverSheet", SimpleBooleanFormatter.class);
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "DV";
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
     * determines if the DV document is in a state that allows printing of the cover sheet
     *
     * @return true if the DV document is in a state that allows printing of the cover sheet; otherwise, return false
     */
    public boolean getCanPrintCoverSheet() {
        DisbursementVoucherDocument disbursementVoucherDocument = (DisbursementVoucherDocument) this.getDocument();
        return SpringContext.getBean(DisbursementVoucherCoverSheetService.class).isCoverSheetPrintable(disbursementVoucherDocument);
    }

    /**
     * @return a list of available travel expense type codes for rendering per diem link page.
     */
    public List<TravelPerDiem> getTravelPerDiemCategoryCodes() {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());

        return (List<TravelPerDiem>) SpringContext.getBean(KeyValuesService.class).findMatching(TravelPerDiem.class, criteria);
    }

    /**
     * @return the per diem link message from the parameters table.
     */
    public String getTravelPerDiemLinkPageMessage() {
        return SpringContext.getBean(ParameterService.class).getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.TRAVEL_PER_DIEM_MESSAGE_PARM_NM);
    }

    /**
     * Gets the payeeIdNumber attribute.
     *
     * @return Returns the payeeIdNumber.
     */
    public String getPayeeIdNumber() {
        return payeeIdNumber;
    }

    /**
     * Sets the payeeIdNumber attribute value.
     *
     * @param payeeIdNumber The payeeIdNumber to set.
     */
    public void setPayeeIdNumber(String payeeIdNumber) {
        String separator = "-";
        if (this.isVendor() && StringUtils.contains(payeeIdNumber, separator)) {
            this.vendorHeaderGeneratedIdentifier = StringUtils.substringBefore(payeeIdNumber, separator);
            this.vendorDetailAssignedIdentifier = StringUtils.substringAfter(payeeIdNumber, separator);
        }

        this.payeeIdNumber = payeeIdNumber;
    }

    /**
     * Gets the payeeIdNumber attribute.
     *
     * @return Returns the payeeIdNumber.
     */
    public String getTempPayeeIdNumber() {
        return tempPayeeIdNumber;
    }

    /**
     * Sets the payeeIdNumber attribute value.
     *
     * @param payeeIdNumber The payeeIdNumber to set.
     */
    public void setTempPayeeIdNumber(String payeeIdNumber) {
        String separator = "-";
        if (this.isVendor() && StringUtils.contains(payeeIdNumber, separator)) {
            this.tempVendorHeaderGeneratedIdentifier = StringUtils.substringBefore(payeeIdNumber, separator);
            this.tempVendorDetailAssignedIdentifier = StringUtils.substringAfter(payeeIdNumber, separator);
        }

        this.tempPayeeIdNumber = payeeIdNumber;
    }


    /**
     * Gets the hasMultipleAddresses attribute.
     *
     * @return Returns the hasMultipleAddresses.
     */
    public boolean hasMultipleAddresses() {
        return hasMultipleAddresses;
    }

    /**
     * Gets the hasMultipleAddresses attribute.
     *
     * @return Returns the hasMultipleAddresses.
     */
    public boolean getHasMultipleAddresses() {
        return hasMultipleAddresses;
    }

    /**
     * Sets the hasMultipleAddresses attribute value.
     *
     * @param hasMultipleAddresses The hasMultipleAddresses to set.
     */
    public void setHasMultipleAddresses(boolean hasMultipleAddresses) {
        this.hasMultipleAddresses = hasMultipleAddresses;
    }

    /**
     * Gets the vendorHeaderGeneratedIdentifier attribute.
     *
     * @return Returns the vendorHeaderGeneratedIdentifier.
     */
    public String getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    /**
     * Sets the vendorHeaderGeneratedIdentifier attribute value.
     *
     * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
     */
    public void setVendorHeaderGeneratedIdentifier(String vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    /**
     * Gets the vendorDetailAssignedIdentifier attribute.
     *
     * @return Returns the vendorDetailAssignedIdentifier.
     */
    public String getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    /**
     * Sets the vendorDetailAssignedIdentifier attribute value.
     *
     * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
     */
    public void setVendorDetailAssignedIdentifier(String vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    /**
     * Gets the vendorAddressGeneratedIdentifier attribute.
     *
     * @return Returns the vendorAddressGeneratedIdentifier.
     */
    public String getVendorAddressGeneratedIdentifier() {
        return vendorAddressGeneratedIdentifier;
    }

    /**
     * Sets the vendorAddressGeneratedIdentifier attribute value.
     *
     * @param vendorAddressGeneratedIdentifier The vendorAddressGeneratedIdentifier to set.
     */
    public void setVendorAddressGeneratedIdentifier(String vendorAddressGeneratedIdentifier) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
    }

    public DisbursementVoucherDocument getDisbursementVoucherDocument() {
       return (DisbursementVoucherDocument) getDocument();

       }
    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        DisbursementVoucherPayeeDetail payeeDetail = getDisbursementVoucherDocument().getDvPayeeDetail();
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(payeeDetail);
        }




    /**
     * Gets the tempVendorHeaderGeneratedIdentifier attribute.
     * @return Returns the tempVendorHeaderGeneratedIdentifier.
     */
    public String getTempVendorHeaderGeneratedIdentifier() {
        return tempVendorHeaderGeneratedIdentifier;
    }

    /**
     * Sets the tempVendorHeaderGeneratedIdentifier attribute value.
     * @param tempVendorHeaderGeneratedIdentifier The tempVendorHeaderGeneratedIdentifier to set.
     */
    public void setTempVendorHeaderGeneratedIdentifier(String tempVendorHeaderGeneratedIdentifier) {
        this.tempVendorHeaderGeneratedIdentifier = tempVendorHeaderGeneratedIdentifier;
    }

    /**
     * Gets the tempVendorDetailAssignedIdentifier attribute.
     * @return Returns the tempVendorDetailAssignedIdentifier.
     */
    public String getTempVendorDetailAssignedIdentifier() {
        return tempVendorDetailAssignedIdentifier;
    }

    /**
     * Sets the tempVendorDetailAssignedIdentifier attribute value.
     * @param tempVendorDetailAssignedIdentifier The tempVendorDetailAssignedIdentifier to set.
     */
    public void setTempVendorDetailAssignedIdentifier(String tempVendorDetailAssignedIdentifier) {
        this.tempVendorDetailAssignedIdentifier = tempVendorDetailAssignedIdentifier;
    }

    /**
     * Gets the tempVendorAddressGeneratedIdentifier attribute.
     * @return Returns the tempVendorAddressGeneratedIdentifier.
     */
    public String getTempVendorAddressGeneratedIdentifier() {
        return tempVendorAddressGeneratedIdentifier;
    }

    /**
     * Sets the tempVendorAddressGeneratedIdentifier attribute value.
     * @param tempVendorAddressGeneratedIdentifier The tempVendorAddressGeneratedIdentifier to set.
     */
    public void setTempVendorAddressGeneratedIdentifier(String tempVendorAddressGeneratedIdentifier) {
        this.tempVendorAddressGeneratedIdentifier = tempVendorAddressGeneratedIdentifier;
    }



    /**
     * Gets the oldPayeeType attribute.
     * @return Returns the oldPayeeType.
     */
    public String getOldPayeeType() {
        return oldPayeeType;
    }

    /**
     * Sets the oldPayeeType attribute value.
     * @param oldPayeeType The oldPayeeType to set.
     */
    public void setOldPayeeType(String oldPayeeType) {
        this.oldPayeeType = oldPayeeType;
    }

    /**
     * determine whether the selected payee is an employee
     */
    public boolean isEmployee() {
        DisbursementVoucherDocument disbursementVoucherDocument = (DisbursementVoucherDocument) this.getDocument();
        return disbursementVoucherDocument.getDvPayeeDetail().isEmployee();
    }

    /**
     * determine whether the selected payee is a vendor
     */
    public boolean isVendor() {
        DisbursementVoucherDocument disbursementVoucherDocument = (DisbursementVoucherDocument) this.getDocument();
        return disbursementVoucherDocument.getDvPayeeDetail().isVendor();
    }

    /**
     * determine whether the selected payee is a customer
     */
    public boolean isCustomer() {
        DisbursementVoucherDocument disbursementVoucherDocument = (DisbursementVoucherDocument) this.getDocument();
        return disbursementVoucherDocument.getDvPayeeDetail().isCustomer();
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#shouldMethodToCallParameterBeUsed(java.lang.String,
     *      java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {
        if (StringUtils.equals(methodToCallParameterName, KRADConstants.DISPATCH_REQUEST_PARAMETER)) {
            if (this.getExcludedmethodToCall().contains(methodToCallParameterValue)) {
                return true;
            }
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#getExcludedmethodToCall()
     */
    @Override
    protected List<String> getExcludedmethodToCall() {
        List<String> execludedMethodToCall = super.getExcludedmethodToCall();
        execludedMethodToCall.add("printDisbursementVoucherCoverSheet");
        execludedMethodToCall.add("showTravelPerDiemLinks");

        return execludedMethodToCall;
    }

    /**
     * Gets the canExport attribute.
     * @return Returns the canExport.
     */
    public boolean isCanExport() {
        return canExport;
    }

    /**
     * Sets the canExport attribute value.
     * @param canExport The canExport to set.
     */
    public void setCanExport(boolean canExport) {
        this.canExport = canExport;
    }

    /**
     * RQ_AP_0760 : Ability to view disbursement information on the
     * Disbursement Voucher Document.
     *
     * This method composes the url to be used when we want to look up
     * the payment details from the disbursementInfo.tag.
     *
     * @return
     */
    public String getDisbursementInfoUrl() {
        final PaymentSourceHelperService paymentSourceHelperService = SpringContext.getBean(PaymentSourceHelperService.class);
        return paymentSourceHelperService.getDisbursementInfoUrl();
    }
}
