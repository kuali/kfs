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

package org.kuali.kfs.module.purap.document.service;

import java.util.Collection;

import org.kuali.kfs.module.purap.businessobject.ReceivingAddress;

public interface ReceivingAddressService {

    /**
     * Finds all of the active receiving addresses with the specified chart/org code.
     * 
     * @param chartCode - chart of accounts code.
     * @param orgCode - organization code.
     * @return - collection of receiving addresses found.
     */
    public Collection<ReceivingAddress> findActiveByChartOrg(String chartCode, String orgCode);

    /**
     * Finds all of the active default receiving addresses with the specified chart/org code.
     * When the database is not in consistent state, there could be more than one active default address per chart/org.
     * 
     * @param chartCode - chart of accounts code.
     * @param orgCode - organization code.
     * @return - collection of receiving addresses found.
     */
    public Collection<ReceivingAddress> findDefaultByChartOrg(String chartCode, String orgCode);

    /**
     * Finds the unique active default receiving addresses with the specified chart/org code.
     * When the database is in consistent state, there shall be no more than one active default address per chart/org.
     * 
     * @param chartCode - chart of accounts code.
     * @param orgCode - organization code.
     * @return - receiving addresses found.
     */
    public ReceivingAddress findUniqueDefaultByChartOrg(String chartCode, String orgCode);

    /**
     * Counts the number of the active receiving addresses with the specified chart/org code.
     * 
     * @param chartCode - chart of accounts code.
     * @param orgCode - organization code.
     * @return - number of receiving addresses found.
     */
    public int countActiveByChartOrg(String chartCode, String orgCode);

}

