/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.service.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityType;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.businessobject.GenerateDunningLettersLookupResult;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.dataaccess.ContractsGrantsInvoiceDocumentDao;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.DunningLetterService;
import org.kuali.kfs.module.ar.service.ContractsGrantsBillingUtilityService;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.PdfFormFillerUtil;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

/**
 * Implementation class for DunningLetterDistributionService.
 */
@Transactional
public class DunningLetterServiceImpl implements DunningLetterService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DunningLetterServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected ContractsGrantsInvoiceDocumentDao contractsGrantsInvoiceDocumentDao;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected ContractsGrantsBillingUtilityService contractsGrantsBillingUtilityService;
    protected DateTimeService dateTimeService;
    protected KualiModuleService kualiModuleService;
    protected NoteService noteService;
    protected FinancialSystemUserService financialSystemUserService;

    /**
     * This method generates the actual pdf file with related invoices to the template to print.
     *
     * @param dunningLetterTemplate
     * @param dunningLetterDistributionLookupResult
     * @return
     */
    protected byte[] createDunningLetters(DunningLetterTemplate dunningLetterTemplate, GenerateDunningLettersLookupResult dunningLetterDistributionLookupResult) {

        List<ContractsGrantsInvoiceDocument> selectedInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        byte[] reportStream = null;
        byte[] finalReportStream = null;
        int lastEventCode;

        if (ObjectUtils.isNotNull(dunningLetterTemplate) && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilename())) {
            // To get list of invoices per award per dunning letter template
            for (ContractsGrantsInvoiceDocument cgInvoice : dunningLetterDistributionLookupResult.getInvoices()) {
                if (StringUtils.equals(cgInvoice.getInvoiceGeneralDetail().getDunningLetterTemplateAssigned(), dunningLetterTemplate.getDunningLetterTemplateCode())) {
                    selectedInvoices.add(cgInvoice);
                    // 1. Now we know that the invoice is going to have its dunning letter processed. So we assume the letter is
                    // sent and set the event for it.
                    CollectionEvent event = new CollectionEvent();
                    event.setInvoiceNumber(cgInvoice.getDocumentNumber());
                    event.setCollectionEventCode(cgInvoice.getNextCollectionEventCode());
                    // To get the Activity Code from the Collection Activity type eDoc based on the indicator.
                    String activityCode = null;
                    List<CollectionActivityType> activityTypes = (List<CollectionActivityType>) getBusinessObjectService().findAll(CollectionActivityType.class);
                    for (CollectionActivityType activityType : activityTypes) {
                        if (activityType.isDunningProcessIndicator()) {
                            activityCode = activityType.getActivityCode();
                        }
                    }
                    if (ObjectUtils.isNotNull(activityCode)) {
                        event.setActivityCode(activityCode);
                        event.setActivityDate(new java.sql.Date(new Date().getTime()));
                        event.setActivityText(ArConstants.DunningLetters.DUNNING_LETTER_SENT_TXT);
                        final Timestamp now = dateTimeService.getCurrentTimestamp();
                        event.setPostedDate(now);

                        if (GlobalVariables.getUserSession() != null && GlobalVariables.getUserSession().getPerson() != null) {
                            Person authorUniversal = GlobalVariables.getUserSession().getPerson();
                            event.setUserPrincipalId(authorUniversal.getPrincipalId());
                            event.setUser(authorUniversal);
                        }
                        businessObjectService.save(event);
                        cgInvoice.getCollectionEvents().add(event);
                    }

                    // 2. To set the Last sent date of the dunning letter.

                    cgInvoice.getInvoiceGeneralDetail().setDunningLetterTemplateSentDate(new java.sql.Date(new Date().getTime()));
                    businessObjectService.save(cgInvoice);
                }
            }

            // to generate dunning letter from templates.
            ModuleConfiguration systemConfiguration = kualiModuleService.getModuleServiceByNamespaceCode(KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE).getModuleConfiguration();
            String templateFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getTemplateFileDirectories().get(KFSConstants.TEMPLATES_DIRECTORY_KEY);
            String templateFilePath = templateFolderPath + File.separator + dunningLetterTemplate.getFilename();
            File templateFile = new File(templateFilePath);
            File outputDirectory = null;
            String outputFileName;
            try {
                // Step2. add parameters to the dunning letter
                outputFileName = dunningLetterDistributionLookupResult.getProposalNumber() + getDateTimeService().toDateStringForFilename(getDateTimeService().getCurrentDate()) + ArConstants.TemplateUploadSystem.EXTENSION;
                Map<String, String> replacementList = getTemplateParameterList(selectedInvoices);
                CustomerAddress address;
                Map<String, Object> primaryKeys = new HashMap<String, Object>();
                primaryKeys.put(KFSPropertyConstants.CUSTOMER_NUMBER, dunningLetterDistributionLookupResult.getCustomerNumber());
                primaryKeys.put("customerAddressTypeCode", "P");
                address = businessObjectService.findByPrimaryKey(CustomerAddress.class, primaryKeys);
                replacementList.put("agency.fullAddressInline", contractsGrantsBillingUtilityService.buildFullAddress(address));
                replacementList.put("agency.fullName", address.getCustomer().getCustomerName());
                replacementList.put("agency.contactName", address.getCustomer().getCustomerContactName());
                if(CollectionUtils.isNotEmpty(selectedInvoices)){
                reportStream = PdfFormFillerUtil.populateTemplate(templateFile, replacementList);

                // Step3. attach each dunning letter to invoice pdfs.
                finalReportStream = generateListOfInvoicesPdfToPrint(selectedInvoices, reportStream);
                }
            }
            catch (DocumentException | IOException ex) {
                // This means that the invoice pdfs were not generated properly. So get only the Dunning letters created.
                LOG.error("An exception occurred while retrieving invoice pdfs." + ex.getMessage());
                finalReportStream = reportStream;
            }
        }
        else {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_FILE_UPLOAD_NO_PDF_FILE_SELECTED_FOR_SAVE, "test");
        }

        return finalReportStream;
    }

    /**
     * Loops through the collection of lookup results, creating pdfs for each and appending the bytes of the pdfs onto the returned "finalReport"
     * @see org.kuali.kfs.module.ar.document.service.DunningLetterDistributionService#createDunningLettersForAllResults(org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate, java.util.Collection)
     */
    @Override
    public byte[] createDunningLettersForAllResults(Collection<GenerateDunningLettersLookupResult> results) throws DocumentException, IOException {
        ByteArrayOutputStream zos = null;
        PdfCopyFields reportCopy = null;
        byte[] finalReport = null;
        try {
            zos = new ByteArrayOutputStream();
            reportCopy = new PdfCopyFields(zos);
            reportCopy.open();
            List<DunningLetterTemplate> dunningLetterTemplates = (List<DunningLetterTemplate>) getBusinessObjectService().findAll(DunningLetterTemplate.class);
            for (DunningLetterTemplate dunningLetterTemplate : dunningLetterTemplates) {
                for (GenerateDunningLettersLookupResult generateDunningLettersLookupResult : results) {
                    final byte[] report = createDunningLetters(dunningLetterTemplate, generateDunningLettersLookupResult);
                    if (ObjectUtils.isNotNull(report)) {
                        reportCopy.addDocument(new PdfReader(report));
                    }
                }
            }
            reportCopy.close();
            finalReport = zos.toByteArray();
        } finally {
            if (zos != null) {
                zos.close();
            }
        }
        return finalReport;
    }

    /**
     * This method generated the template parameter list to populate the pdf invoices that are attached to the Document.
     *
     * @return
     */
    protected Map<String, String> getTemplateParameterList(List<ContractsGrantsInvoiceDocument> invoices) {

        Map<String, String> parameterMap = new HashMap<String, String>();

        if (CollectionUtils.isNotEmpty(invoices)){
            ContractsAndGrantsBillingAward award = invoices.get(0).getInvoiceGeneralDetail().getAward();
            Map primaryKeys = new HashMap<String, Object>();
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "award.proposalNumber", org.apache.commons.lang.ObjectUtils.toString(award.getProposalNumber()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "currentDate", getDateTimeService().toDateTimeString(getDateTimeService().getCurrentDate()));
            if (CollectionUtils.isNotEmpty(invoices)) {
                for (int i = 0; i < invoices.size(); i++) {
                    contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoice[" + i + "].documentNumber", invoices.get(i).getDocumentNumber());
                    contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoice[" + i + "].billingDate", getDateTimeService().toDateString(invoices.get(i).getBillingDate()));
                    contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoice[" + i + "].totalAmount", contractsGrantsBillingUtilityService.formatForCurrency(invoices.get(i).getTotalDollarAmount()));
                    contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoice[" + i + "].customerName", invoices.get(i).getCustomerName());
                    contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoice[" + i + "].customerNumber", invoices.get(i).getAccountsReceivableDocumentHeader().getCustomerNumber());
                }
            }
            if (ObjectUtils.isNotNull(award)) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "award.awardProjectTitle", award.getAwardProjectTitle());
            }
        }
        return parameterMap;
    }

    /**
     * This method generates the actual pdf files to print.
     *
     * @param mapping
     * @param form
     * @param list
     * @return
     */
    @Override
    public boolean createZipOfPDFs(byte[] report, ByteArrayOutputStream baos) throws IOException {

        ZipOutputStream zos = new ZipOutputStream(baos);
        int bytesRead;
        byte[] buffer = new byte[1024];
        CRC32 crc = new CRC32();

        if (ObjectUtils.isNotNull(report)) {
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(report));
            crc.reset();
            while ((bytesRead = bis.read(buffer)) != -1) {
                crc.update(buffer, 0, bytesRead);
            }
            bis.close();
            // Reset to beginning of input stream
            bis = new BufferedInputStream(new ByteArrayInputStream(report));
            ZipEntry entry = new ZipEntry("DunningLetters&Invoices-" + getDateTimeService().toDateStringForFilename(getDateTimeService().getCurrentDate()) + ".pdf");
            entry.setMethod(ZipEntry.STORED);
            entry.setCompressedSize(report.length);
            entry.setSize(report.length);
            entry.setCrc(crc.getValue());
            zos.putNextEntry(entry);
            while ((bytesRead = bis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }
            bis.close();
        }

        zos.close();
        return true;
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService#generateListOfInvoicesPdfToPrint(java.util.Collection)
     */
    public byte[] generateListOfInvoicesPdfToPrint(Collection<ContractsGrantsInvoiceDocument> list, byte[] report) throws DocumentException, IOException {
        Date runDate = new Date();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        generateCombinedPdfForInvoices(list, report, baos);
        return baos.toByteArray();
    }

    /**
     * Generates the pdf file for printing the invoices.
     *
     * @param list
     * @param outputStream
     * @throws DocumentException
     * @throws IOException
     */
    protected void generateCombinedPdfForInvoices(Collection<ContractsGrantsInvoiceDocument> list, byte[] report, OutputStream outputStream) throws DocumentException, IOException {
        PdfCopyFields copy = new PdfCopyFields(outputStream);
        copy.open();
        copy.addDocument(new PdfReader(report));
        for (ContractsGrantsInvoiceDocument invoice : list) {
            for (InvoiceAddressDetail invoiceAddressDetail : invoice.getInvoiceAddressDetails()) {
                Note note = noteService.getNoteByNoteId(invoiceAddressDetail.getNoteId());
                if (ObjectUtils.isNotNull(note) && note.getAttachment().getAttachmentFileSize() > 0) {
                    copy.addDocument(new PdfReader(note.getAttachment().getAttachmentContents()));
                }
            }
        }
        copy.close();
    }

    /**
     *
     * @see org.kuali.kfs.module.ar.document.service.DunningLetterDistributionService#isValidOrganizationForTemplate(org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean isValidOrganizationForTemplate(DunningLetterTemplate template, Person user) {
        final ChartOrgHolder userChartOrg = getFinancialSystemUserService().getPrimaryOrganization(user, ArConstants.AR_NAMESPACE_CODE);

        if (!StringUtils.isBlank(template.getBillByChartOfAccountCode()) && !StringUtils.isBlank(template.getBilledByOrganizationCode())) {
            return StringUtils.equals(template.getBillByChartOfAccountCode(), userChartOrg.getChartOfAccountsCode()) && StringUtils.equals(template.getBilledByOrganizationCode(), userChartOrg.getOrganizationCode());
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.DunningLetterDistributionService#getPopulatedDunningLetterDistributionLookupResults(java.util.Collection)
     */
    @Override
    public Collection<GenerateDunningLettersLookupResult> getPopulatedGenerateDunningLettersLookupResults(Collection<ContractsGrantsInvoiceDocument> invoices) {
        Collection<GenerateDunningLettersLookupResult> populatedGenerateDunningLettersLookupResults = new ArrayList<GenerateDunningLettersLookupResult>();

        if (CollectionUtils.isEmpty(invoices)) {
            return populatedGenerateDunningLettersLookupResults;
        }

        Iterator iter = getContractsGrantsInvoiceDocumentService().getInvoicesByAward(invoices).entrySet().iterator();
        GenerateDunningLettersLookupResult generateDunningLettersLookupResult = null;
        while (iter.hasNext()) {

            Map.Entry entry = (Map.Entry) iter.next();
            List<ContractsGrantsInvoiceDocument> list = (List<ContractsGrantsInvoiceDocument>) entry.getValue();

            if (CollectionUtils.isNotEmpty(list)){
                // Get data from first award for agency data
                ContractsGrantsInvoiceDocument document = list.get(0);
                ContractsAndGrantsBillingAward award = document.getInvoiceGeneralDetail().getAward();
                if (ObjectUtils.isNotNull(award) && !award.isStopWorkIndicator()) {
                    generateDunningLettersLookupResult = new GenerateDunningLettersLookupResult();
                    generateDunningLettersLookupResult.setProposalNumber(award.getProposalNumber());
                    generateDunningLettersLookupResult.setInvoiceDocumentNumber(document.getDocumentNumber());
                    generateDunningLettersLookupResult.setAgencyNumber(award.getAgencyNumber());
                    generateDunningLettersLookupResult.setCustomerNumber(document.getAccountsReceivableDocumentHeader().getCustomerNumber());
                    generateDunningLettersLookupResult.setAwardTotal(award.getAwardTotalAmount());
                    generateDunningLettersLookupResult.setCampaignID(award.getDunningCampaign());
                    if (CollectionUtils.isNotEmpty(document.getAccountDetails())) {
                        generateDunningLettersLookupResult.setAccountNumber(document.getAccountDetails().get(0).getAccountNumber());
                    }
                    generateDunningLettersLookupResult.setInvoices(list);

                    populatedGenerateDunningLettersLookupResults.add(generateDunningLettersLookupResult);
                }
            }
        }

        return populatedGenerateDunningLettersLookupResults;
    }

    /**
     * Maps the given ContractsGrantsInvoiceDocuments by their agency number
     * @param invoices the invoices to Map to agency number
     * @return the Map of the invoices
     */
    protected Map<Long, List<ContractsGrantsInvoiceDocument>> getInvoicesByAward(Collection<ContractsGrantsInvoiceDocument> invoices) {
        Map<Long, List<ContractsGrantsInvoiceDocument>> invoicesByAward = new HashMap<Long, List<ContractsGrantsInvoiceDocument>>();
        for (ContractsGrantsInvoiceDocument invoice : invoices) {
            Long proposalNumber = invoice.getInvoiceGeneralDetail().getProposalNumber();
            if (invoicesByAward.containsKey(proposalNumber)) {
                invoicesByAward.get(proposalNumber).add(invoice);
            }
            else {
                List<ContractsGrantsInvoiceDocument> invoicesByProposalNumber = new ArrayList<ContractsGrantsInvoiceDocument>();
                invoicesByProposalNumber.add(invoice);
                invoicesByAward.put(proposalNumber, invoicesByProposalNumber);
            }
        }
        return invoicesByAward;
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the contractsGrantsInvoiceDocumentDao attribute.
     *
     * @return Returns the contractsGrantsInvoiceDocumentDao.
     */
    public ContractsGrantsInvoiceDocumentDao getContractsGrantsInvoiceDocumentDao() {
        return contractsGrantsInvoiceDocumentDao;
    }

    /**
     * Sets the contractsGrantsInvoiceDocumentDao attribute value.
     *
     * @param contractsGrantsInvoiceDocumentDao The contractsGrantsInvoiceDocumentDao to set.
     */
    public void setContractsGrantsInvoiceDocumentDao(ContractsGrantsInvoiceDocumentDao contractsGrantsInvoiceDocumentDao) {
        this.contractsGrantsInvoiceDocumentDao = contractsGrantsInvoiceDocumentDao;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the kualiModuleService attribute value.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public NoteService getNoteService() {
        return noteService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public FinancialSystemUserService getFinancialSystemUserService() {
        return financialSystemUserService;
    }

    public void setFinancialSystemUserService(FinancialSystemUserService financialSystemUserService) {
        this.financialSystemUserService = financialSystemUserService;
    }

    public ContractsGrantsBillingUtilityService getContractsGrantsBillingUtilityService() {
        return contractsGrantsBillingUtilityService;
    }

    public void setContractsGrantsBillingUtilityService(ContractsGrantsBillingUtilityService contractsGrantsBillingUtilityService) {
        this.contractsGrantsBillingUtilityService = contractsGrantsBillingUtilityService;
    }

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
}
    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }
}