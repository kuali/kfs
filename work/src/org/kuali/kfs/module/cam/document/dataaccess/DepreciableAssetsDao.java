/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.cams.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.module.cams.bo.AssetObjectCode;
import org.kuali.module.cams.bo.DepreciableAssets;

public interface DepreciableAssetsDao {
    public Collection<DepreciableAssets> getListOfDepreciableAssets(Integer fiscalYear, Integer fiscalMonth);
    
    public void updateAssetPayments(List<DepreciableAssets> assetsInDepreciation, Integer fiscalYear, Integer fiscalMonth);
    
    public void checkSum(boolean beforeDepreciationReport, String documentNumber, Integer fiscalYear, Integer fiscalMonth);
    
    public List<String[]> getReportLine();
}
