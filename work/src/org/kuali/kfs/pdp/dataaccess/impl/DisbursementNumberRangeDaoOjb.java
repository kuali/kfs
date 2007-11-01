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
 * Created on Aug 12, 2004
 *
 */
package org.kuali.module.pdp.dao.ojb;

import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.module.pdp.bo.DisbursementNumberRange;
import org.kuali.module.pdp.bo.UserRequired;
import org.kuali.module.pdp.dao.DisbursementNumberRangeDao;


/**
 * @author jsissom
 */
public class DisbursementNumberRangeDaoOjb extends PlatformAwareDaoBaseOjb implements DisbursementNumberRangeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementNumberRangeDaoOjb.class);

    private UniversalUserService userService;

    public DisbursementNumberRangeDaoOjb() {
        super();
    }

    // Inject
    public void setUniversalUserService(UniversalUserService us) {
        userService = us;
    }

    private void updateUser(List l) {
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            updateUser((DisbursementNumberRange) iter.next());
        }
    }

    private void updateUser(DisbursementNumberRange b) {
        UserRequired ur = (UserRequired) b;
        try {
            ur.updateUser(userService);
        }
        catch (UserNotFoundException e) {
            b.setLastUpdateUser(null);
        }
    }

    public List getAll() {
        LOG.debug("getAll() started");

        QueryByCriteria qbc = new QueryByCriteria(DisbursementNumberRange.class);
        qbc.addOrderBy("physCampusProcCode", true);
        qbc.addOrderBy("bank.disbursementType.code", true);

        List l = (List) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        updateUser(l);
        return l;
    }

    public DisbursementNumberRange get(Integer id) {
        LOG.debug("get() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("id", id);

        DisbursementNumberRange d = (DisbursementNumberRange) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(DisbursementNumberRange.class, criteria));
        if (d != null) {
            updateUser(d);
        }
        return d;
    }

    public void save(DisbursementNumberRange dnr) {
        LOG.debug("save() started");

        try {
            getPersistenceBrokerTemplate().store(dnr);
        }
        catch (Exception e) {
            LOG.debug("save() Exception Occurred: " + e);
        }
    }

}
