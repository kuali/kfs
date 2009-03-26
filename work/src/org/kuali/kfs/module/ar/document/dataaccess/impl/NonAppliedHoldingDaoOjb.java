/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.document.dataaccess.NonAppliedHoldingDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.util.OjbCollectionAware;

public class NonAppliedHoldingDaoOjb extends PlatformAwareDaoBaseOjb implements NonAppliedHoldingDao, OjbCollectionAware {

    public Collection<NonAppliedHolding> getNonAppliedHoldingsByListOfDocumentNumbers(List<String> docNumbers) {
        Criteria criteria = new Criteria();
        criteria.addIn("", docNumbers);

        QueryByCriteria query = QueryFactory.newQuery(NonAppliedHolding.class, criteria);
        return new ArrayList<NonAppliedHolding>(this.getPersistenceBrokerTemplate().getCollectionByQuery(query));
    }

}
