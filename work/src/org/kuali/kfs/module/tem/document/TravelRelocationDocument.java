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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.JobClassification;
import org.kuali.kfs.module.tem.businessobject.RelocationReason;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

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
     * This method updates both the internal travel document status value and the app doc status
     * in the document header of workflow
     *
     * @param status
     */
    @Override
    public boolean updateAppDocStatus(String status) {
        boolean updated = false;

        //using the parent's update app doc status logic
        updated = super.updateAppDocStatus(status);

        if (!updated && (status.equals(TravelRelocationStatusCodeKeys.RELO_MANAGER_APPROVED))){
            setAppDocStatus(status);
            updated = saveAppDocStatus();
        }
        return updated;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#initiateDocument()
     */
    @Override
    public void initiateDocument() {
        super.initiateDocument();
        setAppDocStatus(TravelRelocationStatusCodeKeys.IN_PROCESS);
        getTravelPayment().setDocumentationLocationCode(getParameterService().getParameterValueAsString(TravelRelocationDocument.class, TravelParameters.DOCUMENTATION_LOCATION_CODE,
                getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class,TravelParameters.DOCUMENTATION_LOCATION_CODE)));
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChange)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        if (DocumentStatus.FINAL.getCode().equals(statusChangeEvent.getNewRouteStatus())
                || DocumentStatus.PROCESSED.getCode().equals(statusChangeEvent.getNewRouteStatus())) {

            LOG.debug("New route status is: " + statusChangeEvent.getNewRouteStatus());
            updateAppDocStatus(TravelRelocationStatusCodeKeys.RELO_MANAGER_APPROVED);
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(TemWorkflowConstants.REQUIRES_TRAVELER_REVIEW)) {
            return requiresTravelerApprovalRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.ACCOUNT_APPROVAL_REQUIRED)) {
            return requiresAccountApprovalRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.SPECIAL_REQUEST)) {
            return requiresSpecialRequestReviewRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.TAX_MANAGER_APPROVAL_REQUIRED)) {
            return requiresTaxManagerApprovalRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.SEPARATION_OF_DUTIES)) {
            return requiresSeparationOfDutiesRouting();
        }
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

        final String paymentReasonCode = getParameterService().getParameterValueAsString(TravelRelocationDocument.class, TravelRelocationParameters.PAYMENT_REASON_CODE);
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(paymentReasonCode);

        final String paymentLocationCode = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class,TemConstants.TravelParameters.DOCUMENTATION_LOCATION_CODE);
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

        String locationCode = getParameterService().getParameterValueAsString(TravelRelocationDocument.class, TravelParameters.DOCUMENTATION_LOCATION_CODE, getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class,TravelParameters.DOCUMENTATION_LOCATION_CODE));
        String startDate = new SimpleDateFormat("MM/dd/yyyy").format(getTripBegin());
        String endDate = new SimpleDateFormat("MM/dd/yyyy").format(getTripEnd());
        String checkStubText = getTravelDocumentIdentifier() + ", " + startDate + " - " + endDate + ", " + getToCity() + ", " + getToStateCode();

        disbursementVoucherDocument.setDisbVchrPaymentMethodCode(TemConstants.DisbursementVoucherPaymentMethods.CHECK_ACH_PAYMENT_METHOD_CODE);
        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(locationCode);
        disbursementVoucherDocument.setDisbVchrCheckStubText(checkStubText);
    }

    /**
     * Relocation document always require tax manager approval
     *
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#requiresTaxManagerApprovalRouting()
     */
    @Override
    protected boolean requiresTaxManagerApprovalRouting() {
        return true;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#getDisapprovedAppDocStatusMap()
     */
    @Override
    public Map<String, String> getDisapprovedAppDocStatusMap() {
        return TravelRelocationStatusCodeKeys.getDisapprovedAppDocStatusMap();
    }

    /**
     * Returns RECA
     * @see org.kuali.kfs.module.tem.document.TEMReimbursementDocument#getAchCheckDocumentType()
     */
    @Override
    public String getAchCheckDocumentType() {
        return TemConstants.TravelDocTypes.RELOCATION_CHECK_ACH_DOCUMENT;
    }

    /**
     * Returns REWF
     * @see org.kuali.kfs.module.tem.document.TEMReimbursementDocument#getWireTransferOrForeignDraftDocumentType()
     */
    @Override
    public String getWireTransferOrForeignDraftDocumentType() {
        return TemConstants.TravelDocTypes.RELOCATION_WIRE_OR_FOREIGN_DRAFT_DOCUMENT;
    }

    /**
     * Returns "R-"
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#getTripIdPrefix()
     */
    @Override
    protected String getTripIdPrefix() {
        return TemConstants.TripIdPrefix.RELOCATION_PREFIX;
    }

}
