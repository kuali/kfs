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
package org.kuali.module.financial.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.service.CashDrawerService;


/**
 * CashDrawerService implementation.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashDrawerServiceImpl implements CashDrawerService {
    private BusinessObjectService businessObjectService;


    /**
     * @see org.kuali.module.financial.service.CashDrawerService#closeCashDrawer(java.lang.String)
     */
    public void closeCashDrawer(String workgroupName) {
        CashDrawer drawer = getByWorkgroupName(workgroupName, true);
        drawer.setStatusCode(Constants.CashDrawerConstants.STATUS_CLOSED);
        drawer.setReferenceFinancialDocumentNumber(null);

        save(drawer);
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#openCashDrawer(java.lang.String,java.lang.String)
     */
    public void openCashDrawer(String workgroupName, String documentId) {
        if (StringUtils.isBlank(documentId)) {
            throw new IllegalArgumentException("invalid (blank) documentId");
        }

        CashDrawer drawer = getByWorkgroupName(workgroupName, true);
        drawer.setStatusCode(Constants.CashDrawerConstants.STATUS_OPEN);
        drawer.setReferenceFinancialDocumentNumber(documentId);

        save(drawer);
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#lockCashDrawer(java.lang.String,java.lang.String)
     */
    public void lockCashDrawer(String workgroupName, String documentId) {
        if (StringUtils.isBlank(documentId)) {
            throw new IllegalArgumentException("invalid (blank) documentId");
        }

        CashDrawer drawer = getByWorkgroupName(workgroupName, true);
        if (!StringUtils.equals(Constants.CashDrawerConstants.STATUS_OPEN, drawer.getStatusCode())) {
            throw new IllegalStateException("CashDrawer '" + workgroupName + "' cannot be locked because it is not open");
        }
        if (!StringUtils.equals(documentId, drawer.getReferenceFinancialDocumentNumber())) {
            throw new IllegalStateException("CashDrawer '" + workgroupName + "' cannot be locked because it was opened by document " + drawer.getReferenceFinancialDocumentNumber());
        }

        drawer.setStatusCode(Constants.CashDrawerConstants.STATUS_LOCKED);
        drawer.setReferenceFinancialDocumentNumber(documentId);

        save(drawer);
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#unlockCashDrawer(java.lang.String,java.lang.String)
     */
    public void unlockCashDrawer(String workgroupName, String documentId) {
        if (StringUtils.isBlank(documentId)) {
            throw new IllegalArgumentException("invalid (blank) documentId");
        }

        CashDrawer drawer = getByWorkgroupName(workgroupName, true);
        if (!StringUtils.equals(Constants.CashDrawerConstants.STATUS_LOCKED, drawer.getStatusCode())) {
            throw new IllegalStateException("CashDrawer '" + workgroupName + "' cannot be unlocked because it is not locked");
        }
        if (!StringUtils.equals(documentId, drawer.getReferenceFinancialDocumentNumber())) {
            throw new IllegalStateException("CashDrawer '" + workgroupName + "' cannot be unlocked because it was locked by document " + drawer.getReferenceFinancialDocumentNumber());
        }

        drawer.setStatusCode(Constants.CashDrawerConstants.STATUS_OPEN);
        drawer.setReferenceFinancialDocumentNumber(documentId);

        save(drawer);
    }


    /**
     * @see org.kuali.module.financial.service.CashDrawerService#findByWorkgroupName(java.lang.String)
     */
    public CashDrawer getByWorkgroupName(String workgroupName, boolean autocreate) {
        if (StringUtils.isBlank(workgroupName)) {
            throw new IllegalArgumentException("invalid (blank) workgroupName");
        }

        CashDrawer cd = (CashDrawer) businessObjectService.findByPrimaryKey(CashDrawer.class, buildPrimaryKeyMap(workgroupName));
        if (autocreate && (cd == null)) {
            cd = newCashDrawer(workgroupName);
        }
        return cd;
    }


    /**
     * Persists the given CashDrawer instance
     * 
     * @param cashDrawer
     */
    private void save(CashDrawer cashDrawer) {
        if (cashDrawer == null) {
            throw new IllegalArgumentException("invalid (null) cashDrawer");
        }

        businessObjectService.save(cashDrawer);
    }

    /**
     * @param workgroupName
     * @return newly-created (unpersisted) CashDrawer instance for the given workgroupName
     */
    private CashDrawer newCashDrawer(String workgroupName) {
        CashDrawer drawer = new CashDrawer();
        drawer.setWorkgroupName(workgroupName);
        drawer.setStatusCode(Constants.CashDrawerConstants.STATUS_CLOSED);

        return drawer;
    }

    /**
     * @param workgroupName
     * @return Map suitable for use with primaryKey-related OJB methods
     */
    private Map buildPrimaryKeyMap(String workgroupName) {
        Map keyMap = new HashMap();
        keyMap.put("WRKGRP_NM", workgroupName);
        return keyMap;
    }


    // Spring injection
    /**
     * @return current value of businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}