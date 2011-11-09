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
package org.kuali.kfs.fp.service;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This service interface defines methods that a CashDrawerService implementation must provide.
 * 
 */
public interface CashDrawerService {
    /**
     * Closes the CashDrawer instance associated with the given campus code, creating one if necessary.
     * 
     * @param campusCode The campus code used to retrieve the cash drawer to be closed.
     */
    public void closeCashDrawer(String campusCode);

    /**
     * Closes the cash drawer associated with the given document
     * 
     * @param cd The cash drawer to closed.
     */
    public void closeCashDrawer(CashDrawer cd);

    /**
     * 
     * Opens the CashDrawer instance associated with the given campus, creating one if necessary. Records the given
     * documentId as the document which opened the cashdrawer.
     * 
     * @param campusCode The campus code to be used to retrieve the cash drawer to be closed.
     * @param documentId The id of the document used to open the cash drawer.
     * @return The opened version of the cash drawer.
     */
    public CashDrawer openCashDrawer(String campusCode, String documentId);
    
    /**
     * Opens the given cash drawer
     * 
     * @param cd The cash drawer to open
     * @param documentId the document number which is opening the cash drawer
     * @return The opened version of the cash drawer
     */
    public CashDrawer openCashDrawer(CashDrawer cd, String documentId);

    /**
     * Locks the currently-open CashDrawer instance associated with the given campus, throwing an IllegalStateException if
     * that cashDrawer is not open (i.e. is closed or locked). Records the given documentId as the document which locked the
     * cashDrawer.
     * 
     * @param campusCode The campus code used to retrieve the cash drawer to be locked.
     * @param documentId The id of the document used to lock the cash drawer.
     */
    public void lockCashDrawer(String campusCode, String documentId);
    
    /**
     * Locks the given cash drawer, if it is open
     * 
     * @param cd The cash drawer to open
     * @param documentId The document id which is locking the cash drawer
     */
    public void lockCashDrawer(CashDrawer cd, String documentId);

    /**
     * Unlocks the currently-locked CashDrawer instance associated with the given campus code, 
     * throwing an IllegalStateException if that cashDrawer is not locked (i.e. is closed or open). 
     * Records the given documentId as the document which unlocked the cashDrawer.
     * 
     * @param campusCode The campus code used to retrieve the cash drawer to be unlocked.
     * @param documentId The id of the document used to unlock the cash drawer.
     */
    public void unlockCashDrawer(String campusCode, String documentId);
    
    /**
     * Unlocks the given cash drawer, if it is open and locked
     * 
     * @param cd The cash drawer to unlock
     * @param documentId The document which is unlocking the cash drawer
     */
    public void unlockCashDrawer(CashDrawer cd, String documentId);

    /**
     * Retrieves the CashDrawer instance associated with the given campus code, if any. If autocreate is true, 
     * and no CashDrawer for the given campus exists, getByCampusCode will return a newly-created 
     * (non-persisted) CashDrawer instance.
     * 
     * @param campusCode The campus code used to retrieve the cash drawer.
     * @return CashDrawer instance or null
     */
    public CashDrawer getByCampusCode(String campusCode);
    
    /**
     * Calculates the total amount of all the currency in the drawer.  
     * NOTE: The value returned only refers to paper currency in the drawer and does not include coin amounts.
     * 
     * @param drawer The cash drawer to calculate the currency total from.
     * @return The summed amount of currency in the cash drawer.
     */
    public KualiDecimal getCurrencyTotal(CashDrawer drawer);
    
    /**
     * Calculates the total amount of all the coins in the drawer. 
     * 
     * @param drawer The drawer to calculate the coin total from.
     * @return The summed value of coins in the drawer.
     */
    public KualiDecimal getCoinTotal(CashDrawer drawer);
}
