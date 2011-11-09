/*
 * Copyright 2006 The Kuali Foundation
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

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionType;
import org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryExclusionTypeDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This class implements the {@link IndirectCostRecoveryExclusionTypeDao} data access methods using Ojb
 */
public class IndirectCostRecoveryExclusionTypeDaoOjb extends PlatformAwareDaoBaseOjb implements IndirectCostRecoveryExclusionTypeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IndirectCostRecoveryExclusionTypeDaoOjb.class);

    /**
     * @see org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryExclusionTypeDao#getByPrimaryKey(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public IndirectCostRecoveryExclusionType getByPrimaryKey(String accountIndirectCostRecoveryTypeCode, String chartOfAccountsCode, String objectCode) {
        LOG.debug("getByPrimaryKey() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("accountIndirectCostRecoveryTypeCode", accountIndirectCostRecoveryTypeCode);
        crit.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        crit.addEqualTo("financialObjectCode", objectCode);

        QueryByCriteria qbc = QueryFactory.newQuery(IndirectCostRecoveryExclusionType.class, crit);
        return (IndirectCostRecoveryExclusionType) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }
}
