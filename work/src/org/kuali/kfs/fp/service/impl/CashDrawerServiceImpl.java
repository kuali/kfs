/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.service.CashDrawerService;
import org.springframework.transaction.annotation.Transactional;


/**
 * CashDrawerService implementation.
 */
@Transactional
public class CashDrawerServiceImpl implements CashDrawerService {
    private BusinessObjectService businessObjectService;


    /**
     * @see org.kuali.module.financial.service.CashDrawerService#closeCashDrawer(java.lang.String)
     */
    public void closeCashDrawer(String workgroupName) {
        CashDrawer drawer = getByWorkgroupName(workgroupName, true);
        this.closeCashDrawer(drawer);
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#closeCashDrawer(org.kuali.module.financial.bo.CashDrawer)
     */
    public void closeCashDrawer(CashDrawer drawer) {
        drawer.setStatusCode(KFSConstants.CashDrawerConstants.STATUS_CLOSED);
        drawer.setReferenceFinancialDocumentNumber(null);

        save(drawer);
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#openCashDrawer(java.lang.String,java.lang.String)
     */
    public CashDrawer openCashDrawer(String workgroupName, String documentId) {
        if (StringUtils.isBlank(documentId)) {
            throw new IllegalArgumentException("invalid (blank) documentId");
        }

        CashDrawer drawer = getByWorkgroupName(workgroupName, true);
        return this.openCashDrawer(drawer, documentId);
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#openCashDrawer(org.kuali.module.financial.bo.CashDrawer,
     *      java.lang.String)
     */
    public CashDrawer openCashDrawer(CashDrawer drawer, String documentId) {
        if (StringUtils.isBlank(documentId)) {
            throw new IllegalArgumentException("invalid (blank) documentId");
        }

        drawer.setStatusCode(KFSConstants.CashDrawerConstants.STATUS_OPEN);
        drawer.setReferenceFinancialDocumentNumber(documentId);

        save(drawer);
        return drawer;
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#lockCashDrawer(java.lang.String,java.lang.String)
     */
    public void lockCashDrawer(String workgroupName, String documentId) {
        if (StringUtils.isBlank(documentId)) {
            throw new IllegalArgumentException("invalid (blank) documentId");
        }

        CashDrawer drawer = getByWorkgroupName(workgroupName, true);
        this.lockCashDrawer(drawer, documentId);
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#lockCashDrawer(org.kuali.module.financial.bo.CashDrawer,
     *      java.lang.String)
     */
    public void lockCashDrawer(CashDrawer drawer, String documentId) {
        if (StringUtils.isBlank(documentId)) {
            throw new IllegalArgumentException("invalid (blank) documentId");
        }

        if (!StringUtils.equals(KFSConstants.CashDrawerConstants.STATUS_OPEN, drawer.getStatusCode())) {
            throw new IllegalStateException("CashDrawer '" + drawer.getWorkgroupName() + "' cannot be locked because it is not open");
        }
        if (!StringUtils.equals(documentId, drawer.getReferenceFinancialDocumentNumber())) {
            throw new IllegalStateException("CashDrawer '" + drawer.getWorkgroupName() + "' cannot be locked because it was opened by document " + drawer.getReferenceFinancialDocumentNumber());
        }

        drawer.setStatusCode(KFSConstants.CashDrawerConstants.STATUS_LOCKED);
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
        this.unlockCashDrawer(drawer, documentId);
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#unlockCashDrawer(org.kuali.module.financial.bo.CashDrawer,
     *      java.lang.String)
     */
    public void unlockCashDrawer(CashDrawer drawer, String documentId) {
        if (StringUtils.isBlank(documentId)) {
            throw new IllegalArgumentException("invalid (blank) documentId");
        }

        if (!StringUtils.equals(KFSConstants.CashDrawerConstants.STATUS_LOCKED, drawer.getStatusCode())) {
            throw new IllegalStateException("CashDrawer '" + drawer.getWorkgroupName() + "' cannot be unlocked because it is not locked");
        }
        if (!StringUtils.equals(documentId, drawer.getReferenceFinancialDocumentNumber())) {
            throw new IllegalStateException("CashDrawer '" + drawer.getWorkgroupName() + "' cannot be unlocked because it was locked by document " + drawer.getReferenceFinancialDocumentNumber());
        }

        drawer.setStatusCode(KFSConstants.CashDrawerConstants.STATUS_OPEN);
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
        drawer.setStatusCode(KFSConstants.CashDrawerConstants.STATUS_CLOSED);

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

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#getCoinTotal(org.kuali.module.financial.bo.CashDrawer)
     */
    public KualiDecimal getCoinTotal(CashDrawer drawer) {
        KualiDecimal sum = new KualiDecimal(0.0);
        if (drawer != null) {
            if (drawer.getFinancialDocumentHundredCentAmount() != null) {
                sum.add(drawer.getFinancialDocumentHundredCentAmount());
            }
            if (drawer.getFinancialDocumentFiftyCentAmount() != null) {
                sum.add(drawer.getFinancialDocumentFiftyCentAmount());
            }
            if (drawer.getFinancialDocumentTwentyFiveCentAmount() != null) {
                sum.add(drawer.getFinancialDocumentTwentyFiveCentAmount());
            }
            if (drawer.getFinancialDocumentTenCentAmount() != null) {
                sum.add(drawer.getFinancialDocumentTenCentAmount());
            }
            if (drawer.getFinancialDocumentFiveCentAmount() != null) {
                sum.add(drawer.getFinancialDocumentFiveCentAmount());
            }
            if (drawer.getFinancialDocumentOneCentAmount() != null) {
                sum.add(drawer.getFinancialDocumentOneCentAmount());
            }
            if (drawer.getFinancialDocumentOtherCentAmount() != null) {
                sum.add(drawer.getFinancialDocumentOtherCentAmount());
            }
        }
        return sum;
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#getCurrencyTotal(org.kuali.module.financial.bo.CashDrawer)
     */
    public KualiDecimal getCurrencyTotal(CashDrawer drawer) {
        KualiDecimal sum = new KualiDecimal(0.0);
        if (drawer != null) {
            if (drawer.getFinancialDocumentHundredDollarAmount() != null) {
                sum.add(drawer.getFinancialDocumentHundredDollarAmount());
            }
            if (drawer.getFinancialDocumentFiftyDollarAmount() != null) {
                sum.add(drawer.getFinancialDocumentFiftyDollarAmount());
            }
            if (drawer.getFinancialDocumentTwentyDollarAmount() != null) {
                sum.add(drawer.getFinancialDocumentTwentyDollarAmount());
            }
            if (drawer.getFinancialDocumentTenDollarAmount() != null) {
                sum.add(drawer.getFinancialDocumentTenDollarAmount());
            }
            if (drawer.getFinancialDocumentFiveDollarAmount() != null) {
                sum.add(drawer.getFinancialDocumentFiveDollarAmount());
            }
            if (drawer.getFinancialDocumentTwoDollarAmount() != null) {
                sum.add(drawer.getFinancialDocumentTwoDollarAmount());
            }
            if (drawer.getFinancialDocumentOneDollarAmount() != null) {
                sum.add(drawer.getFinancialDocumentOneDollarAmount());
            }
            if (drawer.getFinancialDocumentOtherDollarAmount() != null) {
                sum.add(drawer.getFinancialDocumentOtherDollarAmount());
            }
        }
        return sum;
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