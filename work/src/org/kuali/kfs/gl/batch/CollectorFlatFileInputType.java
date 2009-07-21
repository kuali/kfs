/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.gl.batch;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.gl.batch.service.CollectorHelperService;
import org.kuali.kfs.gl.businessobject.CollectorDetail;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.service.DateTimeService;
import org.springframework.util.StringUtils;;

public class CollectorFlatFileInputType extends BatchInputFileTypeBase {
    private static Logger LOG = Logger.getLogger(CollectorFlatFileInputType.class);
    private AccountService accountService;
    private DateTimeService dateTimeService;
    private CollectorHelperService collectorHelperService;
    private static final String FILE_NAME_PREFIX = "gl_collectorflatfile_";

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getAuthorPrincipalName(java.io.File)
     */
    public String getAuthorPrincipalName(File file) {
        return org.apache.commons.lang.StringUtils.substringBetween(file.getName(), FILE_NAME_PREFIX, "_");
    }

    /**
     * Builds the file name using the following construction: All collector files start with gl_collectorflatfile_ append the chartorg
     * from the batch header append the username of the user who is uploading the file then the user supplied indentifier finally
     * the timestamp
     * 
     * @param user who uploaded the file
     * @param parsedFileContents represents collector batch object
     * @param userIdentifier user identifier for user who uploaded file
     * @return String returns file name using the convention mentioned in the description
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(org.kuali.rice.kim.bo.Person, java.lang.Object,
     *      java.lang.String)
     */
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifer) {
        CollectorBatch collectorBatch = (CollectorBatch) parsedFileContents;
        
        String fileName = FILE_NAME_PREFIX;
        fileName += principalName;
        if (org.apache.commons.lang.StringUtils.isNotBlank(fileUserIdentifer)) {
            fileName += "_" + fileUserIdentifer;
        }
        fileName += "_" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate());

        // remove spaces in filename
        fileName = org.apache.commons.lang.StringUtils.remove(fileName, " ");

        return fileName;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    public String getFileTypeIdentifer() {
        return KFSConstants.COLLECTOR_FLAT_FILE_TYPE_INDENTIFIER;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    public String getTitleKey() {
        return KFSKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_COLLECTOR_FLAT_FILE;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#parse(byte[])
     */
    public Object parse(byte[] fileByteContent) throws ParseException {
        List<CollectorBatch> list = new ArrayList<CollectorBatch>();
        CollectorBatch currentBatch = null;
        BufferedReader bufferedFileReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileByteContent)));
        String fileLine;
        Date curDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        UniversityDate universityDate = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
        int lineNumber = 0;
        int lineNumberOfLastHeader = -1;
        
        try {
            while ((fileLine = bufferedFileReader.readLine()) != null) {
                lineNumber++;
                if (fileLine.length() >= 27) {  //if no rec_type, probably a blank or almost blank line at end of file
                    String recordType = fileLine.substring(25, 27);
                    //this chunk translated from collectorDigesterRules.xml
                    if ("HD".equals(recordType)) {  // this is a header line
                        currentBatch = createCollectorBatch(fileLine);
                    }
                    else if ("DT".equals(recordType)) {  //ID billing detail
                        CollectorDetail collectorDetail = createCollectorDetail(fileLine, curDate, universityDate);
                        currentBatch.addCollectorDetail(collectorDetail);
                    }
                    else if ("TL".equals(recordType)) {  //trailer record
                        updateCollectorDetailWithTrailerRecords(currentBatch, fileLine);
                        currentBatch = null;
                        lineNumberOfLastHeader = -1;
                    }
                    else {  // accounting record/origin entry
                        OriginEntryFull originEntry = createOriginEntry(fileLine, curDate, universityDate);
                        currentBatch.addOriginEntry(originEntry);
                    }
                }
            }
        }
        catch (IOException e) {
            // probably won't happen since we're reading from a byte array, but just in case
            LOG.error("Error encountered reading from file content", e);
            throw new ParseException("Error encountered reading from file content", e);
        }
        return list;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#process(java.lang.String, java.lang.Object)
     */
    public void process(String fileName, Object parsedFileContents) {
        // do not do anything
    }

    protected Date parseSqlDate(String date) throws ParseException {
        try {
            return new Date(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime());
        }
        catch (java.text.ParseException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
    
    protected String addDecimalPoint (String amount) {
        if (!amount.contains(".")) {  //have to add decimal point if it's missing
            int length = amount.length();
            amount = amount.substring(0, length - 2) + "." + amount.substring(length - 2, length);
        }
        return amount;
    }
    
    protected CollectorBatch createCollectorBatch(String headerLine) {
        CollectorBatch newBatch = new CollectorBatch();
        newBatch.setChartOfAccountsCode(StringUtils.trimTrailingWhitespace(headerLine.substring(4, 6)));
        newBatch.setOrganizationCode(StringUtils.trimTrailingWhitespace(headerLine.substring(6, 10)));
        newBatch.setTransmissionDate(parseSqlDate(headerLine.substring(15, 25)));
        //TODO not sure if this is the best thing to do, as there is no equivalent to this in fis_id_billing.pl (it just uses first 28 characters for dupe check, so I guess if blank it should be OK to set it to 0 here)
        if (!headerLine.substring(27, 28).equals(" ")) {
            newBatch.setBatchSequenceNumber(new Integer(headerLine.substring(27, 28)));
        } else {
            newBatch.setBatchSequenceNumber(0);
        }
        newBatch.setEmailAddress(StringUtils.trimTrailingWhitespace(headerLine.substring(28, 68)));
        newBatch.setPersonUserID(StringUtils.trimTrailingWhitespace(headerLine.substring(68, 98)));
        newBatch.setDepartmentName(StringUtils.trimTrailingWhitespace(headerLine.substring(98, 128)));
        newBatch.setMailingAddress(StringUtils.trimTrailingWhitespace(headerLine.substring(128, 158)));
        newBatch.setCampusCode(StringUtils.trimTrailingWhitespace(headerLine.substring(158, 160)));
        newBatch.setPhoneNumber(StringUtils.trimTrailingWhitespace(headerLine.substring(160)));
        return newBatch;
    }
    
    protected CollectorDetail createCollectorDetail(String detailLine, Date curDate, UniversityDate universityDate) {
        CollectorDetail collectorDetail = new CollectorDetail();
        collectorDetail.setCreateDate(curDate);
        collectorDetail.setUniversityFiscalYear(new Integer(StringUtils.trimTrailingWhitespace(detailLine.substring(0, 4))));
        collectorDetail.setAccountNumber(StringUtils.trimTrailingWhitespace(detailLine.substring(6, 13)));
        if (!detailLine.substring(4, 6).equals("  ")) {
            collectorDetail.setChartOfAccountsCode(StringUtils.trimTrailingWhitespace(detailLine.substring(4, 6)));
        }
        else {
            Collection<Account> accounts = accountService.getAccountsForAccountNumber(collectorDetail.getAccountNumber());
            if (accounts.size() != 1)
                collectorDetail.setChartOfAccountsCode("  ");
            else
                collectorDetail.setChartOfAccountsCode(accounts.iterator().next().getChartOfAccountsCode());
        }
        collectorDetail.setSubAccountNumber(StringUtils.trimTrailingWhitespace(detailLine.substring(13, 18)));  //assume something later is setting to single space if blank
        collectorDetail.setFinancialObjectCode(StringUtils.trimTrailingWhitespace(detailLine.substring(18, 22)));
        collectorDetail.setFinancialSubObjectCode(StringUtils.trimTrailingWhitespace(detailLine.substring(22, 25)));  //assume something later is setting to single space if blank
        collectorDetail.setUniversityFiscalPeriodCode(universityDate.getUniversityFiscalAccountingPeriod());
        collectorDetail.setCollectorDetailSequenceNumber(StringUtils.trimTrailingWhitespace(detailLine.substring(27, 29)));  //assume something later is setting to single space if blank
        collectorDetail.setFinancialDocumentTypeCode(StringUtils.trimTrailingWhitespace(detailLine.substring(29, 33)));
        collectorDetail.setDocumentNumber(StringUtils.trimTrailingWhitespace(detailLine.substring(33, 42)));
        collectorDetail.setCollectorDetailItemAmount(addDecimalPoint(StringUtils.trimTrailingWhitespace(detailLine.substring(42, 51))));
        if (detailLine.substring(51, 52).toUpperCase().equals("C")) {
            collectorDetail.setCollectorDetailItemAmount(collectorDetail.getCollectorDetailItemAmount().negated());
        }
        collectorDetail.setCollectorDetailNoteText(StringUtils.trimTrailingWhitespace(detailLine.substring(52)));
      //ignoring these fields for now b/c they're not in FIS (or incoming ID billing files from departments)
//      <call-method-rule pattern="balanceTypeCode"                        methodname="setFinancialBalanceTypeCode" paramcount="0" />
//      <call-method-rule pattern="objectTypeCode"                         methodname="setFinancialObjectTypeCode" paramcount="0" />
//      <call-method-rule pattern="originationCode"                        methodname="setFinancialSystemOriginationCode" paramcount="0" />
        if (collectorDetail.getSubAccountNumber() == null || collectorDetail.getSubAccountNumber().equals("")) {
            collectorDetail.setSubAccountNumber(" ");
        }
        if (collectorDetail.getFinancialSubObjectCode() == null || collectorDetail.getFinancialSubObjectCode().equals("")) {
            collectorDetail.setFinancialSubObjectCode(" ");
        }
        if (collectorDetail.getCollectorDetailSequenceNumber() == null || collectorDetail.getCollectorDetailSequenceNumber().equals("")) {
            collectorDetail.setCollectorDetailSequenceNumber(" ");
        }
        return collectorDetail;
    }
    
    protected void updateCollectorDetailWithTrailerRecords(CollectorBatch currentBatch, String fileLine) {
        currentBatch.setTotalRecords(new Integer(StringUtils.trimTrailingWhitespace(fileLine.substring(46,51))));
        currentBatch.setTotalAmount(addDecimalPoint(StringUtils.trimTrailingWhitespace(fileLine.substring(92,107))));
    }
    
    protected OriginEntryFull createOriginEntry(String fileLine, Date curDate, UniversityDate universityDate) {
        OriginEntryFull originEntry = new OriginEntryFull();
        //TODO do following if & while a better way
        fileLine = org.apache.commons.lang.StringUtils.chomp(fileLine);
        while (fileLine.length() < 172) {
            fileLine = fileLine + " ";
        }
        if (!fileLine.substring(0, 4).equals("    ")) {
            originEntry.setUniversityFiscalYear(new Integer(fileLine.substring(0, 4)));
        } else {
            originEntry.setUniversityFiscalYear(universityDate.getUniversityFiscalYear());
        }
        originEntry.setAccountNumber(StringUtils.trimTrailingWhitespace(fileLine.substring(6, 13)));
        if (!fileLine.substring(4, 6).equals("  ")) {
            originEntry.setChartOfAccountsCode(StringUtils.trimTrailingWhitespace(fileLine.substring(4, 6)));
        } else {
            Collection<Account> accounts = accountService.getAccountsForAccountNumber(originEntry.getAccountNumber());
            if (accounts.size() != 1)
                originEntry.setChartOfAccountsCode("  ");
            else
                originEntry.setChartOfAccountsCode(accounts.iterator().next().getChartOfAccountsCode());
        }
        originEntry.setSubAccountNumber(StringUtils.trimTrailingWhitespace(fileLine.substring(13, 18)));
        originEntry.setFinancialObjectCode(StringUtils.trimTrailingWhitespace(fileLine.substring(18, 22)));
        originEntry.setFinancialSubObjectCode(StringUtils.trimTrailingWhitespace(fileLine.substring(22, 25)));
        originEntry.setFinancialBalanceTypeCode(StringUtils.trimTrailingWhitespace(fileLine.substring(25, 27)));
        originEntry.setFinancialObjectTypeCode(StringUtils.trimTrailingWhitespace(fileLine.substring(27, 29)));
        if (!fileLine.substring(29, 31).equals("  ")) {
            originEntry.setUniversityFiscalPeriodCode(StringUtils.trimTrailingWhitespace(fileLine.substring(29, 31)));
        } else {
            originEntry.setUniversityFiscalPeriodCode(universityDate.getUniversityFiscalAccountingPeriod());
        }
        originEntry.setFinancialDocumentTypeCode(StringUtils.trimTrailingWhitespace(fileLine.substring(31, 35)));
        originEntry.setFinancialSystemOriginationCode(StringUtils.trimTrailingWhitespace(fileLine.substring(35, 37)));
        originEntry.setDocumentNumber(StringUtils.trimTrailingWhitespace(fileLine.substring(37, 46)));
        if (!fileLine.substring(46, 51).equals("     ")) {
            originEntry.setTransactionLedgerEntrySequenceNumber(new Integer(StringUtils.trimTrailingWhitespace(fileLine.substring(46, 51))));
        } else {
            originEntry.setTransactionLedgerEntrySequenceNumber(new Integer(1));
        }
        originEntry.setTransactionLedgerEntryDescription(StringUtils.trimTrailingWhitespace(fileLine.substring(51, 91)));
        originEntry.setTransactionLedgerEntryAmount(addDecimalPoint(StringUtils.trimWhitespace(fileLine.substring(91, 107))));  //sometimes this has leading whitespace too
        originEntry.setTransactionDebitCreditCode(StringUtils.trimTrailingWhitespace(fileLine.substring(107, 108)));
        if (!fileLine.substring(108, 118).equals("          ")) {
            originEntry.setTransactionDate(parseSqlDate(fileLine.substring(108, 118)));
        }
        else {
            originEntry.setTransactionDate(curDate);
        }
        originEntry.setOrganizationDocumentNumber(StringUtils.trimTrailingWhitespace(fileLine.substring(118, 128)));
        originEntry.setProjectCode(StringUtils.trimTrailingWhitespace(fileLine.substring(128, 138)));
        originEntry.setOrganizationReferenceId(StringUtils.trimTrailingWhitespace(fileLine.substring(138, 146)));
        originEntry.setReferenceFinancialDocumentTypeCode(StringUtils.trimTrailingWhitespace(fileLine.substring(146, 150)));
        originEntry.setReferenceFinancialSystemOriginationCode(StringUtils.trimTrailingWhitespace(fileLine.substring(150, 152)));
        originEntry.setReferenceFinancialDocumentNumber(StringUtils.trimTrailingWhitespace(fileLine.substring(152, 161)));
        if (!fileLine.substring(161,171).equals("          ")) {
            originEntry.setFinancialDocumentReversalDate(parseSqlDate(fileLine.substring(161, 171)));
        }
        originEntry.setTransactionEncumbranceUpdateCode(StringUtils.trimTrailingWhitespace(fileLine.substring(171, 172)));
        if (originEntry.getSubAccountNumber() == null || originEntry.getSubAccountNumber().equals("")) {
            originEntry.setSubAccountNumber(" ");
        }
        if (originEntry.getFinancialSubObjectCode() == null || originEntry.getFinancialSubObjectCode().equals("")) {
            originEntry.setFinancialSubObjectCode(" ");
        }
        return originEntry;
    }


    public boolean validate(Object parsedFileContents) {
        boolean isValid = collectorHelperService.performValidation((CollectorBatch) parsedFileContents);
        if (isValid) {
            isValid = collectorHelperService.checkTrailerTotals((CollectorBatch) parsedFileContents, null);
        }
    
        return isValid;
    }
    
    /**
     * Sets the accountService attribute value.
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the collectorHelperService attribute value.
     * @param collectorHelperService The collectorHelperService to set.
     */
    public void setCollectorHelperService(CollectorHelperService collectorHelperService) {
        this.collectorHelperService = collectorHelperService;
    }
}
