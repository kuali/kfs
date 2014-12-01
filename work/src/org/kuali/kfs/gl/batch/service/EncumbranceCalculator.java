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
