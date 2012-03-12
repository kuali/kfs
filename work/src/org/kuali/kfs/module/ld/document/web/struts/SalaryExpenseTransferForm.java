/*
 * Copyright 2006 The Kuali Foundation
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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.web.format.CurrencyFormatter;

/**
 * Struts Action Form for the Salary Expense Transfer document. This method extends the parent ExpenseTransferDocumentFormBase class
 * which contains all of the common form methods and form attributes needed by the Salary Expense Transfer document. It adds a new
 * method which is a convenience method for getting at the Salary Expense Transfer document easier.
 */
public class SalaryExpenseTransferForm extends ExpenseTransferDocumentFormBase {
    protected static Log LOG = LogFactory.getLog(SalaryExpenseTransferForm.class);

    protected String balanceTypeCode;

    /**
     * Constructs a SalaryExpenseTransferForm instance and sets up the appropriately casted document.
     */
    public SalaryExpenseTransferForm() {
        super();

        setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
        setLookupResultsBOClassName(LedgerBalance.class.getName());
        setFormatterType(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.APPROVAL_OBJECT_CODE_BALANCES, CurrencyFormatter.class);
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "ST";
    }
    
    /**
     * Gets the balanceTypeCode attribute.
     * 
     * @return Returns the balanceTypeCode.
     */
    public String getFinancialBalanceTypeCode() {
        return balanceTypeCode;
    }

    /**
     * Sets the balanceTypeCode attribute value.
     * 
     * @param balanceTypeCode The balanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String balanceTypeCode) {
        this.balanceTypeCode = balanceTypeCode;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.pojo.PojoForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
    }

    /**
     * This method returns a reference to the Salary Expense Transfer Document
     * 
     * @return Returns the SalaryExpenseTransferDocument.
     */
    public SalaryExpenseTransferDocument getSalaryExpenseTransferDocument() {
        return (SalaryExpenseTransferDocument) getDocument();
    }

    /**
     * Removes fields from map if users is allowed to edit.
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
     * Populate serach fields (i.e. universal fiscal year and employee ID)
     * 
     * @see org.kuali.kfs.module.ld.document.web.struts.ExpenseTransferDocumentFormBase#populateSearchFields()
     */
    @Override
    public void populateSearchFields() {
        List<ExpenseTransferAccountingLine> sourceAccoutingLines = this.getSalaryExpenseTransferDocument().getSourceAccountingLines();
        if (sourceAccoutingLines != null && !sourceAccoutingLines.isEmpty()) {
            ExpenseTransferAccountingLine sourceAccountingLine = sourceAccoutingLines.get(0);
            this.setUniversityFiscalYear(sourceAccountingLine.getPostingYear());
        }
    }
}

