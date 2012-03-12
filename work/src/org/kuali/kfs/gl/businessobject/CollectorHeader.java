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
package org.kuali.kfs.gl.businessobject;

import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.location.api.campus.Campus;
import org.kuali.rice.location.api.campus.CampusService;

/**
 * This class represents a unique header for use with a CollectorBatch class
 */
public class CollectorHeader extends PersistableBusinessObjectBase {

    private String chartOfAccountsCode;
    private String organizationCode;
    private Date processTransmissionDate;
    private Integer processBatchSequenceNumber;
    private Integer processTotalRecordCount;
    private KualiDecimal processTotalAmount;
    private String campusCode;
    private String contactPersonPhoneNumber;
    private String contactMailingAddress;
    private String contactDepartmentName;

    private Organization organization;
    private Chart chartOfAccounts;
    private Campus campus;

    /**
     * Default constructor.
     */
    public CollectorHeader() {

    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    /**
     * Gets the processTransmissionDate attribute.
     * 
     * @return Returns the processTransmissionDate
     */
    public Date getProcessTransmissionDate() {
        return processTransmissionDate;
    }

    /**
     * Sets the processTransmissionDate attribute.
     * 
     * @param processTransmissionDate The processTransmissionDate to set.
     */
    public void setProcessTransmissionDate(Date processTransmissionDate) {
        this.processTransmissionDate = processTransmissionDate;
    }


    /**
     * Gets the processBatchSequenceNumber attribute.
     * 
     * @return Returns the processBatchSequenceNumber
     */
    public Integer getProcessBatchSequenceNumber() {
        return processBatchSequenceNumber;
    }

    /**
     * Sets the processBatchSequenceNumber attribute.
     * 
     * @param processBatchSequenceNumber The processBatchSequenceNumber to set.
     */
    public void setProcessBatchSequenceNumber(Integer processBatchSequenceNumber) {
        this.processBatchSequenceNumber = processBatchSequenceNumber;
    }


    /**
     * Gets the processTotalRecordCount attribute.
     * 
     * @return Returns the processTotalRecordCount
     */
    public Integer getProcessTotalRecordCount() {
        return processTotalRecordCount;
    }

    /**
     * Sets the processTotalRecordCount attribute.
     * 
     * @param processTotalRecordCount The processTotalRecordCount to set.
     */
    public void setProcessTotalRecordCount(Integer processTotalRecordCount) {
        this.processTotalRecordCount = processTotalRecordCount;
    }


    /**
     * Gets the processTotalAmount attribute.
     * 
     * @return Returns the processTotalAmount
     */
    public KualiDecimal getProcessTotalAmount() {
        return processTotalAmount;
    }

    /**
     * Sets the processTotalAmount attribute.
     * 
     * @param processTotalAmount The processTotalAmount to set.
     */
    public void setProcessTotalAmount(KualiDecimal processTotalAmount) {
        this.processTotalAmount = processTotalAmount;
    }


    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap();
        m.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.chartOfAccountsCode);
        m.put(KFSPropertyConstants.ORGANIZATION_CODE, this.organizationCode);
        if (this.processTransmissionDate != null) {
            m.put(KFSPropertyConstants.PROCESS_TRANSMISSION_DATE, this.processTransmissionDate.toString());
        }
        if (this.processBatchSequenceNumber != null) {
            m.put(KFSPropertyConstants.PROCESS_BATCH_SEQUENCE_NUMBER, this.processBatchSequenceNumber.toString());
        }
        if (this.processTotalRecordCount != null) {
            m.put(KFSPropertyConstants.PROCESS_TOTAL_RECORD_COUNT, this.processTotalRecordCount.toString());
        }
        if (this.processTotalAmount != null) {
            m.put(KFSPropertyConstants.PROCESS_TOTAL_AMOUNT, this.processTotalAmount.toString());
        }
        return m;
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
    public void setContactCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    /**
     * Gets the departmentName attribute.
     * 
     * @return Returns the departmentName.
     */
    public String getContactDepartmentName() {
        return contactDepartmentName;
    }

    /**
     * Sets the departmentName attribute value.
     * 
     * @param departmentName The departmentName to set.
     */
    public void setContactDepartmentName(String contactDepartmentName) {
        this.contactDepartmentName = contactDepartmentName;
    }

    /**
     * Gets the mailingAddress attribute.
     * 
     * @return Returns the mailingAddress.
     */
    public String getContactMailingAddress() {
        return contactMailingAddress;
    }

    /**
     * Sets the mailingAddress attribute value.
     * 
     * @param mailingAddress The mailingAddress to set.
     */
    public void setContactMailingAddress(String contactMailingAddress) {
        this.contactMailingAddress = contactMailingAddress;
    }

    /**
     * Gets the phoneNumber attribute.
     * 
     * @return Returns the phoneNumber.
     */
    public String getContactPersonPhoneNumber() {
        return contactPersonPhoneNumber;
    }

    /**
     * Sets the phoneNumber attribute value.
     * 
     * @param phoneNumber The phoneNumber to set.
     */
    public void setContactPersonPhoneNumber(String contactPersonPhoneNumber) {
        this.contactPersonPhoneNumber = contactPersonPhoneNumber;
    }

    /**
     * Gets the campus attribute.
     * 
     * @return Returns the campus.
     */
    public Campus getCampus() {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KRADPropertyConstants.CAMPUS_CODE, campusCode);
        return campus = SpringContext.getBean(CampusService.class).getCampus(campusCode/*RICE_20_REFACTORME  criteria */);
    }

    /**
     * Sets the campus attribute value.
     * 
     * @param campus The campus to set.
     */
    public void setCampus(Campus campus) {
        this.campus = campus;
    }
}
