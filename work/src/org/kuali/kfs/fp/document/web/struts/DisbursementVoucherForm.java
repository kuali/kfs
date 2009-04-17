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
package org.kuali.kfs.fp.document.web.struts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeExpense;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPreConferenceRegistrant;
import org.kuali.kfs.fp.businessobject.TravelPerDiem;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherCoverSheetService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.format.SimpleBooleanFormatter;

/**
 * This class is the action form for the Disbursement Voucher.
 */
public class DisbursementVoucherForm extends KualiAccountingDocumentFormBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherForm.class);

    private static final long serialVersionUID = 1L;

    private String payeeIdNumber;
    private String vendorHeaderGeneratedIdentifier = StringUtils.EMPTY;
    private String vendorDetailAssignedIdentifier = StringUtils.EMPTY;
    private String vendorAddressGeneratedIdentifier;
    private boolean hasMultipleAddresses = false;

    private DisbursementVoucherNonEmployeeExpense newNonEmployeeExpenseLine;
    private DisbursementVoucherNonEmployeeExpense newPrePaidNonEmployeeExpenseLine;
    private DisbursementVoucherPreConferenceRegistrant newPreConferenceRegistrantLine;
    private String wireChargeMessage;

    /**
     * Override reset to reset checkboxes if they are present on the requesting page
     * 
     * @see org.kuali.core.web.struts.form.KualiDocumentFormBase#reset(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        // TODO: remove the method after identifying the problem happening on CNV
        this.setMethodToCall(null);
        this.setRefreshCaller(null);
        this.setAnchor(null);
        this.setCurrentTabIndex(0);

        if (request.getParameter("checkboxToReset") != null) {
            String[] checkboxesToReset = request.getParameterValues("checkboxToReset");
            
            for (String propertyName : checkboxesToReset) {
                LOG.info("=============" + propertyName);
            }
            
            for (String propertyName : checkboxesToReset) {
                try {
                    LOG.info("=============" + propertyName);
                    
                    if(StringUtils.isNotBlank(propertyName)) {                    
                        PropertyUtils.setNestedProperty(this, propertyName, false);
                    }
                }
                catch (Exception e1) {
                    LOG.info("=============", e1);
                }
            }
        }
    }

    /**
     * Constructs a DisbursementVoucherForm.java.
     */
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
        return SpringContext.getBean(ParameterService.class).getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.TRAVEL_PER_DIEM_MESSAGE_PARM_NM);
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
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#shouldMethodToCallParameterBeUsed(java.lang.String,
     *      java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {
        if (StringUtils.equals(methodToCallParameterName, KNSConstants.DISPATCH_REQUEST_PARAMETER)) {
            if (this.getExcludedmethodToCall().contains(methodToCallParameterValue)) {
                return true;
            }
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#getExcludedmethodToCall()
     */
    protected List<String> getExcludedmethodToCall() {
        List<String> execludedMethodToCall = super.getExcludedmethodToCall();
        execludedMethodToCall.add("printDisbursementVoucherCoverSheet");
        execludedMethodToCall.add("showTravelPerDiemLinks");

        return execludedMethodToCall;
    }
}
