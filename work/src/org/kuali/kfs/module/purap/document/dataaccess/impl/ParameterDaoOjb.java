/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.dataaccess.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.purap.document.dataaccess.ParameterDao;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class ParameterDaoOjb extends PlatformAwareDaoBaseOjb implements ParameterDao {
    
    /**
     * @see org.kuali.kfs.sys.dataaccess.ParameterDao#getParametersGivenLikeCriteria(java.util.Map)
     */
    public List<Parameter> getParametersGivenLikeCriteria(Map<String, String> fieldValues) {
        Criteria criteria = buildLikeCriteria(fieldValues);

        return (List<Parameter>)getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Parameter.class, criteria));
    }
    
    /**
     * Builds the Criteria object based on the given fieldValues Map
     * in the input parameter.
     * 
     * @param   fieldValues The Map to be used to build the Criteria. 
     * @return  the resulting Criteria object.
     */
    private Criteria buildLikeCriteria(Map<String, String> fieldValues) {
        Criteria criteria = new Criteria();
        for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry<String, String> e = (Map.Entry<String, String>) i.next();
            String key = e.getKey();
            String value = e.getValue();
            //If the "%" keyword exists in the value, it means that we must
            //add the LIKE criteria as well. 
            if (value.indexOf("%") >= 0) {
                criteria.addLike(key, value);
            }
            //If the "%" keyword doesn't exist, this is a regular "equal to"
            //criteria. 
            else {
                criteria.addEqualTo(key, value);
            }
        }

        return criteria;
    }
}
