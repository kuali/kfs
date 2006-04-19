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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.service.CashDrawerService;
import org.kuali.module.financial.service.CashManagementService;


/**
 * Stock CashDrawerService implementation.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashDrawerServiceImpl implements CashDrawerService {
    private CashManagementService cashManagementService;
    private BusinessObjectService businessObjectService;


    /**
     * @see org.kuali.module.financial.service.CashDrawerService#closeCashDrawer(java.lang.String,,java.lang.String)
     */
    public void closeCashDrawer(String workgroupName, String documentId) {
        CashDrawer drawer = getByWorkgroupName(workgroupName);
        if (drawer == null) {
            drawer = newCashDrawer(workgroupName);
        }
        drawer.setStatusCode(Constants.CashDrawerConstants.STATUS_CLOSED);

        save(drawer, documentId);
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#openCashDrawer(java.lang.String,java.lang.String)
     */
    public void openCashDrawer(String workgroupName, String documentId) {
        CashDrawer drawer = getByWorkgroupName(workgroupName);
        if (drawer == null) {
            drawer = newCashDrawer(workgroupName);
        }
        drawer.setStatusCode(Constants.CashDrawerConstants.STATUS_OPEN);

        save(drawer, documentId);
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#findByWorkgroupName(java.lang.String)
     */
    public CashDrawer getByWorkgroupName(String workgroupName) {
        if (StringUtils.isBlank(workgroupName)) {
            throw new IllegalArgumentException("invalid (blank) workgroupName");
        }

        CashDrawer cd = (CashDrawer) businessObjectService.findByPrimaryKey(CashDrawer.class, buildPrimaryKeyMap(workgroupName));
        return cd;
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#save(org.kuali.module.financial.bo.CashDrawer,java.lang.String)
     */
    public CashDrawer save(CashDrawer cashDrawer, String documentId) {
        if (cashDrawer == null) {
            throw new IllegalArgumentException("invalid (null) cashDrawer");
        }
        if (StringUtils.isBlank(documentId)) {
            throw new IllegalArgumentException("invalid (blank) documentId");
        }

        cashDrawer.setFinancialDocumentReferenceNumber(documentId);
        businessObjectService.save(cashDrawer);
        return cashDrawer;
    }


    /**
     * @param workgroupName
     * @return newly-created (unpersisted) CashDrawer instance for the given workgroupName
     */
    private CashDrawer newCashDrawer(String workgroupName) {
        CashDrawer drawer = new CashDrawer();
        drawer.setWorkgroupName(workgroupName);
        drawer.setStatusCode(Constants.CashDrawerConstants.STATUS_OPEN);

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
     * Gets the cashManagementService attribute.
     * 
     * @return Returns the cashManagementService.
     */
    public CashManagementService getCashManagementService() {
        return cashManagementService;
    }

    /**
     * Sets the cashManagementService attribute value.
     * 
     * @param cashManagementService The cashManagementService to set.
     */
    public void setCashManagementService(CashManagementService cashManagementService) {
        this.cashManagementService = cashManagementService;
    }

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