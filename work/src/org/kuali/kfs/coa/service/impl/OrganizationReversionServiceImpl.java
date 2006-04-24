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
package org.kuali.module.chart.service.impl;

import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.dao.OrganizationReversionDao;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.OrganizationReversionService;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 * @version $Id$
 */
public class OrganizationReversionServiceImpl implements OrganizationReversionService {
    private OrganizationReversionDao organizationReversionDao;
    private AccountService accountService;

    /**
     * 
     * @param orDao
     */
    public void setOrganizationReversionDao(OrganizationReversionDao orDao){
        organizationReversionDao = orDao;
    }
    
    /**
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * @see org.kuali.module.chart.service.OrganizationReversionService#executeEntry(int)
     */
    public void executeEntry(int entryNumber) {
        // TODO Auto-generated method stub
        // organizationReversion = organizationReversionDao.getByPrimaryId()
    }

    /* (non-Javadoc)
     * @see org.kuali.module.chart.service.OrganizationReversionService#getByPrimaryKey(java.lang.Integer, org.kuali.module.chart.bo.Account)
     */
    public OrganizationReversion getByFiscalYearAndAccount(Integer fiscalYear, Account account) {
        if(null == account) {
            return null;
        }
        
        return organizationReversionDao.getByPrimaryId(fiscalYear, account.getChartOfAccountsCode(), account.getOrganizationCode());
    }

    /* (non-Javadoc)
     * @see org.kuali.module.chart.service.OrganizationReversionService#getByKeys(java.lang.Integer, java.lang.String, java.lang.String)
     */
    public OrganizationReversion getByKeys(Integer fiscalYear, String chartCode, String accountNumber) {
        Account account = accountService.getByPrimaryId(chartCode, accountNumber);
        return getByFiscalYearAndAccount(fiscalYear, account);
    }
    
}
