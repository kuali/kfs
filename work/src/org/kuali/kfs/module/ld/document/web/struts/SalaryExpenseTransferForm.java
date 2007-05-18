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

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import static org.kuali.Constants.MULTIPLE_VALUE;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.labor.bo.LaborUser;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;
import org.kuali.module.labor.service.LaborUserService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is the form class for the Salary Expense Transfer document. This method extends the parent
 * KualiTransactionalDocumentFormBase class which contains all of the common form methods and form attributes needed by the
 * Salary Expense Transfer document. It adds a new method which is a convenience method for getting at the Salary Expense Transfer document easier.
 * 
 * 
 */
public class SalaryExpenseTransferForm extends LaborDocumentFormBase implements MultipleValueLookupBroker {
    private static Log LOG = LogFactory.getLog(SalaryExpenseTransferForm.class);
    private LaborUser user;
    private String balanceTypeCode;
    private Integer fiscalYear;

    /**
     * Used to indicate which result set we're using when refreshing/returning from a multi-value lookup
     */
    private String lookupResultsSequenceNumber;
    /**
     * The type of result returned by the multi-value lookup
     * 
     * TODO: to be persisted in the lookup results service instead?
     */
    private String lookupResultsBOClassName;
    
    /**
     * The name of the collection looked up (by a multiple value lookup)
     */
    private String lookedUpCollectionName;

    /**
     * Constructs a SalaryExpenseTransferForm instance and sets up the appropriately casted document.
     */
    public SalaryExpenseTransferForm() {
        super();
        setUser(new LaborUser(new UniversalUser()));
        setDocument(new SalaryExpenseTransferDocument());
        setFinancialBalanceTypeCode("AC");
    }
    
    /**
     *
     */
    public String getFinancialBalanceTypeCode() {
        return balanceTypeCode;
    }

    /**
     * 
     */
    public void setFinancialBalanceTypeCode(String code) {
        balanceTypeCode = code;
    }
    
    /**
     * @see org.kuali.core.web.struts.form.DocumentFormBase#populate(HttpServletRequest)
     */
    public void populate(HttpServletRequest request) {
        super.populate(request);
    }

    /**
     * 
     * This method returns a refernce to the Salary Expense Transfer Document
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
    * 
    * This method sets the employee ID retrieved from the universal user service
    * @param emplid
    * @throws UserNotFoundException because a lookup at the database discovers user data from the personPayrollIdentifier
    */
    public void setEmplid(String id) throws UserNotFoundException {
        getSalaryExpenseTransferDocument().setEmplid(id);
        
        if (id != null) {
            setUser(((LaborUserService) SpringServiceLocator.getService("laborUserService")).getLaborUserByPersonPayrollIdentifier(id));
        }
    }

    /**
     * 
     * This method returns the employee ID from the UniversalUser table.
     *
     * @return String of the personPayrollIdentifier
     * @throws UserNotFoundException because a lookup at the database discovers user data from the personPayrollIdentifier
     */
    public String getEmplid() throws UserNotFoundException {
        if (user == null) {
            setUser(((LaborUserService) SpringServiceLocator.getService("laborUserService"))
                    .getLaborUserByPersonPayrollIdentifier(getSalaryExpenseTransferDocument().getEmplid()));
        }
        return getSalaryExpenseTransferDocument().getEmplid();
    }
    
    /**
     * @see org.kuali.core.web.struts.form.AccountingDocumentFormBase#getRefreshCaller()
     */
    public String getRefreshCaller() {
        return MULTIPLE_VALUE;
    }

    /**
     * @see MultipleValueLookupBroker#getLookupResultsSequenceNumber()
     */
    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    /**
     * @see MultipleValueLookupBroker#setLookupResultsSequenceNumber(String)
     */
    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    /**
     * @see MultipleValueLookupBroker#getLookupResultsBOClassName()
     */
    public String getLookupResultsBOClassName() {
        return lookupResultsBOClassName;
    }


    /**
     * @see MultipleValueLookupBroker#setLookupResultsBOClassName(String)
     */
    public void setLookupResultsBOClassName(String lookupResultsBOClassName) {
        this.lookupResultsBOClassName = lookupResultsBOClassName;
    }


    /**
     * @see MultipleValueLookupBroker#getLookupedUpCollectionName()
     */
    public String getLookedUpCollectionName() {
        return lookedUpCollectionName;
    }

    /**
     * @see MultipleValueLookupBroker#getLookupedUpCollectionName(String)
     */
    public void setLookedUpCollectionName(String lookedUpCollectionName) {
        this.lookedUpCollectionName = lookedUpCollectionName;
    }
    
    public Integer getUniversityFiscalYear() {
        return fiscalYear;
    }

    public void setUniversityFiscalYear(Integer year) {
        fiscalYear = year;
    }
    
    /**
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#getForcedReadOnlyFields()
     */
    @Override
    public Map getForcedReadOnlyFields() {
        Map map = super.getForcedReadOnlyFields();
        map.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, Boolean.TRUE);
        map.put(KFSPropertyConstants.ACCOUNT_NUMBER, Boolean.TRUE);
        map.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, Boolean.TRUE);
        map.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, Boolean.TRUE);
        map.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, Boolean.TRUE);
        map.put(KFSPropertyConstants.PROJECT_CODE, Boolean.TRUE);
        map.put(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID, Boolean.TRUE);
        map.put(KFSPropertyConstants.POSITION_NUMBER, Boolean.TRUE);
        map.put(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE, Boolean.TRUE);
        return map;
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#getAccountingLineEditableFields()
     */ 
    @Override
    public Map getAccountingLineEditableFields() {
        Map map = super.getAccountingLineEditableFields();
        map.remove(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        map.remove(KFSPropertyConstants.ACCOUNT_NUMBER);
        map.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        map.remove(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        map.remove(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        map.remove(KFSPropertyConstants.PROJECT_CODE);
        map.remove(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID);
        map.remove(KFSPropertyConstants.POSITION_NUMBER);
        return map;
    }
}
