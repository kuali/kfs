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
package org.kuali.module.chart.service;

import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.user.KualiUser;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Delegate;


/**
 * This interface defines methods that an Account Service must provide
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public interface AccountService {
    /**
     * Retrieves an Account object based on primary key.
     * 
     * @param chartOfAccountsCode - Chart of Accounts Code
     * @param accountNumber - Account Number
     * @return Account
     */
    public Account getByPrimaryId(String chartOfAccountsCode, String accountNumber);

    /**
     * This method retrieves the fiscal officers primary delegate based on the chart, account, and document type specified on the
     * example, along with the total dollar amount
     * 
     * @param delegateExample The object that contains the chart, account, and document type that should be used to query the
     *        account delegate table
     * @param totalDollarAmount The amount that should be compared to the from and to amount on the account delegate table
     * @return The primary delegate for this account, document type, and amount
     */
    public Delegate getPrimaryDelegationByExample(Delegate delegateExample, String totalDollarAmount);

    /**
     * This method retrieves the fiscal officers secondary delegates based on the chart, account, and document type specified on the
     * example, along with the total dollar amount
     * 
     * @param delegateExample The object that contains the chart, account, and document type that should be used to query the
     *        account delegate table
     * @param totalDollarAmount The amount that should be compared to the from and to amount on the account delegate table
     * @return The primary delegate for this account, document type, and amount
     */
    public List getSecondaryDelegationsByExample(Delegate delegateExample, String totalDollarAmount);

    /**
     * Fetches the accounts that the user is either the fiscal officer or fiscal officer delegate for.
     * 
     * @param kualiUser
     * @return a list of Accounts that the user has responsibility for
     */
    public List getAccountsThatUserIsResponsibleFor(KualiUser kualiUser);

    /**
     * get all accounts in the system. This is needed by a sufficient funds rebuilder job
     * 
     * @return iterator of all accounts
     */
    public Iterator getAllAccounts();
}