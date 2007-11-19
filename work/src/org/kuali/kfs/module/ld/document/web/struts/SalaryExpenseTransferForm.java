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
package org.kuali.module.labor.web.struts.form;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.LaborUser;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;
import org.kuali.module.labor.service.LaborUserService;

/**
 * Struts Action Form for the Salary Expense Transfer document. This method extends the parent ExpenseTransferDocumentFormBase class
 * which contains all of the common form methods and form attributes needed by the Salary Expense Transfer document. It adds a new
 * method which is a convenience method for getting at the Salary Expense Transfer document easier.
 */
public class SalaryExpenseTransferForm extends ExpenseTransferDocumentFormBase {
    private static Log LOG = LogFactory.getLog(SalaryExpenseTransferForm.class);

    private LaborUser user;
    private String balanceTypeCode;
    private String emplid;

    /**
     * Constructs a SalaryExpenseTransferForm instance and sets up the appropriately casted document.
     */
    public SalaryExpenseTransferForm() {
        super();

        setDocument(new SalaryExpenseTransferDocument());
        setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
        setLookupResultsBOClassName(LedgerBalance.class.getName());
        setUser(new LaborUser(new UniversalUser()));
        setFormatterType(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.APPROVAL_OBJECT_CODE_BALANCES, CurrencyFormatter.class);
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
     * @see org.kuali.core.web.struts.pojo.PojoForm#populate(javax.servlet.http.HttpServletRequest)
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
     * Assign <code>{@link LaborUser}</code> instance to the struts form.
     * 
     * @param user The user to set.
     */
    public void setUser(LaborUser user) {
        this.user = user;
    }

    /**
     * Retrieve <code>{@link LaborUser}</code> instance from the struts from.
     * 
     * @return Returns the LaborUser.
     */
    public LaborUser getUser() {
        return user;
    }

    /**
     * Sets the employee ID retrieved from the universal user service
     * 
     * @param emplid The emplid to set.
     * @throws UserNotFoundException because a lookup at the database discovers user data from the personPayrollIdentifier
     */
    public void setEmplid(String id) {
        getSalaryExpenseTransferDocument().setEmplid(id);

        if (id != null) {
            try {
                setUser(SpringContext.getBean(LaborUserService.class).getLaborUserByPersonPayrollIdentifier(id));
            }
            catch (UserNotFoundException e) {
            }

        }
    }

    /**
     * Returns the employee ID from the UniversalUser table.
     * 
     * @return Returns the personPayrollIdentifier
     * @throws UserNotFoundException because a lookup at the database discovers user data from the personPayrollIdentifier
     */
    public String getEmplid() throws UserNotFoundException {
        if (user == null) {
            try {
                setUser(SpringContext.getBean(LaborUserService.class).getLaborUserByPersonPayrollIdentifier(getSalaryExpenseTransferDocument().getEmplid()));
            }
            catch (UserNotFoundException e) {
            }
        }

        return getSalaryExpenseTransferDocument().getEmplid();
    }

    /**
     * Removes fields from map if users is allowed to edit.
     * 
     * @see org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase#getForcedReadOnlyTargetFields()
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

        // check if user is allowed to edit the object code.
        String adminGroupName = SpringContext.getBean(ParameterService.class).getParameterValue(SalaryExpenseTransferDocument.class, LaborConstants.SalaryExpenseTransfer.SET_ADMIN_WORKGROUP_PARM_NM);
        boolean isAdmin = false;
        try {
            isAdmin = GlobalVariables.getUserSession().getUniversalUser().isMember(adminGroupName);
        }
        catch (Exception e) {
            throw new RuntimeException("Workgroup " + LaborConstants.SalaryExpenseTransfer.SET_ADMIN_WORKGROUP_PARM_NM + " not found", e);
        }
        if (isAdmin) {
            map.remove(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        }

        return map;
    }

    /**
     * Populate serach fields (i.e. universal fiscal year and employee ID)
     * 
     * @see org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase#populateSearchFields()
     */
    @Override
    public void populateSearchFields() {
        List<ExpenseTransferAccountingLine> sourceAccoutingLines = this.getSalaryExpenseTransferDocument().getSourceAccountingLines();
        if (sourceAccoutingLines != null && !sourceAccoutingLines.isEmpty()) {
            ExpenseTransferAccountingLine sourceAccountingLine = sourceAccoutingLines.get(0);
            this.setUniversityFiscalYear(sourceAccountingLine.getPostingYear());
            this.setEmplid(sourceAccountingLine.getEmplid());
        }
    }
}
