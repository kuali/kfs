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
package org.kuali.kfs.pdp.service;

import org.kuali.kfs.pdp.businessobject.ACHBank;

public interface AchBankService {

    /**
     * Read the bank data from a text file and reset table to match this file. The format of the file comes from here:
     * https://www.fededirectory.frb.org/format_ACH.cfm https://www.fededirectory.frb.org/FedACHdir.txt
     * 
     * @param filename
     */
    public boolean reloadTable(String filename);
    
    /**
     * Returns the AchBank for the given routing number
     * 
     * @param bankRoutingNumber pk for AchBank
     * @return AchBank
     */
    public ACHBank getByPrimaryId(String bankRoutingNumber);
}
