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

package org.kuali.module.budget.bo;

import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.workflow.service.KualiWorkflowInfo;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.gl.bo.TransientBalanceInquiryAttributes;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actiontaken.ActionTakenService;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.ActionTakenVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * 
 */
public class BudgetConstructionAccountSelect extends PersistableBusinessObjectBase {

	private String personUniversalIdentifier;
	private Integer universityFiscalYear;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String documentNumber;
	private Integer organizationLevelCode;
	private String organizationChartOfAccountsCode;
	private String organizationCode;
	private String financialDocumentStatusCode;
	private String financialDocumentInitiatorIdentifier;
	private Date financialDocumentCreateDate;

    // we use the linkButtonOption from this object
    private TransientBalanceInquiryAttributes dummyBusinessObject;

    private BudgetConstructionHeader budgetConstructionHeader;
	private Account account;
	private Chart chartOfAccounts;
	private Chart organizationChartOfAccounts;
    private SubAccount subAccount;
    private Org organization;
   
	/**
	 * Default constructor.
	 */
	public BudgetConstructionAccountSelect() {
        super();
        this.dummyBusinessObject = new TransientBalanceInquiryAttributes();
        this.dummyBusinessObject.setLinkButtonOption("Load Document");
	}

	/**
	 * Gets the personUniversalIdentifier attribute.
	 * 
	 * @return Returns the personUniversalIdentifier
	 * 
	 */
	public String getPersonUniversalIdentifier() { 
		return personUniversalIdentifier;
	}

	/**
	 * Sets the personUniversalIdentifier attribute.
	 * 
	 * @param personUniversalIdentifier The personUniversalIdentifier to set.
	 * 
	 */
	public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
		this.personUniversalIdentifier = personUniversalIdentifier;
	}


	/**
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return Returns the universityFiscalYear
	 * 
	 */
	public Integer getUniversityFiscalYear() { 
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 * 
	 * @param universityFiscalYear The universityFiscalYear to set.
	 * 
	 */
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}


	/**
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the accountNumber attribute.
	 * 
	 * @return Returns the accountNumber
	 * 
	 */
	public String getAccountNumber() { 
		return accountNumber;
	}

	/**
	 * Sets the accountNumber attribute.
	 * 
	 * @param accountNumber The accountNumber to set.
	 * 
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	/**
	 * Gets the subAccountNumber attribute.
	 * 
	 * @return Returns the subAccountNumber
	 * 
	 */
	public String getSubAccountNumber() { 
		return subAccountNumber;
	}

	/**
	 * Sets the subAccountNumber attribute.
	 * 
	 * @param subAccountNumber The subAccountNumber to set.
	 * 
	 */
	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
	}


	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the organizationLevelCode attribute.
	 * 
	 * @return Returns the organizationLevelCode
	 * 
	 */
	public Integer getOrganizationLevelCode() { 
		return organizationLevelCode;
	}

	/**
	 * Sets the organizationLevelCode attribute.
	 * 
	 * @param organizationLevelCode The organizationLevelCode to set.
	 * 
	 */
	public void setOrganizationLevelCode(Integer organizationLevelCode) {
		this.organizationLevelCode = organizationLevelCode;
	}


	/**
	 * Gets the organizationChartOfAccountsCode attribute.
	 * 
	 * @return Returns the organizationChartOfAccountsCode
	 * 
	 */
	public String getOrganizationChartOfAccountsCode() { 
		return organizationChartOfAccountsCode;
	}

	/**
	 * Sets the organizationChartOfAccountsCode attribute.
	 * 
	 * @param organizationChartOfAccountsCode The organizationChartOfAccountsCode to set.
	 * 
	 */
	public void setOrganizationChartOfAccountsCode(String organizationChartOfAccountsCode) {
		this.organizationChartOfAccountsCode = organizationChartOfAccountsCode;
	}


	/**
	 * Gets the organizationCode attribute.
	 * 
	 * @return Returns the organizationCode
	 * 
	 */
	public String getOrganizationCode() { 
		return organizationCode;
	}

	/**
	 * Sets the organizationCode attribute.
	 * 
	 * @param organizationCode The organizationCode to set.
	 * 
	 */
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}


	/**
	 * Gets the financialDocumentStatusCode attribute.
	 * 
	 * @return Returns the financialDocumentStatusCode
	 * 
	 */
	public String getFinancialDocumentStatusCode() { 
		return financialDocumentStatusCode;
	}

	/**
	 * Sets the financialDocumentStatusCode attribute.
	 * 
	 * @param financialDocumentStatusCode The financialDocumentStatusCode to set.
	 * 
	 */
	public void setFinancialDocumentStatusCode(String financialDocumentStatusCode) {
		this.financialDocumentStatusCode = financialDocumentStatusCode;
	}


	/**
	 * Gets the financialDocumentInitiatorIdentifier attribute.
	 * 
	 * @return Returns the financialDocumentInitiatorIdentifier
	 * 
	 */
	public String getFinancialDocumentInitiatorIdentifier(){

        // TODO need to add method getActionsTakenIgnoreCurrentInd to KualiWorkflowInfo and Workflow support
        if (this.financialDocumentInitiatorIdentifier == null){
            try {
                Long docNum = Long.valueOf(this.getDocumentNumber());

                ActionTakenService actionTakenService = KEWServiceLocator.getActionTakenService();
                List<ActionTakenValue> actionsTaken = (List<ActionTakenValue>) actionTakenService.findByRouteHeaderIdIgnoreCurrentInd(docNum);
                if (actionsTaken.size() > 0){
                    this.financialDocumentInitiatorIdentifier = SpringContext.getBean(UniversalUserService.class).getUniversalUser(actionsTaken.get(actionsTaken.size()-1).getWorkflowId()).getPersonUserIdentifier();
                    this.financialDocumentCreateDate = new Date(actionsTaken.get(actionsTaken.size()-1).getActionDate().getTime());
                } else {
                    this.financialDocumentInitiatorIdentifier = "NotFound";
                }

//                KualiWorkflowInfo kualiWorkflowInfo = SpringContext.getBean(KualiWorkflowInfo.class);
//                ActionTakenVO[] actionsTaken = kualiWorkflowInfo.getActionsTaken(docNum);
//                if (actionsTaken.length > 0) {
//                    this.financialDocumentInitiatorIdentifier = actionsTaken[actionsTaken.length-1].getUserVO().getNetworkId();
//                    this.financialDocumentCreateDate = new Date(actionsTaken[actionsTaken.length-1].getActionDate().getTimeInMillis());
//                } else {
//                this.financialDocumentInitiatorIdentifier = "NotFound";
//                }

            }
            catch (Exception e){
                this.financialDocumentInitiatorIdentifier = "LookupError";
            }
        }
		return financialDocumentInitiatorIdentifier;
	}

	/**
	 * Sets the financialDocumentInitiatorIdentifier attribute.
	 * 
	 * @param financialDocumentInitiatorIdentifier The financialDocumentInitiatorIdentifier to set.
	 * 
	 */
	public void setFinancialDocumentInitiatorIdentifier(String financialDocumentInitiatorIdentifier) {
		this.financialDocumentInitiatorIdentifier = financialDocumentInitiatorIdentifier;
	}


	/**
	 * Gets the financialDocumentCreateDate attribute.
	 * 
	 * @return Returns the financialDocumentCreateDate
	 * 
	 */
	public Date getFinancialDocumentCreateDate() {

        // TODO need to add method getActionsTakenIgnoreCurrentInd to KualiWorkflowInfo and Workflow support
        if (this.financialDocumentCreateDate == null){
            try {
                Long docNum = Long.valueOf(this.getDocumentNumber());

                ActionTakenService actionTakenService = KEWServiceLocator.getActionTakenService();
                List<ActionTakenValue> actionsTaken = (List<ActionTakenValue>) actionTakenService.findByRouteHeaderIdIgnoreCurrentInd(docNum);
                if (actionsTaken.size() > 0){
                    this.financialDocumentInitiatorIdentifier = SpringContext.getBean(UniversalUserService.class).getUniversalUser(actionsTaken.get(actionsTaken.size()-1).getWorkflowId()).getPersonUserIdentifier();
                    this.financialDocumentCreateDate = new Date(actionsTaken.get(actionsTaken.size()-1).getActionDate().getTime());
                }

//                KualiWorkflowInfo kualiWorkflowInfo = SpringContext.getBean(KualiWorkflowInfo.class);
//                ActionTakenVO[] actionsTaken = kualiWorkflowInfo.getActionsTaken(docNum);
//                if (actionsTaken.length > 0) {
//                    this.financialDocumentInitiatorIdentifier = actionsTaken[actionsTaken.length-1].getUserVO().getNetworkId();
//                    this.financialDocumentCreateDate = new Date(actionsTaken[actionsTaken.length-1].getActionDate().getTimeInMillis());
//                }

            }
            catch (Exception e){
                // nothing
            }
        }
		return financialDocumentCreateDate;
	}

	/**
	 * Sets the financialDocumentCreateDate attribute.
	 * 
	 * @param financialDocumentCreateDate The financialDocumentCreateDate to set.
	 * 
	 */
	public void setFinancialDocumentCreateDate(Date financialDocumentCreateDate) {
		this.financialDocumentCreateDate = financialDocumentCreateDate;
	}


	/**
	 * Gets the budgetConstructionHeader attribute.
	 * 
	 * @return Returns the budgetConstructionHeader
	 * 
	 */
	public BudgetConstructionHeader getBudgetConstructionHeader() { 
		return budgetConstructionHeader;
	}

	/**
	 * Sets the budgetConstructionHeader attribute.
	 * 
	 * @param budgetConstructionHeader The budgetConstructionHeader to set.
	 * @deprecated
	 */
	public void setBudgetConstructionHeader(BudgetConstructionHeader budgetConstructionHeader) {
		this.budgetConstructionHeader = budgetConstructionHeader;
	}

	/**
	 * Gets the organization attribute.
	 * 
	 * @return Returns the organization
	 * 
	 */
	public Org getOrganization() { 
		return organization;
	}

	/**
	 * Sets the organization attribute.
	 * 
	 * @param organization The organization to set.
	 * @deprecated
	 */
	public void setOrganization(Org organization) {
		this.organization = organization;
	}

	/**
	 * Gets the account attribute.
	 * 
	 * @return Returns the account
	 * 
	 */
	public Account getAccount() { 
		return account;
	}

	/**
	 * Sets the account attribute.
	 * 
	 * @param account The account to set.
	 * @deprecated
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * Gets the chartOfAccounts attribute.
	 * 
	 * @return Returns the chartOfAccounts
	 * 
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
	 * Gets the organizationChartOfAccounts attribute.
	 * 
	 * @return Returns the organizationChartOfAccounts
	 * 
	 */
	public Chart getOrganizationChartOfAccounts() { 
		return organizationChartOfAccounts;
	}

	/**
	 * Sets the organizationChartOfAccounts attribute.
	 * 
	 * @param organizationChartOfAccounts The organizationChartOfAccounts to set.
	 * @deprecated
	 */
	public void setOrganizationChartOfAccounts(Chart organizationChartOfAccounts) {
		this.organizationChartOfAccounts = organizationChartOfAccounts;
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
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * Gets the dummyBusinessObject attribute. 
     * @return Returns the dummyBusinessObject.
     */
    public TransientBalanceInquiryAttributes getDummyBusinessObject() {
        return dummyBusinessObject;
    }

    /**
     * Sets the dummyBusinessObject attribute value.
     * @param dummyBusinessObject The dummyBusinessObject to set.
     */
    public void setDummyBusinessObject(TransientBalanceInquiryAttributes dummyBusinessObject) {
        this.dummyBusinessObject = dummyBusinessObject;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }


}
