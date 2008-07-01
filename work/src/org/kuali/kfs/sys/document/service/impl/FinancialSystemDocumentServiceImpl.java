/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.kuali.core.dao.DocumentDao;
import org.kuali.core.document.Document;
import org.kuali.core.service.impl.DocumentServiceImpl;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentDao;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class is a Financial System specific Document Service class to allow for the {@link #findByDocumentHeaderStatusCode(Class, String)} method.
 */
public class FinancialSystemDocumentServiceImpl extends DocumentServiceImpl implements FinancialSystemDocumentService {
    
    /**
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#findByDocumentHeaderStatusCode(java.lang.Class, java.lang.String)
     */
    public Collection findByDocumentHeaderStatusCode(Class clazz, String statusCode) throws WorkflowException {
        Collection foundDocuments = ((FinancialSystemDocumentDao) documentDao).findByDocumentHeaderStatusCode(clazz, statusCode);
        Collection returnDocuments = new ArrayList();
        for (Iterator iter = foundDocuments.iterator(); iter.hasNext();) {
            Document doc = (Document) iter.next();
            returnDocuments.add(getByDocumentHeaderId(doc.getDocumentNumber()));
        }
        return returnDocuments;
    }

    /**
     * @see org.kuali.core.service.impl.DocumentServiceImpl#setDocumentDao(org.kuali.core.dao.DocumentDao)
     */
    @Override
    public void setDocumentDao(DocumentDao documentDao) {
        if (!FinancialSystemDocumentDao.class.isAssignableFrom(documentDao.getClass())) {
            throw new IllegalArgumentException("document dao class being used '" + documentDao.getClass() + "' is not sub class or interface of '" + FinancialSystemDocumentDao.class + "'");
        }
        super.setDocumentDao(documentDao);
    }
    
}
