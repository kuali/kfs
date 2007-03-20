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
package org.kuali.module.labor.dao.ojb;

import java.util.Collection;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.PropertyConstants;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.labor.dao.LaborDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * This class is used to retrieve Calculated Salary Foundation Tracker information.
 */
public class LaborDaoOjb extends PersistenceBrokerDaoSupport implements LaborDao {


    public Collection getCSFTrackerData(Map fieldValues) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new CalculatedSalaryFoundationTracker());

        LookupUtils.applySearchResultsLimit(criteria);

        QueryByCriteria query = QueryFactory.newQuery(CalculatedSalaryFoundationTracker.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * This method builds an OJB query criteria based on the input field map
     * 
     * @param fieldValues the input field map
     * @param businessObject the given business object
     * @return an OJB query criteria
     */
    public Criteria buildCriteriaFromMap(Map fieldValues, Object businessObject) {
        Criteria criteria = new Criteria();

        UniversityDate currentUniversityDate = SpringServiceLocator.getUniversityDateService().getCurrentUniversityDate();
        String currentFiscalPeriodCode = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = currentUniversityDate.getUniversityFiscalYear();

        // deal with null fiscal year and fiscal period code as current fiscal year and period code respectively
        String fiscalPeriodFromForm = null;
        if (fieldValues.containsKey(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE)) {
            fiscalPeriodFromForm = (String) fieldValues.get(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        }

        String fiscalYearFromForm = null;
        if (fieldValues.containsKey(PropertyConstants.UNIVERSITY_FISCAL_YEAR)) {
            fiscalYearFromForm = (String) fieldValues.get(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
        }

        boolean includeNullFiscalPeriodCodeInLookup = null != currentFiscalPeriodCode && currentFiscalPeriodCode.equals(fiscalPeriodFromForm);
        boolean includeNullFiscalYearInLookup = null != currentFiscalYear && currentFiscalYear.toString().equals(fiscalYearFromForm);

        if (includeNullFiscalPeriodCodeInLookup) {
            Criteria apValueCriteria = new Criteria();
            apValueCriteria.addLike(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, fiscalPeriodFromForm);

            Criteria apNullCriteria = new Criteria();
            apNullCriteria.addIsNull(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);

            apValueCriteria.addOrCriteria(apNullCriteria);
            criteria.addAndCriteria(apValueCriteria);

            fieldValues.remove(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        }

        if (includeNullFiscalYearInLookup) {
            Criteria fyValueCriteria = new Criteria();
            fyValueCriteria.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYearFromForm);

            Criteria fyNullCriteria = new Criteria();
            fyNullCriteria.addIsNull(PropertyConstants.UNIVERSITY_FISCAL_YEAR);

            fyValueCriteria.addOrCriteria(fyNullCriteria);
            criteria.addAndCriteria(fyValueCriteria);

            fieldValues.remove(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
        }

        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, businessObject));
        return criteria;
    }

}