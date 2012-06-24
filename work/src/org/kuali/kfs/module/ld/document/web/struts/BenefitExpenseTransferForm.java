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
