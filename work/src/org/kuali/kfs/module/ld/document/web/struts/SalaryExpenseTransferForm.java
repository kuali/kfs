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

import static org.kuali.module.labor.LaborConstants.LABOR_USER_SERVICE_NAME;

import java.sql.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.labor.bo.LaborUser;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;
import org.kuali.module.labor.service.LaborUserService;

/**
 * This class is the form class for the Salary Expense Transfer document. This method extends the parent
 * KualiTransactionalDocumentFormBase class which contains all of the common form methods and form attributes needed by the Salary
 * Expense Transfer document. It adds a new method which is a convenience method for getting at the Salary Expense Transfer document
 * easier.
 */
public class SalaryExpenseTransferForm extends ExpenseTransferDocumentFormBase {
    private static Log LOG = LogFactory.getLog(SalaryExpenseTransferForm.class);

    private LaborUser user;
    private String balanceTypeCode;
    private Integer fiscalYear;

    /**
     * Constructs a SalaryExpenseTransferForm instance and sets up the appropriately casted document.
     */
    public SalaryExpenseTransferForm() {
        super();
        setUser(new LaborUser(new UniversalUser()));
        setDocument(new SalaryExpenseTransferDocument());
        setFinancialBalanceTypeCode("AC");
        setLookupResultsBOClassName(LedgerBalance.class.getName());
        setUniversityFiscalYear(SpringServiceLocator.getAccountingPeriodService().getByDate(new Date(System.currentTimeMillis())).getUniversityFiscalYear());
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
     * @see org.kuali.core.web.struts.form.DocumentFormBase#populate(HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
    }

    /**
     * This method returns a refernce to the Salary Expense Transfer Document
     * 
     * @return SalaryExpenseTransferDocument
     */
    public SalaryExpenseTransferDocument getSalaryExpenseTransferDocument() {
        return (SalaryExpenseTransferDocument) getDocument();
    }

    /**
     * Assign <code>{@link LaborUser}</code> instance to the struts form.
     * 
     * @param user
     */
    public void setUser(LaborUser user) {
        this.user = user;
    }

    /**
     * Retrieve <code>{@link LaborUser}</code> instance from the struts from.
     * 
     * @return LaborUser
     */
    public LaborUser getUser() {
        return user;
    }

    /**
     * This method sets the employee ID retrieved from the universal user service
     * 
     * @param emplid
     * @throws UserNotFoundException because a lookup at the database discovers user data from the personPayrollIdentifier
     */
    public void setEmplid(String id) throws UserNotFoundException {
        getSalaryExpenseTransferDocument().setEmplid(id);

        if (id != null) {
            setUser(((LaborUserService) SpringServiceLocator.getService(LABOR_USER_SERVICE_NAME)).getLaborUserByPersonPayrollIdentifier(id));
        }
    }

    /**
     * This method returns the employee ID from the UniversalUser table.
     * 
     * @return String of the personPayrollIdentifier
     * @throws UserNotFoundException because a lookup at the database discovers user data from the personPayrollIdentifier
     */
    public String getEmplid() throws UserNotFoundException {
        if (user == null) {
            setUser(((LaborUserService) SpringServiceLocator.getService(LABOR_USER_SERVICE_NAME))
                    .getLaborUserByPersonPayrollIdentifier(getSalaryExpenseTransferDocument().getEmplid()));
        }
        return getSalaryExpenseTransferDocument().getEmplid();
    }

    /**
     * @see org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase#getUniversityFiscalYear()
     */
    @Override
    public Integer getUniversityFiscalYear() {
        return fiscalYear;
    }

    /**
     * @see org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase#setUniversityFiscalYear(java.lang.Integer)
     */
    @Override
    public void setUniversityFiscalYear(Integer year) {
        fiscalYear = year;
    }
    
    /**
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#getForcedReadOnlyFields()
     */
    @Override
    public Map getForcedReadOnlyTargetFields() {
        Map map = this.getForcedReadOnlySourceFields();
        map.remove(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        map.remove(KFSPropertyConstants.ACCOUNT_NUMBER);
        map.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        return map;
    }
}
