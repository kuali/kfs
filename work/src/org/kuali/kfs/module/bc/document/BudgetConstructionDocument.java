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
package org.kuali.module.budget.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.PropertyConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.DocumentHeader;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;

public class BudgetConstructionDocument extends TransactionalDocumentBase {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionDocument.class);

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private Integer organizationLevelCode;
    private String organizationLevelChartOfAccountsCode;
    private String organizationLevelOrganizationCode;
    private String budgetLockUserIdentifier;
    private String budgetTransactionLockUserIdentifier;

    private Chart chartOfAccounts;
    private Account account;
    private SubAccount subAccount;
    private UniversalUser budgetLockUser;
    private UniversalUser budgetTransactionLockUser;
    private Org organizationLevelOrganization;
/*    
    private List budgetConstructionAccountSelect;
*/
    private String financialObjectTypeCode;

    private Collection<PendingBudgetConstructionGeneralLedger> pendingBudgetConstructionGeneralLedgerRevenue;
    private Collection<PendingBudgetConstructionGeneralLedger> pendingBudgetConstructionGeneralLedgerExpenditure;
    
    public BudgetConstructionDocument(){
        super();
/*
        budgetConstructionAccountSelect = new ArrayList();
*/
    }
    
/**
 * 
 * move stuff from constructor to here so as to get out of fred's way
 * initiateDocument would be called from BudgetConstructionAction
 */
    public void initiateDocument(BudgetConstructionHeader budgetConstructionHeader) {

        
        Map fieldValues = new HashMap();
//        fieldValues.put("UNIV_FISCAL_YR", new Integer(2008));
//        fieldValues.put("FIN_COA_CD", "BA");
//        fieldValues.put("ACCOUNT_NBR", "6044906");
//        fieldValues.put("SUB_ACCT_NBR", "-----");
        fieldValues.put("UNIV_FISCAL_YR", budgetConstructionHeader.getUniversityFiscalYear());
        fieldValues.put("FIN_COA_CD", budgetConstructionHeader.getChartOfAccountsCode());
        fieldValues.put("ACCOUNT_NBR", budgetConstructionHeader.getAccountNumber());
        fieldValues.put("SUB_ACCT_NBR", budgetConstructionHeader.getSubAccountNumber());
        
        
        fieldValues.put("FIN_OBJ_TYP_CD", "IN");
        
        pendingBudgetConstructionGeneralLedgerRevenue = SpringServiceLocator.getBusinessObjectService().findMatchingOrderBy(PendingBudgetConstructionGeneralLedger.class, fieldValues, "FIN_OBJECT_CD", true);
        if (LOG.isDebugEnabled()) {
            LOG.debug("pendingBudgetConstructionGeneralLedgerRevenue is: "+pendingBudgetConstructionGeneralLedgerRevenue);
        }
        
        fieldValues.remove("FIN_OBJ_TYP_CD");
        fieldValues.put("FIN_OBJ_TYP_CD", "EX");

        pendingBudgetConstructionGeneralLedgerExpenditure = SpringServiceLocator.getBusinessObjectService().findMatchingOrderBy(PendingBudgetConstructionGeneralLedger.class, fieldValues, "FIN_OBJECT_CD", true);
        if (LOG.isDebugEnabled()) {
            LOG.debug("pendingBudgetConstructionGeneralLedgerExpenditure is: "+pendingBudgetConstructionGeneralLedgerExpenditure);
        }
        
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
     * Gets the organizationLevelChartOfAccountsCode attribute.
     * 
     * @return Returns the organizationLevelChartOfAccountsCode
     * 
     */
    public String getOrganizationLevelChartOfAccountsCode() {
        return organizationLevelChartOfAccountsCode;
    }

    /**
     * Sets the organizationLevelChartOfAccountsCode attribute.
     * 
     * @param organizationLevelChartOfAccountsCode The organizationLevelChartOfAccountsCode to set.
     * 
     */
    public void setOrganizationLevelChartOfAccountsCode(String organizationLevelChartOfAccountsCode) {
        this.organizationLevelChartOfAccountsCode = organizationLevelChartOfAccountsCode;
    }


    /**
     * Gets the organizationLevelOrganizationCode attribute.
     * 
     * @return Returns the organizationLevelOrganizationCode
     * 
     */
    public String getOrganizationLevelOrganizationCode() {
        return organizationLevelOrganizationCode;
    }

    /**
     * Sets the organizationLevelOrganizationCode attribute.
     * 
     * @param organizationLevelOrganizationCode The organizationLevelOrganizationCode to set.
     * 
     */
    public void setOrganizationLevelOrganizationCode(String organizationLevelOrganizationCode) {
        this.organizationLevelOrganizationCode = organizationLevelOrganizationCode;
    }


    /**
     * Gets the budgetLockUserIdentifier attribute.
     * 
     * @return Returns the budgetLockUserIdentifier
     * 
     */
    public String getBudgetLockUserIdentifier() {
        return budgetLockUserIdentifier;
    }

    /**
     * Sets the budgetLockUserIdentifier attribute.
     * 
     * @param budgetLockUserIdentifier The budgetLockUserIdentifier to set.
     * 
     */
    public void setBudgetLockUserIdentifier(String budgetLockUserIdentifier) {
        this.budgetLockUserIdentifier = budgetLockUserIdentifier;
    }


    /**
     * Gets the budgetTransactionLockUserIdentifier attribute.
     * 
     * @return Returns the budgetTransactionLockUserIdentifier
     * 
     */
    public String getBudgetTransactionLockUserIdentifier() {
        return budgetTransactionLockUserIdentifier;
    }

    /**
     * Sets the budgetTransactionLockUserIdentifier attribute.
     * 
     * @param budgetTransactionLockUserIdentifier The budgetTransactionLockUserIdentifier to set.
     * 
     */
    public void setBudgetTransactionLockUserIdentifier(String budgetTransactionLockUserIdentifier) {
        this.budgetTransactionLockUserIdentifier = budgetTransactionLockUserIdentifier;
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
    
    public UniversalUser getBudgetLockUser() {
        budgetLockUser = SpringServiceLocator.getUniversalUserService().updateUniversalUserIfNecessary(budgetLockUserIdentifier, budgetLockUser);
        return budgetLockUser;
    }

    /**
     * Sets the budgetLockUser attribute.
     * 
     * @param budgetLockUser The budgetLockUser to set.
     * @deprecated
     */
    public void setBudgetLockUser(UniversalUser budgetLockUser) {
        this.budgetLockUser = budgetLockUser;
    }

    /**
     * Gets the budgetConstructionAccountSelect list.
     * 
     * @return Returns the budgetConstructionAccountSelect list
     * 
     */
/*    public List getBudgetConstructionAccountSelect() {
        return budgetConstructionAccountSelect;
    }
*/
    /**
     * Sets the budgetConstructionAccountSelect list.
     * 
     * @param budgetConstructionAccountSelect The budgetConstructionAccountSelect list to set.
     * 
     */
/*    public void setBudgetConstructionAccountSelect(List budgetConstructionAccountSelect) {
        this.budgetConstructionAccountSelect = budgetConstructionAccountSelect;
    }
*/
    public UniversalUser getBudgetTransactionLockUser() {
        budgetTransactionLockUser = SpringServiceLocator.getUniversalUserService().updateUniversalUserIfNecessary(budgetTransactionLockUserIdentifier, budgetTransactionLockUser);
        return budgetTransactionLockUser;
    }

    /**
     * Sets the budgetTransactionLockUser attribute value.
     * 
     * @param budgetTransactionLockUser The budgetTransactionLockUser to set.
     * @deprecated
     */
    public void setBudgetTransactionLockUser(UniversalUser budgetTransactionLockUser) {
        this.budgetTransactionLockUser = budgetTransactionLockUser;
    }

    /**
     * Gets the organizationLevelOrganization attribute.
     * 
     * @return Returns the organizationLevelOrganization.
     */
    public Org getOrganizationLevelOrganization() {
        return organizationLevelOrganization;
    }

    /**
     * Sets the organizationLevelOrganization attribute value.
     * 
     * @param organizationLevelOrganization The organizationLevelOrganization to set.
     * @deprecated
     */
    public void setOrganizationLevelOrganization(Org organizationLevelOrganization) {
        this.organizationLevelOrganization = organizationLevelOrganization;
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
     * 
     * @see org.kuali.core.document.Document#getExplanation()
     */
    @Override
    public String getExplanation() {
        return documentHeader.getExplanation();
    }

    /**
     * 
     * @see org.kuali.core.document.Document#setExplanation(java.lang.String)
     */
    @Override
    public void setExplanation(String explanation) {
        documentHeader.setExplanation(explanation);
    }

    /**
     * @see org.kuali.core.document.DocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        return new ArrayList();
//        List managedLists = super.buildListOfDeletionAwareLists();

//        managedLists.add(getSourceAccountingLines());
//        managedLists.add(getTargetAccountingLines());

//        return managedLists;
    }

    public Collection<PendingBudgetConstructionGeneralLedger> getPendingBudgetConstructionGeneralLedgerRevenue() {
        return pendingBudgetConstructionGeneralLedgerRevenue;
    }

    public void setPendingBudgetConstructionGeneralLedgerRevenue(Collection<PendingBudgetConstructionGeneralLedger> pendingBudgetConstructionGeneralLedgerRevenue) {
        this.pendingBudgetConstructionGeneralLedgerRevenue = pendingBudgetConstructionGeneralLedgerRevenue;
    }

    public Collection<PendingBudgetConstructionGeneralLedger> getPendingBudgetConstructionGeneralLedgerExpenditure() {
        return pendingBudgetConstructionGeneralLedgerExpenditure;
    }

    public void setPendingBudgetConstructionGeneralLedgerExpenditure(Collection<PendingBudgetConstructionGeneralLedger> pendingBudgetConstructionGeneralLedgerExpenditure) {
        this.pendingBudgetConstructionGeneralLedgerExpenditure = pendingBudgetConstructionGeneralLedgerExpenditure;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        return m;
    }

}