/*
 * Copyright (c) 2005, 2006 The National Association of College and University Business Officers,
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
package org.kuali.module.cg.service.impl;

import org.kuali.module.cg.service.ProjectDirectorService;
import org.kuali.module.cg.service.SubcontractorService;
import org.kuali.module.cg.bo.ProjectDirector;
import org.kuali.module.cg.bo.Subcontractor;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.spring.Cached;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.HashMap;

/**
 * Implementation of the Subcontractor service.
 */
@Transactional
public class SubcontractorServiceImpl implements SubcontractorService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.cg.service.SubcontractorService#getByPrimaryId(String)
     */
    @Cached
    public Subcontractor getByPrimaryId(String subcontractorNumber) {
        return (Subcontractor) businessObjectService.findByPrimaryKey(Subcontractor.class, mapPrimaryKeys(subcontractorNumber));
    }

    private Map<String, Object> mapPrimaryKeys(String subcontractorNumber) {
        Map<String, Object> primaryKeys = new HashMap();
        primaryKeys.put(KFSPropertyConstants.SUBCONTRACTOR_NUMBER, subcontractorNumber.trim());
        return primaryKeys;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}