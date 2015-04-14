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
package org.kuali.kfs.module.cam.document.dataaccess;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;

public interface DepreciableAssetsDao {

    /**
     * This method stores in a collection each item the depreciation report will print out
     * 
     * @param beforeDepreciationReport when true will print out some of the items
     * @param documentNumber Document number that will be print out in the report
     * @param fiscalYear fiscal year of the date in depreciation
     * @param fiscalMonth fiscal month of the date in depreciation
     * @param depreciationDate depreciation date 
     * @param depreciationRunDate depreciation date that will be print out
     * @param a Collection of all AssetObjectCodes
     * @param fiscalStartMonth fiscal month
     * @param errorMessage
     * @return
     */
    public List<String[]> generateStatistics(boolean beforeDepreciationReport, List<String> documentNumbers, Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate, String depreciationRunDate, Collection<AssetObjectCode> assetObjectCodes, int fiscalStartMonth, String errorMessage);


 
}
