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
package org.kuali.kfs.module.endow.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.TransactionArchiveSecurity;
import org.kuali.kfs.module.endow.dataaccess.TransactionArchiveSecurityDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class TransactionArchiveSecurityDaoOjb extends PlatformAwareDaoBaseOjb implements TransactionArchiveSecurityDao {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionArchiveSecurityDaoOjb.class);

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getByPrimaryKey(String, int, String)
     */
    public TransactionArchiveSecurity getByPrimaryKey(String documentNumber, int lineNumber, String lineTypeCode) {
        TransactionArchiveSecurity transactionArchive = null;

        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_DOCUMENT_NUMBER, documentNumber);
        criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_NUMBER, lineNumber);

        criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_TYPE_CODE, lineTypeCode);

        QueryByCriteria query = QueryFactory.newQuery(TransactionArchiveSecurity.class, criteria);
        transactionArchive = (TransactionArchiveSecurity) getPersistenceBrokerTemplate().getObjectByQuery(query);

        return transactionArchive;
    }
}
