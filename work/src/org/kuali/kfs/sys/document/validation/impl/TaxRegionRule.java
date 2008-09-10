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
package org.kuali.kfs.sys.document.validation.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.County;
import org.kuali.kfs.sys.businessobject.PostalZipCode;
import org.kuali.kfs.sys.businessobject.State;
import org.kuali.kfs.sys.businessobject.TaxRegionCounty;
import org.kuali.kfs.sys.businessobject.TaxRegionPostalCode;
import org.kuali.kfs.sys.businessobject.TaxRegionRate;
import org.kuali.kfs.sys.businessobject.TaxRegionState;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorKeyConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class implements add collection line business rule for tax district rate.
 */
public class TaxRegionRule extends KfsMaintenanceDocumentRuleBase {

    private BusinessObjectService businessObjectService;

    public TaxRegionRule() {
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {

        boolean success = true;
        if (KFSConstants.TaxRegionConstants.TAX_REGION_RATES.equals(collectionName)) {
            success &= validateTaxRegionRate((TaxRegionRate) bo);
        }
        else if (KFSConstants.TaxRegionConstants.TAX_REGION_STATES.equals(collectionName)) {
            success &= validateTaxRegionState((TaxRegionState) bo);
        }
        else if (KFSConstants.TaxRegionConstants.TAX_REGION_COUNTIES.equals(collectionName)) {
            success &= validateTaxRegionCounty((TaxRegionCounty) bo);
        }
        else if (KFSConstants.TaxRegionConstants.TAX_REGION_POSTAL_CODES.equals(collectionName)) {
            success &= validateTaxRegionPostalCode((TaxRegionPostalCode) bo);
        }

        return success;
    }

    /**
     * Rules: 1) Effective date should be a future date. 2) Tax rate should be a numeric value between 0 and 1 (inclusive).
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    protected boolean validateTaxRegionRate(TaxRegionRate taxRegionRate) {

        boolean success = true;
        if (taxRegionRate != null) {
            if (taxRegionRate.getEffectiveDate() != null) {
                DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
                Date currentDate = dateTimeService.getCurrentDate();
                int comparison = taxRegionRate.getEffectiveDate().compareTo(currentDate);
                if (comparison == 0 || comparison < 0) {
                    GlobalVariables.getErrorMap().putError("effectiveDate", KFSKeyConstants.ERROR_DOCUMENT_TAX_REGION_CANT_ADD_PAST_OR_CURRENT_DATE_FOR_TAX_DISTRICT);
                    success = false;
                }
            }
            if (taxRegionRate.getTaxRate() != null) {
                if (taxRegionRate.getTaxRate().intValue() > 1 || taxRegionRate.getTaxRate().intValue() < 0) {
                    GlobalVariables.getErrorMap().putError("taxRate", KFSKeyConstants.ERROR_DOCUMENT_TAX_REGION_TAX_RATE_BETWEEN0AND1);
                    success = false;
                }
            }
        }

        return success;
    }

    /**
     * This method returns true if the state on tax region state object is valid.
     * 
     * @param taxRegionState
     * @return
     */
    protected boolean validateTaxRegionState(TaxRegionState taxRegionState) {
        boolean success = true;

        if (StringUtils.isNotEmpty(taxRegionState.getStateCode())) {
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put("postalStateCode", taxRegionState.getStateCode());
            State state = (State) businessObjectService.findByPrimaryKey(State.class, criteria);

            if (ObjectUtils.isNull(state) || !state.isActive()) {
                GlobalVariables.getErrorMap().putError(KFSConstants.TaxRegionConstants.TAX_REGION_STATE_CODE, KFSKeyConstants.ERROR_DOCUMENT_TAX_REGION_INVALID_STATE, taxRegionState.getStateCode());
                success = false;
            }
        }

        return success;
    }

    /**
     * This method returns true if the state and county on the tax region county object is valid.
     * 
     * @param taxRegionCounty
     * @return
     */
    protected boolean validateTaxRegionCounty(TaxRegionCounty taxRegionCounty) {
        boolean success = true;

        if (StringUtils.isNotEmpty(taxRegionCounty.getStateCode()) || StringUtils.isNotEmpty(taxRegionCounty.getCountyCode())) {
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put("stateCode", taxRegionCounty.getStateCode());
            criteria.put("countyCode", taxRegionCounty.getCountyCode());
            County county = (County) businessObjectService.findByPrimaryKey(County.class, criteria);

            if (ObjectUtils.isNull(county) || !county.isActive()) {
                GlobalVariables.getErrorMap().putError(KFSConstants.TaxRegionConstants.TAX_REGION_COUNTY_CODE, KFSKeyConstants.ERROR_DOCUMENT_TAX_REGION_INVALID_COUNTY, new String[] { taxRegionCounty.getCountyCode(), taxRegionCounty.getStateCode() });
                success = false;
            }
        }

        return success;
    }

    /**
     * This method returns true if the postal code on the tax region postal code is valid.
     * 
     * @param taxRegionPostalCode
     * @return
     */
    protected boolean validateTaxRegionPostalCode(TaxRegionPostalCode taxRegionPostalCode) {
        boolean success = true;

        if (StringUtils.isNotEmpty(taxRegionPostalCode.getTaxRegionCode())) {
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put("postalZipCode", taxRegionPostalCode.getPostalCode());
            PostalZipCode postalZipCode = (PostalZipCode) businessObjectService.findByPrimaryKey(PostalZipCode.class, criteria);
 
            if (ObjectUtils.isNull(postalZipCode) || !postalZipCode.isActive()) {
                GlobalVariables.getErrorMap().putError(KFSConstants.TaxRegionConstants.TAX_REGION_POSTAL_CODE, KFSKeyConstants.ERROR_DOCUMENT_TAX_REGION_INVALID_POSTAL_CODE, taxRegionPostalCode.getPostalCode());
                success = false;
            }
        }

        return success;
    }
}