/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.gl.dao.ojb;

import java.sql.Date;
import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.PropertyConstants;
import org.kuali.core.util.spring.Cached;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * 
 * 
 */
public class UniversityDateDaoOjb extends PersistenceBrokerDaoSupport implements UniversityDateDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UniversityDateDaoOjb.class);

    public UniversityDateDaoOjb() {
        super();
    }

    @Cached
    public UniversityDate getByPrimaryKey(Date date) {
        LOG.debug("getByPrimaryKey() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(PropertyConstants.UNIVERSITY_DATE, date);

        QueryByCriteria qbc = QueryFactory.newQuery(UniversityDate.class, crit);

        return (UniversityDate) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    @Cached
    public UniversityDate getByPrimaryKey(java.util.Date date) {
        return getByPrimaryKey(convertDate(date));
    }

    private java.sql.Date convertDate(java.util.Date date) {
        return new Date(date.getTime());
    }

    public UniversityDate getLastFiscalYearDate(Integer fiscalYear) {
        ReportQueryByCriteria subQuery;
        Criteria subCrit = new Criteria();
        Criteria crit = new Criteria();

        subCrit.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        subQuery = QueryFactory.newReportQuery(UniversityDate.class, subCrit);
        subQuery.setAttributes(new String[] { "max(univ_dt)" });

        crit.addGreaterOrEqualThan(PropertyConstants.UNIVERSITY_DATE, subQuery);

        QueryByCriteria qbc = QueryFactory.newQuery(UniversityDate.class, crit);

        return (UniversityDate) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    public UniversityDate getFirstFiscalYearDate(Integer fiscalYear) {
        ReportQueryByCriteria subQuery;
        Criteria subCrit = new Criteria();
        Criteria crit = new Criteria();

        subCrit.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        subQuery = QueryFactory.newReportQuery(UniversityDate.class, subCrit);
        subQuery.setAttributes(new String[] { "min(univ_dt)" });

        crit.addGreaterOrEqualThan(PropertyConstants.UNIVERSITY_DATE, subQuery);

        QueryByCriteria qbc = QueryFactory.newQuery(UniversityDate.class, crit);

        return (UniversityDate) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * @see org.kuali.module.gl.dao.UniversityDateDao#getAccountingPeriodCode()
     */
    public Collection getAccountingPeriodCode() {
        Criteria criteria = new Criteria();

        ReportQueryByCriteria query = QueryFactory.newReportQuery(UniversityDate.class, criteria);
        query.setAttributes(new String[] { "distinct " + PropertyConstants.UNIVERSITY_FISCAL_ACCOUNTING_PERIOD });
        query.addOrderByAscending(PropertyConstants.UNIVERSITY_FISCAL_ACCOUNTING_PERIOD);

        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
}
