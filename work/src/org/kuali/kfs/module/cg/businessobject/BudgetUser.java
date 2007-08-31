/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.budget.bo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.service.ChartUserService;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.service.BudgetPersonnelService;

/**
 * This class...
 * 
 * 
 */
public class BudgetUser extends PersistableBusinessObjectBase implements Comparable {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetUser.class);
    private transient BudgetPersonnelService budgetPersonnelService;
    private transient ChartUserService chartUserService;

    private String documentNumber; // RDOC_NBR
    private Integer budgetUserSequenceNumber; // BDGT_USR_SEQ_NBR
    private String fiscalCampusCode; // EMP_FSCL_CMP_CD
    private String primaryDepartmentCode; // EMP_PRM_DEPT_CD
    private KualiDecimal baseSalary; // PRSN_BASE_SLRY
    private Integer budgetSalaryFiscalYear;
    private String role; //
    private String personUniversalIdentifier;
    private UniversalUser user;
    private String appointmentTypeCode; // Not present in the database - only for the convenience of the user interface
    private String appointmentTypeDescription; // Not present in the database - only for the convenience of the user interface
    private boolean personSeniorKeyIndicator;
    private boolean personSecretarialClericalIndicator;
    private boolean personPostDoctoralIndicator;
    private String personNamePrefixText;
    private String personNameSuffixText;
    private String personSalaryJustificationText;
    private boolean personProjectDirectorIndicator;
    private Integer personHourlyNumber;

    private List userAppointmentTasks = new ArrayList();

    // Only used to ease development of the UI - could/should this be somewhere else?
    private Integer currentTaskNumber;
    private Integer previousTaskNumber;
    private String previousAppointmentTypeCode;
    private String previousSecondaryAppointmentTypeCode;
    private String secondaryAppointmentTypeCode;
    private boolean delete;

    /**
     * Constructs a BudgetUser.java.
     */
    public BudgetUser() {
        super();
        budgetPersonnelService = SpringContext.getBean(BudgetPersonnelService.class);
        chartUserService = (ChartUserService) SpringContext.getBean(KualiModuleService.class).getModule("chart").getModuleUserService();
    }
    
    public BudgetUser(String documentNumber, Integer budgetUserSequenceNumber) {
        this();
        this.documentNumber = documentNumber;
        this.budgetUserSequenceNumber = budgetUserSequenceNumber;
    }

    public BudgetUser(BudgetUser budgetUser) {
        this();
        this.documentNumber = budgetUser.getDocumentNumber();
        this.budgetUserSequenceNumber = budgetUser.getBudgetUserSequenceNumber();
        this.fiscalCampusCode = budgetUser.getFiscalCampusCode();
        this.primaryDepartmentCode = budgetUser.getPrimaryDepartmentCode();
        this.baseSalary = budgetUser.getBaseSalary();
        this.role = budgetUser.getRole();
        this.personUniversalIdentifier = budgetUser.getPersonUniversalIdentifier();
        this.personNamePrefixText = budgetUser.getPersonNamePrefixText();
        this.personNameSuffixText = budgetUser.getPersonNameSuffixText();
        this.personSalaryJustificationText = budgetUser.getPersonSalaryJustificationText();
        this.personProjectDirectorIndicator = budgetUser.isPersonProjectDirectorIndicator();
        this.personSeniorKeyIndicator = budgetUser.isPersonSeniorKeyIndicator();
        this.personSecretarialClericalIndicator = budgetUser.isPersonSecretarialClericalIndicator();
        this.personPostDoctoralIndicator = budgetUser.isPersonPostDoctoralIndicator();
        this.budgetSalaryFiscalYear = budgetUser.getBudgetSalaryFiscalYear();
        
        this.userAppointmentTasks = new ArrayList(budgetUser.getUserAppointmentTasks());
    }

    public void initializeBudgetUser(BudgetDocument budgetDocument) {
        this.setBudgetUserSequenceNumber(budgetDocument.getPersonnelNextSequenceNumber());
        this.setDocumentNumber(budgetDocument.getDocumentNumber());
        this.synchronizeUserObject();
        this.createUserAppointmentTasks(budgetDocument);
    }

    public void createUserAppointmentTasks(BudgetDocument budgetDocument) {
        budgetPersonnelService.createPersonnelDetail(this, budgetDocument);
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("userSequenceNumber", this.budgetUserSequenceNumber);
        return m;
    }


    /**
     * Gets the baseSalary attribute.
     * 
     * @return Returns the baseSalary.
     */
    public KualiDecimal getBaseSalary() {
        return baseSalary;
    }


    /**
     * Sets the baseSalary attribute value.
     * 
     * @param baseSalary The baseSalary to set.
     */
    public void setBaseSalary(KualiDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }


    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the fiscalCampusCode attribute.
     * 
     * @return Returns the fiscalCampusCode.
     */
    public String getFiscalCampusCode() {
        return fiscalCampusCode;
    }


    /**
     * Sets the fiscalCampusCode attribute value.
     * 
     * @param fiscalCampusCode The fiscalCampusCode to set.
     */
    public void setFiscalCampusCode(String fiscalCampusCode) {
        this.fiscalCampusCode = fiscalCampusCode;
    }


    /**
     * Gets the primaryDepartmentCode attribute.
     * 
     * @return Returns the primaryDepartmentCode.
     */
    public String getPrimaryDepartmentCode() {
        return primaryDepartmentCode;
    }


    /**
     * Sets the primaryDepartmentCode attribute value.
     * 
     * @param primaryDepartmentCode The primaryDepartmentCode to set.
     */
    public void setPrimaryDepartmentCode(String primaryDepartmentCode) {
        this.primaryDepartmentCode = primaryDepartmentCode;
    }

    /**
     * Gets the role attribute.
     * 
     * @return Returns the role.
     */
    public String getRole() {
        return role;
    }


    /**
     * Sets the role attribute value.
     * 
     * @param role The role to set.
     */
    public void setRole(String role) {
        this.role = role;
    }


    /**
     * Gets the personUniversalIdentifier attribute.
     * 
     * @return Returns the personUniversalIdentifier.
     */
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }


    /**
     * Sets the personUniversalIdentifier attribute value.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }


    /**
     * Gets the user attribute.
     * 
     * @return Returns the user.
     */
    public UniversalUser getUser() {
        user = SpringContext.getBean(UniversalUserService.class).updateUniversalUserIfNecessary(personUniversalIdentifier, user);
        return user;
    }


    /**
     * Sets the user attribute value.
     * 
     * @param user The user to set.
     * @deprecated - Should not be set, should be retrieved by SpringContext each time.  See getUser() above.
     */
    public void setUser(UniversalUser user) {
        this.user = user;
    }


    /**
     * Gets the userSequenceNumber attribute.
     * 
     * @return Returns the userSequenceNumber.
     */
    public Integer getBudgetUserSequenceNumber() {
        return budgetUserSequenceNumber;
    }


    /**
     * Sets the userSequenceNumber attribute value.
     * 
     * @param userSequenceNumber The userSequenceNumber to set.
     */
    public void setBudgetUserSequenceNumber(Integer userSequenceNumber) {
        this.budgetUserSequenceNumber = userSequenceNumber;
    }

    /**
     * Gets the personNamePrefixText attribute.
     * 
     * @return Returns the personNamePrefixText
     * 
     */
    public String getPersonNamePrefixText() {
        return personNamePrefixText;
    }

    /**
     * Sets the personNamePrefixText attribute.
     * 
     * @param personNamePrefixText The personNamePrefixText to set.
     * 
     */
    public void setPersonNamePrefixText(String personNamePrefixText) {
        this.personNamePrefixText = personNamePrefixText;
    }

    /**
     * Gets the personNameSuffixText attribute.
     * 
     * @return Returns the personNameSuffixText
     * 
     */
    public String getPersonNameSuffixText() {
        return personNameSuffixText;
    }

    /**
     * Sets the personNameSuffixText attribute.
     * 
     * @param personNameSuffixText The personNameSuffixText to set.
     * 
     */
    public void setPersonNameSuffixText(String personNameSuffixText) {
        this.personNameSuffixText = personNameSuffixText;
    }

    /**
     * Gets the personPostDoctoralIndicator attribute.
     * 
     * @return Returns the personPostDoctoralIndicator
     * 
     */
    public boolean isPersonPostDoctoralIndicator() {
        return personPostDoctoralIndicator;
    }

    /**
     * Sets the personPostDoctoralIndicator attribute.
     * 
     * @param personPostDoctoralIndicator The personPostDoctoralIndicator to set.
     * 
     */
    public void setPersonPostDoctoralIndicator(boolean personPostDoctoralIndicator) {
        this.personPostDoctoralIndicator = personPostDoctoralIndicator;
    }

    /**
     * Gets the personSecretarialClericalIndicator attribute.
     * 
     * @return Returns the personSecretarialClericalIndicator
     * 
     */
    public boolean isPersonSecretarialClericalIndicator() {
        return personSecretarialClericalIndicator;
    }

    /**
     * Sets the personSecretarialClericalIndicator attribute.
     * 
     * @param personSecretarialClericalIndicator The personSecretarialClericalIndicator to set.
     * 
     */
    public void setPersonSecretarialClericalIndicator(boolean personSecretarialClericalIndicator) {
        this.personSecretarialClericalIndicator = personSecretarialClericalIndicator;
    }

    /**
     * Gets the personSeniorKeyIndicator attribute.
     * 
     * @return Returns the personSeniorKeyIndicator
     * 
     */
    public boolean isPersonSeniorKeyIndicator() {
        return personSeniorKeyIndicator;
    }

    /**
     * Sets the personSeniorKeyIndicator attribute.
     * 
     * @param personSeniorKeyIndicator The personSeniorKeyIndicator to set.
     * 
     */
    public void setPersonSeniorKeyIndicator(boolean personSeniorKeyIndicator) {
        this.personSeniorKeyIndicator = personSeniorKeyIndicator;
    }

    /**
     * Makes sure that copied fields between the referenced UniversalUser object and this object are properly synchronized.
     */
    public void synchronizeUserObject() {
        if (this.getPersonUniversalIdentifier() != null) {
            this.getUser();
            if (this.getUser().getPersonBaseSalaryAmount() != null) {
                this.baseSalary = this.user.getPersonBaseSalaryAmount();
            }
            else {
                this.baseSalary = new KualiDecimal(0);
            }

            StringTokenizer chartOrg = new StringTokenizer(this.user.getPrimaryDepartmentCode(), "-");
            
            this.fiscalCampusCode = chartOrg.nextToken();
            this.primaryDepartmentCode = chartOrg.hasMoreElements() ? chartOrg.nextToken() : this.fiscalCampusCode;
        }
        else {
            this.baseSalary = new KualiDecimal(0);
            this.fiscalCampusCode = "";
            this.primaryDepartmentCode = "";
        }
    }


    /**
     * Gets the appointmentType attribute.
     * 
     * @return Returns the appointmentType.
     */
    public String getAppointmentTypeCode() {
        return appointmentTypeCode;
    }


    /**
     * Sets the appointmentType attribute value.
     * 
     * @param appointmentType The appointmentType to set.
     */
    public void setAppointmentTypeCode(String appointmentTypeCode) {
        this.appointmentTypeCode = appointmentTypeCode;
    }

    /**
     * Gets the appointmentTypeDescription attribute.
     * 
     * @return Returns the appointmentTypeDescription.
     */
    public String getAppointmentTypeDescription() {
        return appointmentTypeDescription;
    }

    /**
     * Sets the appointmentTypeDescription attribute value.
     * 
     * @param appointmentTypeDescription The appointmentTypeDescription to set.
     */
    public void setAppointmentTypeDescription(String appointmentTypeDescription) {
        this.appointmentTypeDescription = appointmentTypeDescription;
    }
    
    /**
     * Log the state of this object.
     */
    public void logState() {
        LOG.info("userSequenceNumber: (" + this.budgetUserSequenceNumber + ")");
        /* LOG.info(" version: (" + this.getVersion() +")"); */
        LOG.info("  documentNumber: (" + this.documentNumber + ")");
        LOG.info("  fiscalCampusCode: (" + this.fiscalCampusCode + ")");
        LOG.info("  primaryDepartmentCode: (" + this.primaryDepartmentCode + ")");
        LOG.info("  baseSalary: (" + this.baseSalary + ")");
        LOG.info("  role: (" + this.role + ")");
        LOG.info("  personUniversalIdentifier: (" + this.personUniversalIdentifier + ")");
        LOG.info("  appointmentTypeCode: (" + this.appointmentTypeCode + ")");
        LOG.info("  appointmentTypeDescription: (" + this.appointmentTypeDescription + ")");
        LOG.info("  personSeniorKeyIndicator: (" + this.personSeniorKeyIndicator + ")");
        LOG.info("  personSecretarialClericalIndicator: (" + this.personSecretarialClericalIndicator + ")");
        LOG.info("  personPostDoctoralIndicator: (" + this.personPostDoctoralIndicator + ")");
        LOG.info("  personNamePrefixText: (" + this.personNamePrefixText + ")");
        LOG.info("  personNameSuffixText: (" + this.personNameSuffixText + ")");
        if (this.user == null) {
            LOG.info("  user: <null>");
        }
        else {
            LOG.info("  user: (" + this.user.getPersonPayrollIdentifier() + ")");
        }
    }

    public UserAppointmentTask getUserAppointmentTask(int index) {
        while (getUserAppointmentTasks().size() <= index) {
            getUserAppointmentTasks().add(new UserAppointmentTask());
        }
        return (UserAppointmentTask) getUserAppointmentTasks().get(index);
    }

    /**
     * Gets the userAppointmentTasks attribute.
     * 
     * @return Returns the userAppointmentTasks.
     */
    public List<UserAppointmentTask> getUserAppointmentTasks() {
        return userAppointmentTasks;
    }

    /**
     * Sets the userAppointmentTasks attribute value.
     * 
     * @param userAppointmentTasks The userAppointmentTasks to set.
     */
    public void setUserAppointmentTasks(List userAppointmentTasks) {
        this.userAppointmentTasks = userAppointmentTasks;
    }

    /**
     * Gets the budgetPersonnelService attribute.
     * 
     * @return Returns the budgetPersonnelService.
     */
    public BudgetPersonnelService getBudgetPersonnelService() {
        return budgetPersonnelService;
    }

    /**
     * Sets the budgetPersonnelService attribute value.
     * 
     * @param budgetPersonnelService The budgetPersonnelService to set.
     */
    public void setBudgetPersonnelService(BudgetPersonnelService budgetPersonnelService) {
        this.budgetPersonnelService = budgetPersonnelService;
    }

    /**
     * Gets the currentTask attribute.
     * 
     * @return Returns the currentTask.
     */
    public Integer getCurrentTaskNumber() {
        return currentTaskNumber;
    }

    /**
     * Sets the currentTask attribute value.
     * 
     * @param currentTask The currentTask to set.
     */
    public void setCurrentTaskNumber(Integer currentTaskNumber) {
        this.currentTaskNumber = currentTaskNumber;
    }

    /**
     * Gets the personSalaryJustificationText attribute.
     * 
     * @return Returns the personSalaryJustificationText.
     */
    public String getPersonSalaryJustificationText() {
        return personSalaryJustificationText;
    }

    /**
     * Sets the personSalaryJustificationText attribute value.
     * 
     * @param personSalaryJustificationText The personSalaryJustificationText to set.
     */
    public void setPersonSalaryJustificationText(String personSalaryJustificationText) {
        this.personSalaryJustificationText = personSalaryJustificationText;
    }

    /**
     * Gets the personProjectDirectorIndicator attribute.
     * 
     * @return Returns the personProjectDirectorIndicator
     * 
     */
    public boolean isPersonProjectDirectorIndicator() {
        return personProjectDirectorIndicator;
    }

    /**
     * Sets the personProjectDirectorIndicator attribute.
     * 
     * @param personProjectDirectorIndicator The personProjectDirectorIndicator to set.
     * 
     */
    public void setPersonProjectDirectorIndicator(boolean personProjectDirectorIndicator) {
        this.personProjectDirectorIndicator = personProjectDirectorIndicator;
    }

    /**
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeDelete(org.apache.ojb.broker.PersistenceBroker)
     */
    public void beforeDelete(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        super.beforeDelete(persistenceBroker);
        this.refreshReferenceObject("userAppointmentTasks");
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        BudgetUser budgetUser = (BudgetUser) o;
        return (this.isPersonProjectDirectorIndicator() ? new Integer(0) : this.getBudgetUserSequenceNumber()).compareTo(budgetUser.isPersonProjectDirectorIndicator() ? new Integer(0) : budgetUser.getBudgetUserSequenceNumber());
    }

    public String getPreviousAppointmentTypeCode() {
        return previousAppointmentTypeCode;
    }

    public void setPreviousAppointmentTypeCode(String previousAppointmentTypeCode) {
        this.previousAppointmentTypeCode = previousAppointmentTypeCode;
    }

    public Integer getPreviousTaskNumber() {
        return previousTaskNumber;
    }

    public void setPreviousTaskNumber(Integer previousTaskNumber) {
        this.previousTaskNumber = previousTaskNumber;
    }

    public Integer getPersonHourlyNumber() {
        return personHourlyNumber;
    }

    public void setPersonHourlyNumber(Integer personHourlyNumber) {
        this.personHourlyNumber = personHourlyNumber;
    }

    public Integer getBudgetSalaryFiscalYear() {
        return budgetSalaryFiscalYear;
    }

    public void setBudgetSalaryFiscalYear(Integer budgetSalaryFiscalYear) {
        this.budgetSalaryFiscalYear = budgetSalaryFiscalYear;
    }

    public String getSecondaryAppointmentTypeCode() {
        return secondaryAppointmentTypeCode;
    }

    public void setSecondaryAppointmentTypeCode(String secondaryAppointmentTypeCode) {
        this.secondaryAppointmentTypeCode = secondaryAppointmentTypeCode;
    }

    public String getPreviousSecondaryAppointmentTypeCode() {
        return previousSecondaryAppointmentTypeCode;
    }

    public void setPreviousSecondaryAppointmentTypeCode(String previousSecondaryAppointmentTypeCode) {
        this.previousSecondaryAppointmentTypeCode = previousSecondaryAppointmentTypeCode;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
