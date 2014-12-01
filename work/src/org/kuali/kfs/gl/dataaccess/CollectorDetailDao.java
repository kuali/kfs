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
package org.kuali.kfs.gl.dataaccess;

import java.sql.Date;

/**
 * 
 */
public interface CollectorDetailDao {
    /**
     * Purge the table by year/chart
     * 
     * @param chartOfAccountsCode chart of accounts code criteria to purge
     * @param universityFiscalYear university fiscal year criteria to purge
     */
    public void purgeYearByChart(String chartOfAccountsCode, int universityFiscalYear);

    /**
     * Retrieves the DB table name that's mapped to instances of CollectorDetail
     * 
     * @return String representing DB table name for CollectorDetails
     */
    public String retrieveCollectorDetailTableName();
    
    
    public Integer getMaxCreateSequence(Date date);
}
