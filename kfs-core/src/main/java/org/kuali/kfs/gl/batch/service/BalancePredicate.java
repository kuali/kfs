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

import org.kuali.kfs.gl.businessobject.Balance;

/**
 * This class represents business logic, specifically to decide whether a balance should be selected by a FilteringBalanceIterator
 * or not.
 * 
 * @see org.kuali.kfs.gl.batch.service.FilteringBalanceIterator
 */
public interface BalancePredicate {

    /**
     * Should the given balance be selected to be processed?
     * 
     * @param balance a balance to check for selection
     * @return true if the balance should be selected for processing, false if not
     */
    public boolean select(Balance balance);
}
