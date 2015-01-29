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
package org.kuali.kfs.integration.cg;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Do nothing implementation of the ContractsAndGrantsModuleBillingService interface
 */
public class ContractsAndGrantsModuleBillingServiceNoOp implements ContractsAndGrantsModuleBillingService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsAndGrantsModuleBillingServiceNoOp.class);

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleRetrieveService#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    public List<? extends ContractsAndGrantsAward> lookupAwards(Map<String, String> fieldValues, boolean unbounded) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return new ArrayList<ContractsAndGrantsAward>();
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService#updateAwardIfNecessary(java.lang.Long, org.kuali.kfs.integration.cg.ContractsAndGrantsAward)
     */
    @Override
    public ContractsAndGrantsBillingAward updateAwardIfNecessary(Long proposalNumber, ContractsAndGrantsBillingAward currentAward ) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setLastBilledDateToAwardAccount(java.util.Map,
     *      java.lang.String, java.sql.Date)
     */
    @Override
    public void setLastBilledDateToAwardAccount(Map<String, Object> criteria, boolean invoiceReversal, Date lastBilledDate) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");

    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setLastBilledDateToAward(java.lang.Long,
     *      java.sql.Date)
     */
    @Override
    public void setLastBilledDateToAward(Long proposalNumber, Date lastBilledDate) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");

    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setFinalBilledToAwardAccount(java.util.Map, boolean)
     */
    @Override
    public void setFinalBilledToAwardAccount(Map<String, Object> criteria, boolean finalBilled) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setFinalBilledAndLastBilledDateToAwardAccount(java.util.Map, boolean, java.lang.String, java.sql.Date, java.lang.String)
     */
    @Override
    public void setFinalBilledAndLastBilledDateToAwardAccount(Map<String, Object> mapKey, boolean finalBilled, boolean invoiceReversal, Date lastBilledDate) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }
}
