/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
    
    public CorrectionChangeGroup findByDocumentNumberAndCorrectionChangeGroupNumber (Integer docId, int i){
        
        return correctionChangeGroupDao.findByDocumentNumberAndCorrectionChangeGroupNumber(docId, i);
    }
    
    public List findByDocumentHeaderIdAndCorrectionGroupNumber(Integer docId, int i){
        
        return correctionChangeDao.findByDocumentHeaderIdAndCorrectionGroupNumber(docId, i);
    }
    public List findByDocumentNumberAndCorrectionGroupNumber(Integer docId, int i){
        
        return correctionCriteriaDao.findByDocumentNumberAndCorrectionGroupNumber(docId, i);
    }
    
    public CorrectionDocument findByCorrectionDocumentHeaderId(String docId){
        
        return (CorrectionDocument) documentDao.findByDocumentHeaderId(CorrectionDocument.class, docId);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public CorrectionChangeDao getCorrectionChangeDao() {
        return correctionChangeDao;
    }
    public void setCorrectionChangeDao(CorrectionChangeDao correctionChangeDao) {
        this.correctionChangeDao = correctionChangeDao;
    }
    public CorrectionChangeGroupDao getCorrectionChangeGroupDao() {
        return correctionChangeGroupDao;
    }
    public void setCorrectionChangeGroupDao(CorrectionChangeGroupDao correctionChangeGroupDao) {
        this.correctionChangeGroupDao = correctionChangeGroupDao;
    }
    public CorrectionCriteriaDao getCorrectionCriteriaDao() {
        return correctionCriteriaDao;
    }
    public void setCorrectionCriteriaDao(CorrectionCriteriaDao correctionCriteriaDao) {
        this.correctionCriteriaDao = correctionCriteriaDao;
    }

    public DocumentDao getDocumentDao() {
        return documentDao;
    }

    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }
    
    
    
}
