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
package org.kuali.kfs.module.cam.document.service.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetDepreciationConvention;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.module.cam.document.service.AssetDateService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;


public class AssetDateServiceImpl implements AssetDateService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetDateService.class);

    private AssetService assetService;
    private UniversityDateService universityDateService;
    private DateTimeService dateTimeService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetDateService#checkAndUpdateFiscalYearAndPeriod(org.kuali.kfs.module.cam.businessobject.Asset,
     *      org.kuali.kfs.module.cam.businessobject.Asset)
     */
    @Override
    public void checkAndUpdateFiscalYearAndPeriod(Asset oldAsset, Asset newAsset) {
        if (assetService.isInServiceDateChanged(oldAsset, newAsset)) {
            Integer newPostingYear = null;
            String newPostingPeriodCode = null;

            if (ObjectUtils.isNotNull(newAsset.getCapitalAssetInServiceDate())) {
                Map<String, Object> primaryKeys = new HashMap<String, Object>();
                primaryKeys.put(KFSPropertyConstants.UNIVERSITY_DATE, newAsset.getCapitalAssetInServiceDate());
                UniversityDate inServiceDate = businessObjectService.findByPrimaryKey(UniversityDate.class, primaryKeys);

                if (ObjectUtils.isNotNull(inServiceDate)) {
                    newPostingYear = inServiceDate.getUniversityFiscalYear();
                    newPostingPeriodCode = inServiceDate.getUniversityFiscalAccountingPeriod();
                }
            }
            // if the user blank in-service date, posting year and period code will be blank also.
            newAsset.setFinancialDocumentPostingYear(newPostingYear);
            newAsset.setFinancialDocumentPostingPeriodCode(newPostingPeriodCode);
        }
    }

    /**
     * @see org.kuali.module.cams.service.AssetDetailInformationService#checkAndUpdateLastInventoryDate(org.kuali.kfs.module.cam.businessobject.Asset,
     *      org.kuali.kfs.module.cam.businessobject.Asset)
     */
    @Override
    public void checkAndUpdateLastInventoryDate(Asset oldAsset, Asset newAsset) {
        if (!StringUtils.equalsIgnoreCase(oldAsset.getCampusCode(), newAsset.getCampusCode()) || !StringUtils.equalsIgnoreCase(oldAsset.getBuildingCode(), newAsset.getBuildingCode()) || !StringUtils.equalsIgnoreCase(oldAsset.getBuildingRoomNumber(), newAsset.getBuildingRoomNumber()) || !StringUtils.equalsIgnoreCase(oldAsset.getBuildingSubRoomNumber(), newAsset.getBuildingSubRoomNumber()) || !StringUtils.equalsIgnoreCase(oldAsset.getCampusTagNumber(), newAsset.getCampusTagNumber())) {
            Timestamp timestamp = new Timestamp(dateTimeService.getCurrentDate().getTime());
            newAsset.setLastInventoryDate(timestamp);
        }
    }

    /**
     * @see org.kuali.module.cams.service.AssetDetailInformationService#checkAndUpdateDepreciationDate(org.kuali.kfs.module.cam.businessobject.Asset,
     *      org.kuali.kfs.module.cam.businessobject.Asset)
     */
    @Override
    public void checkAndUpdateDepreciationDate(Asset oldAsset, Asset newAsset) {
        if (assetService.isAssetTypeCodeChanged(oldAsset, newAsset) && assetService.isAssetDepreciableLifeLimitZero(newAsset)) {
            // If Asset Type changed to Depreciable Life Limit to "0", set both In-service Date and Depreciation Date to NULL.
            newAsset.setDepreciationDate(null);
            newAsset.setCapitalAssetInServiceDate(null);
        }
        else if (assetService.isInServiceDateChanged(oldAsset, newAsset) || assetService.isFinancialObjectSubTypeCodeChanged(oldAsset, newAsset)) {
            Map<String, String> primaryKeys = new HashMap<String, String>();
            primaryKeys.put(CamsPropertyConstants.AssetDepreciationConvention.FINANCIAL_OBJECT_SUB_TYPE_CODE, newAsset.getFinancialObjectSubTypeCode());
            AssetDepreciationConvention depreciationConvention = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(AssetDepreciationConvention.class, primaryKeys);
            newAsset.setDepreciationDate(computeDepreciationDate(newAsset.getCapitalAssetType(), depreciationConvention, newAsset.getCapitalAssetInServiceDate()));
        }
    }


    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetDateService#computeDepreciationDate(org.kuali.kfs.module.cam.businessobject.AssetType,
     *      org.kuali.kfs.module.cam.businessobject.AssetDepreciationConvention, java.sql.Date)
     */
    @Override
    public java.sql.Date computeDepreciationDate(AssetType assetType, AssetDepreciationConvention depreciationConvention, java.sql.Date inServiceDate) {
        java.sql.Date depreciationDate = null;
        if (assetType.getDepreciableLifeLimit() != null && assetType.getDepreciableLifeLimit().intValue() != 0) {
            if (depreciationConvention == null || CamsConstants.DepreciationConvention.CREATE_DATE.equalsIgnoreCase(depreciationConvention.getDepreciationConventionCode())) {
                depreciationDate = inServiceDate;
            }
            else {
                Integer fiscalYear = universityDateService.getFiscalYear(inServiceDate);
                if (fiscalYear == null) {
                    throw new ValidationException("University Fiscal year is not defined for date - " + inServiceDate);
                }

                //Retrieve new in service start date from parameter
                String newInServiceAssetDepreciationStartMMDD = SpringContext.getBean(ParameterService.class).getParameterValueAsString(AssetGlobal.class, CamsConstants.Parameters.NEW_IN_SERVICE_ASSET_DEPRECIATION_START_DATE);
                java.sql.Date newInServiceFiscalYearStartDate = null;
                try {
                    newInServiceFiscalYearStartDate= getDateTimeService().convertToSqlDate(newInServiceAssetDepreciationStartMMDD + "/" + fiscalYear);
                } catch( Exception e ){
                    //issue to construct the new in service fiscal year start date
                    String error = "Unable to construct new in service fiscal year start date - param value: [ " + newInServiceAssetDepreciationStartMMDD + "]";
                    LOG.error(error, e );
                    throw new IllegalArgumentException(error, e);
                }
                String conventionCode = depreciationConvention.getDepreciationConventionCode();

                //kfsmi-8390 fix...
                //when depreciationConventionCode = FY then NEW_IN_SERVICE_ASSET_DEPRECIATION_START_DATE + fiscal year and then
                //subtracting 1 from the year to set the deprection date
                if (CamsConstants.DepreciationConvention.FULL_YEAR.equalsIgnoreCase(conventionCode)) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(newInServiceFiscalYearStartDate);
                    calendar.add(Calendar.YEAR, -1);
                    depreciationDate = new java.sql.Date(calendar.getTimeInMillis());

                  //  depreciationDate = newInServiceFiscalYearStartDate;
                }
              //when depreciationConventionCode = HY
                else if (CamsConstants.DepreciationConvention.HALF_YEAR.equalsIgnoreCase(conventionCode)) {
                    // Half year depreciation convention
                    //start the month and date as "01/01" and concat the fiscal year...
                    java.sql.Date newInServiceFiscalYearStartDateForHalfYear = null;
                    try {
                        newInServiceFiscalYearStartDateForHalfYear= getDateTimeService().convertToSqlDate("01/01/" + fiscalYear);
                    } catch( Exception e ){
                        //issue to construct the new in service fiscal year start date
                        String error = "Unable to construct new in service fiscal year start date";
                        LOG.error(error, e );
                        throw new IllegalArgumentException(error, e);
                    }

                    depreciationDate = newInServiceFiscalYearStartDateForHalfYear;
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

    /**
     * Gets the dateTimeService attribute.
     *
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetDateService#computeDepreciationDateForPeriod13(java.lang.String, java.sql.Date)
     */
    public java.sql.Date computeDepreciationDateForPeriod13(String depreciationConventionCode, java.sql.Date inServiceDate) {
        java.sql.Date depreciationDate = null;
        Integer fiscalYear = getUniversityDateService().getFiscalYear(inServiceDate);
        java.sql.Date fiscalYearStartDate = new java.sql.Date(getUniversityDateService().getFirstDateOfFiscalYear(fiscalYear).getTime());

        if (CamsConstants.DepreciationConvention.CREATE_DATE.equals(depreciationConventionCode)) {
            depreciationDate = inServiceDate;
        }
        else if (CamsConstants.DepreciationConvention.FULL_YEAR.equals(depreciationConventionCode)) {
            depreciationDate = fiscalYearStartDate;
        }
        else if (CamsConstants.DepreciationConvention.HALF_YEAR.equals(depreciationConventionCode)) {
            // Half year depreciation convention
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fiscalYearStartDate);
            calendar.add(Calendar.MONTH, 6);
            depreciationDate = new java.sql.Date(calendar.getTimeInMillis());
        }
        return depreciationDate;
    }

}
