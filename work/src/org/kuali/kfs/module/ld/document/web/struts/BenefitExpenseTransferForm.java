/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ld.document.web.struts;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.document.BenefitExpenseTransferDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;

/**
 * Struts Action Form for the Benefit Expense Transfer Document.
 */
public class BenefitExpenseTransferForm extends ExpenseTransferDocumentFormBase {

    protected String subAccountNumber;

    /**
     * Constructs a BenefitExpenseTransferForm instance and sets up the appropriately casted document.
     */
    public BenefitExpenseTransferForm() {
        super();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "BT";
    }
    
    /**
     * Gets the BenefitExpenseTransferDocument attribute.
     * 
     * @return Returns the BenefitExpenseTransferDocument
     */
    public BenefitExpenseTransferDocument getBenefitExpenseTransferDocument() {
        return (BenefitExpenseTransferDocument) getDocument();
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return this.getBenefitExpenseTransferDocument().getAccountNumber();
    }

    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.getBenefitExpenseTransferDocument().setAccountNumber(accountNumber);
    }

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Return the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Return the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return this.getBenefitExpenseTransferDocument().getChartOfAccountsCode();
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.getBenefitExpenseTransferDocument().setChartOfAccountsCode(chartOfAccountsCode);
    }

    /**
     * Returns forced read only target fields (i.e only source target fields without chart of accounts code, 
     * account number, sub-account number, financial sub object code, project code, organization reference id, and amount)
     * 
     * @see org.kuali.kfs.module.ld.document.web.struts.ExpenseTransferDocumentFormBase#getForcedReadOnlyTargetFields()
     */
    @Override
    public Map getForcedReadOnlyTargetFields() {
        Map map = this.getForcedReadOnlySourceFields();
        map.remove(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        map.remove(KFSPropertyConstants.ACCOUNT_NUMBER);
        map.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        map.remove(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        map.remove(KFSPropertyConstants.PROJECT_CODE);
        map.remove(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID);
        map.remove(KFSPropertyConstants.AMOUNT);

        return map;
    }

    /**
     * Sets university fiscal year, chart of accounts code, account number, sub-account number from first source accounting line
     * 
     * @see org.kuali.kfs.module.ld.document.web.struts.ExpenseTransferDocumentFormBase#populateSearchFields()
     */
    @Override
    public void populateSearchFields() {
        List<SourceAccountingLine> sourceAccoutingLines = this.getBenefitExpenseTransferDocument().getSourceAccountingLines();
        if (sourceAccoutingLines != null && !sourceAccoutingLines.isEmpty()) {
            SourceAccountingLine sourceAccountingLine = sourceAccoutingLines.get(0);
            this.setUniversityFiscalYear(sourceAccountingLine.getPostingYear());
            this.setChartOfAccountsCode(sourceAccountingLine.getChartOfAccountsCode());
            this.setAccountNumber(sourceAccountingLine.getAccountNumber());
            this.setSubAccountNumber(sourceAccountingLine.getSubAccountNumber());
            if (sourceAccountingLine.getSubAccountNumber() == null) {
                this.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }
        }
    }
}
