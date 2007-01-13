/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl;

import java.util.List;

import org.kuali.core.dao.DocumentDao;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.dao.CorrectionChangeDao;
import org.kuali.module.gl.dao.CorrectionChangeGroupDao;
import org.kuali.module.gl.dao.CorrectionCriteriaDao;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.service.CorrectionDocumentService;

public class CorrectionDocumentServiceImpl implements CorrectionDocumentService {

    private CorrectionChangeGroupDao correctionChangeGroupDao;
    private CorrectionChangeDao correctionChangeDao;
    private CorrectionCriteriaDao correctionCriteriaDao;
    private DocumentDao documentDao;

    /**
     * 
     * @see org.kuali.module.gl.service.CorrectionDocumentService#findByDocumentNumberAndCorrectionChangeGroupNumber(java.lang.String, int)
     */
    public CorrectionChangeGroup findByDocumentNumberAndCorrectionChangeGroupNumber(String docId, int i) {

        return correctionChangeGroupDao.findByDocumentNumberAndCorrectionChangeGroupNumber(docId, i);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.CorrectionDocumentService#findByDocumentHeaderIdAndCorrectionGroupNumber(java.lang.String, int)
     */
    public List findByDocumentHeaderIdAndCorrectionGroupNumber(String docId, int i) {

        return correctionChangeDao.findByDocumentHeaderIdAndCorrectionGroupNumber(docId, i);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.CorrectionDocumentService#findByDocumentNumberAndCorrectionGroupNumber(java.lang.String, int)
     */
    public List findByDocumentNumberAndCorrectionGroupNumber(String docId, int i) {

        return correctionCriteriaDao.findByDocumentNumberAndCorrectionGroupNumber(docId, i);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.CorrectionDocumentService#findByCorrectionDocumentHeaderId(java.lang.String)
     */
    public CorrectionDocument findByCorrectionDocumentHeaderId(String docId) {

        return (CorrectionDocument) documentDao.findByDocumentHeaderId(CorrectionDocument.class, docId);
    }

    public void setCorrectionChangeDao(CorrectionChangeDao correctionChangeDao) {
        this.correctionChangeDao = correctionChangeDao;
    }

    public void setCorrectionChangeGroupDao(CorrectionChangeGroupDao correctionChangeGroupDao) {
        this.correctionChangeGroupDao = correctionChangeGroupDao;
    }

    public void setCorrectionCriteriaDao(CorrectionCriteriaDao correctionCriteriaDao) {
        this.correctionCriteriaDao = correctionCriteriaDao;
    }

    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }
}
