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

