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
package org.kuali.kfs.sys.businessobject;

/**
 * This class is a helper class which will allows us to pass control in and out of the processGeneralLedgerPendingEntry() method by
 * reference. This was necessary since you can't increment an Integer object without breaking reference.
 * 
 * 
 */
public class GeneralLedgerPendingEntrySequenceHelper {
    private int sequenceCounter;

    /**
     * Constructs a GeneralLedgerPendingEntrySequenceHelper.java, initializing the counter to 1.
     */
    public GeneralLedgerPendingEntrySequenceHelper() {
        this.sequenceCounter = 1;
    }

    /**
     * Constructs a GeneralLedgerPendingEntrySequenceHelper.java, initializing the counter to the given int.
     */
    public GeneralLedgerPendingEntrySequenceHelper(int initialCount) {
        this.sequenceCounter = initialCount;
    }

    /**
     * This method retrieves the value of the sequenceCounter attribute.
     * 
     * @return
     */
    public int getSequenceCounter() {
        return sequenceCounter;
    }

    /**
     * This method increments the value of the sequenceCounter attribute.
     */
    public void increment() {
        this.sequenceCounter += 1;
    }

    /**
     * This method decrements the value of the sequenceCounter attribute.
     */
    public void decrement() {
        this.sequenceCounter -= 1;
    }
}
