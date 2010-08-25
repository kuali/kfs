/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.dataaccess.FeeMethodDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class FeeMethodDaoOjb extends PlatformAwareDaoBaseOjb implements FeeMethodDao {

    protected KEMService kemService;
    
    /**
     * @see org.kuali.kfs.module.endow.dataaccess.FeeMethodDao#getAllSecuritiesWithNextPayDateEqualOrLessCurrentDate()
     */
    public List<FeeMethod> getAllFeeMethodWithNextPayDateEqualOrLessCurrentDate() {
        Criteria criteria = new Criteria();
        criteria.addLessOrEqualThan(EndowPropertyConstants.FEE_METHOD_NEXT_PROCESS_DATE, kemService.getCurrentDate());
        criteria.addNotNull(EndowPropertyConstants.FEE_METHOD_FREQUENCY_CODE);
        return (List<FeeMethod>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(FeeMethod.class, criteria));
    }

    /**
     * Sets the kemService attribute value.
     * @param kemService The kemService to set.
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }
    
}
