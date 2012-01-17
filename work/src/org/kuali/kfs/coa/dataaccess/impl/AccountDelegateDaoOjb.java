/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.coa.dataaccess.impl;

import java.sql.Date;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.dataaccess.AccountDelegateDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.util.KRADPropertyConstants;

/**
 * This class is the OJB implementation of the AccountDelegateDao.
 */
public class AccountDelegateDaoOjb extends PlatformAwareDaoBaseOjb implements AccountDelegateDao {

    /**
     * @see org.kuali.kfs.coa.dataaccess.AccountDelegateDao#getLockingDocumentNumber(java.lang.String, java.lang.String)
     */

    public String getLockingDocumentNumber(String lockingRepresentation, String documentNumber) {
        String lockingDocNumber = "";

        // build the query criteria
        Criteria criteria = new Criteria();
        criteria.addEqualTo("lockingRepresentation", lockingRepresentation);

        // if a docHeaderId is specified, then it will be excluded from the
        // locking representation test.
        if (StringUtils.isNotBlank(documentNumber)) {
            criteria.addNotEqualTo(KRADPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        }

        // attempt to retrieve a document based off this criteria
        MaintenanceLock maintenanceLock = (MaintenanceLock) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(MaintenanceLock.class, criteria));

        // if a document was found, then there's already one out there pending, and
        // we consider it 'locked' and we return the docnumber.
        if (maintenanceLock != null) {
            lockingDocNumber = maintenanceLock.getDocumentNumber();
        }
        return lockingDocNumber;
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.AccountDelegateDao#getAccountDelegationsForPerson(java.lang.String)
     */
    public Iterator<AccountDelegate> getAccountDelegationsForPerson(String principalId, boolean primary) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountDelegateSystemId", principalId);
        criteria.addEqualTo("active", "Y");
        criteria.addEqualTo("accountsDelegatePrmrtIndicator", primary);

        return (Iterator<AccountDelegate>) getPersistenceBrokerTemplate().getIteratorByQuery(QueryFactory.newQuery(AccountDelegate.class, criteria));
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.AccountDelegateDao#isPrincipalInAnyWayShapeOrFormPrimaryAccountDelegate(java.lang.String, java.sql.Date)
     */
    public boolean isPrincipalInAnyWayShapeOrFormPrimaryAccountDelegate(String principalId, Date currentSqlDate) {
        return queryPrincipalIsAccountDelegate(principalId, true, currentSqlDate);
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.AccountDelegateDao#isPrincipalInAnyWayShapeOrFormSecondaryAccountDelegate(java.lang.String, java.sql.Date)
     */
    public boolean isPrincipalInAnyWayShapeOrFormSecondaryAccountDelegate(String principalId, Date currentSqlDate) {
        return queryPrincipalIsAccountDelegate(principalId, false, currentSqlDate);
    }

    /**
     * Determines if any non-closed accounts exist where the principal id is an account delegate
     * 
     * @param principalId the principal id to check
     * @param primary whether to check primary delegations (if true) or secondary delegations (if false)
     * @param currentSqlDate current sql date
     * @return true if the principal has an account delegation
     */
    protected boolean queryPrincipalIsAccountDelegate(String principalId, boolean primary, Date currentSqlDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountDelegateSystemId", principalId);
        criteria.addEqualTo("accountsDelegatePrmrtIndicator", (primary ? "Y" : "N"));
        criteria.addEqualTo("active", "Y");
        criteria.addEqualTo("account.active", "Y");
        criteria.addLessOrEqualThan("accountDelegateStartDate", currentSqlDate);

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(AccountDelegate.class, criteria);
        reportQuery.setAttributes(new String[] { "count(*)" });

        int resultCount = 0;
        // TODO: getReportQueryIteratorByQuery can be changed to getCount...
        Iterator iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportQuery);
        while (iter.hasNext()) {
            final Object[] results = (Object[]) iter.next();
            resultCount = (results[0] instanceof Number) ? ((Number) results[0]).intValue() : new Integer(results[0].toString()).intValue();
        }
        return resultCount > 0;
    }
}
