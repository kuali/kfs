/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.gl.batch.service.impl;

import org.kuali.kfs.gl.batch.service.SufficientFundsFullRebuildService;
import org.kuali.kfs.gl.dataaccess.SufficientFundRebuildDao;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of SufficientFundsFullRebuildService
 */
@Transactional
public class SufficientFundsFullRebuildServiceImpl implements SufficientFundsFullRebuildService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsFullRebuildServiceImpl.class);

    private SufficientFundRebuildDao sufficientFundRebuildDao;

    /**
     * Goes through all accounts in the database, and generates a sufficient fund rebuild record for each one!
     * @see org.kuali.kfs.gl.batch.service.SufficientFundsFullRebuildService#syncSufficientFunds()
     */
    public void syncSufficientFunds() {
        LOG.debug("syncSufficientFunds() started");

        sufficientFundRebuildDao.purgeSufficientFundRebuild();
        
        sufficientFundRebuildDao.populateSufficientFundRebuild();
        
    }

    public void setSufficientFundRebuildDao(SufficientFundRebuildDao sfd) {
        sufficientFundRebuildDao = sfd;
    }

}
