/*
 * Copyright 2007 The Kuali Foundation
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
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.RunDateService;
import org.kuali.kfs.gl.batch.service.impl.OriginEntryTotals;
import org.kuali.kfs.gl.businessobject.CollectorDetail;
import org.kuali.kfs.gl.businessobject.CollectorHeader;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.OriginEntrySource;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.service.CollectorDetailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Object representation of collector xml input.
 */
public class CollectorBatch extends PersistableBusinessObjectBase {
    // way to distinguish this batch from others
    private String batchName;

    // common field for header records and trailer records
    private String universityFiscalYear;
    private String chartOfAccountsCode;
    private String organizationCode;
    private Date transmissionDate;
    private String recordType;
    
    // header record additional fields
    private String personUserID;
    private Integer batchSequenceNumber;
    private String emailAddress;
    private String campusCode;
    private String phoneNumber;
    private String mailingAddress;
    private String departmentName;

    private List<OriginEntryFull> originEntries;
    private List<CollectorDetail> collectorDetails;

    // trailer records
    private String firstEmptyField; //first,second,third Empty Fields are dummy fields to read the spaces in the file using dd
    private String secondEmptyField;
    private Integer totalRecords;
    private KualiDecimal totalAmount;
    
    private MessageMap messageMap;
    private OriginEntryTotals originEntryTotals;
    
    private boolean headerlessBatch;
    
    private static CollectorBatchHeaderFieldUtil collectorBatchHeaderFieldUtil;
    private static CollectorBatchTrailerRecordFieldUtil collectorBatchTrailerRecordFieldUtil;
    
    /**
     * Constructs a CollectorBatch
     */
    public CollectorBatch() {
        originEntries = new ArrayList();
        collectorDetails = new ArrayList();
        messageMap = new MessageMap();
        originEntryTotals = null;
        totalRecords = 0;
        headerlessBatch = false;
    }

    /**
     * Gets the universityFiscalYear attribute. 
     */
    public String getUniversityFiscalYear() {
        return universityFiscalYear;
    }
    
    /**
     * Sets the universityFiscalYear attribute
     */
    public void setUniversityFiscalYear(String universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }
    
    /**
     * Gets the batchSequenceNumber attribute.
     */
    public Integer getBatchSequenceNumber() {
        return batchSequenceNumber;
    }

    /**
     * Sets the batchSequenceNumber attribute value.
     */
    public void setBatchSequenceNumber(Integer batchSequenceNumber) {
        this.batchSequenceNumber = batchSequenceNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the organizationCode attribute.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute value.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * Gets the totalAmount attribute.
     */
    public KualiDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the totalAmount attribute value.
     */
    public void setTotalAmount(KualiDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Sets the total amount from the String.
     */
    public void setTotalAmount(String totalAmount) {
        this.totalAmount = new KualiDecimal(totalAmount);
    }

    /**
     * Sets the total amount field to null.
     */
    public void clearTotalAmount() {
        this.totalAmount = null;
    }

    /**
     * Gets the secondEmptyField attribute
     */
    public String getSecondEmptyField() {
        return secondEmptyField;
    }
    
    /**
     * Sets the secondEmptyField attribute
     */
    public void setSecondEmptyField(String secondEmptyField) {
        this.secondEmptyField = secondEmptyField;
    }
    
    /**
     * Gets the firstEmptyField attribute
     */
    public String getFirstEmptyField() {
        return firstEmptyField;
    }
    
    /**
     * Sets the firstEmptyField attribute
     */
    public void setFirstEmptyField(String firstEmptyField) {
        this.firstEmptyField = firstEmptyField;
    }

    /**
     * Gets the totalRecords attribute.
     */
    public Integer getTotalRecords() {
        return totalRecords;
    }

    /**
     * Sets the totalRecords attribute value.
     */
    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    /**
     * Gets the transmissionDate attribute.
     */
    public Date getTransmissionDate() {
        return transmissionDate;
    }

    /**
     * Sets the transmissionDate attribute value.
     */
    public void setTransmissionDate(Date transmissionDate) {
        this.transmissionDate = transmissionDate;
    }

    /**
     * Gets the recordType attribute.
     */
    public String getRecordType() {
        return recordType;
    }
    
    /**
     * Sets the recordType attribute.
     */
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }
        
    /**
     * Gets the emailAddress attribute.
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the emailAddress attribute value.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Gets the personUserID attribute.
     */
    public String getPersonUserID() {
        return personUserID;
    }

    /**
     * Sets the personUserID attribute value.
     */
    public void setPersonUserID(String personUserID) {
        this.personUserID = personUserID;
    }

    /**
     * Gets the idBillings attribute.
     */
    public List<CollectorDetail> getCollectorDetails() {
        return collectorDetails;
    }

    /**
     * Sets the idBillings attribute value.
     */
    public void setCollectorDetails(List<CollectorDetail> idDetails) {
        this.collectorDetails = idDetails;
    }

    /**
     * Gets the originEntries attribute.
     */
    public List<OriginEntryFull> getOriginEntries() {
        return originEntries;
    }

    /**
     * Sets the originEntries attribute value.
     */
    public void setOriginEntries(List<OriginEntryFull> batchOriginEntry) {
        this.originEntries = batchOriginEntry;
    }

    /**
     * Adds a processed origin entry to the list.
     */
    public void addOriginEntry(OriginEntryFull orginEntry) {
        this.originEntries.add(orginEntry);
    }

    /**
     * Adds a processed id billing to the list.
     */
    public void addCollectorDetail(CollectorDetail collectorDetail) {
        this.collectorDetails.add(collectorDetail);
    }

    /**
     * Attempts to retrieve a collector header already exists with the primary key values given for this object
     * 
     * @return the CollectorHeader found in the database
     */
    public CollectorHeader retrieveDuplicateHeader() {
        // checkHeader is used to check whether a record with the same PK values exist already (i.e. only PK values are filled in).
        CollectorHeader checkHeader = createCollectorHeaderWithPKValuesOnly();

        CollectorHeader foundHeader = (CollectorHeader) SpringContext.getBean(BusinessObjectService.class).retrieve(checkHeader);
        return foundHeader;
    }

    /**
     * Sets defaults for fields not populated from file. Store an origin entry group, all gl entries and id billing entries from the
     * processed file. Also write the header for the duplicate file check.
     * 
     * @param originEntryGroup the group into which to store the origin entries
     * @param collectorReportData report data
     */
    public void setDefaultsAndStore(CollectorReportData collectorReportData, String demergerOutputFileName, PrintStream originEntryOutputPs) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);  
        CollectorDetailService collectorDetailService = SpringContext.getBean(CollectorDetailService.class);
        
        // persistHeader is used to persist a collector header record into the DB
        CollectorHeader persistHeader = createCollectorHeaderForStorage();
        CollectorHeader foundHeader = retrieveDuplicateHeader();

        if (foundHeader != null) {
            // update the version number to prevent OptimisticLockingExceptions
            persistHeader.setVersionNumber(foundHeader.getVersionNumber());
        }
        businessObjectService.save(persistHeader);
              
        // store origin entries by using the demerger output file
        
        BufferedReader inputFileReader = null;
        try {
            inputFileReader = new BufferedReader(new FileReader(demergerOutputFileName));
            String line = null;
            while ((line = inputFileReader.readLine()) != null) {
                originEntryOutputPs.printf("%s\n", line);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("IO Error encountered trying to persist collector batch.", e);
        }
        finally {
            IOUtils.closeQuietly(inputFileReader);
            inputFileReader = null;
        }  
        Date nowDate  = new Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
        RunDateService runDateService = SpringContext.getBean(RunDateService.class);
        Date createDate = new java.sql.Date((runDateService.calculateRunDate(nowDate).getTime()));
        
        Integer sequenceNumber = new Integer(0);
        Integer nextSequence = collectorDetailService.getNextCreateSequence(createDate);
        if (nextSequence != null) {
            sequenceNumber = nextSequence;
        }
        int countOfdetails = collectorDetails.size();
        for (int numSavedDetails = 0; numSavedDetails < countOfdetails; numSavedDetails++) {
            CollectorDetail idDetail = this.collectorDetails.get(numSavedDetails);           
          //  setDefaultsCollectorDetail(idDetail);
         
            idDetail.setTransactionLedgerEntrySequenceNumber( ++sequenceNumber);
            idDetail.setCreateDate(createDate);
            CollectorDetail foundIdDetail = (CollectorDetail) businessObjectService.retrieve(idDetail);
            if (foundIdDetail != null) {
                idDetail.setVersionNumber(foundIdDetail.getVersionNumber());
            }

            businessObjectService.save(idDetail);
        }
        
        collectorReportData.setNumSavedDetails(this, countOfdetails);
    }

    /**
     * Uppercases the appropriate fields in the batch, if told to do so by the data dictionary
     */
    public void prepareDataForStorage() {
        BusinessObjectDictionaryService businessObjectDictionaryService = SpringContext.getBean(BusinessObjectDictionaryService.class);
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);

        // uppercase the data used to generate the collector header
        if (dataDictionaryService.getAttributeForceUppercase(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE)) {
            setChartOfAccountsCode(getChartOfAccountsCode().toUpperCase());
        }
        if (dataDictionaryService.getAttributeForceUppercase(Organization.class, KFSPropertyConstants.ORGANIZATION_CODE)) {
            setOrganizationCode(getOrganizationCode().toUpperCase());
        }

        // now uppercase all of the origin entry data
        for (OriginEntryFull entry : originEntries) {
            businessObjectDictionaryService.performForceUppercase(entry);
        }

        // uppercase the id billing entries
        for (CollectorDetail collectorDetail : collectorDetails) {
            businessObjectDictionaryService.performForceUppercase(collectorDetail);
        }
    }

    /**
     * Creates origin entry group from header fields.
     * 
     * @return OriginEntryGroup
     */
    private OriginEntryGroup createOriginEntryGroup() {
        OriginEntryGroup group = new OriginEntryGroup();

        group.setSourceCode(OriginEntrySource.COLLECTOR);
        group.setDate(new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime()));
        group.setProcess(new Boolean(true));
        group.setScrub(new Boolean(true));
        group.setValid(new Boolean(true));

        return group;
    }

    /**
     * Creates a CollectorHeader from the batch to be used for storage
     * 
     * @return CollectorHeader
     */
    public CollectorHeader createCollectorHeaderForStorage() {
        CollectorHeader header = new CollectorHeader();

        header.setChartOfAccountsCode(getChartOfAccountsCode());
        header.setOrganizationCode(getOrganizationCode());
        header.setProcessTransmissionDate(getTransmissionDate());
        header.setProcessBatchSequenceNumber(getBatchSequenceNumber());
        header.setProcessTotalRecordCount(getTotalRecords());
        header.setProcessTotalAmount(getTotalAmount());
        header.setContactCampusCode(getCampusCode());
        header.setContactPersonPhoneNumber(getPhoneNumber());
        header.setContactMailingAddress(getMailingAddress());
        header.setContactDepartmentName(getDepartmentName());

        return header;
    }

    /**
     * Creates an origin entry record with the PK values filled in only. This is useful to check for duplicate headers.
     * 
     * @return CollectorHeader with chart of accounts code, organization code, process transmission date, batch sequence number
     * total record count, and process total amount from this CollectorBatch
     */
    public CollectorHeader createCollectorHeaderWithPKValuesOnly() {
        CollectorHeader header = new CollectorHeader();

        header.setChartOfAccountsCode(getChartOfAccountsCode());
        header.setOrganizationCode(getOrganizationCode());
        header.setProcessTransmissionDate(getTransmissionDate());
        header.setProcessBatchSequenceNumber(getBatchSequenceNumber());
        header.setProcessTotalRecordCount(getTotalRecords());
        header.setProcessTotalAmount(getTotalAmount());

        return header;
    }

//    /**
//     * Sets defaults for missing id billing fields.
//     * 
//     * @param idDetail CollectorDetail object which has its create date being set
//     */
//    private void setDefaultsCollectorDetail(CollectorDetail idDetail) {
//        // TODO: Get current fiscal year and period if blank?
//        // idBilling.setUniversityFiscalPeriodCode(String.valueOf(RandomUtils.nextInt(2)));
//        // idBilling.setCreateSequence(String.valueOf(RandomUtils.nextInt(2)));
//        // idBilling.setInterDepartmentalBillingSequenceNumber(String.valueOf(RandomUtils.nextInt(2)));
//        idDetail.setCreateDate(new Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime()));
//        
//    }

    /**
     * Gets the campusCode attribute.
     * 
     * @return Returns the campusCode.
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute value.
     * 
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    /**
     * Gets the departmentName attribute.
     * 
     * @return Returns the departmentName.
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Sets the departmentName attribute value.
     * 
     * @param departmentName The departmentName to set.
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * Gets the mailingAddress attribute.
     * 
     * @return Returns the mailingAddress.
     */
    public String getMailingAddress() {
        return mailingAddress;
    }

    /**
     * Sets the mailingAddress attribute value.
     * 
     * @param mailingAddress The mailingAddress to set.
     */
    public void setMailingAddress(String mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    /**
     * Gets the phoneNumber attribute.
     * 
     * @return Returns the phoneNumber.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phoneNumber attribute value.
     * 
     * @param phoneNumber The phoneNumber to set.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the batchName attribute.
     * 
     * @return Returns the batchName.
     */
    public String getBatchName() {
        return batchName;
    }

    /**
     * Sets the batchName attribute value.
     * 
     * @param batchName The batchName to set.
     */
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public MessageMap getMessageMap() {
        return messageMap;
    }

    public void setMessageMap(MessageMap messageMap) {
        if (messageMap == null) {
            throw new NullPointerException("messageMap is null");
        }
        if (this.messageMap.hasMessages()) {
            throw new RuntimeException("Cannot reset MessageMap unless original instance has no messages.");
        }
        this.messageMap = messageMap;
    }

    public OriginEntryTotals getOriginEntryTotals() {
        return originEntryTotals;
    }

    public void setOriginEntryTotals(OriginEntryTotals originEntryTotals) {
        this.originEntryTotals = originEntryTotals;
    }

    public boolean isHeaderlessBatch() {
        return headerlessBatch;
    }

    public void setHeaderlessBatch(boolean headerlessBatch) {
        this.headerlessBatch = headerlessBatch;
    }
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("chartOfAccountsCode", getChartOfAccountsCode());
        map.put("organizationCode", getOrganizationCode());
        map.put("transmissionDate", getTransmissionDate());
        map.put("personUserID", getPersonUserID());
        map.put("batchSequenceNumber", getBatchSequenceNumber());
        map.put("emailAddress", getEmailAddress());
        map.put("campusCode", getCampusCode());
        map.put("phoneNumber", getPhoneNumber());
        map.put("mailingAddress", getMailingAddress());
        map.put("departmentName", getDepartmentName());
        map.put("firstEmptyField", getFirstEmptyField());
        map.put("totalRecords", getTotalRecords());        
        map.put("secondEmptyField", getSecondEmptyField());
        map.put("totalAmount", getTotalAmount());
        
        return map;
    }
    
    protected Date parseSqlDate(String date) throws ParseException {
        try {
            return new Date(new SimpleDateFormat("yy-MM-dd").parse(date).getTime());
        }
        catch (java.text.ParseException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
    
    protected String getValue(String headerLine, int s, int e) {
          return org.springframework.util.StringUtils.trimTrailingWhitespace(StringUtils.substring(headerLine, s, e));
      }
    
    /**
     * @return the static instance of the CollectorBatchHeaderFieldUtil
     */
    protected static CollectorBatchHeaderFieldUtil getCollectorBatchHeaderFieldUtil() {
        if (collectorBatchHeaderFieldUtil == null) {
            collectorBatchHeaderFieldUtil = new CollectorBatchHeaderFieldUtil();
        }
        return collectorBatchHeaderFieldUtil;
    }
    
    public void setFromTextFileForCollectorBatch(String headerLine) {
        try{
            final Map<String, Integer> pMap = getCollectorBatchHeaderFieldUtil().getFieldBeginningPositionMap();
            
            headerLine = org.apache.commons.lang.StringUtils.rightPad(headerLine, GeneralLedgerConstants.getSpaceAllCollectorBatchHeaderFields().length(), ' ');
            
            setChartOfAccountsCode(getValue(headerLine, pMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE), pMap.get(KFSPropertyConstants.ORGANIZATION_CODE)));
            setOrganizationCode(getValue(headerLine, pMap.get(KFSPropertyConstants.ORGANIZATION_CODE), pMap.get(KFSPropertyConstants.TRANSMISSION_DATE)));

            String transmissionDate = org.apache.commons.lang.StringUtils.trim(getValue(headerLine, pMap.get(KFSPropertyConstants.TRANSMISSION_DATE), pMap.get(KFSPropertyConstants.COLLECTOR_BATCH_RECORD_TYPE)));
            try {
                setTransmissionDate(parseSqlDate(transmissionDate));
            }
            catch (ParseException e) {
                getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.HEADER_BAD_TRANSMISSION_DATE_FORMAT, transmissionDate);
                setTransmissionDate(null);
            }
            String batchNumber = org.apache.commons.lang.StringUtils.trim(getValue(headerLine, pMap.get(KFSPropertyConstants.BATCH_SEQUENCE_NUMBER), pMap.get(KFSPropertyConstants.EMAIL_ADDRESS)));
            
            if (ObjectUtil.isInteger(batchNumber)) { 
                setBatchSequenceNumber(new Integer(batchNumber));
            } else {
                setBatchSequenceNumber(0);
            }
            setEmailAddress(getValue(headerLine, pMap.get(KFSPropertyConstants.EMAIL_ADDRESS), pMap.get(KFSPropertyConstants.COLLECTOR_BATCH_PERSON_USER_ID)));
            setPersonUserID(getValue(headerLine, pMap.get(KFSPropertyConstants.COLLECTOR_BATCH_PERSON_USER_ID), pMap.get(KFSPropertyConstants.DEPARTMENT_NAME)));
            setDepartmentName(getValue(headerLine, pMap.get(KFSPropertyConstants.DEPARTMENT_NAME), pMap.get(KFSPropertyConstants.MAILING_ADDRESS)));
            setMailingAddress(getValue(headerLine, pMap.get(KFSPropertyConstants.MAILING_ADDRESS), pMap.get(KFSPropertyConstants.CAMPUS_CODE)));
            setCampusCode(getValue(headerLine, pMap.get(KFSPropertyConstants.CAMPUS_CODE), pMap.get(KFSPropertyConstants.PHONE_NUMBER)));
            setPhoneNumber(org.apache.commons.lang.StringUtils.trim(getValue(headerLine, pMap.get(KFSPropertyConstants.PHONE_NUMBER), GeneralLedgerConstants.getSpaceAllCollectorBatchHeaderFields().length())));
        } catch (Exception e){
            throw new RuntimeException(e + " occurred in CollectorBatch.setFromTextFileForCollectorBatch()");
        }
    }
    
    /**
     * @return the static instance of the CollectorBatchTrailerRecordFieldUtil
     */
    protected static CollectorBatchTrailerRecordFieldUtil getCollectorBatchTrailerRecordFieldUtil() {
        if (collectorBatchTrailerRecordFieldUtil == null) {
            collectorBatchTrailerRecordFieldUtil = new CollectorBatchTrailerRecordFieldUtil();
        }
        return collectorBatchTrailerRecordFieldUtil;
    }
    
    public void setFromTextFileForCollectorBatchTrailerRecord(String trailerLine, int lineNumber) {
        final Map<String, Integer> pMap = getCollectorBatchTrailerRecordFieldUtil().getFieldBeginningPositionMap();

        trailerLine = org.apache.commons.lang.StringUtils.rightPad(trailerLine, GeneralLedgerConstants.getSpaceAllCollectorBatchTrailerFields().length(), ' ');
        setTotalRecords(new Integer(org.apache.commons.lang.StringUtils.trim(getValue(trailerLine, pMap.get(KFSPropertyConstants.TOTAL_RECORDS), pMap.get(KFSPropertyConstants.TRAILER_RECORD_SECOND_EMPTY_FIELD)))));
        
        String trailerAmount = org.apache.commons.lang.StringUtils.trim(getValue(trailerLine, pMap.get(KFSPropertyConstants.TOTAL_AMOUNT), GeneralLedgerConstants.getSpaceAllCollectorBatchTrailerFields().length()));
        
        try {
            setTotalAmount(trailerAmount);
        }
        catch (NumberFormatException e) {
            setTotalAmount(KualiDecimal.ZERO);
            getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CUSTOM, "Collector trailer total amount cannot be parsed on line " + lineNumber + " amount string " + trailerAmount);
        }
    }
}