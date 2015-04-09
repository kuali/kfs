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
package org.kuali.kfs.sys.document.validation.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.businessobject.TaxRegionCounty;
import org.kuali.kfs.sys.businessobject.TaxRegionPostalCode;
import org.kuali.kfs.sys.businessobject.TaxRegionRate;
import org.kuali.kfs.sys.businessobject.TaxRegionState;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.county.County;
import org.kuali.rice.location.api.county.CountyService;
import org.kuali.rice.location.api.postalcode.PostalCode;
import org.kuali.rice.location.api.postalcode.PostalCodeService;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.api.state.StateService;

/**
 * This class implements add collection line business rule for tax district rate.
 */
public class TaxRegionRule extends KfsMaintenanceDocumentRuleBase {

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {

        boolean success = true;
        if (KFSConstants.TaxRegionConstants.TAX_REGION_RATES.equals(collectionName)) {
            success &= isValidTaxRegionRate((TaxRegionRate) bo, null);
        }
        else if (KFSConstants.TaxRegionConstants.TAX_REGION_STATES.equals(collectionName)) {
            success &= isValidTaxRegionState((TaxRegionState) bo);
        }
        else if (KFSConstants.TaxRegionConstants.TAX_REGION_COUNTIES.equals(collectionName)) {
            success &= isValidTaxRegionCounty((TaxRegionCounty) bo);
        }
        else if (KFSConstants.TaxRegionConstants.TAX_REGION_POSTAL_CODES.equals(collectionName)) {
            success &= isValidTaxRegionPostalCode((TaxRegionPostalCode) bo);
        }

        return success;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    protected boolean isValidTaxRegionRate(TaxRegionRate taxRegionRate, TaxRegion taxRegion) {

        boolean success = true;
        if (ObjectUtils.isNotNull(taxRegionRate)) {
            success &= isValidEffectiveDate(taxRegionRate);
            success &= isValidTaxRate(taxRegionRate);
        }

        return success;
    }


    /**
     * This method returns true if the effective date is not a date in the past or today's date.
     *
     * @param taxRegionRate
     * @return
     */
    protected boolean isValidEffectiveDate(TaxRegionRate taxRegionRate) {
        boolean success = true;
        if (taxRegionRate.getEffectiveDate() != null) {
            Date currentDate = SpringContext.getBean(DateTimeService.class).getCurrentDate();
            int comparison = taxRegionRate.getEffectiveDate().compareTo(currentDate);
            if (comparison == 0 || comparison < 0) {
                GlobalVariables.getMessageMap().putError(KFSConstants.TaxRegionConstants.TAX_REGION_EFFECTIVE_DATE, KFSKeyConstants.ERROR_DOCUMENT_TAX_REGION_CANT_ADD_PAST_OR_CURRENT_DATE_FOR_TAX_DISTRICT);
                success = false;
            }
        }
        return success;
    }

    /**
     * This method returns true if the tax rate is between 0 and 1.
     *
     * @param taxRegionRate
     * @return
     */
    protected boolean isValidTaxRate(TaxRegionRate taxRegionRate) {
        boolean success = true;
        if (taxRegionRate.getTaxRate() != null) {
            if (taxRegionRate.getTaxRate().intValue() > 1 || taxRegionRate.getTaxRate().intValue() < 0) {
                GlobalVariables.getMessageMap().putError(KFSConstants.TaxRegionConstants.TAX_REGION_TAX_RATE, KFSKeyConstants.ERROR_DOCUMENT_TAX_REGION_TAX_RATE_BETWEEN0AND1);
                success = false;
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
    protected boolean isValidTaxRegionState(TaxRegionState taxRegionState) {
        boolean success = true;

        if ( StringUtils.isNotBlank(taxRegionState.getPostalCountryCode()) && StringUtils.isNotBlank(taxRegionState.getStateCode()) ) {
            State state = SpringContext.getBean(StateService.class).getState(taxRegionState.getPostalCountryCode(),taxRegionState.getStateCode());
            if (ObjectUtils.isNull(state) || !state.isActive()) {
                GlobalVariables.getMessageMap().putError(KFSConstants.TaxRegionConstants.TAX_REGION_STATE_CODE, KFSKeyConstants.ERROR_DOCUMENT_TAX_REGION_INVALID_STATE, taxRegionState.getStateCode());
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
    protected boolean isValidTaxRegionCounty(TaxRegionCounty taxRegionCounty) {
        boolean success = true;

        if ( StringUtils.isNotBlank(taxRegionCounty.getPostalCountryCode()) && StringUtils.isNotBlank(taxRegionCounty.getStateCode()) && StringUtils.isNotBlank(taxRegionCounty.getCountyCode()) ) {
            County county = SpringContext.getBean(CountyService.class).getCounty(taxRegionCounty.getPostalCountryCode(),taxRegionCounty.getStateCode(), taxRegionCounty.getCountyCode());
            if (ObjectUtils.isNull(county) || !county.isActive()) {
                GlobalVariables.getMessageMap().putError(KFSConstants.TaxRegionConstants.TAX_REGION_COUNTY_CODE, KFSKeyConstants.ERROR_DOCUMENT_TAX_REGION_INVALID_COUNTY, new String[] { taxRegionCounty.getCountyCode(), taxRegionCounty.getStateCode() });
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
    protected boolean isValidTaxRegionPostalCode(TaxRegionPostalCode taxRegionPostalCode) {
        boolean success = true;

        if ( StringUtils.isNotBlank(taxRegionPostalCode.getPostalCountryCode()) && StringUtils.isNotBlank(taxRegionPostalCode.getPostalCode()) ) {

            PostalCode postalZipCode = SpringContext.getBean(PostalCodeService.class).getPostalCode( taxRegionPostalCode.getPostalCountryCode(), taxRegionPostalCode.getPostalCode() );
            if (ObjectUtils.isNull(postalZipCode) || !postalZipCode.isActive()) {
                GlobalVariables.getMessageMap().putError(KFSConstants.TaxRegionConstants.TAX_REGION_POSTAL_CODE, KFSKeyConstants.ERROR_DOCUMENT_TAX_REGION_INVALID_POSTAL_CODE, taxRegionPostalCode.getPostalCode());
                success = false;
            }
        }

        return success;
    }
}
