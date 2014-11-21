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
package org.kuali.kfs.module.bc.document.dataaccess;

import java.util.List;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionRequestMove;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Facilates Budget Construction Import requests
 * 
 */
public interface ImportRequestDao {
    
    /**
     * 
     * @return header record or null if record does not exist.
     */
    public BudgetConstructionHeader getHeaderRecord(BudgetConstructionRequestMove record, Integer budgetYear);
    
    /**
     * find all BudgetConstructionRequestMove records with null error codes
     * 
     * @return List<BudgetConstructionRequestMove>
     */
    public List<BudgetConstructionRequestMove> findAllNonErrorCodeRecords(String principalId);
    
    /**
     * Save or update business object based on isUpdate
     * 
     * @param businessObject
     * @param isUpadate
     */
    public void save(BusinessObject businessObject, boolean isUpdate);
   
}

