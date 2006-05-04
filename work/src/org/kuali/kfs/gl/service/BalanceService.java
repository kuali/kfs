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
package org.kuali.module.gl.service;

import java.util.Iterator;
import java.util.Map;

import org.kuali.module.chart.bo.Account;
import org.kuali.module.gl.bo.Balance;

/**
 * @author Laran evans <lc278@cornell.edu>
 * @version $Id: BalanceService.java,v 1.8 2006-05-04 16:08:59 larevans Exp $
 */

public interface BalanceService {
    
    public void save(Balance b); 

    public boolean hasAssetLiabilityFundBalanceBalances(Account account);

    public boolean fundBalanceWillNetToZero(Account account);

    public boolean hasEncumbrancesOrBaseBudgets(Account account);

    public boolean beginningBalanceLoaded(Account account);

    public boolean hasAssetLiabilityOrFundBalance(Account account);
    
    /**
     * 
     * @param fiscalYear
     * @return an Iterator over all balances for a given year
     */
    public Iterator findBalancesForFiscalYear(Integer fiscalYear);
    
    /**
     * This method finds the summary records of balance entries according to input fields an values
     * 
     * @param fieldValues the input fields an values
     * @param isConsolidated consolidation option is applied  or not
     * @return the summary records of balance entries
     */
    public Iterator findCashBalance(Map fieldValues, boolean isConsolidated);
    
    /**
     * This method finds the summary records of balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the summary records of balance entries
     */
    public Iterator findBalance(Map fieldValues, boolean isConsolidated);

    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart
     * @param year
     */
    public void purgeYearByChart(String chart,int year);
}