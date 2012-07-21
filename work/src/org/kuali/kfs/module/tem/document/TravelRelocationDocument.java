/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.JobClassification;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.RelocationReason;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Travel Relocation Document
 * 
 */
@Entity
@Table(name="TEM_RELO_DOC_T")
public class TravelRelocationDocument extends TEMReimbursementDocument implements AmountTotaling {
    
    protected static Logger LOG = Logger.getLogger(TravelRelocationDocument.class);
    
    private String fromAddress1;
    private String fromAddress2;
    private String fromCity;
    private String fromStateCode;
    private String fromCountryCode;
    private String toAddress1;
    private String toAddress2;
    private String toCity;
    private String toStateCode;
    private String toCountryCode;
    private String reasonCode;
    private RelocationReason reason;
    private String jobClsCode;
    private JobClassification jobClassification;
    private String titleCode;
    private String comments;
        
    public TravelRelocationDocument() {
    }
    
    public void setFromAddress1(String fromAddress1){
        this.fromAddress1 = fromAddress1;
    }
    
    @Column(name = "FRM_ADDR_LINE1", length = 50, nullable = true)
    public String getFromAddress1(){
        return this.fromAddress1;
    }
    
    public void setFromAddress2(String fromAddress2){
        this.fromAddress2 = fromAddress2;
    }
    
    @Column(name = "FRM_ADDR_LINE2", length = 50, nullable = true)
    public String getFromAddress2(){
        return this.fromAddress2;
    }
    
    public void setFromCity(String fromCity){
        this.fromCity = fromCity;
    }
    
    @Column(name = "FRM_CITY_NM", length = 30, nullable = true)
    public String getFromCity(){
        return this.fromCity;
    }
    
    public void setFromStateCode(String fromStateCode){
        this.fromStateCode = fromStateCode;
    }
    
    @Column(name = "FRM_STATE_CD", length = 40, nullable = true)
    public String getFromStateCode(){
        return this.fromStateCode;
    }
    
    public void setFromCountryCode(String fromCountryCode){
        this.fromCountryCode = fromCountryCode;
    }
    
    @Column(name = "FRM_COUNTRY_CD", length = 40, nullable = true)
    public String getFromCountryCode(){
        return this.fromCountryCode;
    }
    
    public void setToAddress1(String toAddress1){
        this.toAddress1 = toAddress1;
    }
    
    @Column(name = "TO_ADDR_LINE1", length = 50, nullable = true)
    public String getToAddress1(){
        return this.toAddress1;
    }
    
    public void setToAddress2(String toAddress2){
        this.toAddress2 = toAddress2;
    }
    
    @Column(name = "TO_ADDR_LINE2", length = 50, nullable = true)
    public String getToAddress2(){
        return this.toAddress2;
    }
    
    public void setToCity(String toCity){
        this.toCity = toCity;
    }
    
    @Column(name = "TO_CITY_NM", length = 30, nullable = true)
    public String getToCity(){
        return this.toCity;
    }
    
    public void setToStateCode(String toStateCode){
        this.toStateCode = toStateCode;
    }
    
    @Column(name = "TO_STATE_CD", length = 40, nullable = true)
    public String getToStateCode(){
        return this.toStateCode;
    }
    
    public void setToCountryCode(String toCountryCode){
        this.toCountryCode = toCountryCode;
    }
    
    @Column(name = "TO_COUNTRY_CD", length = 40, nullable = true)
    public String getToCountryCode(){
        return this.toCountryCode;
    }
    
    public void setReasonCode(String reasonCode){
        this.reasonCode = reasonCode;
    }
    
    @Column(name = "REASON_CD", length = 255, nullable = true)
    public String getReasonCode(){
        return this.reasonCode;
    }
    
    public void setReason(RelocationReason reason){
        this.reason = reason;
    }
    @JoinColumn(name="REASON_CD",nullable=true)
    public RelocationReason getReason(){
        return this.reason;
    }
    
    public void setJobClsCode(String jobClsCode){
        this.jobClsCode = jobClsCode;
    }
    
    @Column(name = "JOB_CLS_CD", length = 255, nullable = true)
    public String getJobClsCode(){
        return this.jobClsCode;
    }
    
    public void setJobClassification(JobClassification jobClassification){
        this.jobClassification = jobClassification;
    }
    
    @JoinColumn(name="JOB_CLS_CD",nullable=true)
    public JobClassification getJobClassification(){
        return this.jobClassification;
    }
    
    public void setTitleCode(String titleCode){
        this.titleCode = titleCode;
    }
    
    @Column(name = "TITLE_CD", length = 3, nullable = true)
    public String getTitleCode(){
        return this.titleCode;
    }
    
    public void setComments(String comments){
        this.comments = comments;
    }
    
    @Column(name = "COMMENTS", length = 255, nullable = true)
    public String getComments(){
        return this.comments;
    }
          
    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#prepareForSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        if(getFromCity() != null){
            getPrimaryDestination().setPrimaryDestinationName(getFromCity());
        }
        super.prepareForSave(event);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#initiateDocument()
     */
    @Override
    public void initiateDocument() {
        updateAppDocStatus(TemConstants.TravelRelocationStatusCodeKeys.IN_PROCESS);
        setActualExpenses(new ArrayList<ActualExpense>());
        setPerDiemExpenses(new ArrayList<PerDiemExpense>());

        getDocumentHeader().setDocumentDescription(TemConstants.PRE_FILLED_DESCRIPTION);
        if (getTraveler() == null) {
            setTraveler(new TravelerDetail());
            getTraveler().setTravelerTypeCode(TemConstants.EMP_TRAVELER_TYP_CD);
        }

        Calendar calendar = getDateTimeService().getCurrentCalendar();
        if (getTripBegin() == null) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            setTripBegin(new Timestamp(calendar.getTimeInMillis()));

        }
        if (getTripEnd() == null) {
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            setTripEnd(new Timestamp(calendar.getTimeInMillis()));
        }
        
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if(!getTravelDocumentService().isTravelArranger(currentUser)) {
            TEMProfile temProfile = getTravelService().findTemProfileByPrincipalId(currentUser.getPrincipalId());
            if(temProfile != null) {
                setTemProfile(temProfile);
            }
        }        
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        
        LOG.debug("Handling route status change");
        LOG.debug("Current route status is: " + statusChangeEvent.getNewRouteStatus());
               
        // in this case the status has already been updated and we need to update the internal status code
        String currStatus = getDocumentHeader().getWorkflowDocument().getRouteHeader().getAppDocStatus();
        
        if (ObjectUtils.isNotNull(currStatus)) {
            updateAppDocStatus(currStatus);
        }
        
        // disapproved - update internal app doc status
        if (KEWConstants.ROUTE_HEADER_DISAPPROVED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
            // update app doc status from the disapprove map
            updateAppDocStatus(TemConstants.TravelRelocationStatusCodeKeys.getDisapprovedAppDocStatusMap().get(getAppDocStatus()));
        }
        
        if (KEWConstants.ROUTE_HEADER_FINAL_CD.equals(statusChangeEvent.getNewRouteStatus()) 
                || KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus())) {

            LOG.debug("New route status is: " + statusChangeEvent.getNewRouteStatus());
            updateAppDocStatus(TravelRelocationStatusCodeKeys.RELO_MANAGER_APPROVED);
            if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
                if (getDocumentGrandTotal().isGreaterThan(KualiDecimal.ZERO)) {
                    getTravelDisbursementService().processTEMReimbursementDV(this);
                }
            }
        }
    }
    
    /**
     * Provides answers to the following splits: PurchaseWasReceived VendorIsEmployeeOrNonResidentAlien
     * 
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(TemWorkflowConstants.ACCOUNT_APPROVAL_REQUIRED))
            return requiresAccountApprovalRouting();
        if (nodeName.equals(TemWorkflowConstants.TAX_MANAGER_APPROVAL_REQUIRED))
            return requiresTaxManagerApprovalRouting();
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }
    
    /**
     * 
     * This method returns the preparer
     * @return
     */
    public String getPreparer(){
        return GlobalVariables.getUserSession().getPerson().getPrincipalName();
    }
    
    /**
     * 
     * This method return..
     * @return
     */
    @Override
    public KualiDecimal getTotalPaidAmountToVendor(){
        KualiDecimal totalPaidAmountToVendor = KualiDecimal.ZERO;
        return totalPaidAmountToVendor;
    }
    /**
     * 
     * This method...
     * @return
     */
    @Override
    public KualiDecimal getTotalPaidAmountToRequests(){
        KualiDecimal totalPaidAmountToRequests = KualiDecimal.ZERO;
        return totalPaidAmountToRequests;
    }
    
    /**
     * Given the <code>financialObjectCode</code>, determine the total of the 
     * {@link SourceAccountingLine} instances with that <code>financialObjectCode</code>
     *
     * @param financialObjectCode to search for total on
     * @return @{link KualiDecimal} with total value for {@link AccountingLines} with <code>finanncialObjectCode</code>
     */
    @Override
    public KualiDecimal getTotalFor(final String financialObjectCode) {
        KualiDecimal retval = KualiDecimal.ZERO;
        
        LOG.debug("Getting total for " + financialObjectCode);
        
        for (final AccountingLine line : (List<AccountingLine>) getSourceAccountingLines()) {
            LOG.debug("Comparing " + financialObjectCode + " to " + line.getObjectCode().getCode());
            if (line.getObjectCode().getCode().equals(financialObjectCode)) {
                retval = retval.add(line.getAmount());
            }
        }
        
        return retval;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.TEMReimbursementDocument#populateDisbursementVoucherFields(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    @Override
    public void populateDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument){
        super.populateDisbursementVoucherFields(disbursementVoucherDocument);
        
        final String paymentReasonCode = getParameterService().getParameterValue(TemParameterConstants.TEM_RELOCATION.class, TravelRelocationParameters.RELO_REIMBURSEMENT_DV_REASON_CODE);
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(paymentReasonCode);
        
        final String paymentLocationCode = getParameterService().getParameterValue(TemParameterConstants.TEM_RELOCATION.class,TravelRelocationParameters.RELOCATION_DOCUMENTATION_LOCATION_CODE);
        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(paymentLocationCode);
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#populateRequisitionFields(org.kuali.kfs.module.purap.document.RequisitionDocument, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void populateRequisitionFields(RequisitionDocument reqsDocument, TravelDocument document) {
        super.populateRequisitionFields(reqsDocument, document);
        TravelRelocationDocument reloDocument = (TravelRelocationDocument) document;
        reqsDocument.getDocumentHeader().setDocumentDescription("Requisition for Moving And Relocation");
        reqsDocument.getDocumentHeader().setOrganizationDocumentNumber(reloDocument.getTravelDocumentIdentifier());
        Calendar calendar = getDateTimeService().getCurrentCalendar();
        calendar.setTime(reloDocument.getTripBegin());
        reqsDocument.setPostingYear(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getReportPurpose()
     */
    @Override
    public String getReportPurpose() {
        return reason != null? reason.getReloReasonName() : null;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#populateVendorPayment(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    @Override
    public void populateVendorPayment(DisbursementVoucherDocument disbursementVoucherDocument) {
        super.populateVendorPayment(disbursementVoucherDocument);
        
        disbursementVoucherDocument.setDisbVchrPaymentMethodCode(TemConstants.DisbursementVoucherPaymentMethods.CHECK_ACH_PAYMENT_METHOD_CODE);
        String locationCode = getParameterService().getParameterValue(TemParameterConstants.TEM_RELOCATION.class, TravelRelocationParameters.RELOCATION_DOCUMENTATION_LOCATION_CODE);
        String startDate = new SimpleDateFormat("MM/dd/yyyy").format(getTripBegin());
        String endDate = new SimpleDateFormat("MM/dd/yyyy").format(getTripEnd());
        String checkStubText = getTravelDocumentIdentifier() + ", " + startDate + " - " + endDate + ", " + getToCity() + ", " + getToStateCode();
        
        disbursementVoucherDocument.setDisbVchrPaymentMethodCode(TemConstants.DisbursementVoucherPaymentMethods.CHECK_ACH_PAYMENT_METHOD_CODE);
        
        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(locationCode);
        disbursementVoucherDocument.setDisbVchrCheckStubText(checkStubText);
    }
    
}
