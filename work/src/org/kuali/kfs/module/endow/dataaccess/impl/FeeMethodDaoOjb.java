/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.util.Date;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.dataaccess.FeeMethodDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class FeeMethodDaoOjb extends PlatformAwareDaoBaseOjb implements FeeMethodDao {

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.FeeMethodDao#getFeeMethodWithNextPayDateEqualToCurrentDate(java.util.Date)
     */
    public List<FeeMethod> getFeeMethodWithNextPayDateEqualToCurrentDate(Date currentDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.FEE_METHOD_NEXT_PROCESS_DATE, currentDate);
        criteria.addNotNull(EndowPropertyConstants.FEE_METHOD_FREQUENCY_CODE);
        criteria.addEqualTo(EndowPropertyConstants.KUALICODEBASE_ACTIVE_INDICATOR, "Y");
        return (List<FeeMethod>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(FeeMethod.class, criteria));
    }

}
