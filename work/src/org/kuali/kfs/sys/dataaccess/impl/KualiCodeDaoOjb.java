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
package org.kuali.kfs.sys.dataaccess.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.BudgetAggregationCode;
import org.kuali.kfs.coa.businessobject.FederalFundedCode;
import org.kuali.kfs.coa.businessobject.FundGroup;
import org.kuali.kfs.coa.businessobject.MandatoryTransferEliminationCode;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectSubType;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.RestrictedStatus;
import org.kuali.kfs.fp.businessobject.PaymentReasonCode;
import org.kuali.kfs.gl.businessobject.OriginEntrySource;
import org.kuali.kfs.module.cg.businessobject.AgencyType;
import org.kuali.kfs.pdp.businessobject.ACHTransactionCode;
import org.kuali.kfs.pdp.businessobject.ACHTransactionType;
import org.kuali.kfs.pdp.businessobject.AccountingChangeCode;
import org.kuali.kfs.pdp.businessobject.DisbursementType;
import org.kuali.kfs.pdp.businessobject.PayeeType;
import org.kuali.kfs.pdp.businessobject.PaymentChangeCode;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.pdp.businessobject.PaymentType;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.dataaccess.KualiCodeDao;
import org.kuali.rice.kns.bo.KualiCode;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.springframework.dao.DataAccessException;

/**
 * This class is the OJB implementation of the KualiCodeDao interface.
 */

public class KualiCodeDaoOjb extends PlatformAwareDaoBaseOjb implements KualiCodeDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiCodeDaoOjb.class);

    private static Map codeColumns = new HashMap();
    static {
        codeColumns.put(AgencyType.class, "code");
        codeColumns.put(BalanceType.class, "code");
        codeColumns.put(BudgetAggregationCode.class, "code");
        codeColumns.put(PaymentReasonCode.class, "code");
        codeColumns.put(FederalFundedCode.class, "code");
        codeColumns.put(FundGroup.class, "code");
        codeColumns.put(MandatoryTransferEliminationCode.class, "code");
        codeColumns.put(ObjectCode.class, "financialObjectCode");
        codeColumns.put(ObjectType.class, "code");
        codeColumns.put(ObjectSubType.class, "code");
        codeColumns.put(OriginEntrySource.class, "code");
        codeColumns.put(ProjectCode.class, "code");
        codeColumns.put(RestrictedStatus.class, "accountRestrictedStatusCode");
        codeColumns.put(PaymentStatus.class, "code");
        codeColumns.put(AccountingChangeCode.class, "code");
        codeColumns.put(DisbursementType.class, "code");
        codeColumns.put(PaymentChangeCode.class, "code");
        codeColumns.put(PayeeType.class, "code");
        codeColumns.put(ACHTransactionCode.class, "code");
        codeColumns.put(ACHTransactionType.class, "code");
        codeColumns.put(PaymentType.class, "code");
        // can't add entry for CashDetailTypeCode since the table doesn't yet exist
    }

    private static Map nameColumns = new HashMap();
    static {
        nameColumns.put(AgencyType.class, "name");
        nameColumns.put(BalanceType.class, "name");
        nameColumns.put(BudgetAggregationCode.class, "name");
        nameColumns.put(PaymentReasonCode.class, "name");
        nameColumns.put(FederalFundedCode.class, "name");
        nameColumns.put(FundGroup.class, "name");
        nameColumns.put(MandatoryTransferEliminationCode.class, "name");
        nameColumns.put(ObjectCode.class, "financialObjectCodeName");
        nameColumns.put(ObjectType.class, "name");
        nameColumns.put(ObjectSubType.class, "name");
        nameColumns.put(OriginEntrySource.class, "name");
        nameColumns.put(ProjectCode.class, "name");
        nameColumns.put(RestrictedStatus.class, "accountRestrictedStatusName");
        nameColumns.put(PaymentStatus.class, "name");
        nameColumns.put(AccountingChangeCode.class, "name");
        nameColumns.put(DisbursementType.class, "name");
        nameColumns.put(PaymentChangeCode.class, "name");
        nameColumns.put(PaymentChangeCode.class, "name");
        nameColumns.put(ACHTransactionCode.class, "name");
        nameColumns.put(ACHTransactionType.class, "name");
        nameColumns.put(PaymentType.class, "name");
        // can't add entry for CashDetailTypeCode since the table doesn't yet exist
    }


    /**
     * @param className - the name of the object being used, either KualiCodeBase or a subclass
     * @param code - code to search for
     * @return KualiCodeBase Retrieves an KualiCodeBase object by a given code.
     */
    public KualiCode getByCode(Class queryClass, String code) {
        Criteria criteria = getCriteriaForGivenClass(codeColumns, queryClass, code);
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
     * @return KualiCodeBase Retrieves an KualiCodeBase object by a given exact name.
     */
    public KualiCode getByName(Class queryClass, String name) {
        Criteria criteria = getCriteriaForGivenClass(nameColumns, queryClass, name);
        return (KualiCode) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(queryClass, criteria));
    }

    /**
     * @param kualiCode Pass the method a populated KualiCodeBase object, and it will be saved.
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
     * @see org.kuali.rice.kns.dao.KualiCodeDao#getAllActive(java.lang.Class)
     */
    public Collection getAllActive(Class queryClass) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("FIN_OBJ_ACTIVE_CD", KFSConstants.ACTIVE_INDICATOR);
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(queryClass, criteria));
    }

    /**
     * @see org.kuali.rice.kns.dao.KualiCodeDao#getAll(java.lang.Class)
     */
    public Collection getAll(Class queryClass) {
        Criteria criteria = new Criteria();
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(queryClass, criteria));
    }

}
