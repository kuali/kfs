/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.gl.batch.collector;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.gl.bo.CollectorDetail;
import org.kuali.module.gl.bo.CollectorHeader;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.CollectorDetailService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.util.CollectorReportData;

/**
 * Object representation of collector xml input.
 */
public class CollectorBatch implements Serializable {
    // way to distinguish this batch from others
    private String batchName;

    // header records
    private String chartOfAccountsCode;
    private String organizationCode;
    private Date transmissionDate;
    private String personUserID;
    private Integer batchSequenceNumber;
    private String workgroupName;

    private String campusCode;
    private String phoneNumber;
    private String mailingAddress;
    private String departmentName;

    private List<OriginEntryFull> originEntries;
    private List<CollectorDetail> collectorDetails;

    // trailer records
    private Integer totalRecords;
    private KualiDecimal totalAmount;

    /**
     * Constructs a CollectorBatch
     */
    public CollectorBatch() {
        originEntries = new ArrayList();
        collectorDetails = new ArrayList();
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
     * Gets the workgroupName attribute.
     */
    public String getWorkgroupName() {
        return workgroupName;
    }

    /**
     * Sets the workgroupName attribute value.
     */
    public void setWorkgroupName(String workgroupName) {
        this.workgroupName = workgroupName;
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
    public void setDefaultsAndStore(OriginEntryGroup originEntryGroup, CollectorReportData collectorReportData) {
        // persistHeader is used to persist a collector header record into the DB
        CollectorHeader persistHeader = createCollectorHeaderForStorage();
        CollectorHeader foundHeader = retrieveDuplicateHeader();

        if (foundHeader != null) {
            // update the version number to prevent OptimisticLockingExceptions
            persistHeader.setVersionNumber(foundHeader.getVersionNumber());
        }
        SpringContext.getBean(BusinessObjectService.class).save(persistHeader);

        OriginEntryService originEntryService = SpringContext.getBean(OriginEntryService.class);
        for (OriginEntryFull entry : this.originEntries) {
            entry.setGroup(originEntryGroup);
            if (entry.getFinancialDocumentReversalDate() == null) {
                entry.setFinancialDocumentReversalDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
            }
            // don't need to worry about previous origin entries existing in the DB because there'll never be a
            // duplicate record because a sequence # is a key

            originEntryService.save(entry);
        }

        CollectorDetailService collectorDetailService = SpringContext.getBean(CollectorDetailService.class);
        int numSavedDetails = 0;
        for (CollectorDetail idDetail : this.collectorDetails) {
            setDefaultsCollectorDetail(idDetail);
            CollectorDetail foundIdDetail = (CollectorDetail) SpringContext.getBean(BusinessObjectService.class).retrieve(idDetail);
            if (foundIdDetail != null) {
                idDetail.setVersionNumber(foundIdDetail.getVersionNumber());
            }
            numSavedDetails++;
            collectorDetailService.save(idDetail);
        }
        collectorReportData.setNumSavedDetails(this, numSavedDetails);
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
        if (dataDictionaryService.getAttributeForceUppercase(Org.class, KFSPropertyConstants.ORGANIZATION_CODE)) {
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

    /**
     * Sets defaults for missing id billing fields.
     * 
     * @param idDetail CollectorDetail object which has its create date being set
     */
    private void setDefaultsCollectorDetail(CollectorDetail idDetail) {
        // TODO: Get current fiscal year and period if blank?
        // idBilling.setUniversityFiscalPeriodCode(String.valueOf(RandomUtils.nextInt(2)));
        // idBilling.setCreateSequence(String.valueOf(RandomUtils.nextInt(2)));
        // idBilling.setInterDepartmentalBillingSequenceNumber(String.valueOf(RandomUtils.nextInt(2)));
        idDetail.setCreateDate(new Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime()));
    }

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
}
