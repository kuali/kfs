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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgencyAddress;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityType;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionOnDemandLookupResult;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.businessobject.InvoiceAgencyAddressDetail;
import org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.DunningLetterDistributionOnDemandService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.PdfFormFillerUtil;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.core.web.format.CurrencyFormatter;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

/**
 * Implementation class for DunningLetterDistributionOnDemandService.
 */
public class DunningLetterDistributionOnDemandServiceImpl implements DunningLetterDistributionOnDemandService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DunningLetterDistributionOnDemandServiceImpl.class);
    private BusinessObjectService businessObjectService;
    private ContractsGrantsInvoiceDocumentDao contractsGrantsInvoiceDocumentDao;
    private static final SimpleDateFormat FILE_NAME_TIMESTAMP = new SimpleDateFormat("MM-dd-yyyy");

    /**
     * @see org.kuali.kfs.module.ar.document.service.DunningLetterDistributionOnDemandService#createDunningLetters(org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate,
     *      org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionOnDemandLookupResult)
     */
    @Override
    public byte[] createDunningLetters(DunningLetterTemplate dunningLetterTemplate, DunningLetterDistributionOnDemandLookupResult dunningLetterDistributionOnDemandLookupResult) {

        List<ContractsGrantsInvoiceDocument> selectedInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        byte[] reportStream = null;
        byte[] finalReportStream = null;
        int lastEventCode;

        if (ObjectUtils.isNotNull(dunningLetterTemplate) && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilepath())) {
            // To get list of invoices per award per dunning letter template
            for (ContractsGrantsInvoiceDocument cgInvoice : dunningLetterDistributionOnDemandLookupResult.getInvoices()) {
                if (cgInvoice.getInvoiceGeneralDetail().getDunningLetterTemplateAssigned().equals(dunningLetterTemplate.getLetterTemplateCode())) {
                    selectedInvoices.add(cgInvoice);
                    // 1. Now we know that the invoice is going to have its dunning letter processed. So we assume the letter is
                    // sent and set the event for it.
                    Event event = new Event();
                    event.setInvoiceNumber(cgInvoice.getDocumentNumber());
                    // calculate event code
                    // Add sequence number to event code
                    lastEventCode = this.getFinalEventsCount(cgInvoice.getEvents()) + 1;
                    String eventCode = event.getInvoiceNumber() + "-" + String.format("%03d", lastEventCode);
                    event.setEventCode(eventCode);
                    // To get the Activity Code from the Collection Activity type eDoc basedo n the indicator.
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
                        final Date now = SpringContext.getBean(DateTimeService.class).getCurrentDate();
                        event.setPostedDate(now);

                        if (GlobalVariables.getUserSession() != null && GlobalVariables.getUserSession().getPerson() != null) {
                            Person authorUniversal = GlobalVariables.getUserSession().getPerson();
                            event.setUserPrincipalId(authorUniversal.getPrincipalId());
                            event.setUser(authorUniversal);
                        }
                        businessObjectService.save(event);
                        cgInvoice.getEvents().add(event);
                    }

                    // 2. To set the Last sent date of the dunning letter.

                    cgInvoice.getInvoiceGeneralDetail().setDunningLetterTemplateSentDate(new java.sql.Date(new Date().getTime()));
                    businessObjectService.save(cgInvoice);
                }
            }


            // to generate dunning letter from templates.

            File templateFile = new File(dunningLetterTemplate.getFilepath());
            File outputDirectory = null;
            String outputFileName;
            try {

                // Step2. add parameters to the dunning letter
                outputFileName = dunningLetterDistributionOnDemandLookupResult.getProposalNumber() + FILE_NAME_TIMESTAMP.format(new Date()) + ArConstants.TemplateUploadSystem.EXTENSION;
                Map<String, String> replacementList = getTemplateParameterList(selectedInvoices);
                ContractsAndGrantsAgencyAddress address;
                Map<String, Object> primaryKeys = new HashMap<String, Object>();
                primaryKeys.put(KFSPropertyConstants.AGENCY_NUMBER, dunningLetterDistributionOnDemandLookupResult.getAgencyNumber());
                primaryKeys.put("agencyAddressTypeCode", "P");
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
                replacementList.put("#agency.fullAddressInline", returnProperStringValue(fullAddress));
                replacementList.put("#agency.fullName", returnProperStringValue(address.getAgency().getFullName()));
                replacementList.put("#agency.contactName", returnProperStringValue(address.getAgencyContactName()));
                reportStream = PdfFormFillerUtil.populateTemplate(templateFile, replacementList, "");

                // Step3. attach each dunning letter to invoice pdfs.
                finalReportStream = generateListOfInvoicesPdfToPrint(selectedInvoices, reportStream);
            }
            catch (Exception ex) {
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
     * Gets the number of final events in list.
     *
     * @param events The list of events.
     * @return Returns the number of final events.
     */
    private int getFinalEventsCount(List<Event> events) {
        int count = 0;
        if (CollectionUtils.isNotEmpty(events)) {
            for (Event event : events) {
                System.out.println("..........event " + event.getActivityText());
                System.out.println("route status - " + event.getEventRouteStatus());
                if (event.getEventRouteStatus() == null || event.getEventRouteStatus().equals(KewApiConstants.ROUTE_HEADER_FINAL_CD)) {
                    count++;
                }
                System.out.println("count " + count);
            }
        }
        System.out.println(".....count finally is " + count);
        return count;
    }

    /**
     * This method generated the template parameter list to populate the pdf invoices that are attached to the Document.
     *
     * @return
     */
    private Map<String, String> getTemplateParameterList(List<ContractsGrantsInvoiceDocument> invoices) {
        ContractsAndGrantsBillingAward award = invoices.get(0).getAward();
        Map<String, String> parameterMap = new HashMap<String, String>();
        Map primaryKeys = new HashMap<String, Object>();
        parameterMap.put("#award.proposalNumber", returnProperStringValue(award.getProposalNumber()));
        parameterMap.put("#currentDate", returnProperStringValue(FILE_NAME_TIMESTAMP.format(new Date())));
        if (CollectionUtils.isNotEmpty(invoices)) {
            for (int i = 0; i < invoices.size(); i++) {
                parameterMap.put("#invoice[" + i + "].documentNumber", returnProperStringValue(invoices.get(i).getDocumentNumber()));
                parameterMap.put("#invoice[" + i + "].billingDate", returnProperStringValue(invoices.get(i).getBillingDate()));
                parameterMap.put("#invoice[" + i + "].totalAmount", returnProperStringValue(invoices.get(i).getTotalDollarAmount()));
                parameterMap.put("#invoice[" + i + "].customerName", returnProperStringValue(invoices.get(i).getCustomerName()));
                parameterMap.put("#invoice[" + i + "].customerNumber", returnProperStringValue(invoices.get(i).getAccountsReceivableDocumentHeader().getCustomerNumber()));
            }
        }
        if (ObjectUtils.isNotNull(award)) {
            parameterMap.put("#award.awardProjectTitle", returnProperStringValue(award.getAwardProjectTitle()));
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
     * @throws Exception
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
            ZipEntry entry = new ZipEntry("DunningLetters&Invoices-" + FILE_NAME_TIMESTAMP.format(new Date()) + ".pdf");
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
            // add a document
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, invoice.getDocumentNumber());
            List<InvoiceAgencyAddressDetail> agencyAddresses = (List<InvoiceAgencyAddressDetail>) SpringContext.getBean(BusinessObjectService.class).findMatching(InvoiceAgencyAddressDetail.class, map);
            for (InvoiceAgencyAddressDetail agencyAddress : agencyAddresses) {
                ContractsAndGrantsAgencyAddress address;
                Map<String, Object> primaryKeys = new HashMap<String, Object>();
                primaryKeys.put(KFSPropertyConstants.AGENCY_NUMBER, agencyAddress.getAgencyNumber());
                primaryKeys.put("agencyAddressTypeCode", "P");
                address = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObject(ContractsAndGrantsAgencyAddress.class, primaryKeys);
                Note note = SpringContext.getBean(NoteService.class).getNoteByNoteId(agencyAddress.getNoteId());
                if (ObjectUtils.isNotNull(note) && note.getAttachment().getAttachmentFileSize() > 0) {
                    copy.addDocument(new PdfReader(note.getAttachment().getAttachmentContents()));
                }
            }
        }
        copy.close();
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


}
