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
package org.kuali.kfs.module.ld.service;

import org.kuali.kfs.gl.businessobject.Transaction;

/**
 * This class...
 */
public interface LaborTransactionDescriptionService {

    /**
     * get the transaction description from the description map for the given transaction, where the description map can be defined
     * and injected.
     * 
     * @param transaction the given transaction
     * @return the transaction description indexed by the document type of the given transaction
     */
    public String getTransactionDescription(Transaction transaction);

    /**
     * get the transaction description from the description map with the given key
     * 
     * @param descriptionKey the given key that indexes a description in the description map, where the description map can be
     *        defined and injected.
     * @return the transaction description indexed by the given key
     */
    public String getTransactionDescription(String descriptionKey);
}
