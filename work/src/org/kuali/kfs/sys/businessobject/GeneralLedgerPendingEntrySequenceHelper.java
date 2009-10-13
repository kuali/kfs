/*
 * Copyright 2005-2008 The Kuali Foundation
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
