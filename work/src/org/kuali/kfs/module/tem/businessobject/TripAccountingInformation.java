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
package org.kuali.kfs.module.tem.businessobject;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

@Entity
@Table(name = "TEM_TRP_ACCT_INFO_T")
public class TripAccountingInformation extends PersistableBusinessObjectBase {

    private Integer id;
    
    private Integer agencyStagingDataId;
    private String tripChartCode;
    private String tripAccountNumber;
    private String tripSubAccountNumber;
    private String objectCode;
    private String subObjectCode;
    private String projectCode;
    private String organizationReference;
    private KualiDecimal amount;
    
    private Account account;
    private SubAccount subAccount;
    
    public TripAccountingInformation() {
    }
    
    @Override
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Gets the id attribute. 
     * @return Returns the id.
     */
    @Id
    @GeneratedValue(generator = "TEM_TRP_ACCT_INFO_ID_SEQ")
    @SequenceGenerator(name = "TEM_TRP_ACCT_INFO_ID_SEQ", sequenceName = "TEM_TRP_ACCT_INFO_ID_SEQ", allocationSize = 5)
    @Column(name="ID",nullable=false)
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id attribute value.
     * @param id The id to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the agencyStagingDataId attribute. 
     * @return Returns the agencyStagingDataId.
     */
    @Column(name = "AGENCY_ID",nullable = true)
    public Integer getAgencyStagingDataId() {
        return agencyStagingDataId;
    }

    /**
     * Sets the agencyStagingDataId attribute value.
     * @param agencyStagingDataId The agencyStagingDataId to set.
     */
    public void setAgencyStagingDataId(Integer agencyStagingDataId) {
        this.agencyStagingDataId = agencyStagingDataId;
    }

    /**
     * Gets the tripChartCode attribute. 
     * @return Returns the tripChartCode.
     */
    @Column(name = "FIN_COA_CD", length = 2, nullable = true)
    public String getTripChartCode() {
        return tripChartCode;
    }

    /**
     * Sets the tripChartCode attribute value.
     * @param tripChartCode The tripChartCode to set.
     */
    public void setTripChartCode(String tripChartCode) {
        this.tripChartCode = tripChartCode;
    }

    /**
     * Gets the tripAccountNumber attribute. 
     * @return Returns the tripAccountNumber.
     */
    @Column(name = "ACCT_NBR", length = 7, nullable = true)
    public String getTripAccountNumber() {
        return tripAccountNumber;
    }

    /**
     * Sets the tripAccountNumber attribute value.
     * @param tripAccountNumber The tripAccountNumber to set.
     */
    public void setTripAccountNumber(String tripAccountNumber) {
        this.tripAccountNumber = tripAccountNumber;
    }

    /**
     * Gets the tripSubAccountNumber attribute. 
     * @return Returns the tripSubAccountNumber.
     */
    @Column(name = "SUB_ACCT_NBR", length = 5, nullable = true)
    public String getTripSubAccountNumber() {
        return tripSubAccountNumber;
    }

    /**
     * Sets the tripSubAccountNumber attribute value.
     * @param tripSubAccountNumber The tripSubAccountNumber to set.
     */
    public void setTripSubAccountNumber(String tripSubAccountNumber) {
        this.tripSubAccountNumber = tripSubAccountNumber;
    }

    /**
     * Gets the objectCode attribute. 
     * @return Returns the objectCode.
     */
    @Column(name = "OBJ_CD", length = 4, nullable = true)
    public String getObjectCode() {
        return objectCode;
    }

    /**
     * Sets the objectCode attribute value.
     * @param objectCode The objectCode to set.
     */
    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    /**
     * Gets the subObjectCode attribute. 
     * @return Returns the subObjectCode.
     */
    @Column(name = "SUB_OBJ_CD", length = 3, nullable = true)
    public String getSubObjectCode() {
        return subObjectCode;
    }

    /**
     * Sets the subObjectCode attribute value.
     * @param subObjectCode The subObjectCode to set.
     */
    public void setSubObjectCode(String subObjectCode) {
        this.subObjectCode = subObjectCode;
    }

    /**
     * Gets the projectCode attribute. 
     * @return Returns the projectCode.
     */
    @Column(name = "PRJ_CD", length = 10, nullable = true)
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * Sets the projectCode attribute value.
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * Gets the organizationReference attribute. 
     * @return Returns the organizationReference.
     */
    @Column(name = "ORG_REF", length = 8, nullable = true)
    public String getOrganizationReference() {
        return organizationReference;
    }

    /**
     * Sets the organizationReference attribute value.
     * @param organizationReference The organizationReference to set.
     */
    public void setOrganizationReference(String organizationReference) {
        this.organizationReference = organizationReference;
    }

    /**
     * Gets the amount attribute. 
     * @return Returns the amount.
     */
    @Column(name = "AMOUNT",nullable = true)
    public KualiDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount attribute value.
     * @param amount The amount to set.
     */
    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    /**
     * Gets the account attribute. 
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute value.
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the subAccount attribute. 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * @param subAccount The subAccount to set.
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

}
