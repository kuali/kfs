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

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryType;
import org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryTypeDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This class implements the {@link IndirectCostRecoveryExclusionTypeDao} data access methods using Ojb
 */
public class IndirectCostRecoveryTypeDaoOjb extends PlatformAwareDaoBaseOjb implements IndirectCostRecoveryTypeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IndirectCostRecoveryTypeDaoOjb.class);

    /**
     * @see org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryTypeDao#getByPrimaryKey(java.lang.String)
     */
    public IndirectCostRecoveryType getByPrimaryKey(String code) {
        LOG.debug("getByPrimaryKey() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.CODE, code);

        QueryByCriteria qbc = QueryFactory.newQuery(IndirectCostRecoveryType.class, crit);
        return (IndirectCostRecoveryType) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }
}
