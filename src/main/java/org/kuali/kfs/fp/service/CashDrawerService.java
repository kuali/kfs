/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
