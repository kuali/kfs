/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.module.labor.bo.PendingLedgerEntry;
import org.kuali.module.labor.dao.LaborLedgerPendingEntryDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * 
 * 
 */
public class LaborLedgerPendingEntryDaoOjb extends PersistenceBrokerDaoSupport implements LaborLedgerPendingEntryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerPendingEntryDaoOjb.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.dao.GeneralLedgerPendingEntryDao#delete(Long)
     */
    public void delete(String documentHeaderId) {
        LOG.debug("delete() started");

        if (documentHeaderId != null) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(PropertyConstants.DOCUMENT_NUMBER, documentHeaderId);

            getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(PendingLedgerEntry.class, criteria));
            getPersistenceBrokerTemplate().clearCache();
        }
    }

    public void deleteEntriesForCancelledOrDisapprovedDocuments() {
        LOG.debug("deleteEntriesForCancelledOrDisapprovedDocuments() started");

        Criteria subCriteria = new Criteria();
        Criteria criteria = new Criteria();

        List codes = new ArrayList();
        codes.add(Constants.DocumentStatusCodes.DISAPPROVED);
        codes.add(Constants.DocumentStatusCodes.CANCELLED);

        subCriteria.addIn(PropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, codes);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(DocumentHeader.class, subCriteria);

        subQuery.setAttributes(new String[] { PropertyConstants.DOCUMENT_NUMBER });

        criteria.addIn(PropertyConstants.DOCUMENT_NUMBER, subQuery);

        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(PendingLedgerEntry.class, criteria));
    }
}