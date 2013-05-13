/*
 * Copyright 2010 The Kuali Foundation.
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
