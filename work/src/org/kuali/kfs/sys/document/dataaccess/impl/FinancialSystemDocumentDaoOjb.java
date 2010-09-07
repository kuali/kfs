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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentDao;
import org.kuali.rice.kns.dao.BusinessObjectDao;
import org.kuali.rice.kns.dao.impl.DocumentDaoOjb;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentAdHocService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSPropertyConstants;

/**
 * This class is the KFS specific document dao implementation
 */
public class FinancialSystemDocumentDaoOjb extends PlatformAwareDaoBaseOjb implements FinancialSystemDocumentDao {
    
    protected DocumentAdHocService documentAdHocService;

    public <T extends Document> Collection<T> findByDocumentHeaderStatusCode(Class<T> clazz, String statusCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KNSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, statusCode);

        QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);

        ArrayList<T> tempList =  new ArrayList<T>(this.getPersistenceBrokerTemplate().getCollectionByQuery(query));
        for (Document doc : tempList) getDocumentAdHocService().addAdHocs(doc);
        return tempList;
    }

    public DocumentAdHocService getDocumentAdHocService() {
        return documentAdHocService;
    }

    public void setDocumentAdHocService(DocumentAdHocService documentAdHocService) {
        this.documentAdHocService = documentAdHocService;
    }

}
