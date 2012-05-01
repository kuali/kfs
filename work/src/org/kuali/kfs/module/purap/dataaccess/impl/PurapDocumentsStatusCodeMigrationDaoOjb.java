/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.dataaccess.impl;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * A class to do the database queries needed to fetch requisitions, po, preq etc., for 
 * migration of status code from purap documents to the document header's workflowdocument.
 */
public class PurapDocumentsStatusCodeMigrationDaoOjb extends PlatformAwareDaoBaseOjb implements PurapDocumentsStatusCodeMigrationDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapDocumentsStatusCodeMigrationDaoOjb.class);
    
    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#findDocumentTypes()
     */
    public List<RequisitionDocument> getRequisitionDocumentsForStatusCodeMigration() {
        LOG.debug("getRequisitionDocumentsForStatusCodeMigration() started");
        
        Criteria criteria = new Criteria();
        criteria.addNotNull("REQS_STAT_CD");
        
        QueryByCriteria query = QueryFactory.newQuery(RequisitionDocument.class, criteria);
        return (List<RequisitionDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao#getPurchaseOrderDocumentsForStatusCodeMigration()
     */
    public List<PurchaseOrderDocument> getPurchaseOrderDocumentsForStatusCodeMigration() {
        LOG.debug("getPurchaseOrderDocumentsForStatusCodeMigration() started");
        
        Criteria criteria = new Criteria();
        criteria.addNotNull("PO_STAT_CD");
        
        QueryByCriteria query = QueryFactory.newQuery(PurchaseOrderDocument.class, criteria);
        return (List<PurchaseOrderDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
    
}   
