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
package org.kuali.kfs.module.ar.document;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgencyAddress;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBill;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsInvoiceTemplate;
import org.kuali.kfs.integration.cg.ContractsAndGrantsMilestone;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService;
import org.kuali.kfs.integration.cg.ContractsGrantsAwardInvoiceAccountInformation;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequency;
import org.kuali.kfs.module.ar.businessobject.AwardAccountObjectCodeTotalBilled;
import org.kuali.kfs.module.ar.businessobject.ContractsAndGrantsCategories;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.businessobject.FinalDisposition;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAgencyAddressDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;
import org.kuali.kfs.module.ar.businessobject.ReferralType;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.PdfFormFillerUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.bo.Attachment;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.AttachmentService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Contracts Grants Invoice document extending Customer Invoice document.
 */

public class ContractsGrantsInvoiceDocument extends CustomerInvoiceDocument {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceDocument.class);
    private Long proposalNumber;
    private KualiDecimal paymentAmount = KualiDecimal.ZERO;
    private KualiDecimal balanceDue = KualiDecimal.ZERO;
    private List<InvoiceDetail> invoiceDetails;
    private List<Event> events;
    private List<InvoiceDetail> directCostInvoiceDetails;
    private List<InvoiceDetail> inDirectCostInvoiceDetails;
    private List<InvoiceDetail> totalInvoiceDetails;
    private List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes;
    private List<InvoiceAgencyAddressDetail> agencyAddressDetails;
    private List<InvoiceAccountDetail> accountDetails;
    private InvoiceGeneralDetail invoiceGeneralDetail;
    private List<InvoiceMilestone> invoiceMilestones;
    private List<InvoiceBill> invoiceBills;
    private List<InvoiceSuspensionCategory> invoiceSuspensionCategories;
    private ContractsAndGrantsCGBAward award;
    private static final SimpleDateFormat FILE_NAME_TIMESTAMP = new SimpleDateFormat("MM-dd-yyyy");

    protected String locCreationType;// To categorize the CG Invoices based on Award LOC Type
    protected String letterOfCreditFundGroupCode;
    protected String letterOfCreditFundCode;

    private String referralTypeCode;
    private String finalDispositionCode;

    private ReferralType referralType;
    private FinalDisposition finalDisposition;

    public java.util.Date dateReportProcessed;
    public Date paymentDate;
    public String markedForProcessing;
    private final String REQUIRES_APPROVAL_SPLIT = "RequiresApprovalSplit";
    private boolean showEvents = true;

    /**
     * Default constructor.
     */
    public ContractsGrantsInvoiceDocument() {

        agencyAddressDetails = new ArrayList<InvoiceAgencyAddressDetail>();
        invoiceDetails = new ArrayList<InvoiceDetail>();
        events = new ArrayList<Event>();
        directCostInvoiceDetails = new ArrayList<InvoiceDetail>();
        inDirectCostInvoiceDetails = new ArrayList<InvoiceDetail>();
        totalInvoiceDetails = new ArrayList<InvoiceDetail>();
        accountDetails = new ArrayList<InvoiceAccountDetail>();
        invoiceMilestones = new ArrayList<InvoiceMilestone>();
        invoiceBills = new ArrayList<InvoiceBill>();
        invoiceDetailAccountObjectCodes = new ArrayList<InvoiceDetailAccountObjectCode>();
        invoiceSuspensionCategories = new ArrayList<InvoiceSuspensionCategory>();
    }

    /**
     * Gets the finalizable attribute.
     *
     * @return Returns the finalizable.
     */
    public boolean isFinalizable() {
        if (this == null) {
            return false;
        }
        return this.getDocumentHeader().getWorkflowDocument().getDateCreated().isAfter(this.getAward().getAwardEndingDate().getTime());
    }

    /**
     * This method returns true if this is a correction document
     *
     * @return is Error Correction Document
     */
    public boolean isCorrectionDocument() {
        return !StringUtils.isEmpty(this.getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber());
    }

    /**
     * Gets the markedForProcessing attribute.
     *
     * @return Returns the markedForProcessing.
     */
    public String getMarkedForProcessing() {
        return markedForProcessing;
    }

    /**
     * Sets the markedForProcessing attribute value.
     *
     * @param markedForProcessing The markedForProcessing to set.
     */

    public void setMarkedForProcessing(String markedForProcessing) {
        this.markedForProcessing = markedForProcessing;
    }

    /**
     * Gets the dateReportProcessed attribute.
     *
     * @return Returns the dateReportProcessed.
     */
    public java.util.Date getDateReportProcessed() {
        return dateReportProcessed;
    }

    /**
     * Sets the dateReportProcessed attribute value.
     *
     * @param dateReportProcessed The dateReportProcessed to set.
     */
    public void setDateReportProcessed(java.util.Date date) {
        this.dateReportProcessed = date;
    }

    /**
     * This method updates the awardAccounts when the final invoice is generated.
     */
    public void doWhenFinalInvoice() {
        if (this.getInvoiceGeneralDetail().isFinalBill()) {
            Iterator it = accountDetails.iterator();
            while (it.hasNext()) {
                InvoiceAccountDetail id = (InvoiceAccountDetail) it.next();
                this.setAwardAccountFinalBilledValue(id, true);
            }
        }
    }

    /**
     * This method generates the attached invoices for the agency addresses in the Contracts and Grants Invoice Document.
     */
    private void generateInvoicesForAgencyAddresses() {
        ContractsAndGrantsInvoiceTemplate invoiceTemplate = null;
        Iterator<InvoiceAgencyAddressDetail> iterator = agencyAddressDetails.iterator();
        while (iterator.hasNext()) {
            InvoiceAgencyAddressDetail invoiceAgencyAddressDetail = iterator.next();
            byte[] reportStream;
            byte[] copyReportStream;
            // validating the invoice template
            if (ObjectUtils.isNotNull(invoiceAgencyAddressDetail.getPreferredAgencyInvoiceTemplateCode())) {

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("invoiceTemplateCode", invoiceAgencyAddressDetail.getPreferredAgencyInvoiceTemplateCode());
                invoiceTemplate = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsInvoiceTemplate.class).getExternalizableBusinessObject(ContractsAndGrantsInvoiceTemplate.class, map);

            }
            else if (ObjectUtils.isNotNull(invoiceAgencyAddressDetail.getAgencyInvoiceTemplateCode())) {

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("invoiceTemplateCode", invoiceAgencyAddressDetail.getAgencyInvoiceTemplateCode());
                invoiceTemplate = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsInvoiceTemplate.class).getExternalizableBusinessObject(ContractsAndGrantsInvoiceTemplate.class, map);
            }
            else {
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_FILE_UPLOAD_NO_PDF_FILE_SELECTED_FOR_SAVE, ArConstants.ACTIVE_INVOICE_TEMPLATE_ERROR);
            }

            // generate invoices from templates.
            if (ObjectUtils.isNotNull(invoiceTemplate) && invoiceTemplate.isActive() && ObjectUtils.isNotNull(invoiceTemplate.getFilepath())) {
                File templateFile = new File(invoiceTemplate.getFilepath());
                File outputDirectory = null;
                String outputFileName;
                try {
                    // genrating original invoice
                    outputFileName = this.getDocumentNumber() + "_" + invoiceAgencyAddressDetail.getAgencyAddressName() + FILE_NAME_TIMESTAMP.format(new Date()) + ArConstants.TemplateUploadSystem.EXTENSION;
                    Map<String, String> replacementList = getTemplateParameterList();
                    ContractsAndGrantsAgencyAddress address;
                    Map<String, Object> primaryKeys = new HashMap<String, Object>();
                    primaryKeys.put(KFSPropertyConstants.AGENCY_NUMBER, invoiceAgencyAddressDetail.getAgencyNumber());
                    primaryKeys.put("agencyAddressIdentifier", invoiceAgencyAddressDetail.getAgencyAddressIdentifier());
                    address = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObject(ContractsAndGrantsAgencyAddress.class, primaryKeys);
                    String fullAddress = "";
                    if (StringUtils.isNotEmpty(address.getAgencyLine1StreetAddress())) {
                        fullAddress += returnProperStringValue(address.getAgencyLine1StreetAddress()) + "\n";
                    }
                    if (StringUtils.isNotEmpty(address.getAgencyLine2StreetAddress())) {
                        fullAddress += returnProperStringValue(address.getAgencyLine2StreetAddress()) + "\n";
                    }
                    if (StringUtils.isNotEmpty(address.getAgencyCityName())) {
                        fullAddress += returnProperStringValue(address.getAgencyCityName());
                    }
                    if (StringUtils.isNotEmpty(address.getAgencyStateCode())) {
                        fullAddress += " " + returnProperStringValue(address.getAgencyStateCode());
                    }
                    if (StringUtils.isNotEmpty(address.getAgencyZipCode())) {
                        fullAddress += "-" + returnProperStringValue(address.getAgencyZipCode());
                    }
                    replacementList.put("#agency.fullAddress", returnProperStringValue(fullAddress));
                    reportStream = PdfFormFillerUtil.populateTemplate(templateFile, replacementList, "");
                    // creating and saving the original note with an attachment
                    if (ObjectUtils.isNotNull(this.getInvoiceGeneralDetail()) && this.getInvoiceGeneralDetail().isFinalBill()) {
                        reportStream = PdfFormFillerUtil.createFinalmarkOnFile(reportStream, "FINAL INVOICE");
                    }
                    Note note = new Note();
                    note.setNotePostedTimestampToCurrent();
                    note.setNoteText("Auto-generated invoice for Agency Address-" + this.getDocumentNumber() + "-" + invoiceAgencyAddressDetail.getAgencyAddressName());
                    note.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
                    note = SpringContext.getBean(NoteService.class).createNote(note, this, null);
                    Attachment attachment = SpringContext.getBean(AttachmentService.class).createAttachment(note, outputFileName, ArConstants.TemplateUploadSystem.TEMPLATE_MIME_TYPE, reportStream.length, new ByteArrayInputStream(reportStream), "");
                    // adding attachment to the note
                    note.setAttachment(attachment);
                    SpringContext.getBean(NoteService.class).save(note);
                    attachment.setNoteIdentifier(note.getNoteIdentifier());
                    SpringContext.getBean(BusinessObjectService.class).save(attachment);
                    this.addNote(note);

                    // generating Copy invoice
                    outputFileName = this.getDocumentNumber() + "_" + invoiceAgencyAddressDetail.getAgencyAddressName() + FILE_NAME_TIMESTAMP.format(new Date()) + "_COPY" + ArConstants.TemplateUploadSystem.EXTENSION;
                    copyReportStream = PdfFormFillerUtil.createWatermarkOnFile(reportStream, "COPY");
                    // creating and saving the copy note with an attachment
                    Note copyNote = new Note();
                    copyNote.setNotePostedTimestampToCurrent();
                    copyNote.setNoteText("Auto-generated invoice (Copy) for Agency Address-" + this.getDocumentNumber() + "-" + invoiceAgencyAddressDetail.getAgencyAddressName());
                    copyNote.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
                    copyNote = SpringContext.getBean(NoteService.class).createNote(copyNote, this, null);
                    Attachment copyAttachment = SpringContext.getBean(AttachmentService.class).createAttachment(copyNote, outputFileName, ArConstants.TemplateUploadSystem.TEMPLATE_MIME_TYPE, copyReportStream.length, new ByteArrayInputStream(copyReportStream), "");
                    // adding attachment to the note
                    copyNote.setAttachment(copyAttachment);
                    SpringContext.getBean(NoteService.class).save(copyNote);
                    copyAttachment.setNoteIdentifier(copyNote.getNoteIdentifier());
                    SpringContext.getBean(BusinessObjectService.class).save(copyAttachment);
                    this.addNote(copyNote);
                    invoiceAgencyAddressDetail.setNoteId(note.getNoteIdentifier());
                    // saving the note to the document header
                    SpringContext.getBean(DocumentService.class).updateDocument(this);
                }
                catch (IOException ex) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_FILE_UPLOAD_NO_PDF_FILE_SELECTED_FOR_SAVE, ArConstants.INVOICE_TEMPLATE_NOT_FOUND_ERROR + invoiceTemplate.getInvoiceTemplateCode() + ".");
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else {
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_FILE_UPLOAD_NO_PDF_FILE_SELECTED_FOR_SAVE, ArConstants.ACTIVE_INVOICE_TEMPLATE_ERROR);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.CustomerInvoiceDocument#prepareForSave()
     */

    @Override
    public void prepareForSave() {
        super.prepareForSave();
        // To do a recalculate of current expenditures in invoice details section so that the totals get affected properly.

        // To be performed whenever the document is saved only for awards without Milestones, Bills or LOC Billing
        if (!this.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE) && !this.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) && !this.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.LOC_BILLING_SCHEDULE_CODE)) {
            ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);

            contractsGrantsInvoiceDocumentService.recalculateNewTotalBilled(this);


        }
    }


    /**
     * @see org.kuali.kfs.module.ar.document.CustomerInvoiceDocument#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @SuppressWarnings("null")
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        // To set the status of the document to award account.
        setAwardAccountInvoiceDocumentStatus(this.getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus());
        // performed only when document is in final state
        if (getDocumentHeader().getWorkflowDocument().isFinal()) {


            // update award accounts to final billed
            this.doWhenFinalInvoice();
            if (this.isInvoiceReversal()) { // Invoice correction process when corrected invoice goes to FINAL
                try {
                    this.getInvoiceGeneralDetail().setFinalBill(false);
                    ContractsGrantsInvoiceDocument invoice = (ContractsGrantsInvoiceDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(this.getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber());
                    if (ObjectUtils.isNotNull(invoice)) {
                        invoice.setInvoiceDueDate(new java.sql.Date(new Date().getTime()));
                        invoice.getInvoiceGeneralDetail().setFinalBill(false);
                        SpringContext.getBean(DocumentService.class).updateDocument(invoice);
                        // update correction to the AwardAccount Objects since the Invoice was unmarked as Final
                        invoice.updateUnfinalizationToAwardAccount();
                        // Update invoice, award account and award with the last billed Date.
                        String invoiceStatus = "CORRECTED";
                        this.updateLastBilledDate(invoiceStatus);// Setting award account and award last billed date
                        this.getInvoiceGeneralDetail().setLastBilledDate(null);// Set invoice last billed date to null.

                        if (invoice.getInvoiceGeneralDetail().getBillingFrequency().equals(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE)) {
                            invoice.correctMilestones();
                        }
                        else if (invoice.getInvoiceGeneralDetail().getBillingFrequency().equals(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
                            invoice.correctBills();
                        }
                    }
                    else {
                        GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CORRECTED_INVOICE_NOT_FOUND_ERROR, ArConstants.CORRECTED_INVOICE_NOT_FOUND_ERROR);
                    }
                }
                catch (WorkflowException ex) {
                    ex.printStackTrace();
                }
            }
            else {
                // update Milestones and Bills when invoice goes to final state
                this.updateBillsAndMilestones(KFSConstants.ParameterValues.STRING_YES);
                // Update invoice, award account and award with the last billed Date.
                String invoiceStatus = "FINAL";
                this.updateLastBilledDate(invoiceStatus);
            }


            ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
            contractsGrantsInvoiceDocumentService.addToAccountObjectCodeBilledTotal(invoiceDetailAccountObjectCodes);

            // generate the invoices from templates
            this.generateInvoicesForAgencyAddresses();
        }


    }

    /**
     * This method sets the last billed date to Award and Award Account objects based on the status of the invoice. Final or
     * Corrected.
     *
     * @param invoiceStatus
     */
    public void updateLastBilledDate(String invoiceStatus) {
        // To calculate and update Last Billed Date based on the status of the invoice. Final or Corrected.
        // 1. Set last Billed Date to Award Accounts

        Iterator<InvoiceAccountDetail> iterator = accountDetails.iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = iterator.next();
            this.calculateAwardAccountLastBilledDate(id, invoiceStatus, this.getInvoiceGeneralDetail().getLastBilledDate());
        }

        // 2. Set last Billed to Award = least of last billed date of award account.
        java.sql.Date awdLastBilledDate;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, getProposalNumber());
        ContractsAndGrantsCGBAward award = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAward.class).getExternalizableBusinessObject(ContractsAndGrantsCGBAward.class, map);
        ContractsAndGrantsCGBAwardAccount awardAccount = award.getActiveAwardAccounts().get(0);
        awdLastBilledDate = award.getActiveAwardAccounts().get(0).getCurrentLastBilledDate();
        for (int i = 0; i < award.getActiveAwardAccounts().size(); i++) {
            if (ObjectUtils.isNull(awdLastBilledDate) || ObjectUtils.isNull(award.getActiveAwardAccounts().get(i).getCurrentLastBilledDate())) {
                // The dates would be null if the user is correcting the first invoice created for the award. Then the award last
                // billed date should also be null.
                awdLastBilledDate = null;
            }
            else if (awdLastBilledDate.after(award.getActiveAwardAccounts().get(i).getCurrentLastBilledDate())) {
                awdLastBilledDate = award.getActiveAwardAccounts().get(i).getCurrentLastBilledDate();
            }
        }

        // To set last billed Date to award.
        SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setLastBilledDateToAward(award.getProposalNumber(), awdLastBilledDate);
    }

    /**
     * This method updates the AwardAccount object's last billed Variable with the value provided
     *
     * @param id
     * @param value
     */
    private void calculateAwardAccountLastBilledDate(InvoiceAccountDetail id, String invoiceStatus, java.sql.Date lastBilledDate) {
        Map<String, Object> mapKey = new HashMap<String, Object>();
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.getProposalNumber());
        // To set previous and current last Billed Date for award account .
        SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setLastBilledDateToAwardAccount(mapKey, invoiceStatus, lastBilledDate);

    }

    /**
     * This method updates the Bills and Milestone objects isItBilles Field.
     *
     * @param string
     */
    protected void updateBillsAndMilestones(String string) {
        updateMilestonesIsItBilled(string);
        updateBillsIsItBilled(string);
    }

    /**
     * Update Milestone objects isItBilled value.
     *
     * @param string
     */
    protected void updateMilestonesIsItBilled(String string) {
        // Get a list of invoiceMilestones from the CGIN document. Then search for the actual Milestone object in this list through
        // dao
        // Finally, set these milestones to billed
        List<InvoiceMilestone> invoiceMilestones = this.getInvoiceMilestones();
        if (invoiceMilestones != null && !invoiceMilestones.isEmpty()) {

            List<Long> milestoneIds = new ArrayList<Long>();
            for (InvoiceMilestone invoiceMilestone : invoiceMilestones) {
                milestoneIds.add(invoiceMilestone.getMilestoneIdentifier());
            }

            try {
                ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
                contractsGrantsInvoiceDocumentService.retrieveAndUpdateMilestones(invoiceMilestones, string);

            }
            catch (Exception ex) {
                LOG.error("An error occurred while updating Milestones as billed: " + ex.toString());
            }
        }
    }

    /**
     * Update Bill objects isItBilled value.
     *
     * @param string
     */
    protected void updateBillsIsItBilled(String string) {
        /* update Bill */
        List<InvoiceBill> invoiceBills = this.getInvoiceBills();
        if (invoiceBills != null && !invoiceBills.isEmpty()) {

            try {
                ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
                contractsGrantsInvoiceDocumentService.retrieveAndUpdateBills(invoiceBills, string);

            }
            catch (Exception ex) {
                LOG.error("An error occurred while updating Bills as billed: " + ex.toString());
            }
        }
    }

    /**
     * This method generated the template parameter list to populate the pdf invoices that are attached to the Document.
     *
     * @return
     */
    private Map<String, String> getTemplateParameterList() {
            ContractsAndGrantsCGBAward award = getAward();
            Map<String, String> parameterMap = new HashMap<String, String>();
            Map primaryKeys = new HashMap<String, Object>();
            primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, accountingPeriod.getUniversityFiscalYear());
            primaryKeys.put("processingChartOfAccountCode", accountsReceivableDocumentHeader.getProcessingChartOfAccountCode());
            primaryKeys.put("processingOrganizationCode", accountsReceivableDocumentHeader.getProcessingOrganizationCode());
            SystemInformation sysInfo = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SystemInformation.class, primaryKeys);
            parameterMap.put("#documentNumber", returnProperStringValue(documentNumber));
            if (ObjectUtils.isNotNull(this.getDocumentHeader().getWorkflowDocument().getDateCreated())) {
                parameterMap.put("#date", returnProperStringValue(FILE_NAME_TIMESTAMP.format(this.getDocumentHeader().getWorkflowDocument().getDateCreated())));
            }
            if (ObjectUtils.isNotNull(new Date(this.getDocumentHeader().getWorkflowDocument().getDateFinalized().getMillis()))) {
                parameterMap.put("#finalStatusDate", returnProperStringValue(FILE_NAME_TIMESTAMP.format(new Date(this.getDocumentHeader().getWorkflowDocument().getDateFinalized().getMillis()))));
            }
            parameterMap.put("#proposalNumber", returnProperStringValue(proposalNumber));
            parameterMap.put("#payee.name", returnProperStringValue(this.getBillingAddressName()));
            parameterMap.put("#payee.addressLine1", returnProperStringValue(this.getBillingLine1StreetAddress()));
            parameterMap.put("#payee.addressLine2", returnProperStringValue(this.getBillingLine2StreetAddress()));
            parameterMap.put("#payee.city", returnProperStringValue(this.getBillingCityName()));
            parameterMap.put("#payee.state", returnProperStringValue(this.getBillingStateCode()));
            parameterMap.put("#payee.zipcode", returnProperStringValue(this.getBillingZipCode()));
            parameterMap.put("#advanceFlag", convertBooleanValue(this.isAdvance()));
            parameterMap.put("#reimbursementFlag", convertBooleanValue(!(this.isAdvance())));
            parameterMap.put("#accountDetails.contractControlAccountNumber", returnProperStringValue(getRecipientAccountNumber()));
            if (ObjectUtils.isNotNull(sysInfo)) {
                parameterMap.put("#systemInformation.feinNumber", returnProperStringValue(sysInfo.getUniversityFederalEmployerIdentificationNumber()));
                parameterMap.put("#systemInformation.name", returnProperStringValue(sysInfo.getOrganizationRemitToAddressName()));
                parameterMap.put("#systemInformation.addressLine1", returnProperStringValue(sysInfo.getOrganizationRemitToLine1StreetAddress()));
                parameterMap.put("#systemInformation.addressLine2", returnProperStringValue(sysInfo.getOrganizationRemitToLine2StreetAddress()));
                parameterMap.put("#systemInformation.city", returnProperStringValue(sysInfo.getOrganizationRemitToCityName()));
                parameterMap.put("#systemInformation.state", returnProperStringValue(sysInfo.getOrganizationRemitToStateCode()));
                parameterMap.put("#systemInformation.zipcode", returnProperStringValue(sysInfo.getOrganizationRemitToZipCode()));
            }
            if (CollectionUtils.isNotEmpty(getInvoiceDetails())) {
                for (int i = 0; i < getInvoiceDetails().size(); i++) {
                    parameterMap.put("#invoiceDetail[" + i + "].invoiceDetailIdentifier", returnProperStringValue(getInvoiceDetails().get(i).getInvoiceDetailIdentifier()));
                    parameterMap.put("#invoiceDetail[" + i + "].documentNumber", returnProperStringValue(getInvoiceDetails().get(i).getDocumentNumber()));
                    parameterMap.put("#invoiceDetail[" + i + "].categories", returnProperStringValue(getInvoiceDetails().get(i).getCategory()));
                    parameterMap.put("#invoiceDetail[" + i + "].budget", returnProperStringValue(getInvoiceDetails().get(i).getBudget()));
                    parameterMap.put("#invoiceDetail[" + i + "].expenditure", returnProperStringValue(getInvoiceDetails().get(i).getExpenditures()));
                    parameterMap.put("#invoiceDetail[" + i + "].cumulative", returnProperStringValue(getInvoiceDetails().get(i).getCumulative()));
                    parameterMap.put("#invoiceDetail[" + i + "].balance", returnProperStringValue(getInvoiceDetails().get(i).getBalance()));
                    parameterMap.put("#invoiceDetail[" + i + "].billed", returnProperStringValue(getInvoiceDetails().get(i).getBilled()));
                    parameterMap.put("#invoiceDetail[" + i + "].adjustedCumulativeExpenditures", returnProperStringValue(getInvoiceDetails().get(i).getAdjustedCumExpenditures()));
                    parameterMap.put("#invoiceDetail[" + i + "].adjustedBalance", returnProperStringValue(getInvoiceDetails().get(0).getAdjustedBalance()));
                }
            }
            if (CollectionUtils.isNotEmpty(getDirectCostInvoiceDetails())) {
                parameterMap.put("#directCostInvoiceDetail.invoiceDetailIdentifier", returnProperStringValue(getDirectCostInvoiceDetails().get(0).getInvoiceDetailIdentifier()));
                parameterMap.put("#directCostInvoiceDetail.documentNumber", returnProperStringValue(getDirectCostInvoiceDetails().get(0).getDocumentNumber()));
                parameterMap.put("#directCostInvoiceDetail.categories", returnProperStringValue(getDirectCostInvoiceDetails().get(0).getCategory()));
                parameterMap.put("#directCostInvoiceDetail.budget", returnProperStringValue(getDirectCostInvoiceDetails().get(0).getBudget()));
                parameterMap.put("#directCostInvoiceDetail.expenditure", returnProperStringValue(getDirectCostInvoiceDetails().get(0).getExpenditures()));
                parameterMap.put("#directCostInvoiceDetail.cumulative", returnProperStringValue(getDirectCostInvoiceDetails().get(0).getCumulative()));
                parameterMap.put("#directCostInvoiceDetail.balance", returnProperStringValue(getDirectCostInvoiceDetails().get(0).getBalance()));
                parameterMap.put("#directCostInvoiceDetail.billed", returnProperStringValue(getDirectCostInvoiceDetails().get(0).getBilled()));
                parameterMap.put("#directCostInvoiceDetail.adjustedCumulativeExpenditures", returnProperStringValue(getDirectCostInvoiceDetails().get(0).getAdjustedCumExpenditures()));
                parameterMap.put("#directCostInvoiceDetail.adjustedBalance", returnProperStringValue(getDirectCostInvoiceDetails().get(0).getAdjustedBalance()));
            }
            if (CollectionUtils.isNotEmpty(getInDirectCostInvoiceDetails())) {
                parameterMap.put("#inDirectCostInvoiceDetail.invoiceDetailIdentifier", returnProperStringValue(getInDirectCostInvoiceDetails().get(0).getInvoiceDetailIdentifier()));
                parameterMap.put("#inDirectCostInvoiceDetail.documentNumber", returnProperStringValue(getInDirectCostInvoiceDetails().get(0).getDocumentNumber()));
                parameterMap.put("#inDirectCostInvoiceDetail.categories", returnProperStringValue(getInDirectCostInvoiceDetails().get(0).getCategory()));
                parameterMap.put("#inDirectCostInvoiceDetail.budget", returnProperStringValue(getInDirectCostInvoiceDetails().get(0).getBudget()));
                parameterMap.put("#inDirectCostInvoiceDetail.expenditure", returnProperStringValue(getInDirectCostInvoiceDetails().get(0).getExpenditures()));
                parameterMap.put("#inDirectCostInvoiceDetail.cumulative", returnProperStringValue(getInDirectCostInvoiceDetails().get(0).getCumulative()));
                parameterMap.put("#inDirectCostInvoiceDetail.balance", returnProperStringValue(getInDirectCostInvoiceDetails().get(0).getBalance()));
                parameterMap.put("#inDirectCostInvoiceDetail.billed", returnProperStringValue(getInDirectCostInvoiceDetails().get(0).getBilled()));
                parameterMap.put("#inDirectCostInvoiceDetail.adjustedCumulativeExpenditures", returnProperStringValue(getInDirectCostInvoiceDetails().get(0).getAdjustedCumExpenditures()));
                parameterMap.put("#inDirectCostInvoiceDetail.adjustedBalance", returnProperStringValue(getInDirectCostInvoiceDetails().get(0).getAdjustedBalance()));
            }
            if (CollectionUtils.isNotEmpty(getTotalInvoiceDetails())) {
                parameterMap.put("#totalInvoiceDetail.invoiceDetailIdentifier", returnProperStringValue(getTotalInvoiceDetails().get(0).getInvoiceDetailIdentifier()));
                parameterMap.put("#totalInvoiceDetail.documentNumber", returnProperStringValue(getTotalInvoiceDetails().get(0).getDocumentNumber()));
                parameterMap.put("#totalInvoiceDetail.categories", returnProperStringValue(getTotalInvoiceDetails().get(0).getCategory()));
                parameterMap.put("#totalInvoiceDetail.budget", returnProperStringValue(getTotalInvoiceDetails().get(0).getBudget()));
                parameterMap.put("#totalInvoiceDetail.expenditure", returnProperStringValue(getTotalInvoiceDetails().get(0).getExpenditures()));
                parameterMap.put("#totalInvoiceDetail.cumulative", returnProperStringValue(getTotalInvoiceDetails().get(0).getCumulative()));
                parameterMap.put("#totalInvoiceDetail.balance", returnProperStringValue(getTotalInvoiceDetails().get(0).getBalance()));
                parameterMap.put("#totalInvoiceDetail.billed", returnProperStringValue(getTotalInvoiceDetails().get(0).getBilled()));
                parameterMap.put("#totalInvoiceDetail.estimatedCost", returnProperStringValue(getTotalInvoiceDetails().get(0).getBilled().add(getTotalInvoiceDetails().get(0).getExpenditures())));
                parameterMap.put("#totalInvoiceDetail.adjustedCumulativeExpenditures", returnProperStringValue(getTotalInvoiceDetails().get(0).getAdjustedCumExpenditures()));
                parameterMap.put("#totalInvoiceDetail.adjustedBalance", returnProperStringValue(getTotalInvoiceDetails().get(0).getAdjustedBalance()));
            }
            if (CollectionUtils.isNotEmpty(agencyAddressDetails)) {
                for (int i = 0; i < agencyAddressDetails.size(); i++) {
                    parameterMap.put("#agencyAddressDetails[" + i + "].documentNumber", returnProperStringValue(agencyAddressDetails.get(i).getDocumentNumber()));
                    parameterMap.put("#agencyAddressDetails[" + i + "].agencyNumber", returnProperStringValue(agencyAddressDetails.get(i).getAgencyNumber()));
                    parameterMap.put("#agencyAddressDetails[" + i + "].agencyAddressIdentifier", returnProperStringValue(agencyAddressDetails.get(i).getAgencyAddressIdentifier()));
                    parameterMap.put("#agencyAddressDetails[" + i + "].agencyAddressTypeCode", returnProperStringValue(agencyAddressDetails.get(i).getAgencyAddressTypeCode()));
                    parameterMap.put("#agencyAddressDetails[" + i + "].agencyAddressName", returnProperStringValue(agencyAddressDetails.get(i).getAgencyAddressName()));
                    parameterMap.put("#agencyAddressDetails[" + i + "].agencyInvoiceTemplateCode", returnProperStringValue(agencyAddressDetails.get(i).getAgencyInvoiceTemplateCode()));
                    parameterMap.put("#agencyAddressDetails[" + i + "].preferredAgencyInvoiceTemplateCode", returnProperStringValue(agencyAddressDetails.get(i).getPreferredAgencyInvoiceTemplateCode()));
                }
            }
            if (CollectionUtils.isNotEmpty(accountDetails)) {
                for (int i = 0; i < accountDetails.size(); i++) {
                    parameterMap.put("#accountDetails[" + i + "].documentNumber", returnProperStringValue(accountDetails.get(i).getDocumentNumber()));
                    parameterMap.put("#accountDetails[" + i + "].accountNumber", returnProperStringValue(accountDetails.get(i).getAccountNumber()));
                    parameterMap.put("#accountDetails[" + i + "].proposalNumber", returnProperStringValue(accountDetails.get(i).getProposalNumber()));
                    parameterMap.put("#accountDetails[" + i + "].universityFiscalYear", returnProperStringValue(accountDetails.get(i).getUniversityFiscalYear()));
                    parameterMap.put("#accountDetails[" + i + "].chartOfAccountsCode", returnProperStringValue(accountDetails.get(i).getChartOfAccountsCode()));
                    parameterMap.put("#accountDetails[" + i + "].budgetAmount", returnProperStringValue(accountDetails.get(i).getBudgetAmount()));
                    parameterMap.put("#accountDetails[" + i + "].expenditureAmount", returnProperStringValue(accountDetails.get(i).getExpenditureAmount()));
                    parameterMap.put("#accountDetails[" + i + "].balanceAmount", returnProperStringValue(accountDetails.get(i).getBalanceAmount()));
                    Map map = new HashMap<String, Object>();
                    map.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountDetails.get(i).getAccountNumber());
                    map.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, accountDetails.get(i).getChartOfAccountsCode());
                    Account account = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Account.class, map);
                    if(ObjectUtils.isNotNull(account)) {
                        parameterMap.put("#accountDetails[" + i + "].account.responsibilityID", returnProperStringValue(account.getContractsAndGrantsAccountResponsibilityId()));
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(invoiceMilestones)) {
                for (int i = 0; i < invoiceMilestones.size(); i++) {
                    parameterMap.put("#invoiceMilestones[" + i + "].proposalNumber", returnProperStringValue(invoiceMilestones.get(i).getProposalNumber()));
                    parameterMap.put("#invoiceMilestones[" + i + "].milestoneNumber", returnProperStringValue(invoiceMilestones.get(i).getMilestoneNumber()));
                    parameterMap.put("#invoiceMilestones[" + i + "].milestoneIdentifier", returnProperStringValue(invoiceMilestones.get(i).getMilestoneIdentifier()));
                    parameterMap.put("#invoiceMilestones[" + i + "].milestoneDescription", returnProperStringValue(invoiceMilestones.get(i).getMilestoneDescription()));
                    parameterMap.put("#invoiceMilestones[" + i + "].milestoneAmount", returnProperStringValue(invoiceMilestones.get(i).getMilestoneAmount()));
                    parameterMap.put("#invoiceMilestones[" + i + "].milestoneExpectedCompletionDate", returnProperStringValue(invoiceMilestones.get(i).getMilestoneExpectedCompletionDate()));
                    parameterMap.put("#invoiceMilestones[" + i + "].milestoneCompletionDate", returnProperStringValue(invoiceMilestones.get(i).getMilestoneActualCompletionDate()));
                    parameterMap.put("#invoiceMilestones[" + i + "].isItBilled", returnProperStringValue(invoiceMilestones.get(i).getIsItBilled()));
                }
            }
            if (ObjectUtils.isNotNull(invoiceGeneralDetail)) {
                parameterMap.put("#invoiceGeneralDetail.documentNumber", returnProperStringValue(invoiceGeneralDetail.getDocumentNumber()));
                parameterMap.put("#invoiceGeneralDetail.awardDateRange", returnProperStringValue(invoiceGeneralDetail.getAwardDateRange()));
                parameterMap.put("#invoiceGeneralDetail.billingFrequency", returnProperStringValue(invoiceGeneralDetail.getBillingFrequency()));
                parameterMap.put("#invoiceGeneralDetail.finalBill", convertBooleanValue(invoiceGeneralDetail.isFinalBill()));
                parameterMap.put("#invoiceGeneralDetail.finalInvoice", convertBooleanValue(invoiceGeneralDetail.isFinalBill()));
                if (invoiceGeneralDetail.isFinalBill()) {
                    parameterMap.put("#invoiceGeneralDetail.finalInvoiceYesNo", "Yes");
                }
                else {
                    parameterMap.put("#invoiceGeneralDetail.finalInvoiceYesNo", "No");
                }
                parameterMap.put("#invoiceGeneralDetail.billingPeriod", returnProperStringValue(invoiceGeneralDetail.getBillingPeriod()));
                parameterMap.put("#invoiceGeneralDetail.contractGrantType", returnProperStringValue(invoiceGeneralDetail.getContractGrantType()));
                parameterMap.put("#invoiceGeneralDetail.awardTotal", returnProperStringValue(invoiceGeneralDetail.getAwardTotal()));
                parameterMap.put("#invoiceGeneralDetail.newTotalBilled", returnProperStringValue(invoiceGeneralDetail.getNewTotalBilled()));
                parameterMap.put("#invoiceGeneralDetail.amountRemainingToBill", returnProperStringValue(invoiceGeneralDetail.getAmountRemainingToBill()));
                parameterMap.put("#invoiceGeneralDetail.billedToDate", returnProperStringValue(invoiceGeneralDetail.getBilledToDate()));
                parameterMap.put("#invoiceGeneralDetail.costShareAmount", returnProperStringValue(invoiceGeneralDetail.getCostShareAmount()));
                parameterMap.put("#invoiceGeneralDetail.lastBilledDate", returnProperStringValue(invoiceGeneralDetail.getLastBilledDate()));
                String strArray[] = invoiceGeneralDetail.getBillingPeriod().split(" to ");
                if (ObjectUtils.isNotNull(strArray[0])) {
                    parameterMap.put("#invoiceGeneralDetail.invoicingPeriodStartDate", returnProperStringValue(strArray[0]));
                }
                if (ObjectUtils.isNotNull(strArray[1])){
                    parameterMap.put("#invoiceGeneralDetail.invoicingPeriodEndDate", returnProperStringValue(strArray[1]));
                    parameterMap.put("#award.cumulativePeriod", returnProperStringValue(award.getAwardBeginningDate().toString() + " to " + strArray[1]));
                }
            }

            if (CollectionUtils.isNotEmpty(invoiceBills)) {
                for (int i = 0; i < invoiceBills.size(); i++) {
                    parameterMap.put("#invoiceBills[" + i + "].proposalNumber", returnProperStringValue(invoiceBills.get(i).getProposalNumber()));
                    parameterMap.put("#invoiceBills[" + i + "].billNumber", returnProperStringValue(invoiceBills.get(i).getBillNumber()));
                    parameterMap.put("#invoiceBills[" + i + "].billDescription", returnProperStringValue(invoiceBills.get(i).getBillDescription()));
                    parameterMap.put("#invoiceBills[" + i + "].billIdentifier", returnProperStringValue(invoiceBills.get(i).getBillIdentifier()));
                    parameterMap.put("#invoiceBills[" + i + "].billDate", returnProperStringValue(invoiceBills.get(i).getBillDate()));
                    parameterMap.put("#invoiceBills[" + i + "].amount", returnProperStringValue(invoiceBills.get(i).getEstimatedAmount()));
                    parameterMap.put("#invoiceBills[" + i + "].isItBilled", returnProperStringValue(invoiceBills.get(i).getIsItBilled()));
                }
            }
            if (ObjectUtils.isNotNull(award)) {
                KualiDecimal billing = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class).getAwardBilledToDateByProposalNumber(award.getProposalNumber());
                KualiDecimal payments = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class).calculateTotalPaymentsToDateByAward(award);
                KualiDecimal receivable = billing.subtract(payments);
                parameterMap.put("#award.billings", returnProperStringValue(billing));
                parameterMap.put("#award.payments", returnProperStringValue(payments));
                parameterMap.put("#award.receivables", returnProperStringValue(receivable));
                parameterMap.put("#award.proposalNumber", returnProperStringValue(award.getProposalNumber()));
                parameterMap.put("#award.awardId", returnProperStringValue(award.getAwardId()));
                if (ObjectUtils.isNotNull(award.getAwardBeginningDate())) {
                    parameterMap.put("#award.awardBeginningDate", returnProperStringValue(FILE_NAME_TIMESTAMP.format(award.getAwardBeginningDate())));
                }
                if (ObjectUtils.isNotNull(award.getAwardEndingDate())) {
                    parameterMap.put("#award.awardEndingDate", returnProperStringValue(FILE_NAME_TIMESTAMP.format(award.getAwardEndingDate())));
                }
                parameterMap.put("#award.awardTotalAmount", returnProperStringValue(award.getAwardTotalAmount()));
                parameterMap.put("#award.awardAddendumNumber", returnProperStringValue(award.getAwardAddendumNumber()));
                parameterMap.put("#award.awardAllocatedUniversityComputingServicesAmount", returnProperStringValue(award.getAwardAllocatedUniversityComputingServicesAmount()));
                parameterMap.put("#award.federalPassThroughFundedAmount", returnProperStringValue(award.getFederalPassThroughFundedAmount()));
                if (ObjectUtils.isNotNull(award.getAwardEntryDate())) {
                    parameterMap.put("#award.awardEntryDate", returnProperStringValue(FILE_NAME_TIMESTAMP.format(award.getAwardEntryDate())));
                }
                parameterMap.put("#award.agencyFuture1Amount", returnProperStringValue(award.getAgencyFuture1Amount()));
                parameterMap.put("#award.agencyFuture2Amount", returnProperStringValue(award.getAgencyFuture2Amount()));
                parameterMap.put("#award.agencyFuture3Amount", returnProperStringValue(award.getAgencyFuture3Amount()));
                parameterMap.put("#award.awardDocumentNumber", returnProperStringValue(award.getAwardDocumentNumber()));
                if (ObjectUtils.isNotNull(award.getAwardLastUpdateDate())) {
                    parameterMap.put("#award.awardLastUpdateDate", returnProperStringValue(FILE_NAME_TIMESTAMP.format(award.getAwardLastUpdateDate())));
                }
                parameterMap.put("#award.federalPassthroughIndicator", convertBooleanValue(award.getFederalPassThroughIndicator()));
                parameterMap.put("#award.oldProposalNumber", returnProperStringValue(award.getOldProposalNumber()));
                parameterMap.put("#award.awardDirectCostAmount", returnProperStringValue(award.getAwardDirectCostAmount()));
                parameterMap.put("#award.awardIndirectCostAmount", returnProperStringValue(award.getAwardIndirectCostAmount()));
                parameterMap.put("#award.federalFundedAmount", returnProperStringValue(award.getFederalFundedAmount()));
                parameterMap.put("#award.awardCreateTimestamp", returnProperStringValue(award.getAwardCreateTimestamp()));
                if (ObjectUtils.isNotNull(award.getAwardClosingDate())) {
                    parameterMap.put("#award.awardClosingDate", returnProperStringValue(FILE_NAME_TIMESTAMP.format(award.getAwardClosingDate())));
                }
                parameterMap.put("#award.proposalAwardTypeCode", returnProperStringValue(award.getProposalAwardTypeCode()));
                parameterMap.put("#award.awardStatusCode", returnProperStringValue(award.getAwardStatusCode()));
                if (ObjectUtils.isNotNull(award.getLetterOfCreditFund())) {
                    parameterMap.put("#award.letterOfCreditFundGroupCode", returnProperStringValue(award.getLetterOfCreditFund().getLetterOfCreditFundGroupCode()));
                }
                parameterMap.put("#award.letterOfCreditFundCode", returnProperStringValue(award.getLetterOfCreditFundCode()));
                parameterMap.put("#award.grantDescriptionCode", returnProperStringValue(award.getGrantDescriptionCode()));
                if(ObjectUtils.isNotNull(award.getProposal())) {
                    parameterMap.put("#award.grantNumber", returnProperStringValue(award.getProposal().getGrantNumber()));
                }
                parameterMap.put("#agencyNumber", returnProperStringValue(award.getAgencyNumber()));
                parameterMap.put("#agency.fullName", returnProperStringValue(award.getAgency().getFullName()));
                parameterMap.put("#award.federalPassThroughAgencyNumber", returnProperStringValue(award.getFederalPassThroughAgencyNumber()));
                parameterMap.put("#award.agencyAnalystName", returnProperStringValue(award.getAgencyAnalystName()));
                parameterMap.put("#award.analystTelephoneNumber;", returnProperStringValue(award.getAnalystTelephoneNumber()));
                parameterMap.put("#award.preferredBillingFrequency", returnProperStringValue(award.getPreferredBillingFrequency()));
                parameterMap.put("#award.preferredReportTemplate", returnProperStringValue(award.getPreferredReportTemplate()));
                parameterMap.put("#award.preferredReportFrequency", returnProperStringValue(award.getPreferredReportFrequency()));
                parameterMap.put("#award.awardProjectTitle", returnProperStringValue(award.getAwardProjectTitle()));
                parameterMap.put("#award.awardCommentText", returnProperStringValue(award.getAwardCommentText()));
                parameterMap.put("#award.awardPurposeCode", returnProperStringValue(award.getAwardPurposeCode()));
                parameterMap.put("#award.active", convertBooleanValue(award.isActive()));
                parameterMap.put("#award.kimGroupNames", returnProperStringValue(award.getKimGroupNames()));
                parameterMap.put("#award.routingOrg", returnProperStringValue(award.getRoutingOrg()));
                parameterMap.put("#award.routingChart", returnProperStringValue(award.getRoutingChart()));
                parameterMap.put("#award.suspendInvoicing", convertBooleanValue(award.isSuspendInvoicing()));
                parameterMap.put("#award.additionalFormsRequired", convertBooleanValue(award.isAdditionalFormsRequired()));
                parameterMap.put("#award.additionalFormsDescription", returnProperStringValue(award.getAdditionalFormsDescription()));
                parameterMap.put("#award.contractGrantType", returnProperStringValue(award.getContractGrantType()));
                parameterMap.put("#award.awardsourceOfFundsCode", returnProperStringValue(award.getAwardsourceOfFundsCode()));
                parameterMap.put("#award.minInvoiceAmount", returnProperStringValue(award.getMinInvoiceAmount()));
                parameterMap.put("#award.autoApprove", returnProperStringValue(award.getAutoApprove()));
                parameterMap.put("#award.lookupPersonUniversalIdentifier", returnProperStringValue(award.getLookupPersonUniversalIdentifier()));
                parameterMap.put("#award.lookupPerson", returnProperStringValue(award.getLookupPerson().getPrincipalName()));
                parameterMap.put("#award.userLookupRoleNamespaceCode", returnProperStringValue(award.getUserLookupRoleNamespaceCode()));
                parameterMap.put("#award.userLookupRoleName", returnProperStringValue(award.getUserLookupRoleName()));
                parameterMap.put("#award.fundingExpirationDate", returnProperStringValue(award.getFundingExpirationDate()));
                parameterMap.put("#award.drawNumber", returnProperStringValue(award.getDrawNumber()));
                parameterMap.put("#award.commentText", returnProperStringValue(award.getCommentText()));
                parameterMap.put("#award.awardProjectDirector.name", returnProperStringValue(award.getAwardPrimaryProjectDirector().getProjectDirector().getName()));
                parameterMap.put("#award.letterOfCreditFundCode", returnProperStringValue(award.getLetterOfCreditFundCode()));
                if(ObjectUtils.isNotNull(award.getAwardPrimaryFundManager())){
                    parameterMap.put("#award.primaryFundManager.name", returnProperStringValue(award.getAwardPrimaryFundManager().getFundManager().getName()));
                    parameterMap.put("#award.primaryFundManager.email", returnProperStringValue(award.getAwardPrimaryFundManager().getFundManager().getEmailAddress()));
                    parameterMap.put("#award.primaryFundManager.phone", returnProperStringValue(award.getAwardPrimaryFundManager().getFundManager().getPhoneNumber()));
                }
                if (ObjectUtils.isNotNull(invoiceGeneralDetail)) {
                    parameterMap.put("#totalAmountDue", returnProperStringValue(receivable.add(invoiceGeneralDetail.getNewTotalBilled())));
                }
            }
            return parameterMap;
    }

    /**
     * returns proper contract control Account Number.
     *
     * @return
     */
    private String getRecipientAccountNumber() {
        if (ObjectUtils.isNull(accountDetails.get(0).getContractControlAccountNumber())) {
            return accountDetails.get(0).getAccountNumber();
        }
        return accountDetails.get(0).getContractControlAccountNumber();
    }

    /**
     * Returns true if the billing Frequency is Predetermined Billing.
     *
     * @return
     */
    private boolean isAdvance() {
        if (ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE.equals(invoiceGeneralDetail.getBillingFrequency())) {
            return true;
        }
        return false;
    }

    /**
     * Returns a proper String Value. Also returns proper value for currency (USD)
     *
     * @param string
     * @return
     */
    private String returnProperStringValue(Object string) {
        if (ObjectUtils.isNotNull(string)) {
            if (string instanceof KualiDecimal) {
                String amount = (new CurrencyFormatter()).format(string).toString();
                return "$" + (StringUtils.isEmpty(amount) ? "0.00" : amount);
            }
            return string.toString();
        }
        return "";
    }

    /**
     * iText compatible boolean value converter.
     *
     * @param bool
     * @return
     */
    private String convertBooleanValue(boolean bool) {
        if (bool) {
            return "Yes";
        }
        return "Off";
    }

    /**
     * This method updates the ContractsAndGrantsCGBAwardAccount object's invoiceDocumentStatus Variable with the value provided
     *
     * @param id
     * @param value
     */
    private void setAwardAccountInvoiceDocumentStatus(String value) {
        Iterator iterator = accountDetails.iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            Map<String, Object> mapKey = new HashMap<String, Object>();
            mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
            mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
            mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.getProposalNumber());

            // To set invoiceDocumentStatus to award Account
            SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setAwardAccountInvoiceDocumentStatus(mapKey, value);
        }
    }

    /**
     * This method updates the ContractsAndGrantsCGBAwardAccount object's FinalBilled Variable with the value provided
     *
     * @param id
     * @param value
     */
    private void setAwardAccountFinalBilledValue(InvoiceAccountDetail id, boolean value) {
        Map<String, Object> mapKey = new HashMap<String, Object>();
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.getProposalNumber());

        // To set final Billed to award Account
        SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setFinalBilledToAwardAccount(mapKey, value);
    }

    /**
     * This method updates AwardAccounts
     */
    protected void updateUnfinalizationToAwardAccount() {
        Iterator iterator = accountDetails.iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            this.setAwardAccountFinalBilledValue(id, false);
        }
    }

    /**
     * Make changes here to implement what needs to be done when correction button is clicked
     *
     * @see org.kuali.kfs.module.ar.document.CustomerInvoiceDocument#toErrorCorrection()
     */
    @Override
    public void toErrorCorrection() throws WorkflowException {
        super.toErrorCorrection();
        this.correctContractsGrantsInvoiceDocument();
    }

    /**
     * Corrects the Contracts and Grants Invoice Document.
     *
     * @throws WorkflowException
     */
    public void correctContractsGrantsInvoiceDocument() throws WorkflowException {
        Iterator iterator = invoiceDetails.iterator();
        // correct Invoice Details.
        while (iterator.hasNext()) {
            InvoiceDetail id = (InvoiceDetail) iterator.next();
            id.correctInvoiceDetailsCurrentExpenditure();
        }

        // update correction to the InvoiceAccountDetail objects
        iterator = accountDetails.iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            id.correctInvoiceAccountDetailsCurrentExpenditureAmount();
        }

        // correct invoiceDetailAccountObjectCode.
        iterator = invoiceDetailAccountObjectCodes.iterator();
        while (iterator.hasNext()) {
            InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode = (InvoiceDetailAccountObjectCode) iterator.next();
            invoiceDetailAccountObjectCode.correctInvoiceDetailAccountObjectCodeExpenditureAmount();
        }

        // correct Bills
        KualiDecimal totalBillingAmount = KualiDecimal.ZERO;
        iterator = invoiceBills.iterator();
        while (iterator.hasNext()) {
            InvoiceBill bill = (InvoiceBill) iterator.next();
            bill.setEstimatedAmount(bill.getEstimatedAmount().negated());
            totalBillingAmount = totalBillingAmount.add(bill.getEstimatedAmount());
        }

        // correct Milestones
        KualiDecimal totalMilestonesAmount = KualiDecimal.ZERO;
        iterator = invoiceMilestones.iterator();
        while (iterator.hasNext()) {
            InvoiceMilestone milestone = (InvoiceMilestone) iterator.next();
            milestone.setMilestoneAmount(milestone.getMilestoneAmount().negated());
            totalMilestonesAmount = totalMilestonesAmount.add(milestone.getMilestoneAmount());
        }

        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        // set the billed to Date Field
        if (this.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE) && CollectionUtils.isNotEmpty(invoiceMilestones)){// To
            // check
            // if
            // award
            // has
            // milestones
            invoiceGeneralDetail.setBilledToDate(contractsGrantsInvoiceDocumentService.getMilestonesBilledToDate(getProposalNumber()));
            // update the new total billed for the invoice.
            invoiceGeneralDetail.setNewTotalBilled(invoiceGeneralDetail.getNewTotalBilled().add(totalMilestonesAmount));
        }
        else if (this.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) && CollectionUtils.isNotEmpty(invoiceBills)){// To
            // check
            // if
            // award
            // has
            // bills
            invoiceGeneralDetail.setBilledToDate(contractsGrantsInvoiceDocumentService.getPredeterminedBillingBilledToDate(getProposalNumber()));
            // update the new total billed for the invoice.
            invoiceGeneralDetail.setNewTotalBilled(invoiceGeneralDetail.getNewTotalBilled().add(totalBillingAmount));
        }
        else{
            invoiceGeneralDetail.setBilledToDate(contractsGrantsInvoiceDocumentService.getAwardBilledToDateByProposalNumber(getProposalNumber()));
            // update the new total billed for the invoice.
            invoiceGeneralDetail.setNewTotalBilled(invoiceGeneralDetail.getNewTotalBilled().add(getTotalInvoiceDetails().get(0).getExpenditures()));
        }

        //to set Marked for processing and Date report processed to null.
        this.setMarkedForProcessing(null);
        this.setDateReportProcessed(null);

    }

    /**
     * This method corrects the Maintenance Document for Predetermined Billing
     *
     * @throws WorkflowException
     */
    protected void correctBills() throws WorkflowException {
        updateBillsIsItBilled(KFSConstants.ParameterValues.STRING_NO);
    }

    /**
     * This method corrects the Maintenance Document for milestones
     *
     * @throws WorkflowException
     */
    protected void correctMilestones() throws WorkflowException {
        updateMilestonesIsItBilled(KFSConstants.ParameterValues.STRING_NO);
    }

    /**
     * This method takes all the applicable attributes from the associated award object and sets those attributes into their
     * corresponding invoice attributes.
     *
     * @param award The associated award that the invoice will be linked to.
     */
    public void populateInvoiceFromAward(ContractsAndGrantsCGBAward award, List<ContractsAndGrantsCGBAwardAccount> awardAccounts) {
        List<ContractsAndGrantsMilestone> milestones = new ArrayList<ContractsAndGrantsMilestone>();
        List<ContractsAndGrantsBill> bills = new ArrayList<ContractsAndGrantsBill>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
        milestones = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsMilestone.class).getExternalizableBusinessObjectsList(ContractsAndGrantsMilestone.class, map);
        bills = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBill.class).getExternalizableBusinessObjectsList(ContractsAndGrantsBill.class, map);


        if (ObjectUtils.isNotNull(award)) {

            // Invoice General Detail section
            this.setProposalNumber(award.getProposalNumber());
            InvoiceGeneralDetail invoiceGeneralDetail = new InvoiceGeneralDetail();
            invoiceGeneralDetail.setDocumentNumber(this.getDocumentNumber());

            ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
            VerifyBillingFrequency verifyBillingFrequency = SpringContext.getBean(VerifyBillingFrequency.class);

            // Set the last Billed Date and Billing Period
            Timestamp ts = new Timestamp(new java.util.Date().getTime());
            java.sql.Date today = new java.sql.Date(ts.getTime());
            AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
            java.sql.Date[] pair = verifyBillingFrequency.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
            invoiceGeneralDetail.setBillingPeriod(pair[0] + " to " + pair[1]);
            invoiceGeneralDetail.setLastBilledDate(pair[1]);


            invoiceGeneralDetail.populateInvoiceFromAward(award);
            this.setInvoiceGeneralDetail(invoiceGeneralDetail);
            // To set Bill by address identifier because it is a required field - set the value to 1 as it is never being used.
            setCustomerBillToAddressIdentifier(Integer.parseInt("1"));

            // Set Invoice due date to current date as it is required field and never used.
            setInvoiceDueDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight());

            // copy award's agency address to invoice agency address details
            getAgencyAddressDetails().clear();

            List<ContractsAndGrantsAgencyAddress> agencyAddresses = new ArrayList<ContractsAndGrantsAgencyAddress>();
            Map<String, Object> mapKey = new HashMap<String, Object>();
            mapKey.put(KFSPropertyConstants.AGENCY_NUMBER, award.getAgency().getAgencyNumber());
            agencyAddresses = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObjectsList(ContractsAndGrantsAgencyAddress.class, mapKey);
            for (ContractsAndGrantsAgencyAddress agencyAddress : agencyAddresses) {

                InvoiceAgencyAddressDetail invoiceAgencyAddressDetail = new InvoiceAgencyAddressDetail();
                invoiceAgencyAddressDetail.setDocumentNumber(this.getDocumentNumber());
                invoiceAgencyAddressDetail.setAgencyNumber(agencyAddress.getAgencyNumber());
                invoiceAgencyAddressDetail.setAgencyAddressIdentifier(agencyAddress.getAgencyAddressIdentifier());
                invoiceAgencyAddressDetail.setAgencyAddressTypeCode(agencyAddress.getAgencyAddressTypeCode());
                invoiceAgencyAddressDetail.setAgencyAddressName(agencyAddress.getAgencyAddressName());
                invoiceAgencyAddressDetail.setAgencyInvoiceTemplateCode(agencyAddress.getAgencyInvoiceTemplateCode());
                invoiceAgencyAddressDetail.setPreferredAgencyInvoiceTemplateCode(agencyAddress.getAgencyInvoiceTemplateCode());
                invoiceAgencyAddressDetail.setInvoiceIndicatorCode(agencyAddress.getInvoiceIndicatorCode());
                invoiceAgencyAddressDetail.setPreferredInvoiceIndicatorCode(agencyAddress.getInvoiceIndicatorCode());

                getAgencyAddressDetails().add(invoiceAgencyAddressDetail);
            }

            java.sql.Date invoiceDate = this.getInvoiceGeneralDetail().getLastBilledDate();
            if (this.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE) && CollectionUtils.isNotEmpty(milestones)) {// To
                // check
                // if
                // award
                // has
                // milestones

                // copy award milestones to invoice milestones
                getInvoiceMilestones().clear();
                for (ContractsAndGrantsMilestone awdMilestone : milestones) {
                    // To consider the completed milestones only.
                    // To check for null - Milestone Completion date.

                    if (awdMilestone.getMilestoneActualCompletionDate() != null && !invoiceDate.before(awdMilestone.getMilestoneActualCompletionDate()) && awdMilestone.getIsItBilled().equals(KFSConstants.ParameterValues.STRING_NO) && awdMilestone.getMilestoneAmount().isGreaterThan(KualiDecimal.ZERO)) {

                        InvoiceMilestone invMilestone = new InvoiceMilestone();
                        invMilestone.setProposalNumber(awdMilestone.getProposalNumber());
                        invMilestone.setMilestoneNumber(awdMilestone.getMilestoneNumber());
                        invMilestone.setMilestoneIdentifier(awdMilestone.getMilestoneIdentifier());
                        invMilestone.setMilestoneDescription(awdMilestone.getMilestoneDescription());
                        invMilestone.setIsItBilled(awdMilestone.getIsItBilled());
                        invMilestone.setMilestoneActualCompletionDate(awdMilestone.getMilestoneActualCompletionDate());
                        invMilestone.setMilestoneAmount(awdMilestone.getMilestoneAmount());
                        getInvoiceMilestones().add(invMilestone);
                    }
                }
            }
            else if (this.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) && CollectionUtils.isNotEmpty(bills)) {// To
                // check
                // if
                // award
                // has
                // bills

                // copy award milestones to invoice milestones
                getInvoiceBills().clear();
                for (ContractsAndGrantsBill awdBill : bills) {
                    // To check for null - Bill Completion date.
                    // To consider the completed milestones only.
                    if (awdBill.getBillDate() != null && !invoiceDate.before(awdBill.getBillDate()) && awdBill.getIsItBilled().equals(KFSConstants.ParameterValues.STRING_NO) && awdBill.getEstimatedAmount().isGreaterThan(KualiDecimal.ZERO)) {
                        InvoiceBill invBill = new InvoiceBill();
                        invBill.setProposalNumber(awdBill.getProposalNumber());
                        invBill.setBillNumber(awdBill.getBillNumber());
                        invBill.setBillIdentifier(awdBill.getBillIdentifier());
                        invBill.setBillDescription(awdBill.getBillDescription());
                        invBill.setIsItBilled(awdBill.getIsItBilled());
                        invBill.setBillDate(awdBill.getBillDate());
                        invBill.setEstimatedAmount(awdBill.getEstimatedAmount());
                        getInvoiceBills().add(invBill);
                    }
                }
            }
            else {

                // To set values for categories and populate invoice details section
                generateValuesForAccountObjectCodes(awardAccounts, award);
                generateValuesForCategories(awardAccounts);
            }

            // copy award's accounts to invoice account details
            getAccountDetails().clear();
            for (ContractsAndGrantsCGBAwardAccount awardAccount : awardAccounts) {

                InvoiceAccountDetail invoiceAccountDetail = new InvoiceAccountDetail();
                invoiceAccountDetail.setDocumentNumber(this.getDocumentNumber());

                invoiceAccountDetail.setAccountNumber(awardAccount.getAccountNumber());
                if (ObjectUtils.isNotNull(awardAccount.getAccount()) && StringUtils.isNotEmpty(awardAccount.getAccount().getContractControlAccountNumber())) {
                    invoiceAccountDetail.setContractControlAccountNumber(awardAccount.getAccount().getContractControlAccountNumber());
                }
                invoiceAccountDetail.setChartOfAccountsCode(awardAccount.getChartOfAccountsCode());
                invoiceAccountDetail.setProposalNumber(award.getProposalNumber());
                invoiceAccountDetail.setBudgetsAndCumulatives(this.getInvoiceGeneralDetail().getLastBilledDate(), this.getInvoiceGeneralDetail().getBillingFrequency(), award.getAwardBeginningDate());
                getAccountDetails().add(invoiceAccountDetail);
            }
            // Set some basic values to invoice Document
            setUpValuesForContractsGrantsInvoiceDocument(award);

        }
    }

    /**
     * This method helps in setting up basic values for Contracts Grants Invoice Document
     */
    public void setUpValuesForContractsGrantsInvoiceDocument(ContractsAndGrantsCGBAward award) {
        if (ObjectUtils.isNotNull(award.getAgency())) {
            if (ObjectUtils.isNotNull(this.getAccountsReceivableDocumentHeader())) {
                this.getAccountsReceivableDocumentHeader().setCustomerNumber(award.getAgency().getCustomerNumber());
            }
            Customer customer = SpringContext.getBean(CustomerService.class).getByPrimaryKey(award.getAgency().getCustomerNumber());
            if (ObjectUtils.isNotNull(customer)) {
                this.setCustomerName(customer.getCustomerName());
            }
        }
        // To set open invoice indicator to true to help doing cash control for the invoice
        this.setOpenInvoiceIndicator(true);

        // To set LOC creation type and appropriate values from award.
        if (StringUtils.isNotEmpty(award.getLocCreationType())) {
            this.setLocCreationType(award.getLocCreationType());
        }
        // To set up values for Letter of Credit Fund and Fund Group irrespective of the LOC Creation type.
        if (StringUtils.isNotEmpty(award.getLetterOfCreditFundCode())) {
            this.setLetterOfCreditFundCode(award.getLetterOfCreditFundCode());
        }
        if (ObjectUtils.isNotNull(award.getLetterOfCreditFund())) {
            if (StringUtils.isNotEmpty(award.getLetterOfCreditFund().getLetterOfCreditFundGroupCode())) {
                this.setLetterOfCreditFundGroupCode(award.getLetterOfCreditFund().getLetterOfCreditFundGroupCode());
            }
        }

        // To set Account Receivable object code when the parameter is 3.

        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean isUsingReceivableFAU = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption);
        List<ContractsGrantsAwardInvoiceAccountInformation> awardInvoiceAccounts = new ArrayList<ContractsGrantsAwardInvoiceAccountInformation>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
        map.put(KFSPropertyConstants.ACTIVE, true);
        awardInvoiceAccounts = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsGrantsAwardInvoiceAccountInformation.class).getExternalizableBusinessObjectsList(ContractsGrantsAwardInvoiceAccountInformation.class, map);
        if (isUsingReceivableFAU) {
            if (CollectionUtils.isNotEmpty(awardInvoiceAccounts)) {
                for (ContractsGrantsAwardInvoiceAccountInformation awardInvoiceAccount : awardInvoiceAccounts) {
                    if (awardInvoiceAccount.getAccountType().equals(ArPropertyConstants.AR_ACCOUNT)) {
                        if (awardInvoiceAccount.isActive()) {// consider the active invoice account only.
                            this.setPaymentChartOfAccountsCode(awardInvoiceAccount.getChartOfAccountsCode());
                            this.setPaymentAccountNumber(awardInvoiceAccount.getAccountNumber());
                            this.setPaymentSubAccountNumber(awardInvoiceAccount.getSubAccountNumber());
                            this.setPaymentFinancialObjectCode(awardInvoiceAccount.getObjectCode());
                            this.setPaymentFinancialSubObjectCode(awardInvoiceAccount.getSubObjectCode());
                        }
                    }
                }
            }
        }

        // set totalBilled by Account Number in Account Details
        Map<String, KualiDecimal> totalBilledByAccountNumberMap = new HashMap<String, KualiDecimal>();
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            String accountNumber = invoiceDetailAccountObjectCode.getAccountNumber();
            KualiDecimal totalBilled = totalBilledByAccountNumberMap.get(accountNumber);
            // if account number not found in map, then create new total, 0
            if (totalBilled == null) {
                totalBilled = new KualiDecimal(0);
            }
            totalBilled = totalBilled.add(invoiceDetailAccountObjectCode.getTotalBilled());
            totalBilledByAccountNumberMap.put(accountNumber, totalBilled);
        }

        for (InvoiceAccountDetail invAcctD : this.getAccountDetails()) {
            if (totalBilledByAccountNumberMap.get(invAcctD.getAccountNumber()) != null) {
                invAcctD.setBilledAmount(totalBilledByAccountNumberMap.get(invAcctD.getAccountNumber()));
            }
        }


        KualiDecimal totalMilestoneAmount = KualiDecimal.ZERO;
        // To calculate the total milestone amount.
        if (this.getInvoiceMilestones().size() > 0) {
            for (InvoiceMilestone milestone : this.getInvoiceMilestones()) {
                if (milestone.getMilestoneAmount() != null) {
                    totalMilestoneAmount = totalMilestoneAmount.add(milestone.getMilestoneAmount());
                }
            }
        }
        totalMilestoneAmount = totalMilestoneAmount.add(this.getInvoiceGeneralDetail().getBilledToDate());

        KualiDecimal totalBillAmount = KualiDecimal.ZERO;
        // To calculate the total bill amount.
        if (this.getInvoiceBills().size() > 0) {
            for (InvoiceBill bill : this.getInvoiceBills()) {
                if (bill.getEstimatedAmount() != null) {
                    totalBillAmount = totalBillAmount.add(bill.getEstimatedAmount());
                }
            }
        }
        totalBillAmount = totalBillAmount.add(this.getInvoiceGeneralDetail().getBilledToDate());

        // to set the account detail expenditure amount
        KualiDecimal totalExpendituredAmount = KualiDecimal.ZERO;
        for (InvoiceAccountDetail invAcctD : this.getAccountDetails()) {
            KualiDecimal currentExpenditureAmount = KualiDecimal.ZERO;

            currentExpenditureAmount = invAcctD.getCumulativeAmount().subtract(invAcctD.getBilledAmount());
            invAcctD.setExpenditureAmount(currentExpenditureAmount);
            // overwriting account detail expenditure amount if locReview Indicator is true - and award belongs to LOC Billing
            for (ContractsAndGrantsCGBAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                if (awardAccount.getAccountNumber().equals(invAcctD.getAccountNumber()) && awardAccount.isLocReviewIndicator() && award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.LOC_BILLING_SCHEDULE_CODE)) {
                    currentExpenditureAmount = awardAccount.getAmountToDraw();
                    invAcctD.setExpenditureAmount(currentExpenditureAmount);
                }
            }
            totalExpendituredAmount = totalExpendituredAmount.add(currentExpenditureAmount);
        }
        totalExpendituredAmount = totalExpendituredAmount.add(this.getInvoiceGeneralDetail().getBilledToDate());

        // To set the New Total Billed Amount.
        if (this.getInvoiceMilestones().size() > 0) {
            this.getInvoiceGeneralDetail().setNewTotalBilled(totalMilestoneAmount);
        }
        else if (this.getInvoiceBills().size() > 0) {
            this.getInvoiceGeneralDetail().setNewTotalBilled(totalBillAmount);
        }
        else {
            this.getInvoiceGeneralDetail().setNewTotalBilled(totalExpendituredAmount);
        }

    }

    /**
     * Gets the invoiceDetails attribute.
     *
     * @return Returns the invoiceDetails.
     */
    public List<InvoiceDetail> getInvoiceDetails() {
        // To get the list of invoice Details without the Total fields or any indirect cost categories
        List<InvoiceDetail> invDetails = new ArrayList<InvoiceDetail>();
        for (InvoiceDetail invD : invoiceDetails) {
            if (!invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_COST_CD) && !invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_DIRECT_COST_CD) && !invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_IN_DIRECT_COST_CD) && !invD.isIndirectCostIndicator()) {
                invDetails.add(invD);
            }
        }

        return invDetails;
    }

    public List<InvoiceDetail> getInvoiceDetailsWithIndirectCosts(){
     // To get the list of invoice Details without the Total fields
        List<InvoiceDetail> invDetails = new ArrayList<InvoiceDetail>();
        for (InvoiceDetail invD : invoiceDetails) {
            if (!invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_COST_CD) && !invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_DIRECT_COST_CD) && !invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_IN_DIRECT_COST_CD)) {
                invDetails.add(invD);
            }
        }

        return invDetails;
    }


    /**
     * This method returns a list of invoice details which are indirect costs only.
     * These invoice details are not shown on the document and is different form the
     * other method getInDirectCostInvoiceDetails() because that method returns the total.
     */
    public List<InvoiceDetail> getInvoiceDetailsIndirectCostOnly(){
        List<InvoiceDetail> invDetails = new ArrayList<InvoiceDetail>();
        for (InvoiceDetail invD : invoiceDetails) {
            if (!invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_COST_CD) && !invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_DIRECT_COST_CD) && !invD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_IN_DIRECT_COST_CD) && invD.isIndirectCostIndicator()) {
               invDetails.add(invD);
            }
        }

        return invDetails;
    }

    /**
     * Sets the invoiceDetails attribute value.
     *
     * @param invoiceDetails The invoiceDetails to set.
     */
    public void setInvoiceDetails(List<InvoiceDetail> invoiceDetails) {
        this.invoiceDetails = invoiceDetails;
    }


    /**
     * @return
     */
    public List<InvoiceDetailAccountObjectCode> getInvoiceDetailAccountObjectCodes() {
        return invoiceDetailAccountObjectCodes;
    }

    /**
     * @param invoiceDetailAccountObjectCodes
     */
    public void setInvoiceDetailAccountObjectCodes(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        this.invoiceDetailAccountObjectCodes = invoiceDetailAccountObjectCodes;
    }

    /**
     * Gets the agencyAddressDetails attribute.
     *
     * @return Returns the agencyAddressDetails.
     */
    public List<InvoiceAgencyAddressDetail> getAgencyAddressDetails() {
        return agencyAddressDetails;
    }

    /**
     * Sets the agencyAddressDetails attribute value.
     *
     * @param agencyAddressDetails The agencyAddressDetails to set.
     */
    public void setAgencyAddressDetails(List<InvoiceAgencyAddressDetail> agencyAddressDetails) {
        this.agencyAddressDetails = agencyAddressDetails;
    }

    /**
     * Gets the accountDetails attribute.
     *
     * @return Returns the accountDetails.
     */
    public List<InvoiceAccountDetail> getAccountDetails() {
        return accountDetails;
    }

    /**
     * Sets the accountDetails attribute value.
     *
     * @param accountDetails The accountDetails to set.
     */
    public void setAccountDetails(List<InvoiceAccountDetail> accountDetails) {
        this.accountDetails = accountDetails;
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    @Override
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    @Override
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, this.documentNumber);
        m.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        m.put("letterOfCreditFundCode", letterOfCreditFundCode);
        m.put("invoiceGeneralDetail", invoiceGeneralDetail);
        m.put("letterOfCreditFundGroupCode", letterOfCreditFundGroupCode);
        m.put("award", award);
        m.put("locCreationType", locCreationType);
        return m;
    }

    /**
     * This method takes a ContractsAndGrantsCategory, retrieves the specified object code or object code range. It then parses this
     * string, and returns all the possible object codes specified by this range.
     *
     * @param category
     * @return Set<String> objectCodes
     */
    public Set<String> getObjectCodeArrayFromSingleCategory(ContractsAndGrantsCategories category) throws IllegalArgumentException {
        Set<String> objectCodeArray = new HashSet<String>();
        List<String> objectCodes = Arrays.asList(category.getCategoryObjectCodes().split(","));

        // get a list of qualifying object codes listed in the categories
        for (int j = 0; j < objectCodes.size(); j++) {

            // This is to check if the object codes are in a range of values like 1001-1009 or 100* or 10* or 1*. The wildcard
            // should be included in the suffix only.
            if (objectCodes.get(j).contains("-")) {// To check ranges like A000 - ZZZZ (includes A001, A002 .. A009 , A00A to A00Z
                // and so on to ZZZZ)
                String obCodeFirst = StringUtils.substringBefore(objectCodes.get(j), "-").trim();
                String obCodeLast = StringUtils.substringAfter(objectCodes.get(j), "-").trim();
                // To validate if the object Code formed is in proper format of [0-9a-zA-Z]{4}

                if (obCodeFirst.matches("[0-9a-zA-Z]{4}") && obCodeLast.matches("[0-9a-zA-Z]{4}")) {
                    try {

                        List<String> objectCodeValues = incrementAlphaNumericString(obCodeFirst, obCodeLast);
                        // To Check for the first value as it is not being included in the array
                        objectCodeArray.add(obCodeFirst);

                        for (int i = 0; i < objectCodeValues.size(); i++) {
                            objectCodeArray.add(objectCodeValues.get(i));
                        }
                    }
                    catch (Exception ex) {
                        String msg = String.format("Failed to get Object Codes for Contracts and Grants Invoice", ex.getMessage());
                        LOG.error(msg);
                        throw new RuntimeException(msg, ex);
                    }
                }
                else {
                    throw new IllegalArgumentException("Invalid Object Code range specification for the category:" + category.getCategoryName());
                }
            }
            else if (objectCodes.get(j).contains("*")) {// To check for wildcard suffix
                String obCodeFirst = StringUtils.substringBefore(objectCodes.get(j), "*").trim();
                String obCodeLast = StringUtils.substringBefore(objectCodes.get(j), "*").trim(); // substringBefore is correct here
                // To make the code work for wildcards like 1* 10* 100* etc
                // 10* will give you from 100 - 10Z.

                for (int x = obCodeFirst.length(); x < 4; x++) {
                    obCodeFirst = obCodeFirst.concat("0");
                }

                for (int x = obCodeLast.length(); x < 4; x++) {
                    obCodeLast = obCodeLast.concat("Z");
                }
                if (obCodeFirst.matches("[0-9a-zA-Z]{4}") && obCodeLast.matches("[0-9a-zA-Z]{4}")) {
                    try {
                        List<String> obCodeValues = incrementAlphaNumericString(obCodeFirst, obCodeLast);

                        // To Check for the first value as it is not being included in the array
                        objectCodeArray.add(obCodeFirst);
                        for (int i = 0; i < obCodeValues.size(); i++) {
                            objectCodeArray.add(obCodeValues.get(i));
                        }
                    }
                    catch (Exception ex) {
                        String msg = String.format("Failed to get Object Codes for Contracts and Grants Invoice for the category:" + category.getCategoryName(), ex.getMessage());
                        LOG.error(msg);
                        throw new RuntimeException(msg, ex);
                    }
                }
                else {
                    throw new IllegalArgumentException("Invalid Object Code range specification for the category:" + category.getCategoryName());
                }
            }
            else {// If the object code is directly provided.
                if (objectCodes.get(j).trim().matches("[0-9a-zA-Z]{4}")) {

                    objectCodeArray.add(objectCodes.get(j).trim());
                }
                else {
                    throw new IllegalArgumentException("Invalid Object Code range specification for the category:" + category.getCategoryName());
                }
            }

        }
        return objectCodeArray;
    }

    /**
     * This method returns the complete set of object codes for ALL ContractsAndGrantsCategories.
     *
     * @return Set<String> objectCodes
     */
    public Set<String> getObjectCodeArrayFromContractsAndGrantsCategories() {
        Set<String> objectCodeArray = new HashSet<String>();
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.ACTIVE, true);
        Collection<ContractsAndGrantsCategories> contractsAndGrantsCategories = SpringContext.getBean(BusinessObjectService.class).findMatching(ContractsAndGrantsCategories.class, criteria);
        Iterator<ContractsAndGrantsCategories> contractsAndGrantsCategoriesIterator = contractsAndGrantsCategories.iterator();

        while (contractsAndGrantsCategoriesIterator.hasNext()) {
            ContractsAndGrantsCategories category = contractsAndGrantsCategoriesIterator.next();
            objectCodeArray.addAll(getObjectCodeArrayFromSingleCategory(category));
        }

        return objectCodeArray;
    }

    /**
     * @param awardAccounts
     * @param award
     */
    public void generateValuesForAccountObjectCodes(List<ContractsAndGrantsCGBAwardAccount> awardAccounts, ContractsAndGrantsCGBAward award) {

        List<Balance> glBalances = new ArrayList<Balance>();
        List<Integer> fiscalYears = new ArrayList<Integer>();
        Calendar c = Calendar.getInstance();
        Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        Map<String, Set<String>> objectCodeFromCategoriesMap = new HashMap<String, Set<String>>();

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.ACTIVE, true);
        Collection<ContractsAndGrantsCategories> contractsAndGrantsCategories = SpringContext.getBean(BusinessObjectService.class).findMatching(ContractsAndGrantsCategories.class, criteria);
        // get the categories and create a new arraylist for each one
        for (ContractsAndGrantsCategories category : contractsAndGrantsCategories) {
            // populate the category object code maps
            objectCodeFromCategoriesMap.put(category.getCategoryCode(), getObjectCodeArrayFromSingleCategory(category));

        }
        for (ContractsAndGrantsCGBAwardAccount awardAccount : awardAccounts) {
            //Changes made to retrieve balances of previous years (useful when the award is billed for the first time and in case of fiscal year change)
            //1. If award is billed for the first time.


            Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getFiscalYear(award.getAwardBeginningDate());

            for(Integer i = fiscalYear; i<= currentYear; i++ ){
             fiscalYears.add(i);
            }

            for(Integer eachFiscalYr: fiscalYears){
                Map<String, Object> balanceKeys = new HashMap<String, Object>();
                balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
                balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
                balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, eachFiscalYr);
                balanceKeys.put("objectTypeCode", ArPropertyConstants.EXPENSE_OBJECT_TYPE);
                balanceKeys.put("balanceTypeCode", ArPropertyConstants.ACTUAL_BALANCE_TYPE);
    
                glBalances.addAll(SpringContext.getBean(BusinessObjectService.class).findMatching(Balance.class, balanceKeys));
            }
        } // now you have a list of balances from all accounts;


        for (Balance bal : glBalances) {
           if(!StringUtils.equalsIgnoreCase(bal.getSubAccount().getA21SubAccount().getSubAccountTypeCode(),KFSConstants.SubAccountType.COST_SHARE)){ 
            
            ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);

            for (ContractsAndGrantsCategories category : contractsAndGrantsCategories) {
                Set<String> objectCodeFromCategoriesSet = objectCodeFromCategoriesMap.get(category.getCategoryCode());

                // if the object code from this balance is in the list of object code retrieved from the category, then include in
                // the detail
                if (objectCodeFromCategoriesSet.contains(bal.getObjectCode())) {
                    InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode;
                    //Check if there is an existing invoice detail account object code existing (if there are more than one fiscal years)
                    Map<String, Object> invDtlKeys = new HashMap<String, Object>();
                    invDtlKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
                    invDtlKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, bal.getChartOfAccountsCode());
                    invDtlKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, bal.getAccountNumber());
                    invDtlKeys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, bal.getObjectCode());
                    invDtlKeys.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentHeader().getDocumentNumber());
                    invoiceDetailAccountObjectCode = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(InvoiceDetailAccountObjectCode.class, invDtlKeys);

                    if(ObjectUtils.isNull(invoiceDetailAccountObjectCode)){
                    invoiceDetailAccountObjectCode = new InvoiceDetailAccountObjectCode();
                    invoiceDetailAccountObjectCode.setDocumentNumber(this.getDocumentHeader().getDocumentNumber());
                    invoiceDetailAccountObjectCode.setProposalNumber(proposalNumber);
                    invoiceDetailAccountObjectCode.setFinancialObjectCode(bal.getObjectCode());
                    invoiceDetailAccountObjectCode.setCategoryCode(category.getCategoryCode());
                    invoiceDetailAccountObjectCode.setAccountNumber(bal.getAccountNumber());
                    invoiceDetailAccountObjectCode.setChartOfAccountsCode(bal.getChartOfAccountsCode());
                    invoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode);
                    }
                    // Retrieve cumulative amounts based on the biling period.

                    if (this.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MONTHLY_BILLING_SCHEDULE_CODE) || this.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.QUATERLY_BILLING_SCHEDULE_CODE) || this.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.SEMI_ANNUALLY_BILLING_SCHEDULE_CODE) || this.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.ANNUALLY_BILLING_SCHEDULE_CODE)) {
                        invoiceDetailAccountObjectCode.setCumulativeExpenditures(invoiceDetailAccountObjectCode.getCumulativeExpenditures().add(contractsGrantsInvoiceDocumentService.retrieveAccurateBalanceAmount(this.getInvoiceGeneralDetail().getLastBilledDate(), bal)));
                    }
                    else if (this.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.BILLED_AT_TERM)) {
                        invoiceDetailAccountObjectCode.setCumulativeExpenditures(invoiceDetailAccountObjectCode.getCumulativeExpenditures().add(bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount())));
                    }
                    else {// This code should be removed. This is temporary - just to make sure the amounts are pulled up.
                        invoiceDetailAccountObjectCode.setCumulativeExpenditures(invoiceDetailAccountObjectCode.getCumulativeExpenditures().add(bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount())));
                    }

                    // add this single account object code item to the list in the Map
                    SpringContext.getBean(BusinessObjectService.class).save(invoiceDetailAccountObjectCode);


                    break; // found a match into which category, we can stop and move on to next balance entry
                }
            }
        }
        }

        // Modifying the code to set invoiceDetailaccountobject codes calculation here checking with loc review indicator and also
        // accounting more than one fiscal years.

        for (ContractsAndGrantsCGBAwardAccount awdAcct : awardAccounts) {
            if (awdAcct.isLocReviewIndicator() && award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.LOC_BILLING_SCHEDULE_CODE)) {
                KualiDecimal amountToDrawForObjectCodes = KualiDecimal.ZERO;

                List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodeList = new ArrayList<InvoiceDetailAccountObjectCode>();
                for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
                    if (invoiceDetailAccountObjectCode.getDocumentNumber().equals(this.getDocumentNumber()) && invoiceDetailAccountObjectCode.getProposalNumber().equals(this.getProposalNumber()) && invoiceDetailAccountObjectCode.getAccountNumber().equals(awdAcct.getAccountNumber()) && invoiceDetailAccountObjectCode.getChartOfAccountsCode().equals(awdAcct.getChartOfAccountsCode())) {
                        invoiceDetailAccountObjectCodeList.add(invoiceDetailAccountObjectCode);
                    }
                }
                amountToDrawForObjectCodes = awdAcct.getAmountToDraw().divide(new KualiDecimal(invoiceDetailAccountObjectCodeList.size()));

                // Now to set the divided value equally to all the object code rows.
                for (InvoiceDetailAccountObjectCode invDtllAcctOB : invoiceDetailAccountObjectCodeList) {
                    invDtllAcctOB.setCurrentExpenditures(amountToDrawForObjectCodes);

                }
            }
            else {
                // code to write values from award acct total billed amount to invoice detail account object code..

                Map<String, Object> totalBilledKeys = new HashMap<String, Object>();
                totalBilledKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
                totalBilledKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awdAcct.getChartOfAccountsCode());
                totalBilledKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awdAcct.getAccountNumber());

                List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilledList = (List<AwardAccountObjectCodeTotalBilled>) SpringContext.getBean(BusinessObjectService.class).findMatching(AwardAccountObjectCodeTotalBilled.class, totalBilledKeys);

                    for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
                        if (CollectionUtils.isNotEmpty(awardAccountObjectCodeTotalBilledList)) {
                        for (AwardAccountObjectCodeTotalBilled awardAccountObjectCodeTotalBilled : awardAccountObjectCodeTotalBilledList) {
                            if (invoiceDetailAccountObjectCode.getFinancialObjectCode().equalsIgnoreCase(awardAccountObjectCodeTotalBilled.getFinancialObjectCode())) {
                                invoiceDetailAccountObjectCode.setTotalBilled(awardAccountObjectCodeTotalBilled.getTotalBilled());
                            }
                        }
                        }
                        // Set current expenditures
                        invoiceDetailAccountObjectCode.setCurrentExpenditures(invoiceDetailAccountObjectCode.getCumulativeExpenditures().subtract(invoiceDetailAccountObjectCode.getTotalBilled()));

                    }



            }

        }


    }


    /**
     * 1. This method is responsible to populate categories column for the ContractsGrantsInvoice Document. 2. The categories are
     * retrieved from the Maintenance document as a collection and then a logic with conditions to handle ranges of Object Codes. 3.
     * Once the object codes are retrieved and categories are set the performAccountingCalculations method of InvoiceDetail BO will
     * do all the accounting calculations.
     */
    public void generateValuesForCategories(List<ContractsAndGrantsCGBAwardAccount> awardAccounts) {

        Set<String> categoryArray = new HashSet<String>();

        // To get only the active categories.
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.ACTIVE, true);
        Collection<ContractsAndGrantsCategories> contractsAndGrantsCategories = SpringContext.getBean(BusinessObjectService.class).findMatching(ContractsAndGrantsCategories.class, criteria);
        Iterator<ContractsAndGrantsCategories> it = contractsAndGrantsCategories.iterator();

        // query database for award account object code details. then divi them up into categories
        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilleds = contractsGrantsInvoiceDocumentService.getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(awardAccounts);

        while (it.hasNext()) {
            ContractsAndGrantsCategories category = it.next();
            // To add all the values from Category Array to Invoice Details category only if they are retrieved well.

            InvoiceDetail invDetail = new InvoiceDetail();
            invDetail.setDocumentNumber(this.getDocumentHeader().getDocumentNumber());

            invDetail.setCategory(category.getCategoryName());
            invDetail.setCategoryCode(category.getCategoryCode());
            invDetail.setIndirectCostIndicator(category.isIndirectCostIndicator());
            // calculate total billed first
            Set<String> completeObjectCodeArrayForSingleCategory = getObjectCodeArrayFromSingleCategory(category);
            invDetail.performTotalBilledCalculation(awardAccountObjectCodeTotalBilleds, completeObjectCodeArrayForSingleCategory);


            invDetail.performCumulativeExpenditureCalculation(invoiceDetailAccountObjectCodes, completeObjectCodeArrayForSingleCategory);

            invDetail.performCurrentExpenditureCalculation(invoiceDetailAccountObjectCodes, completeObjectCodeArrayForSingleCategory);

            // calculate the rest using billed to date
            invDetail.performBudgetCalculations(awardAccounts, completeObjectCodeArrayForSingleCategory, award.getAwardBeginningDate());// accounting calculations
            // happening here

            invoiceDetails.add(invDetail);
        }


        // To calculate total values for Invoice Detail section.

        KualiDecimal totalDirectCostBudget = KualiDecimal.ZERO;
        KualiDecimal totalDirectCostCumulative = KualiDecimal.ZERO;
        KualiDecimal totalDirectCostExpenditures = KualiDecimal.ZERO;
        KualiDecimal totalDirectCostBalance = KualiDecimal.ZERO;
        KualiDecimal totalDirectCostBilled = KualiDecimal.ZERO;
        KualiDecimal totalInDirectCostBudget = KualiDecimal.ZERO;
        KualiDecimal totalInDirectCostCumulative = KualiDecimal.ZERO;
        KualiDecimal totalInDirectCostExpenditures = KualiDecimal.ZERO;
        KualiDecimal totalInDirectCostBalance = KualiDecimal.ZERO;
        KualiDecimal totalInDirectCostBilled = KualiDecimal.ZERO;
        Iterator<InvoiceDetail> o = invoiceDetails.iterator();

        while (o.hasNext()) {

            InvoiceDetail invD = o.next();
            // To sum up values for indirect Cost Invoice Details

            if (invD.isIndirectCostIndicator()) {
                if (null != invD.getBudget()) {
                    totalInDirectCostBudget = totalInDirectCostBudget.add(invD.getBudget());
                }
                if (null != invD.getCumulative()) {
                    totalInDirectCostCumulative = totalInDirectCostCumulative.add(invD.getCumulative());

                }
                if (null != invD.getBalance()) {
                    totalInDirectCostBalance = totalInDirectCostBalance.add(invD.getBalance());
                }
                if (null != invD.getBilled()) {
                    totalInDirectCostBilled = totalInDirectCostBilled.add(invD.getBilled());
                }
                if (null != invD.getExpenditures()) {
                    totalInDirectCostExpenditures = totalInDirectCostExpenditures.add(invD.getExpenditures());
                }

            }
            else {
                if (null != invD.getBudget()) {
                    totalDirectCostBudget = totalDirectCostBudget.add(invD.getBudget());
                }
                if (null != invD.getCumulative()) {
                    totalDirectCostCumulative = totalDirectCostCumulative.add(invD.getCumulative());

                }
                if (null != invD.getBalance()) {
                    totalDirectCostBalance = totalDirectCostBalance.add(invD.getBalance());
                }
                if (null != invD.getBilled()) {
                    totalDirectCostBilled = totalDirectCostBilled.add(invD.getBilled());
                }
                if (null != invD.getExpenditures()) {
                    totalDirectCostExpenditures = totalDirectCostExpenditures.add(invD.getExpenditures());
                }
            }
        }
        InvoiceDetail directCostInvDetail = new InvoiceDetail();
        directCostInvDetail.setDocumentNumber(this.getDocumentHeader().getDocumentNumber());

        directCostInvDetail.setCategory(ArConstants.TOTAL_DIRECT_COST);
        directCostInvDetail.setCategoryCode(ArConstants.TOTAL_DIRECT_COST_CD);
        directCostInvDetail.setBudget(totalDirectCostBudget);
        directCostInvDetail.setExpenditures(totalDirectCostExpenditures);
        directCostInvDetail.setCumulative(totalDirectCostCumulative);
        directCostInvDetail.setBalance(totalDirectCostBalance);
        directCostInvDetail.setBilled(totalDirectCostBilled);
        directCostInvoiceDetails.add(directCostInvDetail);

        // To create a Total In Direct Cost invoice detail to add values for indirect cost invoice details.

        InvoiceDetail indInvDetail = new InvoiceDetail();
        indInvDetail.setDocumentNumber(this.getDocumentHeader().getDocumentNumber());
        indInvDetail.setIndirectCostIndicator(true);
        indInvDetail.setCategory(ArConstants.TOTAL_IN_DIRECT_COST);
        indInvDetail.setCategoryCode(ArConstants.TOTAL_IN_DIRECT_COST_CD);
        indInvDetail.setBudget(totalInDirectCostBudget);
        indInvDetail.setExpenditures(totalInDirectCostExpenditures);
        indInvDetail.setCumulative(totalInDirectCostCumulative);
        indInvDetail.setBalance(totalInDirectCostBalance);
        indInvDetail.setBilled(totalInDirectCostBilled);
        inDirectCostInvoiceDetails.add(indInvDetail);

        // Sum up the direct cost and indirect cost invoice details.

        InvoiceDetail totalInvDetail = new InvoiceDetail();
        totalInvDetail.setDocumentNumber(this.getDocumentHeader().getDocumentNumber());

        totalInvDetail.setCategory(ArConstants.TOTAL_COST);
        totalInvDetail.setCategoryCode(ArConstants.TOTAL_COST_CD);

        totalInvDetail.setBudget(directCostInvoiceDetails.get(0).getBudget().add(totalInDirectCostBudget));
        totalInvDetail.setExpenditures(directCostInvoiceDetails.get(0).getExpenditures().add(totalInDirectCostExpenditures));
        totalInvDetail.setCumulative(directCostInvoiceDetails.get(0).getCumulative().add(totalInDirectCostCumulative));
        totalInvDetail.setBalance(directCostInvoiceDetails.get(0).getBalance().add(totalInDirectCostBalance));
        totalInvDetail.setBilled(directCostInvoiceDetails.get(0).getBilled().add(totalInDirectCostBilled));

        totalInvoiceDetails.add(totalInvDetail);

    }

    /**
     * This method returns a list of character strings that represent base 36 integers from start(non-inclusive) to limit
     * (inclusive).
     *
     * @param start the starting point of the list. This value is not included in the list.
     * @param limit the ending point of the list. This value is included in the list
     * @return the list of strings
     * @throws IllegalArgumentException if start is not less than limit
     */
    public List<String> incrementAlphaNumericString(String stringToIncrement, String stringLimit) throws IllegalArgumentException {
        int startInt = Integer.parseInt(stringToIncrement, 36);
        int limitInt = Integer.parseInt(stringLimit, 36);
        if (startInt >= limitInt) {
            throw new IllegalArgumentException("Starting code must be less than limit code.");
        }
        List<String> retval = new ArrayList<String>();
        for (int i = startInt + 1; i <= limitInt; i++) {
            // format below forces the string back to 4 characters and replace makes the extra
            // characters '0'
            retval.add(String.format("%4s", Integer.toString(i, 36)).replace('\u0020', '0'));
        }
        return retval;
    }

    /**
     * Gets the invoiceGeneralDetail attribute.
     *
     * @return Returns the invoiceGeneralDetail.
     */
    public InvoiceGeneralDetail getInvoiceGeneralDetail() {
        return invoiceGeneralDetail;
    }

    /**
     * Sets the invoiceGeneralDetail attribute value.
     *
     * @param invoiceGeneralDetail The invoiceGeneralDetail to set.
     */
    public void setInvoiceGeneralDetail(InvoiceGeneralDetail invoiceGeneralDetail) {
        this.invoiceGeneralDetail = invoiceGeneralDetail;
    }

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber.
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute value.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the award attribute.
     *
     * @return Returns the award.
     */
    public ContractsAndGrantsCGBAward getAward() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.getProposalNumber());
        return award = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAward.class).getExternalizableBusinessObject(ContractsAndGrantsCGBAward.class, map);
    }

    /**
     * Sets the award attribute value.
     *
     * @param award The award to set.
     */
    public void setAward(ContractsAndGrantsCGBAward award) {
        this.award = award;
    }


    /**
     * Gets the invoiceMilestones attribute.
     *
     * @return Returns the invoiceMilestones.
     */
    public List<InvoiceMilestone> getInvoiceMilestones() {
        return invoiceMilestones;
    }

    /**
     * Sets the invoiceMilestones attribute value.
     *
     * @param invoiceMilestones The invoiceMilestones to set.
     */
    public void setInvoiceMilestones(List<InvoiceMilestone> invoiceMilestones) {
        this.invoiceMilestones = invoiceMilestones;
    }

    /**
     * Gets the invoiceBills attribute.
     *
     * @return Returns the invoiceBills.
     */
    public List<InvoiceBill> getInvoiceBills() {
        return invoiceBills;
    }

    /**
     * Sets the invoiceBills attribute value.
     *
     * @param invoiceBills The invoiceBills to set.
     */
    public void setInvoiceBills(List<InvoiceBill> invoiceBills) {
        this.invoiceBills = invoiceBills;
    }

    /**
     * Gets the locCreationType attribute.
     *
     * @return Returns the locCreationType.
     */
    public String getLocCreationType() {
        return locCreationType;
    }

    /**
     * Sets the locCreationType attribute value.
     *
     * @param locCreationType The locCreationType to set.
     */
    public void setLocCreationType(String locCreationType) {
        this.locCreationType = locCreationType;
    }

    /**
     * Gets the letterOfCreditFundGroupCode attribute.
     *
     * @return Returns the letterOfCreditFundGroupCode.
     */
    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }

    /**
     * Sets the letterOfCreditFundGroupCode attribute value.
     *
     * @param letterOfCreditFundGroupCode The letterOfCreditFundGroupCode to set.
     */
    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }

    /**
     * Gets the letterOfCreditFundCode attribute.
     *
     * @return Returns the letterOfCreditFundCode.
     */
    public String getLetterOfCreditFundCode() {
        return letterOfCreditFundCode;
    }

    /**
     * Sets the letterOfCreditFundCode attribute value.
     *
     * @param letterOfCreditFundCode The letterOfCreditFundCode to set.
     */
    public void setLetterOfCreditFundCode(String letterOfCreditFundCode) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
    }

    /**
     * Gets the directCostInvoiceDetails attribute.
     *
     * @return Returns the directCostInvoiceDetails.
     */
    public List<InvoiceDetail> getDirectCostInvoiceDetails() {
        // To get the list of invoice Details for direct cost
        List<InvoiceDetail> dcInvDetails = new ArrayList<InvoiceDetail>();
        for (InvoiceDetail dInvD : directCostInvoiceDetails) {
            if (dInvD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_DIRECT_COST_CD)) {
                dcInvDetails.add(dInvD);
            }
        }

        return dcInvDetails;
    }

    /**
     * Sets the directCostInvoiceDetails attribute value.
     *
     * @param directCostInvoiceDetails The directCostInvoiceDetails to set.
     */
    public void setDirectCostInvoiceDetails(List<InvoiceDetail> directCostInvoiceDetails) {
        this.directCostInvoiceDetails = directCostInvoiceDetails;
    }

    /**
     * Gets the list of Events.
     *
     * @return Returns the events.
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * Sets the list of Events.
     *
     * @param events The events to set.
     */
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     * Gets the inDirectCostInvoiceDetails attribute.
     *
     * @return Returns the inDirectCostInvoiceDetails.
     */
    public List<InvoiceDetail> getInDirectCostInvoiceDetails() {
        // To get the list of invoice Details for indirect cost
        List<InvoiceDetail> indInvDetails = new ArrayList<InvoiceDetail>();
        for (InvoiceDetail indInvD : inDirectCostInvoiceDetails) {
            if (indInvD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_IN_DIRECT_COST_CD)) {
                indInvDetails.add(indInvD);
            }
        }

        return indInvDetails;
    }

    /**
     * Sets the inDirectCostInvoiceDetails attribute value.
     *
     * @param inDirectCostInvoiceDetails The inDirectCostInvoiceDetails to set.
     */
    public void setInDirectCostInvoiceDetails(List<InvoiceDetail> inDirectCostInvoiceDetails) {
        this.inDirectCostInvoiceDetails = inDirectCostInvoiceDetails;
    }

    /**
     * Gets the totalInvoiceDetails attribute.
     *
     * @return Returns the totalInvoiceDetails.
     */
    public List<InvoiceDetail> getTotalInvoiceDetails() {
        // To get the list of invoice Details for total cost
        List<InvoiceDetail> totalInvDetails = new ArrayList<InvoiceDetail>();
        for (InvoiceDetail tInvD : totalInvoiceDetails) {
            if (tInvD.getCategoryCode().equalsIgnoreCase(ArConstants.TOTAL_COST_CD)) {
                totalInvDetails.add(tInvD);
            }
        }

        return totalInvDetails;
    }

    /**
     * Sets the totalInvoiceDetails attribute value.
     *
     * @param totalInvoiceDetails The totalInvoiceDetails to set.
     */
    public void setTotalInvoiceDetails(List<InvoiceDetail> totalInvoiceDetails) {
        this.totalInvoiceDetails = totalInvoiceDetails;
    }

    /**
     * @return
     */
    public List<InvoiceSuspensionCategory> getInvoiceSuspensionCategories() {
        return invoiceSuspensionCategories;
    }

    /**
     * @param invoiceSuspensionCategories
     */
    public void setInvoiceSuspensionCategories(List<InvoiceSuspensionCategory> invoiceSuspensionCategories) {
        this.invoiceSuspensionCategories = invoiceSuspensionCategories;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.CustomerInvoiceDocument#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(REQUIRES_APPROVAL_SPLIT)) {
            return isRequiresFundingManagerApproval();
        }
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }

    /**
     * @return
     */
    private boolean isRequiresFundingManagerApproval() {
        // if auto approve on the award is false or suspension exists, then we need to have funds manager approve.
        boolean result;
        result = !this.getAward().getAutoApprove() || !this.getInvoiceSuspensionCategories().isEmpty();
        return result;
    }

    public KualiDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(KualiDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public KualiDecimal getBalanceDue() {
        return balanceDue;
    }

    public void setBalanceDue(KualiDecimal balanceDue) {
        this.balanceDue = balanceDue;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * Gets the finalDispositionCode attribute.
     *
     * @return Returns the finalDispositionCode.
     */
    public String getFinalDispositionCode() {
        return finalDispositionCode;
    }

    /**
     * Sets the finalDispositionCode attribute.
     *
     * @param finalDispositionCode The finalDispositionCode to set.
     */
    public void setFinalDispositionCode(String finalDispositionCode) {
        this.finalDispositionCode = finalDispositionCode;
    }

    /**
     * Gets the referralTypeCode attribute.
     *
     * @return Returns the referralTypeCode attribute.
     */
    public String getReferralTypeCode() {
        return referralTypeCode;
    }

    /**
     * Sets the referralTypeCode attribute.
     *
     * @param referralTypeCode The referralTypeCode to set.
     */
    public void setReferralTypeCode(String referralTypeCode) {
        this.referralTypeCode = referralTypeCode;
    }

    /**
     * Gets the finalDisposition attribute.
     *
     * @return Returns the finalDisposition attribute.
     */
    public FinalDisposition getFinalDisposition() {
        return finalDisposition;
    }

    /**
     * Sets the finalDisposition attribute.
     *
     * @param finalDisposition The finalDisposition to set.
     */
    public void setFinalDisposition(FinalDisposition finalDisposition) {
        this.finalDisposition = finalDisposition;
    }

    /**
     * Gets the referralType attribute.
     *
     * @return Returns the referralType attribute.
     */
    public ReferralType getReferralType() {
        return referralType;
    }

    /**
     * Sets the referralType attribute.
     *
     * @param referralType The referralType to set.
     */
    public void setReferralType(ReferralType referralType) {
        this.referralType = referralType;
    }

    /**
     * Gets the showEvents attribute.
     *
     * @return Returns the showEvents attribute.
     */
    public boolean isShowEvents() {
        return showEvents;
    }

    /**
     * Sets the showEvents attribute.
     *
     * @param showEvents The showEvents to set.
     */
    public void setShowEvents(boolean showEvents) {
        this.showEvents = showEvents;
    }
}
