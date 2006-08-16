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
package org.kuali.module.gl.service.impl;

import java.util.Collection;

import org.kuali.Constants;
import org.kuali.module.gl.bo.SufficientFundRebuild;
import org.kuali.module.gl.dao.SufficientFundRebuildDao;
import org.kuali.module.gl.service.SufficientFundRebuildService;

public class SufficientFundRebuildServiceImpl implements SufficientFundRebuildService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundRebuildServiceImpl.class);

    SufficientFundRebuildDao sufficientFundRebuildDao;

    public Collection getAll() {
        LOG.debug("getAll() started");

        return sufficientFundRebuildDao.getAll();
    }

    public Collection getAllAccountEntries() {
        LOG.debug("getAllAccountEntries() started");

        return sufficientFundRebuildDao.getByType(Constants.SF_TYPE_ACCOUNT);
    }

    public Collection getAllObjectEntries() {
        LOG.debug("getAllObjectEntries() started");

        return sufficientFundRebuildDao.getByType(Constants.SF_TYPE_OBJECT);
    }

    public SufficientFundRebuild getByAccount(String chartOfAccountsCode, String accountNumberFinancialObjectCode) {
        LOG.debug("getByAccount() started");

        return sufficientFundRebuildDao.getByAccount(chartOfAccountsCode, accountNumberFinancialObjectCode);
    }

    public SufficientFundRebuild get(String chartOfAccountsCode, String accountFinancialObjectTypeCode, String accountNumberFinancialObjectCode) {
        LOG.debug("get() started");

        return sufficientFundRebuildDao.get(chartOfAccountsCode, accountFinancialObjectTypeCode, accountNumberFinancialObjectCode);
    }

    public void save(SufficientFundRebuild sfrb) {
        LOG.debug("save() started");

        sufficientFundRebuildDao.save(sfrb);
    }

    public void delete(SufficientFundRebuild sfrb) {
        LOG.debug("delete() started");

        sufficientFundRebuildDao.delete(sfrb);
    }

    public void setSufficientFundRebuildDao(SufficientFundRebuildDao sufficientFundRebuildDao) {
        this.sufficientFundRebuildDao = sufficientFundRebuildDao;
    }
}
