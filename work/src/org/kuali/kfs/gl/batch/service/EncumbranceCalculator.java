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
package org.kuali.kfs.gl.batch.service;

import java.util.Collection;

import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.Transaction;

/**
 * An interface which declares the methods needed to post a transaction against an encumbrance
 */
public interface EncumbranceCalculator {
    /**
     * This method is used by the balance inquiry screens. It will take a list of selected encumbrances and a pending entry. It will
     * return the Encumbrance row that is affected by the transaction.
     * 
     * @param encumbranceList list of Encumbrance objects
     * @param t A transaction
     * @return the matching Encumbrance from the list or null if not applicable
     */
    public Encumbrance findEncumbrance(Collection encumbranceList, Transaction t);

    /**
     * This will update the amounts in an Encumbrance records based on the data in the transaction.
     * 
     * @param t the transaction to compare
     * @param enc An encumbrance to update
     */
    public void updateEncumbrance(Transaction t, Encumbrance enc);
}
