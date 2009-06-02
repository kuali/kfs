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
package org.kuali.kfs.module.cam.document.dataaccess;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KualiDecimal;

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
     * @param capitalAssetNumber
     * @param paymentSequenceNumber
     * @param transactionAmount
     * @param accumulatedDepreciationAmount
     * @param fiscalMonth
     */        
    public void updateAssetPayments(String capitalAssetNumber, String paymentSequenceNumber, KualiDecimal transactionAmount, KualiDecimal accumulatedDepreciationAmount, Integer fiscalMonth);

    /**
     * 
     * This method stores in a collection each item the depreciation report will print out
     * 
     * @param beforeDepreciationReport when true will print out some of the items
     * @param documentNumber Document number that will be print out in the report
     * @param fiscalYear fiscal year of the date in depreciation 
     * @param fiscalMonth fiscal month of the date in depreciation
     * @param depreciationDate depreciation date that will be print out
     * @return
     */
    public List<String[]> generateStatistics(boolean beforeDepreciationReport, String documentNumber, Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate);


    /**
     * 
     * This method adds the depreciation amount of each month and stores it in the PreviousYearPrimaryDepreciationAmount field then,
     * each depreciation month field is initialized with a value equals to zero
     *   
     * @param fiscalMonth
     */
    public void initializeAssetPayment(Integer fiscalMonth) throws Exception ;

    /**
     * 
     * This method retrieves a list of valid asset object codes for a particular fiscal year
     * 
     * @param fiscalYear
     * @return Collection<AssetObjectCode>
     */
    public Collection<AssetObjectCode> getAssetObjectCodes(Integer fiscalYear);
    
    /**
     * 
     * Setter for Kuali Configuration Service
     * @param kcs
     */
    public void setKualiConfigurationService(KualiConfigurationService kcs);
    
    
    /**
     * 
     * updates every asset posting year, posting period and depreciation date. This update will take place
     * when the fiscal period is equals 12 (last fiscal month of the year).
     *  
     * @param fiscalMonth
     * @param fiscalYear
     */
    public void updateAssets(Integer fiscalMonth, Integer fiscalYear);    
}
