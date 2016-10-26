package edu.arizona.kfs.pdp.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

public class ShippingAccount extends PersistableBusinessObjectBase {
	private String shippingAccountNumber;
	private String shippingAccountType;
	private String accountNumber;
	private String financialObjectCode;
	private String chartOfAccountsCode;
	private String contactPrincipalId;
	private String subAccountNumber;
	private String financialSubObjectCode;
	private String projCode;
	private String organizationReferenceId;
	private boolean active;
	
	private Person contactUser;
	private Account account;
	private Chart chartOfAccounts;
	private SubAccount subAccount;
	private ObjectCode	objectCode;
	private SubObjectCode subObjectCode;
	private ProjectCode	projectCode;
	private Person accountFiscalOfficerUser;
	
	public String getShippingAccountNumber() {
		return shippingAccountNumber;
	}
	
	public void setShippingAccountNumber(String shippingAccountNumber) {
		this.shippingAccountNumber = shippingAccountNumber;
	}
	
	public String getShippingAccountType() {
		return shippingAccountType;
	}
	
	public void setShippingAccountType(String shippingAccountType) {
		this.shippingAccountType = shippingAccountType;
	}
	
	public String getAccountNumber() {
		return accountNumber;
	}
	
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	public String getFinancialObjectCode() {
		return financialObjectCode;
	}
	
	public void setFinancialObjectCode(String financialObjectCode) {
		this.financialObjectCode = financialObjectCode;
	}
	
	public String getChartOfAccountsCode() {
		return chartOfAccountsCode;
	}
	
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}
	
	public String getContactPrincipalId() {
		return contactPrincipalId;
	}
	
	public void setContactPrincipalId(String contactPrincipalId) {
		this.contactPrincipalId = contactPrincipalId;
	}
	
	public String getSubAccountNumber() {
		return subAccountNumber;
	}
	
	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
	}
	
	public String getFinancialSubObjectCode() {
		return financialSubObjectCode;
	}
	
	public void setFinancialSubObjectCode(String financialSubObjectCode) {
		this.financialSubObjectCode = financialSubObjectCode;
	}
	
	public String getProjCode() {
		return projCode;
	}
	
	public void setProjCode(String projCode) {
		this.projCode = projCode;
	}
	
	public String getOrganizationReferenceId() {
		return organizationReferenceId;
	}
	
	public void setOrganizationReferenceId(String organizationReferenceId) {
		this.organizationReferenceId = organizationReferenceId;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public Person getContactUser() {
		contactUser = SpringContext.getBean(PersonService.class).updatePersonIfNecessary(contactPrincipalId, contactUser);
		return contactUser;
	}
	
	public void setContactUser(Person contactUser) {
		this.contactUser = contactUser;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}
	
	public Chart getChartOfAccounts() {
		return chartOfAccounts;
	}
	
	public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}
	
	public SubAccount getSubAccount() {
		return subAccount;
	}
	
	public void setSubAccount(SubAccount subAccount) {
		this.subAccount = subAccount;
	}
	
	public ObjectCode getObjectCode() {
		return objectCode;
	}
	
	public void setObjectCode(ObjectCode objectCode) {
		this.objectCode = objectCode;
	}
	
	public SubObjectCode getSubObjectCode() {
		return subObjectCode;
	}
	
	public void setSubObjectCode(SubObjectCode subObjectCode) {
		this.subObjectCode = subObjectCode;
	}
	
	public ProjectCode getProjectCode() {
		return projectCode;
	}
	
	public void setProjectCode(ProjectCode projectCode) {
		this.projectCode = projectCode;
	}
	
	public Person getAccountFiscalOfficerUser() {
		if (ObjectUtils.isNotNull(account) && StringUtils.isNotBlank(account.getAccountFiscalOfficerSystemIdentifier())) {
			accountFiscalOfficerUser = SpringContext.getBean(PersonService.class).updatePersonIfNecessary(account.getAccountFiscalOfficerSystemIdentifier(), accountFiscalOfficerUser);
			return accountFiscalOfficerUser;
		}
		else {
			return null;
		}
	}
	
	public void setAccountFiscalOfficerUser(Person accountFiscalOfficerUser) {
		this.accountFiscalOfficerUser = accountFiscalOfficerUser;
	}	
	
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		
		map.put("shippingAccountNumber", getShippingAccountNumber());
		map.put("shippingAccountType", getShippingAccountType());
		map.put("accountNumber", getAccountNumber());
		map.put("financialObjectCode", getFinancialObjectCode());
		
		return map;		
	}
	
}
