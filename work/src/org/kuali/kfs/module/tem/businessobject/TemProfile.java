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

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.util.ObjectUtils;


@Entity
@Table(name = "TEM_PROFILE_T")
public class TemProfile extends BaseTemProfile {
    private Integer profileId;

    private String updatedBy;
    private Date lastUpdate;

    private List<TemProfileArranger> arrangers;
    private List<TemProfileAccount> accounts;
    private TemProfileAddress temProfileAddress;

    private String employeeId;
    private String homeDepartment;
    private String homeDeptOrgCode;
    private String homeDeptChartOfAccountsCode;
    private Organization homeDeptOrg;
    private String defaultChartCode;
    private String defaultAccount;
    private String defaultSubAccount;
    private String defaultProjectCode;

    private Chart chart;
    private Account account;
    private SubAccount subAccount;
    private ProjectCode project;
    private Person principal;
    private org.kuali.rice.kim.api.identity.entity.Entity kimEntityInfo;

    private String achSignUp;
    private String achTransactionType;

    private List<TemProfileEmergencyContact> emergencyContacts;
    private List<Note> boNotes;

    public TemProfile() {
        super();
        emergencyContacts = new ArrayList<TemProfileEmergencyContact>();
        arrangers = new ArrayList<TemProfileArranger>();
        accounts = new ArrayList<TemProfileAccount>();
        temProfileAddress = new TemProfileAddress();
    }

    /**
     * Gets the profileId attribute.
     *
     * @return Returns the profileId.
     */
    @Id
    @GeneratedValue(generator = "TEM_PROFILE_ID_SEQ")
    @SequenceGenerator(name = "TEM_PROFILE_ID_SEQ", sequenceName = "TEM_PROFILE_ID_SEQ", allocationSize = 5)
    @Column(name = "profile_id", nullable = false, length = 19)
    public Integer getProfileId() {
        return profileId;
    }

    /**
     * Sets the profileId attribute value.
     *
     * @param profileId The profileId to set.
     */
    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    /**
     * Gets the updatedBy attribute.
     *
     * @return Returns the updatedBy.
     */
    @Column(name = "updated_by", length = 40, nullable = true)
    public String getUpdatedBy() {
        return updatedBy;
    }


    /**
     * Sets the updatedBy attribute value.
     *
     * @param updatedBy The updatedBy to set.
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }


    /**
     * Gets the lastUpdate attribute.
     *
     * @return Returns the lastUpdate.
     */
    @Column(name = "last_update")
    public Date getLastUpdate() {
        return lastUpdate;
    }


    /**
     * Sets the lastUpdate attribute value.
     *
     * @param lastUpdate The lastUpdate to set.
     */
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    @ManyToOne
    @JoinColumn(name = "traveler_typ_cd")
    public TravelerType getTravelerType() {
        return travelerType;
    }


    @Override
    public void setTravelerType(TravelerType travelerType) {
        this.travelerType = travelerType;
    }

    @OneToMany
    @JoinColumn(name = "arranger_id")
    public List<TemProfileArranger> getArrangers() {
        return arrangers;
    }


    public void setArrangers(List<TemProfileArranger> arrangers) {
        this.arrangers = arrangers;
    }

    public List<TemProfileAccount> getAccounts() {
        return accounts;
    }

    /**
     * Gets the motorVehicleRecordCheck attribute.
     *
     * @return Returns the motorVehicleRecordCheck.
     */
    @Override
    @Column(length = 1, nullable = true)
    public boolean isMotorVehicleRecordCheck() {
        return motorVehicleRecordCheck;
    }

    /**
     * Sets the motorVehicleRecordCheck attribute value.
     *
     * @param motorVehicleRecordCheck The motorVehicleRecordCheck to set.
     */
    @Override
    public void setMotorVehicleRecordCheck(boolean motorVehicleRecordCheck) {
        this.motorVehicleRecordCheck = motorVehicleRecordCheck;
    }

    /**
     * Gets the employeeId attribute.
     *
     * @return Returns the employeeId.
     */
    @Column(length = 40, nullable = true)
    public String getEmployeeId() {
        return employeeId;
    }

    /**
     * Sets the employeeId attribute value.
     *
     * @param employeeId The employeeId to set.
     */
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * Gets the homeDepartment attribute.
     *
     * @return Returns the homeDepartment.
     */
    @Column(name = "home_dept", length = 40, nullable = false)
    public String getHomeDepartment() {
        // null check to prevent "-" being returned when org and coa have null as values.
        if (getHomeDeptChartOfAccountsCode() != null && getHomeDeptOrgCode() != null) {
            return getHomeDeptChartOfAccountsCode() + "-" + getHomeDeptOrgCode();
        }

        return "";
    }

    /**
     * Gets the homeDeptOrgCode attribute.
     *
     * @return Returns the homeDeptOrgCode.
     */
    public String getHomeDeptOrgCode() {
        return homeDeptOrgCode;
    }

    /**
     * Sets the homeDeptOrgCode attribute value.
     *
     * @param homeDeptOrgCode The homeDeptOrgCode to set.
     */
    public void setHomeDeptOrgCode(String homeDeptOrgCode) {
        this.homeDeptOrgCode = homeDeptOrgCode;
    }

    /**
     * Gets the homeDeptChartOfAccountsCode attribute.
     *
     * @return Returns the homeDeptChartOfAccountsCode.
     */
    public String getHomeDeptChartOfAccountsCode() {
        return homeDeptChartOfAccountsCode;
    }

    /**
     * Sets the homeDeptChartOfAccountsCode attribute value.
     *
     * @param homeDeptChartOfAccountsCode The homeDeptChartOfAccountsCode to set.
     */
    public void setHomeDeptChartOfAccountsCode(String homeDeptChartOfAccountsCode) {
        this.homeDeptChartOfAccountsCode = homeDeptChartOfAccountsCode;
    }

    /**
     * Gets the homeDeptOrg attribute.
     *
     * @return Returns the homeDeptOrg.
     */
    public Organization getHomeDeptOrg() {
        return homeDeptOrg;
    }

    /**
     * Sets the homeDeptOrg attribute value.
     *
     * @param homeDeptOrg The homeDeptOrg to set.
     */
    public void setHomeDeptOrg(Organization homeDeptOrg) {
        this.homeDeptOrg = homeDeptOrg;
    }

    /**
     * Gets the defaultChartCode attribute.
     *
     * @return Returns the defaultChartCode.
     */
    @Column(length = 2, nullable = true)
    public String getDefaultChartCode() {
        if(defaultChartCode == null && homeDeptChartOfAccountsCode != null) {
            defaultChartCode = homeDeptChartOfAccountsCode;
        }

        return defaultChartCode;
    }

    /**
     * Sets the defaultChartCode attribute value.
     *
     * @param defaultChartCode The defaultChartCode to set.
     */
    public void setDefaultChartCode(String defaultChartCode) {
        this.defaultChartCode = defaultChartCode;
    }

    /**
     * Gets the defaultAccount attribute.
     *
     * @return Returns the defaultAccount.
     */
    @Column(length = 7, nullable = true)
    public String getDefaultAccount() {
        return defaultAccount;
    }

    /**
     * Sets the defaultAccount attribute value.
     *
     * @param defaultAccount The defaultAccount to set.
     */
    public void setDefaultAccount(String defaultAccount) {
        this.defaultAccount = defaultAccount;
    }

    /**
     * Gets the defaultSubAccount attribute.
     *
     * @return Returns the defaultSubAccount.
     */
    @Column(length = 5, nullable = true)
    public String getDefaultSubAccount() {
        return defaultSubAccount;
    }

    /**
     * Sets the defaultSubAccount attribute value.
     *
     * @param defaultSubAccount The defaultSubAccount to set.
     */
    public void setDefaultSubAccount(String defaultSubAccount) {
        this.defaultSubAccount = defaultSubAccount;
    }

    /**
     * Gets the defaultProjectCode attribute.
     *
     * @return Returns the defaultProjectCode.
     */
    @Column(length = 10, nullable = false)
    public String getDefaultProjectCode() {
        return defaultProjectCode;
    }

    /**
     * Sets the defaultProjectCode attribute value.
     *
     * @param defaultProjectCode The defaultProjectCode to set.
     */
    public void setDefaultProjectCode(String defaultProjectCode) {
        this.defaultProjectCode = defaultProjectCode;
    }

    /**
     * Gets the chart attribute.
     *
     * @return Returns the chart.
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute value.
     *
     * @param chart The chart to set.
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * Gets the account attribute.
     *
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute value.
     *
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the subAccount attribute.
     *
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     *
     * @param subAccount The subAccount to set.
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * Gets the project attribute.
     *
     * @return Returns the project.
     */
    public ProjectCode getProject() {
        return project;
    }

    /**
     * Sets the project attribute value.
     *
     * @param project The project to set.
     */
    public void setProject(ProjectCode project) {
        this.project = project;
    }

    /**
     * Gets the achSignUp attribute.
     *
     * @return Returns the achSignUp.
     */
    public String getAchSignUp() {
        return achSignUp;
    }

    /**
     * Sets the achSignUp attribute value.
     *
     * @param achSignUp The achSignUp to set.
     */
    public void setAchSignUp(String achSignUp) {
        this.achSignUp = achSignUp;
    }

    /**
     * Gets the achTransactionType attribute.
     *
     * @return Returns the achTransactionType.
     */
    public String getAchTransactionType() {
        return achTransactionType;
    }

    /**
     * Sets the achTransactionType attribute value.
     *
     * @param achTransactionType The achTransactionType to set.
     */
    public void setAchTransactionType(String achTransactionType) {
        this.achTransactionType = achTransactionType;
    }

    /**
     * Gets the principal attribute.
     *
     * @return Returns the principal.
     */
    public Person getPrincipal() {
        return principal;
    }

    /**
     * Sets the principal attribute value.
     *
     * @param principal The principal to set.
     */
    public void setPrincipal(Person principal) {
        this.principal = principal;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

    /**
     * Gets the kimEntityInfo attribute.
     *
     * @return Returns the kimEntityInfo.
     */
    public org.kuali.rice.kim.api.identity.entity.Entity getKimEntityInfo() {
        return kimEntityInfo;
    }

    /**
     * Sets the kimEntityInfo attribute value.
     *
     * @param kimEntityInfo The kimEntityInfo to set.
     */
    public void setKimEntityInfo(org.kuali.rice.kim.api.identity.entity.Entity kimEntityInfo) {
        this.kimEntityInfo = kimEntityInfo;
    }

    /**
     * Gets the temProfileAddress attribute.
     *
     * @return Returns the temProfileAddress.
     */
    public TemProfileAddress getTemProfileAddress() {
        TemProfileService profileService = SpringContext.getBean(TemProfileService.class);

        if(ObjectUtils.isNull(temProfileAddress)) {
        	temProfileAddress = new TemProfileAddress();
        }

        return profileService.getAddressFromProfile(this, temProfileAddress);
    }

    /**
     * Sets the temProfileAddress attribute value.
     *
     * @param temProfileAddress The temProfileAddress to set.
     */
    public void setTemProfileAddress(TemProfileAddress temProfileAddress) {
        this.temProfileAddress = temProfileAddress;
    }

    public void setHomeDepartment(String homeDepartment) {
        this.homeDepartment = homeDepartment;
    }


	/**
	 * Sets the accounts attribute value.
	 * @param accounts The accounts to set.
	 */
	public void setAccounts(List<TemProfileAccount> accounts) {
		this.accounts = accounts;
	}

    public List<TemProfileEmergencyContact> getEmergencyContacts() {
        return emergencyContacts;
    }


    public void setEmergencyContacts(List<TemProfileEmergencyContact> emergencyContacts) {
        this.emergencyContacts = emergencyContacts;
    }

    /**
     * Set Notes related to this BO
     *
     * @return
     */
    public void setBoNotes(List<Note>notes){
        this.boNotes = notes;
    }

    /**
     * Get Notes related to this BO
     *
     * @return
     */
    public List<Note> getBoNotes(){
        if (ObjectUtils.isNull(boNotes)){
            List<Note> noteList = KRADServiceLocator.getNoteService().getByRemoteObjectId(getObjectId());
            boNotes = noteList;
        }
        return boNotes;
    }

}
