/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.dataaccess.impl;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentHeaderDao;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.dao.impl.DocumentHeaderDaoOjb;

/**
 * This class is the financial system document header dao ojb implementation
 */
public class FinancialSystemDocumentHeaderDaoOjb extends DocumentHeaderDaoOjb implements FinancialSystemDocumentHeaderDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinancialSystemDocumentHeaderDaoOjb.class);

    /**
     * Constructs a FinancialSystemDocumentHeaderDaoOjb.java.
     */
    public FinancialSystemDocumentHeaderDaoOjb() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.dao.DocumentHeaderDao#getCorrectingDocumentHeader(java.lang.Long)
     */
    public DocumentHeader getCorrectingDocumentHeader(String documentId) {
        Criteria correctedByCriteria = new Criteria();
        correctedByCriteria.addEqualTo("financialDocumentInErrorNumber", documentId);

        return (DocumentHeader) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(getDocumentHeaderBaseClass(), correctedByCriteria));
    }

    /**
     * @see org.kuali.rice.kns.dao.DocumentHeaderDao#getByDocumentFinalDate(Date documentFinalDate)
     */
    public Collection getByDocumentFinalDate(Date documentFinalDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_FINAL_DATE, documentFinalDate);
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(getDocumentHeaderBaseClass(), criteria));
    }
    
    public Class getDocumentHeaderBaseClass() {
        LOG.debug("Method getDocumentHeaderBaseClass() returning class " + FinancialSystemDocumentHeader.class.getName());
        return FinancialSystemDocumentHeader.class;
    }
    
    public Collection getByDocumentNumbers(List documentNumbers) {
        Criteria criteria = new Criteria();
        criteria.addIn(KFSConstants.CustomerOpenItemReport.DOCUMENT_NUMBER, documentNumbers);
        
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(getDocumentHeaderBaseClass(), criteria));   
    }
}
