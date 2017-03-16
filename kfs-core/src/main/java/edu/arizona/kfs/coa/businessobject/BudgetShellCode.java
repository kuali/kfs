package edu.arizona.kfs.coa.businessobject;

import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.KualiCodeBase;


/**
 * UAF-120 / MOD-FP0072-01 : Budget Shell and Cross Organization Attributes for
 * Account
 * 
 * @author Jonathan Keller <keller.jonathan@gmail.com>
 * @author Adam Kost <kosta@email.arizona.edu>
 */
public class BudgetShellCode extends KualiCodeBase implements MutableInactivatable {

    private static final long serialVersionUID = 1L;
    private static transient volatile PersonService personService;

    // Database fields
    private String description;
    private String groupCode;
    private String groupName;
    private Integer fiscalYearClosed;
    private boolean reportableGroup;
    private String vicePresidentPrincipalId;
    private String departmentHeadPrincipalId;
    private String budgetAnalystPrincipalId;
    private String budgetShellContactPrincipalId;

    // Additional unused database fields
    private String summaryGroupId;
    private String summaryGroupCode;
    private String summaryGroupName;
    private String reportingLevelCode;
    private String reportingLevelName;
    private boolean internalIndicator;
    private String notes;
    private String comments;

    // Helper objects
    private transient volatile Person vicePresident;
    private transient volatile Person departmentHead;
    private transient volatile Person budgetAnalyst;
    private transient volatile Person budgetShellContact;
    private transient volatile Person summaryGroup;
    private transient volatile SystemOptions systemOptions;
    
    public String getBudgetShellCode() {
        return getCode();
    }
    
    public void setBudgetShellCode( String budgetShellCode ) {
        setCode( budgetShellCode );
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getFiscalYearClosed() {
        return fiscalYearClosed;
    }

    public void setFiscalYearClosed(Integer fiscalYearClosed) {
        this.fiscalYearClosed = fiscalYearClosed;
    }

    public boolean isReportableGroup() {
        return reportableGroup;
    }

    public void setReportableGroup(boolean reportableGroup) {
        this.reportableGroup = reportableGroup;
    }

    public String getVicePresidentPrincipalId() {
        return vicePresidentPrincipalId;
    }

    public void setVicePresidentPrincipalId(String vicePresidentPrincipalId) {
        this.vicePresidentPrincipalId = vicePresidentPrincipalId;
    }

    public String getDepartmentHeadPrincipalId() {
        return departmentHeadPrincipalId;
    }

    public void setDepartmentHeadPrincipalId(String departmentHeadPrincipalId) {
        this.departmentHeadPrincipalId = departmentHeadPrincipalId;
    }

    public String getBudgetAnalystPrincipalId() {
        return budgetAnalystPrincipalId;
    }

    public void setBudgetAnalystPrincipalId(String budgetAnalystPrincipalId) {
        this.budgetAnalystPrincipalId = budgetAnalystPrincipalId;
    }

    public String getBudgetShellContactPrincipalId() {
        return budgetShellContactPrincipalId;
    }

    public void setBudgetShellContactPrincipalId(String budgetShellContactPrincipalId) {
        this.budgetShellContactPrincipalId = budgetShellContactPrincipalId;
    }

    public String getSummaryGroupId() {
        return summaryGroupId;
    }

    public void setSummaryGroupId(String summaryGroupId) {
        this.summaryGroupId = summaryGroupId;
    }

    public String getSummaryGroupCode() {
        return summaryGroupCode;
    }

    public void setSummaryGroupCode(String summaryGroupCode) {
        this.summaryGroupCode = summaryGroupCode;
    }

    public String getSummaryGroupName() {
        return summaryGroupName;
    }

    public void setSummaryGroupName(String summaryGroupName) {
        this.summaryGroupName = summaryGroupName;
    }

    public String getReportingLevelCode() {
        return reportingLevelCode;
    }

    public void setReportingLevelCode(String reportingLevelCode) {
        this.reportingLevelCode = reportingLevelCode;
    }

    public String getReportingLevelName() {
        return reportingLevelName;
    }

    public void setReportingLevelName(String reportingLevelName) {
        this.reportingLevelName = reportingLevelName;
    }

    public boolean isInternalIndicator() {
        return internalIndicator;
    }

    public void setInternalIndicator(boolean internalIndicator) {
        this.internalIndicator = internalIndicator;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Person getVicePresident() {
        vicePresident = getPersonService().updatePersonIfNecessary(vicePresidentPrincipalId, vicePresident);
        return vicePresident;
    }

    public void setVicePresident(Person vicePresident) {
        this.vicePresident = vicePresident;
        setVicePresidentPrincipalId(vicePresident.getPrincipalId());
    }

    public Person getDepartmentHead() {
        departmentHead = getPersonService().updatePersonIfNecessary(departmentHeadPrincipalId, departmentHead);
        return departmentHead;
    }

    public void setDepartmentHead(Person departmentHead) {
        this.departmentHead = departmentHead;
        setDepartmentHeadPrincipalId(departmentHead.getPrincipalId());
    }

    public Person getBudgetAnalyst() {
        budgetAnalyst = getPersonService().updatePersonIfNecessary(budgetAnalystPrincipalId, budgetAnalyst);
        return budgetAnalyst;
    }

    public void setBudgetAnalyst(Person budgetAnalyst) {
        this.budgetAnalyst = budgetAnalyst;
        setBudgetAnalystPrincipalId(budgetAnalyst.getPrincipalId());
    }

    public Person getBudgetShellContact() {
        budgetShellContact = getPersonService().updatePersonIfNecessary(budgetShellContactPrincipalId, budgetShellContact);
        return budgetShellContact;
    }

    public void setBudgetShellContact(Person budgetShellContact) {
        this.budgetShellContact = budgetShellContact;
        setBudgetShellContactPrincipalId(budgetShellContact.getPrincipalId());
    }
    
    public Person getSummaryGroup() {
        summaryGroup = getPersonService().updatePersonIfNecessary(summaryGroupId, summaryGroup);
        return summaryGroup;
    }
    
    public void setSummaryGroup(Person summaryGroup) {
        this.summaryGroup = summaryGroup;
    }

    public SystemOptions getSystemOptions() {
        return systemOptions;
    }

    public void setSystemOptions(SystemOptions systemOptions) {
        this.systemOptions = systemOptions;
    }

    protected static PersonService getPersonService() {
        if (personService == null) {
            personService = SpringContext.getBean(PersonService.class);
        }
        return personService;
    }

}