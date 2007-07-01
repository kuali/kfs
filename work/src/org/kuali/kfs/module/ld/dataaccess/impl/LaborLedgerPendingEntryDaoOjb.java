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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.dao.ojb.GeneralLedgerPendingEntryDaoOjb;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.dao.LaborLedgerPendingEntryDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

public class LaborLedgerPendingEntryDaoOjb extends GeneralLedgerPendingEntryDaoOjb implements LaborLedgerPendingEntryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerPendingEntryDaoOjb.class);
    
    /**
    * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntriesForAccountBalance(java.util.Map, boolean,
            *      boolean)
            */
           public Iterator findPendingLedgerEntriesForAccountBalance(Map fieldValues, boolean isApproved) {
               LOG.debug("findPendingLedgerEntriesForAccountBalance started");

               Criteria criteria = buildCriteriaFromMap(fieldValues, new GeneralLedgerPendingEntry());

               // add the status codes into the criteria
               addStatusCode(criteria, isApproved);

               QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
               
               return getPersistenceBrokerTemplate().getIteratorByQuery(query);
           }

           /**
            * add the status code into the given criteria. The status code can be categorized into approved and all.
            * 
            * @param criteria the given criteria
            * @param isApproved the flag that indictates if only approved status code can be added into the given searach criteria 
            */
           private void addStatusCode(Criteria criteria, boolean isOnlyApproved) {
               // add criteria for the approved pending entries
               if (isOnlyApproved) {
                   criteria.addIn("documentHeader.financialDocumentStatusCode", this.buildApprovalCodeList());
               }
               criteria.addNotIn("documentHeader.financialDocumentStatusCode", this.buildExcludedStatusCodeList());
               criteria.addNotEqualTo("financialDocumentApprovedCode", KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
           }
           
           /**
            * build a status code list including the legal approval codes
            * 
            * @return an approval code list
            */
           private List buildApprovalCodeList() {
               List approvalCodeList = new ArrayList();

               approvalCodeList.add(KFSConstants.DocumentStatusCodes.APPROVED);
               return approvalCodeList;
           }

           /**
            * build a status code list including the codes that will not be processed
            * 
            * @return a status code list including the codes that will not be processed
            */
           private List buildExcludedStatusCodeList() {
               List exclusiveCodeList = new ArrayList();

               exclusiveCodeList.add(KFSConstants.DocumentStatusCodes.CANCELLED);
               exclusiveCodeList.add(KFSConstants.DocumentStatusCodes.DISAPPROVED);
               return exclusiveCodeList;
           }
           
    
    @Override
    public Class getEntryClass(){
        return LaborLedgerPendingEntry.class;
    }
}
