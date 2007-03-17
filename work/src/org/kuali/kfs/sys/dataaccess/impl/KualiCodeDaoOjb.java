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
package org.kuali.kfs.dao.ojb;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.Constants;
import org.kuali.core.bo.ApplicationConstant;
import org.kuali.core.bo.KualiCode;
import org.kuali.core.bo.KualiSystemCode;
import org.kuali.kfs.dao.KualiCodeDao;
import org.kuali.module.cg.bo.AgencyType;
import org.kuali.module.chart.bo.FundGroup;
import org.kuali.module.chart.bo.ObjSubTyp;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.RestrictedStatus;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.chart.bo.codes.BudgetAggregationCode;
import org.kuali.module.chart.bo.codes.FederalFundedCode;
import org.kuali.module.chart.bo.codes.MandatoryTransferEliminationCode;
import org.kuali.module.financial.bo.PaymentReasonCode;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.kra.budget.bo.NonpersonnelSubCategory;
import org.springframework.dao.DataAccessException;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * This class is the OJB implementation of the KualiCodeDao interface.
 * 
 *
 */

public class KualiCodeDaoOjb extends PersistenceBrokerDaoSupport implements KualiCodeDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiCodeDaoOjb.class);

    private static Map codeColumns = new HashMap();
    static {
        codeColumns.put(AgencyType.class, "code");
        codeColumns.put(ApplicationConstant.class, "code");
        codeColumns.put(BalanceTyp.class, "code");
        codeColumns.put(BudgetAggregationCode.class, "code");
        codeColumns.put(PaymentReasonCode.class, "code");
        codeColumns.put(FederalFundedCode.class, "code");
        codeColumns.put(FundGroup.class, "code");
        codeColumns.put(MandatoryTransferEliminationCode.class, "code");
        codeColumns.put(NonpersonnelSubCategory.class, "code");
        codeColumns.put(ObjectCode.class, "financialObjectCode");
        codeColumns.put(ObjectType.class, "code");
        codeColumns.put(ObjSubTyp.class, "code");
        codeColumns.put(OriginEntrySource.class, "code");
        codeColumns.put(ProjectCode.class, "code");
        codeColumns.put(RestrictedStatus.class, "accountRestrictedStatusCode");

        // can't add entry for CashDetailTypeCode since the table doesn't yet exist
    }

    private static Map nameColumns = new HashMap();
    static {
        nameColumns.put(AgencyType.class, "name");
        nameColumns.put(ApplicationConstant.class, "name");
        nameColumns.put(BalanceTyp.class, "name");
        nameColumns.put(BudgetAggregationCode.class, "name");
        nameColumns.put(PaymentReasonCode.class, "name");
        nameColumns.put(FederalFundedCode.class, "name");
        nameColumns.put(FundGroup.class, "name");
        nameColumns.put(MandatoryTransferEliminationCode.class, "name");
        nameColumns.put(NonpersonnelSubCategory.class, "name");
        nameColumns.put(ObjectCode.class, "financialObjectCodeName");
        nameColumns.put(ObjectType.class, "name");
        nameColumns.put(ObjSubTyp.class, "name");
        nameColumns.put(OriginEntrySource.class, "name");
        nameColumns.put(ProjectCode.class, "name");
        nameColumns.put(RestrictedStatus.class, "accountRestrictedStatusName");

        // can't add entry for CashDetailTypeCode since the table doesn't yet exist
    }


    /**
     * @param className - the name of the object being used, either KualiCodeBase or a subclass
     * @param code - code to search for
     * @return KualiCodeBase
     * 
     * Retrieves an KualiCodeBase object by a given code.
     */
    public KualiCode getByCode(Class queryClass, String code) {
        Criteria criteria = getCriteriaForGivenClass(codeColumns, queryClass, code);
        if (KualiSystemCode.class.isAssignableFrom(queryClass)) {
            criteria.addEqualTo("CLASS_NAME", queryClass.getName());
            // queryClass = KualiSystemCode.class;
        }

        return (KualiCode) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(queryClass, criteria));
    }

    private Criteria getCriteriaForGivenClass(Map map, Class queryClass, String value) {
        Criteria criteria = new Criteria();
        String column = (String) map.get(queryClass);
        if (column == null) {
            System.err.println("Need to add logic for " + queryClass.getName());
            throw new RuntimeException("Need to add logic for " + queryClass.getName());
        }
        criteria.addEqualTo(column, value);
        return criteria;
    }

    public KualiCode getSystemCode(Class clazz, String code) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("CODE", code);
        criteria.addEqualTo("CLASS_NAME", clazz.getName());

        return (KualiCode) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(clazz, criteria));
    }

    /**
     * @param className - the name of the object being used, either KualiCodeBase or a subclass
     * @param name - name to search for
     * @return KualiCodeBase
     * 
     * Retrieves an KualiCodeBase object by a given exact name.
     */
    public KualiCode getByName(Class queryClass, String name) {
        Criteria criteria = getCriteriaForGivenClass(nameColumns, queryClass, name);
        if (KualiSystemCode.class.isAssignableFrom(queryClass)) {
            criteria.addEqualTo("className", queryClass.getName());
            queryClass = KualiSystemCode.class;
        }

        return (KualiCode) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(queryClass, criteria));
    }

    /**
     * @param kualiCode
     * 
     * Pass the method a populated KualiCodeBase object, and it will be saved.
     */
    public void save(KualiCode kualiCode) throws DataAccessException {
        getPersistenceBrokerTemplate().store(kualiCode);
    }

    /**
     * Deletes the object-record passed in.
     * 
     * @param kualiCode
     * @throws DataAccessException
     */
    public void delete(KualiCode kualiCode) {
        // try to delete the retrieved object
        getPersistenceBrokerTemplate().delete(kualiCode);
    }

    /**
     * @see org.kuali.core.dao.KualiCodeDao#getAllActive(java.lang.Class)
     */
    public Collection getAllActive(Class queryClass) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("FIN_OBJ_ACTIVE_CD", Constants.ACTIVE_INDICATOR);
        if (KualiSystemCode.class.isAssignableFrom(queryClass)) {
            criteria.addEqualTo("className", queryClass.getName());
            queryClass = KualiSystemCode.class;
        }

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(queryClass, criteria));
    }

    /**
     * @see org.kuali.core.dao.KualiCodeDao#getAll(java.lang.Class)
     */
    public Collection getAll(Class queryClass) {
        Criteria criteria = new Criteria();
        if (KualiSystemCode.class.isAssignableFrom(queryClass)) {
            criteria.addEqualTo("className", queryClass.getName());
            // queryClass = KualiSystemCode.class;
        }

        // This previously was passing a NULL into the criteria paramter, for no
        // apparent reason I could see. The result was that it was ignoring class
        // and returning all codes, which is definitely not what we want, and
        // definitely not what the interface JavaDocs said. This was fixed.
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(queryClass, criteria));
    }

}
