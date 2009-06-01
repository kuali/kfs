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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.LedgerPostingDocument;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentDao;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.impl.DocumentServiceImpl;

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
     * @see org.kuali.rice.kns.service.impl.DocumentServiceImpl#setDocumentDao(org.kuali.rice.kns.dao.DocumentDao)
     */
    @Override
    public void setDocumentDao(DocumentDao documentDao) {
        if (!FinancialSystemDocumentDao.class.isAssignableFrom(documentDao.getClass())) {
            throw new IllegalArgumentException("document dao class being used '" + documentDao.getClass() + "' is not sub class or interface of '" + FinancialSystemDocumentDao.class + "'");
        }
        super.setDocumentDao(documentDao);
    }
}
