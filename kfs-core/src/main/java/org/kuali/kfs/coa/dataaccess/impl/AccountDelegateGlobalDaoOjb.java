/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.coa.dataaccess.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.dataaccess.AccountDelegateGlobalDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.util.KRADPropertyConstants;

/**
 * This class is the OJB implementation of AccountDelegateGlobalDao
 */
public class AccountDelegateGlobalDaoOjb extends PlatformAwareDaoBaseOjb implements AccountDelegateGlobalDao {

   /**
    * 
    * @see org.kuali.kfs.coa.dataaccess.AccountDelegateGlobalDao#getLockingDocumentNumber(java.lang.String, java.lang.String)
    */
    
    public String getLockingDocumentNumber(String lockingRepresentation, String documentNumber) {
        String lockingDocNumber = "";

        lockingRepresentation = convertForLikeCriteria(lockingRepresentation);
        
        // build the query criteria
        Criteria criteria = new Criteria();
        criteria.addLike("lockingRepresentation", lockingRepresentation);

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
     * Parses the lockingRepresentation and replaces the document type with a wild card.
     * 
     * @param lockingRepresentation The String representation of the MaintenanceLock created by this record
     * @return The String representation of MaintenanceLock with the financialDocumentTypeCode replaced with a wildcard(%).
     */
    
    protected String convertForLikeCriteria(String lockingRepresentation) {
        //org.kuali.kfs.coa.businessobject.AccountDelegate!!chartOfAccountsCode^^BL::accountNumber^^1031400::financialDocumentTypeCode^^IB::accountsDelegatePrmrtIndicator^^true
        String[] values = StringUtils.split(lockingRepresentation, "::");
        StringBuilder sb = new StringBuilder();
        for (String val : values) {
            if (val.startsWith("financialDocumentTypeCode")) {
                String[] meh = StringUtils.split(val, "^^");
                meh[1] = "%";
                val = meh[0] +"^^"+meh[1];
                sb.append(val);
                break;
            } else {
                sb.append(val+"::");
            }
        }
        
        return sb.toString();
    }
}
