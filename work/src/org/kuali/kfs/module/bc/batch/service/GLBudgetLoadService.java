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
package org.kuali.kfs.module.bc.batch.service;

public interface GLBudgetLoadService {
    
//
//  loads pending budget construction GL (replacing any corresponding GL rows which already exist)
//  overloaded methods are provided so that one can load only pending GL rows for a specific fiscal year key
//  (if there happens to be more than one fiscal year key in the pending budget construction GL table) or
//  for the coming fiscal year.    
//
//
//  load pending budget construction GL for a specific fiscal year
    public void loadPendingBudgetConstructionGeneralLedger(Integer FiscalYear);
//
//  load for the fiscal year following the fiscal year of the current date
    public void loadPendingBudgetConstructionGeneralLedger();

}
