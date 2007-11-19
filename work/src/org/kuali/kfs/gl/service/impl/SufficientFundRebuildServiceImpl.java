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
package org.kuali.module.gl.service.impl;

import java.util.Collection;

import org.kuali.kfs.KFSConstants;
import org.kuali.module.gl.bo.SufficientFundRebuild;
import org.kuali.module.gl.dao.SufficientFundRebuildDao;
import org.kuali.module.gl.service.SufficientFundRebuildService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The default implementation of SufficientFundRebuildService
 */
@Transactional
public class SufficientFundRebuildServiceImpl implements SufficientFundRebuildService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundRebuildServiceImpl.class);

    SufficientFundRebuildDao sufficientFundRebuildDao;

    /**
     * Returns all sufficient funds records in the persistence store.  Defers to the DAO.
     * 
     * @return a Collection of all sufficient fund rebuild records
     * @see org.kuali.module.gl.service.SufficientFundRebuildService#getAll()
     */
    public Collection getAll() {
        LOG.debug("getAll() started");

        return sufficientFundRebuildDao.getAll();
    }

    /**
     * Returns all sufficient fund rebuild records using account numbers.  Defers to the DAO.
     * 
     * @return a Collection of sufficient fund rebuild records
     * @see org.kuali.module.gl.service.SufficientFundRebuildService#getAllAccountEntries()
     */
    public Collection getAllAccountEntries() {
        LOG.debug("getAllAccountEntries() started");

        return sufficientFundRebuildDao.getByType(KFSConstants.SF_TYPE_ACCOUNT);
    }

    /**
     * Returns all sufficient fund rebuild records using object codes.  Defers to the DAO.
     * 
     * @return a Collection of sufficient fund rebuild records
     * @see org.kuali.module.gl.service.SufficientFundRebuildService#getAllObjectEntries()
     */
    public Collection getAllObjectEntries() {
        LOG.debug("getAllObjectEntries() started");

        return sufficientFundRebuildDao.getByType(KFSConstants.SF_TYPE_OBJECT);
    }

    /**
     * Returns a sufficient fund rebuild record given the parameters as keys.  Defers to the DAO.
     * 
     * @param chartOfAccountsCode the chart of the record to return
     * @param accountNumberFinancialObjectCode either an account number or an object code of the sufficient fund rebuild record to return
     * @return the qualifying sufficient fund rebuild record, or null if not found
     * @see org.kuali.module.gl.service.SufficientFundRebuildService#getByAccount(java.lang.String, java.lang.String)
     */
    public SufficientFundRebuild getByAccount(String chartOfAccountsCode, String accountNumberFinancialObjectCode) {
        LOG.debug("getByAccount() started");

        return sufficientFundRebuildDao.getByAccount(chartOfAccountsCode, accountNumberFinancialObjectCode);
    }

    /**
     * Returns a sufficient fund rebuild record, based on the given keys.  Defers to the DAO.
     * 
     * @param chartOfAccountsCode the chart of the sufficient fund rebuild record to return
     * @param accountFinancialObjectTypeCode if the record has an object code, the object code of the sufficient fund rebuild record to return
     * @param accountNumberFinancialObjectCode if the record has an account number, the account number of the sufficient fund rebuild record to return
     * @return the qualifying sufficient fund rebuild record, or null if not found
     * @see org.kuali.module.gl.service.SufficientFundRebuildService#get(java.lang.String, java.lang.String, java.lang.String)
     */
    public SufficientFundRebuild get(String chartOfAccountsCode, String accountFinancialObjectTypeCode, String accountNumberFinancialObjectCode) {
        LOG.debug("get() started");

        return sufficientFundRebuildDao.get(chartOfAccountsCode, accountFinancialObjectTypeCode, accountNumberFinancialObjectCode);
    }

    /**
     * Saves a sufficient fund rebuild record to the persistence store.  Defers to the DAO.
     * 
     * @param sfrb the sufficient fund rebuild record to save
     * @see org.kuali.module.gl.service.SufficientFundRebuildService#save(org.kuali.module.gl.bo.SufficientFundRebuild)
     */
    public void save(SufficientFundRebuild sfrb) {
        LOG.debug("save() started");

        sufficientFundRebuildDao.save(sfrb);
    }
    
    /**
     * Deletes a SufficientFundRebuild record from the persistence store.  Defers to the DAO.
     *
     * @param sfrb the sufficient fund rebuild record to delete
     * @see org.kuali.module.gl.service.SufficientFundRebuildService#delete(org.kuali.module.gl.bo.SufficientFundRebuild)
     */
    public void delete(SufficientFundRebuild sfrb) {
        LOG.debug("delete() started");

        sufficientFundRebuildDao.delete(sfrb);
    }

    public void setSufficientFundRebuildDao(SufficientFundRebuildDao sufficientFundRebuildDao) {
        this.sufficientFundRebuildDao = sufficientFundRebuildDao;
    }
}
