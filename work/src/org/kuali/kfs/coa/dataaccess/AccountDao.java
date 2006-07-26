/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.dao;

import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.user.KualiUser;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Delegate;


/**
 * This interface defines basic methods that Account Dao's must provide
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
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
    public List getAccountsThatUserIsResponsibleFor(KualiUser kualiUser);

    /**
     * get all accounts in the system. This is needed by a sufficient funds rebuilder job
     * 
     * @return iterator of all accounts
     */
    public Iterator getAllAccounts();
}