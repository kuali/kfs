/*
 * Copyright 2008 The Kuali Foundation
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
