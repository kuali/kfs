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
package org.kuali.kfs.module.endow.document.service;

/**
 * This KemidHoldingTaxLotOpenRecordsService class provides a method to test whether a KEMID has open records in Holding Tax Lot:
 * records with values greater or less than zero for the following fields: Holding Units, Holding Cost and Current Accrual.
 */
public interface KemidHoldingTaxLotOpenRecordsService {

    /**
     * Checks if KEMID has open records in in Holding Tax Lot: records with values greater or less than zero for the following
     * fields: Holding Units, Holding Cost and Current Accrual.
     * 
     * @param kemid
     * @return true if it has open records, false otherwise
     */
    public boolean hasKemidHoldingTaxLotOpenRecords(String kemid);

}
