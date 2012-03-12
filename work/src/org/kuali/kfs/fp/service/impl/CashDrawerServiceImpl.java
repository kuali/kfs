/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;


/**
 * This is the default implementation of the CashDrawerService interface.
 */
@Transactional
public class CashDrawerServiceImpl implements CashDrawerService {
    private BusinessObjectService businessObjectService;
    private static final String CAMPUS_CODE_PROPERTY = "campusCode";


    /**
     * Retrieves the CashDrawer associated with the campus code provided and sets the state of the drawer to closed.
     * 
     * @param campusCode The code of the campus associated with the cash drawer being retrieved.
     * @see org.kuali.kfs.fp.service.CashDrawerService#closeCashDrawer(java.lang.String)
     */
    public void closeCashDrawer(String campusCode) {
        CashDrawer drawer = getByCampusCode(campusCode);
        this.closeCashDrawer(drawer);
    }
    
    /**
     * Sets the status of the drawer provided to closed and saves the new state.
     * 
     * @param drawer The instance of the cash drawer to be closed.
     * @see org.kuali.kfs.fp.service.CashDrawerService#closeCashDrawer(org.kuali.kfs.fp.businessobject.CashDrawer)
     */
    public void closeCashDrawer(CashDrawer drawer) {
        drawer.setStatusCode(KFSConstants.CashDrawerConstants.STATUS_CLOSED);
        drawer.setReferenceFinancialDocumentNumber(null);

        save(drawer);
    }

    /**
     * Retrieves an instance of a cash drawer based on the parameters provided and sets the status of the drawer to open, 
     * persists the state change and then returns an instance of the drawer in it's new state.
     * 
     * @param campusCode The campus code associated with the cash drawer we wish to retrieve and open.
     * @param documentId The id of the reference document linked to the drawer.
     * @return  A new instance of the cash drawer in open status.
     * 
     * @see org.kuali.kfs.fp.service.CashDrawerService#openCashDrawer(java.lang.String, java.lang.String)
     */
    public CashDrawer openCashDrawer(String campusCode, String documentId) {
        if (StringUtils.isBlank(documentId)) {
            throw new IllegalArgumentException("invalid (blank) documentId");
        }

        CashDrawer drawer = getByCampusCode(campusCode);
        return this.openCashDrawer(drawer, documentId);
    }
    
    /**
     * Sets the status of the cash drawer provided to open, persists this new state and returns the drawer.
     * 
     * @param drawer An instance of the drawer being opened.
     * @param documentId The id of the reference document linked to the drawer.
     * @return An instance of the cash drawer with a status of open.
     * 
     * @see org.kuali.kfs.fp.service.CashDrawerService#openCashDrawer(org.kuali.kfs.fp.businessobject.CashDrawer, java.lang.String)
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
     * Retrieves a cash drawer using the campus code provided, updates the state to locked, then persists this state change.
     * 
     * @param campusCode The campus code associated with the cash drawer.
     * @param documentId The reference document id to be set to the cash drawer.
     * 
     * @see org.kuali.kfs.fp.service.CashDrawerService#lockCashDrawer(java.lang.String,java.lang.String)
     */
    public void lockCashDrawer(String campusCode, String documentId) {
        if (StringUtils.isBlank(documentId)) {
            throw new IllegalArgumentException("invalid (blank) documentId");
        }

        CashDrawer drawer = getByCampusCode(campusCode);
        this.lockCashDrawer(drawer, documentId);
    }
    
    /**
     * Sets the state of the cash drawer provided to locked and persists this new state.
     * 
     * @param drawer The cash drawer to be locked.
     * @param documentId The reference document id to be set to the cash drawer.
     * 
     * @see org.kuali.kfs.fp.service.CashDrawerService#lockCashDrawer(org.kuali.kfs.fp.businessobject.CashDrawer, java.lang.String)
     */
    public void lockCashDrawer(CashDrawer drawer, String documentId) {
        if (StringUtils.isBlank(documentId)) {
            throw new IllegalArgumentException("invalid (blank) documentId");
        }
        
        if (!StringUtils.equals(KFSConstants.CashDrawerConstants.STATUS_OPEN, drawer.getStatusCode())) {
            throw new IllegalStateException("CashDrawer '" + drawer.getCampusCode() + "' cannot be locked because it is not open");
        }
        if (!StringUtils.equals(documentId, drawer.getReferenceFinancialDocumentNumber())) {
            throw new IllegalStateException("CashDrawer '" + drawer.getCampusCode() + "' cannot be locked because it was opened by document " + drawer.getReferenceFinancialDocumentNumber());
        }

        drawer.setStatusCode(KFSConstants.CashDrawerConstants.STATUS_LOCKED);
        drawer.setReferenceFinancialDocumentNumber(documentId);

        save(drawer);
    }

    /**
     * Retrieves a cash drawer using the campus code provided, updates the state to open, then persists this state change.
     * 
     * @param campusCode The campus code associated with the cash drawer.
     * @param documentId The reference document id to be set to the cash drawer.
     * 
     * @see org.kuali.kfs.fp.service.CashDrawerService#unlockCashDrawer(java.lang.String,java.lang.String)
     */
    public void unlockCashDrawer(String campusCode, String documentId) {
        if (StringUtils.isBlank(documentId)) {
            throw new IllegalArgumentException("invalid (blank) documentId");
        }

        CashDrawer drawer = getByCampusCode(campusCode);
        this.unlockCashDrawer(drawer, documentId);
    }

    /**
     * Sets the state of the cash drawer provided to open and persists this new state.
     * 
     * @param drawer The cash drawer to be unlocked.
     * @param documentId The reference document id to be set to the cash drawer.
     * 
     * @see org.kuali.kfs.fp.service.CashDrawerService#unlockCashDrawer(org.kuali.kfs.fp.businessobject.CashDrawer, java.lang.String)
     */
    public void unlockCashDrawer(CashDrawer drawer, String documentId) {
        if (StringUtils.isBlank(documentId)) {
            throw new IllegalArgumentException("invalid (blank) documentId");
        }

        if (!StringUtils.equals(KFSConstants.CashDrawerConstants.STATUS_LOCKED, drawer.getStatusCode())) {
            throw new IllegalStateException("CashDrawer '" + drawer.getCampusCode() + "' cannot be unlocked because it is not locked");
        }
        if (!StringUtils.equals(documentId, drawer.getReferenceFinancialDocumentNumber())) {
            throw new IllegalStateException("CashDrawer '" + drawer.getCampusCode() + "' cannot be unlocked because it was locked by document " + drawer.getReferenceFinancialDocumentNumber());
        }

        drawer.setStatusCode(KFSConstants.CashDrawerConstants.STATUS_OPEN);
        drawer.setReferenceFinancialDocumentNumber(documentId);

        save(drawer);
    }

    /**
     * This method retrieves a cash drawer instance using the campus code provided as a search parameter.  If no drawer can
     * be found for the campus provided and the autocreate flag is set to true, then a new instance of a cash drawer will
     * be generated and returned.  If the autocreate flag is false, then a null value will be returned.
     * 
     * NOTE: The new instance created if autocreate is set to true is an unpersisted instance.
     * 
     * @param campusCode The campus code used to retrieve the cash drawer.
     * @return An instance of a cash drawer matching the value provided.
     * 
     * @see org.kuali.kfs.fp.service.CashDrawerService#findByCampusCode(java.lang.String)
     */
    public CashDrawer getByCampusCode(String campusCode) {
        if (StringUtils.isBlank(campusCode)) {
            throw new IllegalArgumentException("invalid (blank) campusCode");
        }

        return (CashDrawer)businessObjectService.findByPrimaryKey(CashDrawer.class, buildPrimaryKeyMap(campusCode));
    }


    /**
     * Persists the given CashDrawer instance.
     * 
     * @param cashDrawer The cash drawer to be persisted.
     */
    protected void save(CashDrawer cashDrawer) {
        if (cashDrawer == null) {
            throw new IllegalArgumentException("invalid (null) cashDrawer");
        }

        businessObjectService.save(cashDrawer);
    }

    /**
     * This method creates a primary key map by adding the associated campus code to a new map instance and returning 
     * this map new instance.
     * 
     * @param campusCode The campus code to be added to the map.
     * @return Map suitable for use with primaryKey-related OJB methods
     */
    protected Map buildPrimaryKeyMap(String campusCode) {
        Map keyMap = new HashMap();
        keyMap.put(CAMPUS_CODE_PROPERTY, campusCode);
        return keyMap;
    }

    /**
     * This method calculates the total of all the coins in the cash drawer.  This is accomplished by totaling the values from
     * each of the *CentAmount() methods (ie. getFinancialDocumentHundredCentAmount()) from the drawer and returning the resulting 
     * value.
     * 
     * @param drawer The drawer being totaled.
     * @return The sum of all the coin amounts in the drawer.
     * 
     * @see org.kuali.kfs.fp.service.CashDrawerService#getCoinTotal(org.kuali.kfs.fp.businessobject.CashDrawer)
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
     * This method calculates the total of all the currency in the cash drawer.  This is accomplished by totaling the values from
     * each of the *DollarAmount() methods (ie. getFinancialDocumentHundredDollarAmount()) from the drawer and returning the resulting 
     * value.
     * 
     * @param drawer The drawer being totaled.
     * @return The sum of all the currency amounts in the drawer.
     * 
     * @see org.kuali.kfs.fp.service.CashDrawerService#getCurrencyTotal(org.kuali.kfs.fp.businessobject.CashDrawer)
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
     * Gets the businessObjectService attribute value.
     * 
     * @return The current value of businessObjectService.
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
