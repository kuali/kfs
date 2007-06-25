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

package org.kuali.module.labor.bo;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.bo.user.PersonPayrollId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.bo.user.UserId;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.dao.LaborDao;
import org.springframework.beans.factory.BeanFactory;

public class AccountStatusCurrentFunds extends LedgerBalance {

    private String personName;
    private KualiDecimal ytdActualAmount;
    private KualiDecimal outstandingEncum;
    private KualiDecimal month1AccountLineAmount;
    private LaborDao laborDao;
    
    
    /**
     * Constructs an AccountStatusCurrentFunds.java.
     */
    public AccountStatusCurrentFunds() {
        super();
        this.setMonth1Amount(KualiDecimal.ZERO);
        this.setOutstandingEncum(KualiDecimal.ZERO);
    }

    /**
     * 
     * This method returns the person name
     * @return
     */
    public String getPersonName() {
        UserId empl = new PersonPayrollId(getEmplid());
        UniversalUser universalUser = null;
        
        try{
            universalUser = SpringServiceLocator.getUniversalUserService().getUniversalUser(empl);
        }catch(UserNotFoundException e){
            return LaborConstants.BalanceInquiries.UnknownPersonName;
        }

        return universalUser.getPersonName();
    }        
    
    /**
     * 
     * This method set thes persons name
     * @param personName
     */
    
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    /**
     * 
     * This method returns an outstanding encumberance value 
     * @return
     */
    public KualiDecimal getOutstandingEncum() {
        
        Map fieldValues = new HashMap(); 
        
        fieldValues.put("universityFiscalYear", getUniversityFiscalYear());
        fieldValues.put("chartOfAccountsCode", getChartOfAccountsCode());
        fieldValues.put("accountNumber", getAccountNumber());
        fieldValues.put("subAccountNumber", getSubAccountNumber());       
        fieldValues.put("financialObjectCode", getFinancialObjectCode());
        fieldValues.put("financialSubObjectCode", getFinancialSubObjectCode());
        fieldValues.put("emplid", getEmplid());
        
        BeanFactory beanFactory = SpringServiceLocator.getBeanFactory();
        laborDao = (LaborDao) beanFactory.getBean("laborDao");
        KualiDecimal EncumTotal = (KualiDecimal) laborDao.getEncumbranceTotal(fieldValues);
        this.outstandingEncum = EncumTotal;
        return EncumTotal;       
    }

    /**
     * 
     * This method sets an outstanding encumberance value 
     * @param outstandingEncum
     */
    public void setOutstandingEncum(KualiDecimal outstandingEncum) {
        this.outstandingEncum = outstandingEncum;
    }

    public KualiDecimal getMonth1AccountLineAmount() {
        return month1AccountLineAmount;
    }

    public void setMonth1AccountLineAmount(KualiDecimal month1AccountLineAmount) {
        this.month1AccountLineAmount = month1AccountLineAmount;
    }
}