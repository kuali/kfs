/*
 * Copyright 2007 The Kuali Foundation.
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
/*
 * Created on Aug 7, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.pdp.dataaccess.ReferenceDao;
import org.kuali.kfs.pdp.exception.ConfigurationError;
import org.kuali.rice.kns.bo.KualiCodeBase;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.UniversalUserService;


/**
 * 
 */
public class ReferenceDaoOjb extends PlatformAwareDaoBaseOjb implements ReferenceDao {
    /*private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReferenceDaoOjb.class);

    private UniversalUserService userService;

    public ReferenceDaoOjb() {
        super();
    }

    // Inject
    public void setUniversalUserService(UniversalUserService us) {
        userService = us;
    }

    private Class getClass(String name) {
        String fullName = "org.kuali.kfs.pdp.businessobject." + name;

        try {
            return Class.forName(fullName);
        }
        catch (ClassNotFoundException e) {
            throw new ConfigurationError("Unknown type: " + name);
        }
    }

    public List getAll(String type) {
        LOG.debug("getAll() for " + type);

        QueryByCriteria qbc = new QueryByCriteria(getClass(type));
        qbc.addOrderBy("name", true);

        List l = (List) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        
        return l;
    }

    public Map getAllMap(String type) {
        LOG.debug("getAllMap() for " + type);

        Map hm = new HashMap();

        for (Iterator iter = getAll(type).iterator(); iter.hasNext();) {
            KualiCodeBase element = (KualiCodeBase) iter.next();
            hm.put(element.getCode(), element);
        }
        return hm;
    }

    // FROM TAFKAT
    public void updateCode(String code, String description, String type, UniversalUser u) {
        LOG.debug("updateCode() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("code", code);

    }*/

}
