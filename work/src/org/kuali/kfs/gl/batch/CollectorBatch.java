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

import org.apache.commons.lang.math.RandomUtils;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.gl.bo.CollectorHeader;
import org.kuali.module.gl.bo.InterDepartmentalBilling;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.InterDepartmentalBillingService;
import org.kuali.module.gl.service.OriginEntryService;

/**
 * Object representation of collector xml input.
 */
public class CollectorBatch implements Serializable {

    // header records
    private String chartOfAccountsCode;
    private String organizationCode;
    private Date transmissionDate;
    private String personUserID;
    private Integer batchSequenceNumber;
    private String workgroupName;

    private List<OriginEntry> originEntries;
    private List<InterDepartmentalBilling> idBillings;

    // trailer records
    private Integer totalRecords;
    private KualiDecimal totalAmount;

    public CollectorBatch() {
        originEntries = new ArrayList();
        idBillings = new ArrayList();
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
    public List<InterDepartmentalBilling>  getIdBillings() {
        return idBillings;
    }
    
    /**
     * Sets the idBillings attribute value.
     */
    public void setIdBillings(List<InterDepartmentalBilling> idBillings) {
        this.idBillings = idBillings;
    }

    /**
     * Gets the originEntries attribute.
     */
    public List<OriginEntry> getOriginEntries() {
        return originEntries;
    }

    /**
     * Sets the originEntries attribute value.
     */
    public void setOriginEntries(List<OriginEntry> batchOriginEntry) {
        this.originEntries = batchOriginEntry;
    }

    /**
     * Adds a processed origin entry to the list.
     */
    public void addOriginEntry(OriginEntry orginEntry) {
        this.originEntries.add(orginEntry);
    }

    /**
     * Adds a processed id billing to the list.
     */
    public void addIDBilling(InterDepartmentalBilling idBilling) {
        this.idBillings.add(idBilling);
    }
    
    /**
     * Sets defaults for fields not populated from file. Store an origin entry group,
     * all gl entries and id billing entries from the processed file. Also write the header
     * for the duplicate file check.
     */
    public void setDefaultsAndStore() {
        OriginEntryGroup entryGroup = createOriginEntryGroup();
        SpringServiceLocator.getOriginEntryGroupService().save(entryGroup);
        
        CollectorHeader header = createCollectorHeader();
        CollectorHeader foundHeader = (CollectorHeader) SpringServiceLocator.getBusinessObjectService().retrieve(header);
        if (foundHeader != null) { 
            // update the version number to prevent OptimisticLockingExceptions
            header.setVersionNumber(foundHeader.getVersionNumber());
        }
        SpringServiceLocator.getBusinessObjectService().save(header);
        
        OriginEntryService originEntryService = SpringServiceLocator.getOriginEntryService();
        for(OriginEntry entry: this.originEntries) {
            entry.setGroup(entryGroup);
            if (entry.getFinancialDocumentReversalDate() == null) {
                entry.setFinancialDocumentReversalDate(SpringServiceLocator.getDateTimeService().getCurrentSqlDate());
            }
            // don't need to worry about previous origin entries existing in the DB because there'll never be a
            // duplicate record because a sequence # is a key
            
            originEntryService.save(entry);
        }
        
        InterDepartmentalBillingService idBillingService = SpringServiceLocator.getInterDepartmentalBillingService();
        for(InterDepartmentalBilling idBilling: this.idBillings) {
            setDefaultsInterDepartmentalBilling(idBilling);
            InterDepartmentalBilling foundIdBilling = (InterDepartmentalBilling) SpringServiceLocator.getBusinessObjectService().retrieve(idBilling);
            if (foundIdBilling != null) {
                idBilling.setVersionNumber(foundIdBilling.getVersionNumber());
            }
            idBillingService.save(idBilling);
        }
    }
    
    /**
     * Uppercases the appropriate fields in the batch, if told to do so by the data dictionary
     * 
     */
    public void prepareDataForStorage() {
        BusinessObjectDictionaryService businessObjectDictionaryService = SpringServiceLocator.getBusinessObjectDictionaryService();
        DataDictionaryService dataDictionaryService = SpringServiceLocator.getDataDictionaryService();
        
        // uppercase the data used to generate the collector header
        if (dataDictionaryService.getAttributeForceUppercase(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE)) {
            setChartOfAccountsCode(getChartOfAccountsCode().toUpperCase());
        }
        if (dataDictionaryService.getAttributeForceUppercase(Org.class, KFSPropertyConstants.ORGANIZATION_CODE)) {
            setOrganizationCode(getOrganizationCode().toUpperCase());
        }
        
        // now uppercase all of the origin entry data
        for (OriginEntry entry : originEntries) {
            businessObjectDictionaryService.performForceUppercase(entry);
        }
        
        // uppercase the id billing entries
        for (InterDepartmentalBilling interDepartmentalBilling : idBillings) {
            businessObjectDictionaryService.performForceUppercase(interDepartmentalBilling);
        }
    }
    
    /**
     * Creates origin entry group from header fields.
     * @return OriginEntryGroup
     */
    private OriginEntryGroup createOriginEntryGroup() {
        OriginEntryGroup group = new OriginEntryGroup();
        
        group.setSourceCode(OriginEntrySource.COLLECTOR);
        group.setDate(new java.sql.Date(SpringServiceLocator.getDateTimeService().getCurrentDate().getTime()));
        group.setProcess(new Boolean(true));
        group.setScrub(new Boolean(true));
        group.setValid(new Boolean(true));
        
        return group;
    }
    
    /**
     * Creates a CollectorHeader from the batch.
     * @return CollectorHeader
     */
    public CollectorHeader createCollectorHeader() {
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
     * @param idBilling
     */
    private void setDefaultsInterDepartmentalBilling(InterDepartmentalBilling idBilling) {
        // TODO: Get current fiscal year and period if blank?
//        idBilling.setUniversityFiscalPeriodCode(String.valueOf(RandomUtils.nextInt(2)));
//        idBilling.setCreateSequence(String.valueOf(RandomUtils.nextInt(2)));
//        idBilling.setInterDepartmentalBillingSequenceNumber(String.valueOf(RandomUtils.nextInt(2)));
        idBilling.setCreateDate(new Date(SpringServiceLocator.getDateTimeService().getCurrentDate().getTime()));
    }
}
