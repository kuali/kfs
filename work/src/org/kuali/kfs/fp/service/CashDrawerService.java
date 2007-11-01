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
package org.kuali.module.financial.service;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.bo.CashDrawer;

/**
 * This interface defines methods that a CashDrawer service implementation must provide.
 */
public interface CashDrawerService {
    /**
     * Closes the CashDrawer instance associated with the given workgroupName, creating one if necessary.
     */
    public void closeCashDrawer(String workgroupName);

    /**
     * Closes the cash drawer associated with the given document
     * 
     * @param cmDoc the cash drawer to close
     */
    public void closeCashDrawer(CashDrawer cd);

    /**
     * Opens the CashDrawer instance associated with the given workgroupName, creating one if necessary. Records the given
     * documentId as the document which opened the cashdrawer.
     * 
     * @return the opened version of the cash drawer
     */
    public CashDrawer openCashDrawer(String workgroupName, String documentId);

    /**
     * Opens the given cash drawer
     * 
     * @param cd the cash drawer to open
     * @param documentId the document number which is opening the cash drawer
     * @return the opened version of the cash drawer
     */
    public CashDrawer openCashDrawer(CashDrawer cd, String documentId);

    /**
     * Locks the currently-open CashDrawer instance associated with the given workgroupName, throwing an IllegalStateException if
     * that cashDrawer is not open (i.e. is closed or locked). Records the given documentId as the document which locked the
     * cashDrawer.
     */
    public void lockCashDrawer(String workgroupName, String documentId);

    /**
     * Locks the given cash drawer, if it is open
     * 
     * @param cd the cash drawer to open
     * @param documentId the document id which is locking the cash drawer
     */
    public void lockCashDrawer(CashDrawer cd, String documentId);

    /**
     * Unlocks the currently-locked CashDrawer instance associated with the given workgroupName, throwing an IllegalStateException
     * if that cashDrawer is not locked (i.e. is closed or open). Records the given documentId as the document which unlocked the
     * cashDrawer.
     */
    public void unlockCashDrawer(String workgroupName, String documentId);

    /**
     * Unlocks the given cash drawer, if it is open and locked
     * 
     * @param cd the cash drawer to unlock
     * @param documentId the document which is unlocking the cash drawer
     */
    public void unlockCashDrawer(CashDrawer cd, String documentId);


    /**
     * Retrieves the CashDrawer instance associated with the given workgroupName, if any. If autocreate is true, and no CashDrawer
     * for the given workgroupName exists, getByWorkgroupName will return a newly-created (non-persisted) CashDrawer instance.
     * 
     * @param workgroupName
     * @param autocreate
     * @return CashDrawer instance or null
     */
    public CashDrawer getByWorkgroupName(String workgroupName, boolean autocreate);

    /**
     * Calculates the total amount of all the currency in the drawer.
     * 
     * @param drawer the drawer to calculate on
     * @return the summed amount of currency in the drawer
     */
    public KualiDecimal getCurrencyTotal(CashDrawer drawer);

    /**
     * Calcuates the total amount of all the coins in the drawer.
     * 
     * @param drawer the drawer to calculate on
     * @return the summed value of coins in the drawer
     */
    public KualiDecimal getCoinTotal(CashDrawer drawer);
}