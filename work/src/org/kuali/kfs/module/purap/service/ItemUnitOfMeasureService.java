/*
 * Copyright 2005-2009 The Kuali Foundation
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
