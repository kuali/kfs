/*
 * Copyright 2006 The Kuali Foundation.
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
