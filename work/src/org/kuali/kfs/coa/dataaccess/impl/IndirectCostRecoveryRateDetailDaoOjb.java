/*
 * Copyright 2006-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.coa.dataaccess.impl;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;
import org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryRateDetailDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This class implements the {@link IndirectCostRecoverRateDetailDao} data access methods using Ojb
 */
public class IndirectCostRecoveryRateDetailDaoOjb extends PlatformAwareDaoBaseOjb implements IndirectCostRecoveryRateDetailDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IndirectCostRecoveryRateDetailDaoOjb.class);

    /**
     * @see org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryRateDetailDao#getEntriesBySeries(java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    public Collection<IndirectCostRecoveryRateDetail> getActiveRateDetailsByRate(Integer universityFiscalYear, String financialIcrSeriesIdentifier) {
        LOG.debug("getEntriesBySeries() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        crit.addEqualTo(KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER, financialIcrSeriesIdentifier);
        crit.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        
        QueryByCriteria qbc = QueryFactory.newQuery(IndirectCostRecoveryRateDetail.class, crit);
        qbc.addOrderByAscending(KFSPropertyConstants.AWARD_INDR_COST_RCVY_ENTRY_NBR);

        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }
}
