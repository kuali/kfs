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

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.cams.bo.AssetObjectCode;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.bo.DepreciableAssets;

public interface DepreciableAssetsDao {
    /**
     * 
     * This method gets a list of assets eligible for depreciation
     * @param fiscalYear
     * @param fiscalMonth
     * @return collection 
     */
    public Collection<AssetPayment> getListOfDepreciableAssets(Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate);
    
    /**
     * 
     * This method updates the asset payment table fields with the calculated depreciation for each asset
     * 
     * @param assetsInDepreciation
     * @param fiscalYear
     * @param fiscalMonth
     */    
    public void updateAssetPayments(String capitalAssetNumber, String paymentSequenceNumber, KualiDecimal transactionAmount, KualiDecimal accumulatedDepreciationAmount, Integer fiscalMonth);
    
    /**
     * 
     * This method generates a report log of the depreciation process 
     * @param beforeDepreciationReport
     * @param documentNumber
     * @param fiscalYear
     * @param fiscalMonth
     * @return
     */
    public List<String[]> checkSum(boolean beforeDepreciationReport, String documentNumber, Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate);


    /**
     * 
     * This method adds the depreciation amount of each month and stores it in the PreviousYearPrimaryDepreciationAmount field then,
     * each depreciation month field is initialized with a value equals to cero
     *   
     * @param fiscalMonth
     */
    public void initializeAssetPayment(Integer fiscalMonth);
    
    /**
     * 
     * This method..
     * @param kcs
     */
    public void setKualiConfigurationService(KualiConfigurationService kcs);    
    
}
