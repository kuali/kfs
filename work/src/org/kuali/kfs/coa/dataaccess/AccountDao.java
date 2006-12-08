/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/dataaccess/AccountDao.java,v $
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
package org.kuali.module.chart.dao;

import java.util.Iterator;
import java.util.List;


import org.kuali.core.bo.user.UniversalUser;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Delegate;


/**
 * This interface defines basic methods that Account Dao's must provide
 * 
 * 
 */
public interface AccountDao {

    /**
     * @param chartOfAccountsCode - part of composite key
     * @param accountNumber - part of composite key
     * @return Account
     * 
     * Retrieves an Account object based on primary key.
     */
    public Account getByPrimaryId(String chartOfAccountsCode, String accountNumber);

    /**
     * @see org.kuali.module.chart.service.AccountService#getPrimaryDelegationByExample(org.kuali.module.chart.bo.Delegate,
     *      java.lang.String)
     */
    public Delegate getPrimaryDelegationByExample(Delegate delegateExample, String totalDollarAmount);

    /**
     * @see org.kuali.module.chart.service.AccountService#getSecondaryDelegationsByExample(org.kuali.module.chart.bo.Delegate,
     *      java.lang.String)
     */
    public List getSecondaryDelegationsByExample(Delegate delegateExample, String totalDollarAmount);

    /**
     * fetch the AccountResponsibility objects that the user has associated with them
     * 
     * @param kualiUser
     * @return a list of AccountResponsibility objects
     */
    public List getAccountsThatUserIsResponsibleFor(UniversalUser kualiUser);

    /**
     * get all accounts in the system. This is needed by a sufficient funds rebuilder job
     * 
     * @return iterator of all accounts
     */
    public Iterator getAllAccounts();
}