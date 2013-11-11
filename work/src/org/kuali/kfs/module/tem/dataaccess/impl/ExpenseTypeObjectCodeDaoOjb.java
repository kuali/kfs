/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.dataaccess.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.dataaccess.ExpenseTypeObjectCodeDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * OJB implementation of the ExpenseTypeObjectCodeDao
 */
public class ExpenseTypeObjectCodeDaoOjb extends PlatformAwareDaoBaseOjb implements ExpenseTypeObjectCodeDao {

    /**
     *
     * @see org.kuali.kfs.module.tem.dataaccess.ExpenseTypeObjectCodeDao#findMatchingExpenseTypeObjectCodes(java.lang.String, java.util.List, java.lang.String, java.lang.String)
     */
    @Override
    public List<ExpenseTypeObjectCode> findMatchingExpenseTypeObjectCodes(String expenseCodeType, Set<String> documentTypes, String tripType, String travelerType) {
        if (StringUtils.isBlank(expenseCodeType)) {
            throw new IllegalArgumentException("The expenseCodeType parameter of ExpenseTypeCodeDao#findMatchingExpenseTypeObjectCodes may not be null or empty");
        }
        Criteria c = new Criteria();
        c.addEqualTo(TemPropertyConstants.EXPENSE_TYPE_CODE, expenseCodeType);
        if (documentTypes != null && !documentTypes.isEmpty()) {
            c.addIn(KFSPropertyConstants.DOCUMENT_TYPE_NAME, documentTypes);
        }

        Set<String> tripTypes = new HashSet<String>();
        tripTypes.add(TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRIP_TYPE);
        if (!StringUtils.isBlank(tripType)) {
            tripTypes.add(tripType);
        }
        c.addIn(TemPropertyConstants.TRIP_TYPE_CODE, tripTypes);

        Set<String> travelerTypes = new HashSet<String>();
        travelerTypes.add(TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRAVELER_TYPE);
        if (!StringUtils.isBlank(travelerType)) {
            travelerTypes.add(travelerType);
        }
        c.addIn(TemPropertyConstants.TRAVELER_TYPE_CODE, travelerTypes);

        c.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);

        final Query query = QueryFactory.newQuery(ExpenseTypeObjectCode.class, c);
        List<ExpenseTypeObjectCode> results = new ArrayList<ExpenseTypeObjectCode>();
        results.addAll(getPersistenceBrokerTemplate().getCollectionByQuery(query)); // we're going to reorder these - let's put them in their own array
        return results;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.dataaccess.ExpenseTypeObjectCodeDao#findMatchingExpenseTypesObjectCodes(java.util.Set, java.lang.String, java.lang.String)
     */
    @Override
    public List<ExpenseTypeObjectCode> findMatchingExpenseTypesObjectCodes(Set<String> documentTypes, String tripType, String travelerType) {
        if (documentTypes == null || documentTypes.isEmpty()) {
            throw new IllegalArgumentException("The documentTypes parameter of ExpenseTypeCodeDao#findMatchingExpenseTypeObjectCodes may not be null or empty");
        }
        Criteria c = new Criteria();
        c.addIn(KFSPropertyConstants.DOCUMENT_TYPE_NAME, documentTypes);
        c.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        if (!StringUtils.isBlank(tripType)) {
            Set<String> tripTypes = new HashSet<String>();
            tripTypes.add(tripType);
            tripTypes.add(TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRIP_TYPE);
            c.addIn(TemPropertyConstants.TRIP_TYPE_CODE, tripTypes);
        }
        if (!StringUtils.isBlank(travelerType)) {
            Set<String> travelerTypes = new HashSet<String>();
            travelerTypes.add(travelerType);
            travelerTypes.add(TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRAVELER_TYPE);
            c.addIn(TemPropertyConstants.TRAVELER_TYPE_CODE, travelerTypes);
        }
        QueryByCriteria query = QueryFactory.newQuery(ExpenseTypeObjectCode.class, c);
        query.addOrderByAscending(TemPropertyConstants.EXPENSE_TYPE+"."+KFSPropertyConstants.NAME);
        List<ExpenseTypeObjectCode> results = new ArrayList<ExpenseTypeObjectCode>();
        results.addAll(getPersistenceBrokerTemplate().getCollectionByQuery(query));
        return results;
    }
}
