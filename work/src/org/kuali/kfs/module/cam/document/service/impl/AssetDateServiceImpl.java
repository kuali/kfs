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
package org.kuali.module.cams.service.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetDepreciationConvention;
import org.kuali.module.cams.bo.AssetType;
import org.kuali.module.cams.service.AssetDateService;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.financial.service.UniversityDateService;


public class AssetDateServiceImpl implements AssetDateService {

    AssetService assetService;
    UniversityDateService universityDateService;

    /**
     * @see org.kuali.module.cams.service.AssetDetailInformationService#checkAndUpdateLastInventoryDate(org.kuali.module.cams.bo.Asset,
     *      org.kuali.module.cams.bo.Asset)
     */
    public void checkAndUpdateLastInventoryDate(Asset oldAsset, Asset newAsset) {
        if (!StringUtils.equalsIgnoreCase(oldAsset.getCampusCode(), newAsset.getCampusCode()) || !StringUtils.equalsIgnoreCase(oldAsset.getBuildingCode(), newAsset.getBuildingCode()) || !StringUtils.equalsIgnoreCase(oldAsset.getBuildingRoomNumber(), newAsset.getBuildingRoomNumber()) || !StringUtils.equalsIgnoreCase(oldAsset.getBuildingSubRoomNumber(), newAsset.getBuildingSubRoomNumber()) || !StringUtils.equalsIgnoreCase(oldAsset.getCampusTagNumber(), newAsset.getCampusTagNumber())) {
            newAsset.setLastInventoryDate(new Timestamp(new Date().getTime()));
        }
    }

    /**
     * @see org.kuali.module.cams.service.AssetDetailInformationService#checkAndUpdateDepreciationDate(org.kuali.module.cams.bo.Asset,
     *      org.kuali.module.cams.bo.Asset)
     */
    public void checkAndUpdateDepreciationDate(Asset oldAsset, Asset newAsset) {
        if (assetService.isAssetTypeCodeChanged(oldAsset, newAsset) && assetService.isAssetDepreciableLifeLimitZero(newAsset)) {
            // If Asset Type changed to Depreciable Life Limit to "0", set both In-service Date and Depreciation Date to NULL.
            newAsset.setDepreciationDate(null);
            newAsset.setCapitalAssetInServiceDate(null);
        }
        else if (assetService.isInServiceDateChanged(oldAsset, newAsset) || assetService.isFinancialObjectSubTypeCodeChanged(oldAsset, newAsset)) {
            newAsset.setDepreciationDate(computeDepreciationDate(newAsset.getCapitalAssetType(), newAsset.getAssetDepreciationConvention(), newAsset.getCapitalAssetInServiceDate()));
        }
    }


    /**
     * @see org.kuali.module.cams.service.AssetDateService#computeDepreciationDate(org.kuali.module.cams.bo.AssetType,
     *      org.kuali.module.cams.bo.AssetDepreciationConvention, java.sql.Date)
     */
    public java.sql.Date computeDepreciationDate(AssetType assetType, AssetDepreciationConvention depreciationConvention, java.sql.Date inServiceDate) {
        java.sql.Date depreciationDate = null;
        if (assetType.getDepreciableLifeLimit().intValue() != 0) {
            if (depreciationConvention == null || CamsConstants.DepreciationConvention.CREATE_DATE.equalsIgnoreCase(depreciationConvention.getDepreciationConventionCode())) {
                depreciationDate = inServiceDate;
            }
            else {
                Integer fiscalYear = universityDateService.getFiscalYear(inServiceDate);
                if (fiscalYear == null) {
                    throw new ValidationException("University Fiscal year is not defined for date - " + inServiceDate);
                }

                java.sql.Date newInServiceFiscalYearStartDate = new java.sql.Date(universityDateService.getFirstDateOfFiscalYear(fiscalYear).getTime());
                String conventionCode = depreciationConvention.getDepreciationConventionCode();

                if (CamsConstants.DepreciationConvention.FULL_YEAR.equalsIgnoreCase(conventionCode)) {
                    depreciationDate = newInServiceFiscalYearStartDate;
                }
                else if (CamsConstants.DepreciationConvention.HALF_YEAR.equalsIgnoreCase(conventionCode)) {
                    // Half year depreciation convention
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(newInServiceFiscalYearStartDate);
                    calendar.add(Calendar.MONTH, 6);
                    depreciationDate = new java.sql.Date(calendar.getTimeInMillis());
                }

            }
        }
        return depreciationDate;
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }


}