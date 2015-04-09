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
package org.kuali.kfs.vnd.service;

import org.kuali.kfs.vnd.businessobject.CommodityCode;

/**
 * This interface defines methods that a Commodity Code Service must provide
 */
public interface CommodityCodeService {
    /**
     * Retrieves a commodity code object by its primary key - the purchasing commodity code.
     * 
     * @param  purchasingCommodityCode
     * @return CommodityCode the commodity code object which has the purchasingCommodityCode
     *         in the input parameter to match its the primary key.
     */
    public CommodityCode getByPrimaryId(String purchasingCommodityCode);

    /**
     * Checks whether the commodity code in wildcard form exists.
     * For example, if the wildCardCommodityCode in the input parameter is
     * 100* and the database has commodity code 10023, then this method 
     * will return true. If no matching found then this method returns false.
     * 
     * @param wildCardCommodityCode The string containing wild card character to be queried to the database.
     * 
     * @return boolean true if the wildcardCommodityCode exists in the database.
     */
    public boolean wildCardCommodityCodeExists(String wildCardCommodityCode);
}
