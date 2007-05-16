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
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.InterDepartmentalBilling;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
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
     * all gl entries and id billing entries from the processed file.
     */
    public void setDefaultsAndStore() {
        OriginEntryGroup entryGroup = createOriginEntryGroup();
        SpringServiceLocator.getOriginEntryGroupService().save(entryGroup);
        
        OriginEntryService originEntryService = SpringServiceLocator.getOriginEntryService();
        for(OriginEntry entry: this.originEntries) {
            entry.setGroup(entryGroup);
            if (entry.getFinancialDocumentReversalDate() == null) {
                entry.setFinancialDocumentReversalDate(SpringServiceLocator.getDateTimeService().getCurrentSqlDate());
            }
            originEntryService.save(entry);
        }
        
        InterDepartmentalBillingService idBillingService = SpringServiceLocator.getInterDepartmentalBillingService();
        for(InterDepartmentalBilling idBilling: this.idBillings) {
            setDefaultsInterDepartmentalBilling(idBilling);
            idBillingService.save(idBilling);
        }
    }
    
    /**
     * Creates origin entry group from header fields.
     * @return OriginEntryGroup
     */
    private OriginEntryGroup createOriginEntryGroup() {
        OriginEntryGroup group = new OriginEntryGroup();
        
        group.setSourceCode("COLL");
        group.setDate(new java.sql.Date(SpringServiceLocator.getDateTimeService().getCurrentDate().getTime()));
        group.setProcess(new Boolean(true));
        group.setScrub(new Boolean(true));
        group.setValid(new Boolean(true));
        
        return group;
    }
    
    /**
     * Sets defaults for missing id billing fields.
     * @param idBilling
     */
    private void setDefaultsInterDepartmentalBilling(InterDepartmentalBilling idBilling) {
        idBilling.setUniversityFiscalPeriodCode(String.valueOf(RandomUtils.nextInt(2)));
        idBilling.setCreateSequence(String.valueOf(RandomUtils.nextInt(2)));
        idBilling.setInterDepartmentalBillingSequenceNumber(String.valueOf(RandomUtils.nextInt(2)));
        idBilling.setFinancialSystemOriginationCode(String.valueOf(RandomUtils.nextInt(2)));
        idBilling.setCreateDate(new Date(SpringServiceLocator.getDateTimeService().getCurrentDate().getTime()));
    }
}
