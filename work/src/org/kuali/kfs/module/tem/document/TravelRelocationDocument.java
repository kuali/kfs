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

import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;
import static org.kuali.kfs.module.tem.util.BufferedLogger.error;
import static org.kuali.kfs.module.tem.util.BufferedLogger.logger;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelEntertainmentParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.JobClassification;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.RelocationReason;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelRelocationService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.DV_PAYEE_TYPE_CODE_V;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.PARAM_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.RELOCATION_DOCUMENTATION_LOCATION_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.VENDOR_PAYMENT_DV_REASON_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.AWAIT_RELO_MANAGER;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.AWAIT_TAX_MANAGER;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.AWAIT_EXECUTIVE;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.AWAIT_AWARD;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.AWAIT_FISCAL;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.AWAIT_ORG;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.AWAIT_SUB;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.DAPRVD_RELO_MANAGER;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.DAPRVD_TAX_MANAGER;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.DAPRVD_EXECUTIVE;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.DAPRVD_AWARD;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.DAPRVD_FISCAL;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.DAPRVD_ORG;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.DAPRVD_SUB;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.RELO_MANAGER_APPROVED;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_RELO_DV_IN_ACTION_LIST;
import static org.kuali.rice.kns.util.GlobalVariables.getMessageList;

/**
 * Travel Relocation Document
 * 
 */
@Entity
@Table(name="TEM_RELO_DOC_T")
public class TravelRelocationDocument extends TEMReimbursementDocument implements AmountTotaling {
    
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
        if (this.getTraveler() == null) {
            this.setTraveler(new TravelerDetail());
            this.getTraveler().setTravelerTypeCode(TemConstants.EMP_TRAVELER_TYP_CD);
        }

        Calendar calendar = getDateTimeService().getCurrentCalendar();
        if (this.getTripBegin() == null) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            setTripBegin(new Timestamp(calendar.getTimeInMillis()));

        }
        if (this.getTripEnd() == null) {
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            setTripEnd(new Timestamp(calendar.getTimeInMillis()));
        }
        
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if(!getTravelDocumentService().isTravelArranger(currentUser, null)) {
            TEMProfile temProfile = getTravelService().findTemProfileByPrincipalId(currentUser.getPrincipalId());
            if(temProfile != null) {
                setTemProfile(temProfile);
            }
        }        
    }
    
    /**
     * 
     * @return
     */
    public TEMProfile retrieveTravelerProfile(){
        return getTravelService().findTemProfileByPrincipalId(getTraveler().getPrincipalId());
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        
        debug("Handling route status change");
        debug("route status is ", statusChangeEvent.getNewRouteStatus());
               
        // in this case the status has already been updated and we need to update the internal status code
        String currStatus = getDocumentHeader().getWorkflowDocument().getRouteHeader().getAppDocStatus();
        
        if (ObjectUtils.isNotNull(currStatus)) {
            updateAppDocStatus(currStatus);
        }
        
        if (KEWConstants.ROUTE_HEADER_DISAPPROVED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
         // first we need to see where we were so we can change the app doc status
            String currAppDocStatus = getAppDocStatus();
            if (currAppDocStatus.equals(AWAIT_FISCAL)) {
                updateAppDocStatus(DAPRVD_FISCAL);
            }
            if (currAppDocStatus.equals(AWAIT_ORG)) {
                updateAppDocStatus(DAPRVD_ORG);
            }
            if (currAppDocStatus.equals(AWAIT_SUB)) {
                updateAppDocStatus(DAPRVD_SUB);
            }
            if (currAppDocStatus.equals(AWAIT_AWARD)) {
                updateAppDocStatus(DAPRVD_AWARD);
            }
            if (currAppDocStatus.equals(AWAIT_EXECUTIVE)) {
                updateAppDocStatus(DAPRVD_EXECUTIVE);
            }
            if (currAppDocStatus.equals(AWAIT_TAX_MANAGER)) {
                updateAppDocStatus(DAPRVD_TAX_MANAGER);
            }
            if (currAppDocStatus.equals(AWAIT_RELO_MANAGER)) {
                updateAppDocStatus(DAPRVD_RELO_MANAGER);
            }
        }
        
        if (KEWConstants.ROUTE_HEADER_FINAL_CD.equals(statusChangeEvent.getNewRouteStatus()) || KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
            debug("New route status is ", statusChangeEvent.getNewRouteStatus());
            
            updateAppDocStatus(RELO_MANAGER_APPROVED);
            
            if(this.getDocumentHeader().getWorkflowDocument().stateIsProcessed()){            
            
                if(getDocumentGrandTotal().isGreaterThan(KualiDecimal.ZERO)){
                    try{
                        getTravelRelocationService().createDVReimbursementDocument(this);
                    }
                    catch(Exception ex){
                        error("Could not spawn DV for reimbursement.", ex.getMessage());
                        if (logger().isDebugEnabled()) {
                            ex.printStackTrace();
                        }
                    }
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
        
        debug("Getting total for ", financialObjectCode);
        
        for (final AccountingLine line : (List<AccountingLine>) getSourceAccountingLines()) {
            debug("Comparing ", financialObjectCode, " to ", line.getObjectCode().getCode());
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
        
        disbursementVoucherDocument.setDisbVchrCheckStubText(StringUtils.defaultString(getDocumentTitle()));               
        disbursementVoucherDocument.getDocumentHeader().setDocumentDescription("Generated for RELO doc: " + StringUtils.defaultString(getDocumentTitle(), getTravelDocumentIdentifier()));
        getTravelDocumentService().trimFinancialSystemDocumentHeader(disbursementVoucherDocument.getDocumentHeader());
        
        disbursementVoucherDocument.setDisbVchrPaymentMethodCode(TemConstants.DisbursementVoucherPaymentMethods.CHECK_ACH_PAYMENT_METHOD_CODE);
        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, RELOCATION_DOCUMENTATION_LOCATION_CODE));
        
        final String dvReasonCode = getParameterService().getParameterValue(PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE, VENDOR_PAYMENT_DV_REASON_CODE);
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(dvReasonCode);
        //disbursementVoucherDocument.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(DV_PAYEE_TYPE_CODE_V);
        //disbursementVoucherDocument.setDisbVchrCheckTotalAmount(this.getDocumentGrandTotal());
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
     * 
     * @return
     */
    protected TravelRelocationService getTravelRelocationService() {
        return SpringContext.getBean(TravelRelocationService.class);
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
        String locationCode = getParameterService().getParameterValue(PARAM_NAMESPACE, TravelRelocationParameters.PARAM_DTL_TYPE, TravelRelocationParameters.RELOCATION_DOCUMENTATION_LOCATION_CODE);
        String startDate = new SimpleDateFormat("MM/dd/yyyy").format(this.getTripBegin());
        String endDate = new SimpleDateFormat("MM/dd/yyyy").format(this.getTripEnd());
        String checkStubText = this.getTravelDocumentIdentifier() + ", " + startDate + " - " + endDate + ", " + this.getToCity() + ", " + this.getToStateCode();
        
        disbursementVoucherDocument.setDisbVchrPaymentMethodCode(TemConstants.DisbursementVoucherPaymentMethods.CHECK_ACH_PAYMENT_METHOD_CODE);
        
        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(locationCode);
        disbursementVoucherDocument.setDisbVchrCheckStubText(checkStubText);
    }
    
}
