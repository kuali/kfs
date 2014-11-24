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


public interface BudgetConstructionAccountObjectDetailReportDao {


   /**
   * 
   * adds rows with object detail to the temporary table used for budget construction account balance reporting.
   * @param principalName: the id of the user initiating the report
   */
    public void updateReportsAccountObjectDetailTable(String principalName, String expenditureINList, String revenueINList);
    
    /**
     * 
     * adds rows consolidated at the object code level to the temporary table used for budget construction account balance reporting.
     * @param principalName: the id of the user initiating the report
     */
    public void updateReportsAccountObjectConsolidatedTable(String principalName, String expenditureINList, String revenueINList);

    
}

