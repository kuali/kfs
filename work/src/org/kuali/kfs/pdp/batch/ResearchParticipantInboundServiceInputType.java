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
package org.kuali.kfs.pdp.batch;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.batch.service.ExtractPaymentService;
import org.kuali.kfs.pdp.businessobject.LoadPaymentStatus;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentHeader;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.pdp.businessobject.ResearchParticipantPaymentDetail;
import org.kuali.kfs.pdp.businessobject.ResearchParticipantUpload;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.kfs.pdp.service.PaymentFileService;
import org.kuali.kfs.pdp.service.ResearchParticipantPaymentValidationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.kfs.sys.businessobject.MappingCSVReader;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryService;

import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;

// Created for Research Participant Upload
public class ResearchParticipantInboundServiceInputType extends BatchInputFileTypeBase {
    private static Logger LOG = Logger.getLogger(ResearchParticipantInboundServiceInputType.class);
    private static final String FILE_NAME_PREFIX = "research_participant_";
    private static final String PAYMENT_HEADER = "Payment Header";
    private static final String ACCOUNTING_LINE = "Accounting Line";
    private static final String PAYMENT_DETAIL = "Payment Detail";
    private static final String GL_DESCRIPTION = "GL Description";

    private DateTimeService dateTimeService;
    private CustomerProfileService customerProfileService;
    private PaymentFileService paymentFileService;
    private DictionaryValidationService dictionaryValidationService;
    private SequenceAccessorService sequenceAccessorService;
    private CountryService countryService;
    private ResearchParticipantPaymentValidationService researchParticipantPaymentValidationService;
    private ExtractPaymentService extractPaymentService;

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    @Override
    public String getFileTypeIdentifer() {
        return PdpConstants.RESEARCH_PARTICIPANT_INPUT_FILE_TYPE_INDENTIFIER;
    }

    /**
     * Builds the file name using the following construction: All research participant inbound service files start with
     * research_participant_ append the username of the user who is uploading the file then the fileUserIdentifier
     * then the timestamp.
     *
     * @param user who uploaded the file
     * @param parsedFileContents represents collector batch object
     * @param userIdentifier user identifier for user who uploaded file
     * @return String returns file name using the convention mentioned in the description
     *
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(org.kuali.rice.kim.bo.Person, java.lang.Object,
     *      java.lang.String)
     */

    @Override
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifer) {
        return "";
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    @Override
    public void process(String fileName, Object parsedFileContents) {
        LoadPaymentStatus status = new LoadPaymentStatus();
        status.setMessageMap(new MessageMap());
        paymentFileService.loadPayments((PaymentFileLoad)parsedFileContents, status, getFileName(GlobalVariables.getUserSession().getPrincipalName(), null, null));

    }

    public CustomerProfileService getCustomerProfileService() {
        return customerProfileService;
    }

    public void setCustomerProfileService(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    public PaymentFileService getPaymentFileService() {
        return paymentFileService;
    }

    public void setPaymentFileService(PaymentFileService paymentFileService) {
        this.paymentFileService = paymentFileService;
    }

    public DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

    public SequenceAccessorService getSequenceAccessorService() {
        return sequenceAccessorService;
    }

    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }

    public CountryService getCountryService() {
        return countryService;
    }

    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    public ExtractPaymentService getExtractPaymentService() {
        return extractPaymentService;
    }

    public void setExtractPaymentService(ExtractPaymentService extractPaymentService) {
        this.extractPaymentService = extractPaymentService;
    }

    /**
     * Override the superclass method to parse the incoming spreadsheet
     * file and convert into PaymentFileLoad, then return it to the invoker.
     *
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#parse(byte[])
     */
    @Override
    public Object parse(byte[] fileByteContent) throws ParseException {

        List<CollectorBatch> batchList = new ArrayList<CollectorBatch>();
        ResearchParticipantUpload uploadFile = new ResearchParticipantUpload();

        try {

            InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(fileByteContent));
            MappingCSVReader reader = new MappingCSVReader(inputStreamReader);

            String[] fileLine = null;

            while ((fileLine = reader.readNext()) != null) {

                ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy();
                if (fileLine[0].equalsIgnoreCase(PAYMENT_HEADER)) {
                    //If there's already a payment header on the uploadFile,
                    //we'll give error message to the user that something's wrong
                    //with the file format because the file can only have 1 payment
                    //header.
                    if (uploadFile.getPaymentHeader() != null) {
                        GlobalVariables.getMessageMap().putError("uploadFile", PdpKeyConstants.ERROR_BATCH_UPLOAD_BAD_FORMAT, PdpConstants.MULTIPLE_PAYMENT_HEADERS);
                        return null;
                    }
                    PaymentHeader ph = parsePaymentHeader(fileLine, reader, strat);
                    uploadFile.setPaymentHeader(ph);
                }
                else if (fileLine[0].equalsIgnoreCase(ACCOUNTING_LINE)) {
                    //If there's already an accounting line on the uploadFile,
                    //we'll give error message to the user that something's wrong
                    //with the file format because the file can only have 1
                    //accountingLine
                    if (uploadFile.getPaymentAccountDetail() != null) {
                        GlobalVariables.getMessageMap().putError("uploadFile", PdpKeyConstants.ERROR_BATCH_UPLOAD_BAD_FORMAT, PdpConstants.MULTIPLE_ACCOUNTS);
                        return null;
                    }
                    PaymentAccountDetail ac = parseAccountingLine(fileLine, reader, strat);
                    uploadFile.setPaymentAccountDetail(ac);
                }
                else if (fileLine[0].equalsIgnoreCase(PAYMENT_DETAIL)) {
                    ResearchParticipantPaymentDetail pd = parsePaymentDetail(fileLine, reader, strat);
                    uploadFile.addPaymentDetail(pd);
                }
                else if (fileLine[0].equalsIgnoreCase(GL_DESCRIPTION)) {
                    uploadFile.setGenericDescription(fileLine[1]);
                }
            }
        }
        catch (IOException ioe) {
            LOG.error("Error encountered reading from file content", ioe);
            throw new ParseException("Error encountered reading from file content", ioe);
        }

        if ((uploadFile.getPaymentDetails().size() < 1) || uploadFile.getPaymentHeader() == null || uploadFile.getPaymentAccountDetail() == null) {
            GlobalVariables.getMessageMap().putError("uploadFile", PdpKeyConstants.ERROR_BATCH_UPLOAD_BAD_FORMAT, KFSConstants.EMPTY_STRING);
            return null;
        }
        PaymentFileLoad paymentFileLoad = convertToPaymentFileLoad(uploadFile);

        return paymentFileLoad;
    }

    /**
     *
     * Converts the ResearchParticipantUpload which represents the spreadsheet
     * into the existing PaymentFileLoad.
     * @param uploadFile
     * @return
     */
    protected PaymentFileLoad convertToPaymentFileLoad(ResearchParticipantUpload uploadFile) {
        PaymentFileLoad paymentFileLoad = new PaymentFileLoad();
        paymentFileLoad.setChart(uploadFile.getPaymentHeader().getChartOfAccountsCode());
        paymentFileLoad.setUnit(uploadFile.getPaymentHeader().getUnit());
        paymentFileLoad.setSubUnit(uploadFile.getPaymentHeader().getSubUnit());
        paymentFileLoad.setCreationDate(uploadFile.getPaymentHeader().getCreationDate());
        paymentFileLoad.setPaymentCount(uploadFile.getPaymentDetails().size());
        paymentFileLoad.setPaymentTotalAmount(uploadFile.getPaymentTotalAmount());
        paymentFileLoad.setPaymentGroups(createPaymentGroups(uploadFile));
        KualiInteger batchId = new KualiInteger(sequenceAccessorService.getNextAvailableSequenceNumber(PdpConstants.SequenceNames.PDP_PMT_FIL_ID, PaymentFileLoad.class).intValue());
        paymentFileLoad.setBatchId(batchId);

        return paymentFileLoad;
    }

    /**
     * Creates the list of payment groups given the upload file.
     *
     * @param uploadFile
     * @return
     */
    protected List<PaymentGroup> createPaymentGroups(ResearchParticipantUpload uploadFile) {
        List<PaymentGroup> result = new ArrayList<PaymentGroup>();
        for (ResearchParticipantPaymentDetail detail : uploadFile.getPaymentDetails()) {
            String payeeName = detail.getPayeeName();
            PaymentGroup paymentGroup = new PaymentGroup();
            paymentGroup.setPayeeName(payeeName);
            paymentGroup.setLine1Address(detail.getAddressLine1());
            paymentGroup.setLine2Address(detail.getAddressLine2());
            paymentGroup.setLine3Address(detail.getAddressLine3());
            paymentGroup.setCity(detail.getCity());
            paymentGroup.setState(detail.getState());
            paymentGroup.setZipCd(detail.getZip());
            paymentGroup.setPayeeIdTypeCd(uploadFile.getPaymentHeader().getVendorOrEmployee());
            Country defaultCountry = countryService.getDefaultCountry();
            paymentGroup.setCountry(defaultCountry.getCode());
            paymentGroup.setPaymentDate(uploadFile.getPaymentHeader().getPaymentDate());
            paymentGroup.setPaymentStatusCode(KFSConstants.PdpConstants.PAYMENT_OPEN_STATUS_CODE);

            PaymentDetail paymentDetail = new PaymentDetail();
            paymentDetail.setInvoiceDate(uploadFile.getPaymentHeader().getPaymentDate());
            paymentDetail.setCustPaymentDocNbr(uploadFile.getPaymentHeader().getSourceDocNumber());
            paymentDetail.setFinancialSystemOriginCode(KFSConstants.ORIGIN_CODE_KUALI);

            List<String> formattedCheckNoteLines = this.getExtractPaymentService().formatCheckNoteLines(detail.getCheckStubText());
            int count = 1;
            for (String noteText : formattedCheckNoteLines) {
                PaymentNoteText note = new PaymentNoteText();
                note.setCustomerNoteText(noteText);
                note.setCustomerNoteLineNbr(new KualiInteger(count++));
                paymentDetail.addNote(note);
            }

            uploadFile.getPaymentAccountDetail().setAccountNetAmount(detail.getAmount());

            PaymentAccountDetail ac = new PaymentAccountDetail();
            ac.setFinChartCode(uploadFile.getPaymentAccountDetail().getFinChartCode());
            ac.setAccountNbr(uploadFile.getPaymentAccountDetail().getAccountNbr());
            ac.setFinObjectCode(uploadFile.getPaymentAccountDetail().getFinObjectCode());
            ac.setOrgReferenceId(uploadFile.getPaymentAccountDetail().getOrgReferenceId());
            ac.setAccountNetAmount(uploadFile.getPaymentAccountDetail().getAccountNetAmount());

            // change nulls into ---'s for the fields that need it
            if (StringUtils.isBlank(uploadFile.getPaymentAccountDetail().getFinSubObjectCode())) {
                ac.setFinSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            else {
                ac.setFinSubObjectCode(uploadFile.getPaymentAccountDetail().getFinSubObjectCode());
            }
            if (StringUtils.isBlank(uploadFile.getPaymentAccountDetail().getSubAccountNbr())) {
                ac.setSubAccountNbr(KFSConstants.getDashSubAccountNumber());
            }
            else {
                ac.setSubAccountNbr(uploadFile.getPaymentAccountDetail().getSubAccountNbr());
            }
            if (StringUtils.isBlank(uploadFile.getPaymentAccountDetail().getProjectCode())) {
                ac.setProjectCode(KFSConstants.getDashProjectCode());
            }
            else {
                ac.setProjectCode(uploadFile.getPaymentAccountDetail().getProjectCode());
            }

            paymentDetail.addAccountDetail(ac);
            paymentGroup.addPaymentDetails(paymentDetail);
            result.add(paymentGroup);
        }

        return result;
    }

    /**
     * Parses the payment header information in the file.
     *
     * @param fileLine
     * @param reader
     * @param strat
     * @return
     */
    protected PaymentHeader parsePaymentHeader(String[] fileLine, MappingCSVReader reader, ColumnPositionMappingStrategy strat) {
        strat.setType(PaymentHeader.class);
        String[] headerColumns = new String[] { PdpConstants.PaymentHeader.CHART,
                PdpConstants.PaymentHeader.UNIT, PdpConstants.PaymentHeader.SUBUNIT,
                PdpConstants.PaymentHeader.CREATION_DATE, PdpConstants.PaymentHeader.VENDOR_OR_EMPLOYEE,
                PdpConstants.PaymentHeader.SOURCE_DOC_NUMBER, PdpConstants.PaymentHeader.PAYMENT_DATE};
        strat.setColumnMapping(headerColumns);
        String[] newNextLine = Arrays.copyOfRange(fileLine, 1, fileLine.length);
        PaymentHeader ph = (PaymentHeader) reader.processLine(strat, newNextLine);
        return ph;
    }

    /**
     * Parses the accounting line information in the file.
     *
     * @param fileLine
     * @param reader
     * @param strat
     * @return
     */
    protected PaymentAccountDetail parseAccountingLine(String[] fileLine, MappingCSVReader reader, ColumnPositionMappingStrategy strat) {
        strat.setType(PaymentAccountDetail.class);
        String[] headerColumns = new String[] { PdpConstants.PaymentAccountDetail.CHART,
                PdpConstants.PaymentAccountDetail.ACCOUNT_NBR,
                PdpConstants.PaymentAccountDetail.SUB_ACCOUNT_NBR, PdpConstants.PaymentAccountDetail.OBJECT_CODE,
                PdpConstants.PaymentAccountDetail.SUB_OBJECT_CODE, PdpConstants.PaymentAccountDetail.PROJECT_CODE,
                PdpConstants.PaymentAccountDetail.ORG_REF_ID };
        strat.setColumnMapping(headerColumns);
        String[] newNextLine = Arrays.copyOfRange(fileLine, 1, fileLine.length);
        PaymentAccountDetail ac = (PaymentAccountDetail)reader.processLine(strat, newNextLine);
        return ac;

    }

    /**
     * Parses the payment detail information in the file.
     *
     * @param fileLine
     * @param reader
     * @param strat
     * @return
     */
    protected ResearchParticipantPaymentDetail parsePaymentDetail(String[] fileLine, MappingCSVReader reader, ColumnPositionMappingStrategy strat) {
        strat.setType(ResearchParticipantPaymentDetail.class);
        String[] headerColumns = new String[] { PdpConstants.PaymentDetail.PAYEE_NAME,
                PdpConstants.PaymentDetail.ADDRESS_LINE_1, PdpConstants.PaymentDetail.ADDRESS_LINE_2,
                PdpConstants.PaymentDetail.ADDRESS_LINE_3, PdpConstants.PaymentDetail.CITY,
                PdpConstants.PaymentDetail.STATE, PdpConstants.PaymentDetail.ZIP,
                PdpConstants.PaymentDetail.CHECK_STUB_TEXT, PdpConstants.PaymentDetail.AMOUNT };
        strat.setColumnMapping(headerColumns);
        String[] newNextLine = Arrays.copyOfRange(fileLine, 1, fileLine.length);
        ResearchParticipantPaymentDetail pd = (ResearchParticipantPaymentDetail) reader.processLine(strat, newNextLine);
        return pd;
    }

    @Override
    public String getTitleKey() {
        return PdpKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_RESEARCH_PARTICIPANT_FILE;
    }

    @Override
    public String getAuthorPrincipalName(File file) {
        return org.apache.commons.lang.StringUtils.substringBetween(file.getName(), FILE_NAME_PREFIX, PdpConstants.FILE_NAME_PART_DELIMITER);
    }

    /**
     * Validates the information in the upload file using the doPaymentFileValidation
     * in the existing paymentFileService from the foundation. In addition to that,
     * it's also going to invoke Data Dictionary Validation for the PaymentFileLoad,
     * for all the PaymentGroup objects and all of the PaymentDetail and PaymentAccountDetail
     * objects to do some basic format validation such as length, requiredness, etc in place of
     * the xml schema validation.
     *
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    @Override
    public boolean validate(Object parsedFileContents) {
        PaymentFileLoad paymentFile = (PaymentFileLoad)parsedFileContents;

        dictionaryValidationService.validateBusinessObject(paymentFile);
        for (PaymentGroup paymentGroup : paymentFile.getPaymentGroups()) {
            dictionaryValidationService.validateBusinessObject(paymentGroup);
            for (PaymentDetail paymentDetail : paymentGroup.getPaymentDetails()) {
                dictionaryValidationService.validateBusinessObject(paymentDetail);
                for (PaymentAccountDetail account : paymentDetail.getAccountDetail()) {
                    dictionaryValidationService.validateBusinessObject(account);
                }
            }
        }

        MessageMap errorMessageMap = GlobalVariables.getMessageMap();

        boolean isAccountDetailValid = this.getResearchParticipantPaymentValidationService().validatePaymentAccount(paymentFile, errorMessageMap);
        if(isAccountDetailValid){
            paymentFileService.doPaymentFileValidation(paymentFile, errorMessageMap);
        }

        return paymentFile.isPassedValidation();
    }

    /**
     * Gets the researchParticipantPaymentValidationService attribute.
     * @return Returns the researchParticipantPaymentValidationService.
     */
    public ResearchParticipantPaymentValidationService getResearchParticipantPaymentValidationService() {
        return researchParticipantPaymentValidationService;
    }

    /**
     * Sets the researchParticipantPaymentValidationService attribute value.
     * @param researchParticipantPaymentValidationService The researchParticipantPaymentValidationService to set.
     */
    public void setResearchParticipantPaymentValidationService(ResearchParticipantPaymentValidationService researchParticipantPaymentValidationService) {
        this.researchParticipantPaymentValidationService = researchParticipantPaymentValidationService;
    }

    /**
     * For Research Participant Upload, we intentionally don't want to save
     * the input file for security purposes. Therefore this method will return false.
     *
     * @see org.kuali.kfs.sys.batch.BatchInputFileTypeBase#shouldSave()
     */
    @Override
    public boolean shouldSave() {
        return false;
    }
}
