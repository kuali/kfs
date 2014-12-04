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
package org.kuali.kfs.module.purap.service;

import org.kuali.kfs.sys.businessobject.UnitOfMeasure;

/**
 * This interface defines methods that an Item Unit of Measure Service must provide
 */
public interface ItemUnitOfMeasureService {
    /**
     * Retrieves a unit of measure object by its primary key - the item unit of measure code.
     * 
     * @param  itemUnitOfMeasureCode
     * @return UnitOfMeasure the unit of measure object which has the itemUnitOfMeasureCode
     *         in the input parameter to match its the primary key.
     */
    public UnitOfMeasure getByPrimaryId(String itemUnitOfMeasureCode);

}
