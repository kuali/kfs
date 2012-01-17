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

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentHeaderDao;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.dao.impl.DocumentHeaderDaoOjb;

/**
 * This class is the financial system document header dao ojb implementation
 */
public class FinancialSystemDocumentHeaderDaoOjb extends DocumentHeaderDaoOjb implements FinancialSystemDocumentHeaderDao {


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

}
