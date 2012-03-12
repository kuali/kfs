/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.gl.batch;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.gl.batch.service.CollectorHelperService;
import org.kuali.kfs.gl.batch.service.impl.OriginEntryTotals;
import org.kuali.kfs.gl.businessobject.CollectorDetail;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.springframework.util.AutoPopulatingList;

public class CollectorFlatFileInputType extends BatchInputFileTypeBase {
    protected static Logger LOG = Logger.getLogger(CollectorFlatFileInputType.class);
    protected DateTimeService dateTimeService;
    protected CollectorHelperService collectorHelperService;
    protected static final String FILE_NAME_PREFIX = "gl_collectorflatfile_";

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
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(org.kuali.rice.kim.api.identity.Person, java.lang.Object,
     *      java.lang.String)
     */
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifer) {
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
        List<CollectorBatch> batchList = new ArrayList<CollectorBatch>();
        CollectorBatch currentBatch = null;
        BufferedReader bufferedFileReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileByteContent)));
        String fileLine;
        Date curDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        UniversityDate universityDate = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
        int lineNumber = 0;
        
        //temporary storage of the balance type map of each accounting record/origin entry
        Map<String, String> accountRecordBalanceTypeMap = new HashMap<String, String>();
        
        try {
            while ((fileLine = bufferedFileReader.readLine()) != null) {
                lineNumber++;
                String preprocessedLine = preprocessLine(fileLine);
                if (fileLine.length() >= 27) {  //if no rec_type, probably a blank or almost blank line at end of file
                    String recordType = extractRecordType(preprocessedLine);
                    if ("HD".equals(recordType)) {  // this is a header line
                        ensurePreviousBatchTerminated(currentBatch, lineNumber);
                        try {
                            currentBatch = createCollectorBatch(preprocessedLine, batchList);
                        }
                        catch (RuntimeException e) {
                            if (currentBatch == null){
                                currentBatch = new CollectorBatch();
                                batchList.add(currentBatch);
                            }
                            currentBatch.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CUSTOM, e.getMessage());
                        }
                        
                    }
                    else if ("DT".equals(recordType)) {  //ID billing detail
                        currentBatch = createHeaderlessBatchIfNecessary(currentBatch, batchList, lineNumber);
                        try {
                            CollectorDetail collectorDetail = createCollectorDetail(preprocessedLine, accountRecordBalanceTypeMap, curDate, universityDate, lineNumber, currentBatch.getMessageMap());
                            currentBatch.addCollectorDetail(collectorDetail);
                        }
                        catch (RuntimeException e) {
                            currentBatch.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CUSTOM, e.getMessage());
                        }
                    }
                    else if ("TL".equals(recordType)) {  //trailer record
                        currentBatch = createHeaderlessBatchIfNecessary(currentBatch, batchList, lineNumber);
                        try {
                            updateCollectorDetailWithTrailerRecords(currentBatch, preprocessedLine, lineNumber);
                        }
                        catch (RuntimeException e) {
                            currentBatch.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CUSTOM, e.getMessage());
                        }
                        currentBatch = null;
                    }
                    else {  // accounting record/origin entry
                        currentBatch = createHeaderlessBatchIfNecessary(currentBatch, batchList, lineNumber);
                        try {
                            OriginEntryFull originEntry = createOriginEntry(preprocessedLine, curDate, universityDate, lineNumber, currentBatch.getMessageMap());
                            currentBatch.addOriginEntry(originEntry);
                            
                            //use origin entry to derive a key to store the balance type to the map
                            String accountRecordKey = generateAccountRecordBalanceTypeKey(originEntry);
                            accountRecordBalanceTypeMap.put(accountRecordKey, originEntry.getFinancialBalanceTypeCode());
                        }
                        catch (RuntimeException e) {
                            currentBatch.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CUSTOM, e.getMessage());
                        }
                    }
                }
            }
            // in case we come across a batch that didn't have a trailer record
            ensurePreviousBatchTerminated(currentBatch, lineNumber);
        } catch (Exception e) {
            LOG.error(e.getMessage() + " happend in CollectorFlatFileInputType.parse.", e);
            throw new ParseException(e.getMessage() + " happend in CollectorFlatFileInputType.parse.", e);
        }

        
        for (CollectorBatch batch : batchList) {
            OriginEntryTotals totals = new OriginEntryTotals();
            totals.addToTotals(batch.getOriginEntries().iterator());
            batch.setOriginEntryTotals(totals);
            
            // now copy all the messages from the per-batch message map into the global map, so that we can display the messages on the upload screen
            copyAllMessages(batch.getMessageMap(), GlobalVariables.getMessageMap());
        }
        return batchList;
    }

    /**
     * Account record balance type key 
     * Fiscal Year - Chart Code - Account Number - Sub-account Number - Object code - Sub-object Code
     * 
     * For the two optional fields sub-account and sub-object code, create an additional filter to replace
     * the usual place holder - with spaces
     * 
     * @param originEntry
     * @return
     */
    private String generateAccountRecordBalanceTypeKey(OriginEntryFull originEntry) {
        StringBuilder builder = new StringBuilder();
        builder.append(originEntry.getUniversityFiscalYear()).append("|")
            .append(originEntry.getChartOfAccountsCode()).append("|")
            .append(originEntry.getAccountNumber()).append("|")
            .append(StringUtils.replace(originEntry.getSubAccountNumber(), "-", "")).append("|")
            .append(originEntry.getFinancialObjectCode()).append("|")
            .append(StringUtils.replace(originEntry.getFinancialSubObjectCode(), "-", ""));
        return builder.toString();
    }

    private void copyAllMessages(MessageMap sourceMap, MessageMap destMap) {
        copyAllMessagesHelper(sourceMap.getInfoMessages(), "info", destMap);
        copyAllMessagesHelper(sourceMap.getWarningMessages(), "warning", destMap);
        copyAllMessagesHelper(sourceMap.getErrorMessages(), "error", destMap);
    }
    
    private void copyAllMessagesHelper(Map<String, AutoPopulatingList<ErrorMessage>> sourceMessages, String type, MessageMap destMap) {
        for (String key : sourceMessages.keySet()) {
            AutoPopulatingList<ErrorMessage> messages = sourceMessages.get(key);
            if (messages != null) {
                for (Object o : messages) {
                    ErrorMessage message = (ErrorMessage) o;
                    if ("info".equals(type)) {
                        destMap.putInfoWithoutFullErrorPath(key, message.getErrorKey(), message.getMessageParameters());
                    }
                    else if ("warning".equals(type)) {
                        destMap.putWarningWithoutFullErrorPath(key, message.getErrorKey(), message.getMessageParameters());
                    }
                    else if ("error".equals(type)) {
                        destMap.putErrorWithoutFullErrorPath(key, message.getErrorKey(), message.getMessageParameters());
                    }
                    else {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }
    }
    protected void ensurePreviousBatchTerminated(CollectorBatch currentBatch, int lineNumber) {
        if (currentBatch != null) {
            // we've encountered a new header, when we're still not done parsing the previous batch (i.e. trailer not found).  This is an error, and mark the old batch as such
            currentBatch.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.MISSING_TRAILER_RECORD, Integer.toString(lineNumber));
            currentBatch.setTotalAmount("0");
            currentBatch.setTotalRecords(0);
        }
    }
    
    protected CollectorBatch createHeaderlessBatchIfNecessary(CollectorBatch currentBatch, List<CollectorBatch> batchList, int lineNumber) {
        if (currentBatch != null) {
            return currentBatch;
        }
        CollectorBatch headerlessBatch = new CollectorBatch();
        headerlessBatch.setHeaderlessBatch(true);
        headerlessBatch.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.MISSING_HEADER_RECORD, Integer.toString(lineNumber));
        batchList.add(headerlessBatch);
        return headerlessBatch;
    }
    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#process(java.lang.String, java.lang.Object)
     */
    public void process(String fileName, Object parsedFileContents) {
        // do not do anything
    }

    protected String addDecimalPoint (String amount) {
        if (!amount.contains(".")) {  //have to add decimal point if it's missing
            int length = amount.length();
            amount = amount.substring(0, length - 2) + "." + amount.substring(length - 2, length);
        }
        return amount;
    }
        
    protected CollectorBatch createCollectorBatch(String headerLine, List<CollectorBatch> batchList) {
        CollectorBatch newBatch = new CollectorBatch();
        newBatch.setFromTextFileForCollectorBatch(headerLine);
        batchList.add(newBatch);
        
        return newBatch;
    }
    
    protected CollectorDetail createCollectorDetail(String detailLine, Map<String, String>accountRecordBalanceTypeMap, Date curDate, UniversityDate universityDate, int lineNumber, MessageMap messageMap) {
        CollectorDetail collectorDetail = new CollectorDetail();
        collectorDetail.setFromFileForCollectorDetail(detailLine, accountRecordBalanceTypeMap, curDate, universityDate, lineNumber, messageMap);
        
        return collectorDetail;
    }
    
    protected void updateCollectorDetailWithTrailerRecords(CollectorBatch currentBatch, String fileLine, int lineNumber) {
        currentBatch.setFromTextFileForCollectorBatchTrailerRecord(fileLine, lineNumber);
    }
    
    protected OriginEntryFull createOriginEntry(String fileLine, Date curDate, UniversityDate universityDate, int lineNumber, MessageMap messageMap) {
        OriginEntryFull originEntry = new OriginEntryFull();
        try{        
            List<Message> originEntryErrorMessages = originEntry.setFromTextFileForBatch(fileLine, lineNumber);

            if (null == originEntry.getTransactionDate()) {
                originEntry.setTransactionDate(curDate);
            }
            
            if (StringUtils.isBlank(originEntry.getUniversityFiscalPeriodCode())) {
                originEntry.setUniversityFiscalPeriodCode(universityDate.getUniversityFiscalAccountingPeriod());
            }
            
            if ( null == originEntry.getTransactionLedgerEntrySequenceNumber() ) {
                originEntry.setTransactionLedgerEntrySequenceNumber(new Integer(1));
            }
                
            if (StringUtils.isBlank(originEntry.getSubAccountNumber())) {
                originEntry.setSubAccountNumber(" ");
            }
            if (StringUtils.isBlank(originEntry.getFinancialSubObjectCode())) {
                originEntry.setFinancialSubObjectCode(" ");
            }
            
            for (Message orginEntryError : originEntryErrorMessages) {
               messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CUSTOM, orginEntryError.getMessage()); 
            }
        
        } catch (Exception e){
            throw new RuntimeException(e + " occurred in CollectorFlatFileInputType.createOriginEntry()",e);
        }

        originEntry.setTransactionLedgerEntryAmount(addDecimalPoint(originEntry.getTransactionLedgerEntryAmount().toString()));

        return originEntry;
    }

    public boolean validate(Object parsedFileContents) {
        List<CollectorBatch> collectorBatches = (List<CollectorBatch>) parsedFileContents;
        for (CollectorBatch collectorBatch : collectorBatches) {
            boolean isValid = collectorHelperService.performValidation(collectorBatch);
            if (isValid) {
                isValid = collectorHelperService.checkTrailerTotals(collectorBatch, null);
            }
            if (!isValid) {
                return false;
            }
        }
    
        return true;
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
    
    protected String extractRecordType(String line) {
        if (line == null || line.length() < 27) {
            return null;
        }
        
        String recordType = null;
        CollectorBatch collectorBatch = new CollectorBatch();
        final Map<String, Integer> pMap = CollectorBatch.getCollectorBatchHeaderFieldUtil().getFieldBeginningPositionMap();
        recordType = (collectorBatch.getValue(line, pMap.get(KFSPropertyConstants.COLLECTOR_BATCH_RECORD_TYPE), pMap.get(KFSPropertyConstants.BATCH_SEQUENCE_NUMBER)));

        return recordType;
    }
    
    protected String preprocessLine(String line) {
        return line;
    }
}
