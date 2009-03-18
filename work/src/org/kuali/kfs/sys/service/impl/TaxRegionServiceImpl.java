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
package org.kuali.kfs.sys.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.businessobject.TaxRegionCounty;
import org.kuali.kfs.sys.businessobject.TaxRegionPostalCode;
import org.kuali.kfs.sys.businessobject.TaxRegionState;
import org.kuali.kfs.sys.service.TaxRegionService;
import org.kuali.rice.kns.bo.PostalCode;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.PostalCodeService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TaxRegionServiceImpl implements TaxRegionService {

    private BusinessObjectService businessObjectService;
    private PostalCodeService postalCodeService;

    /**
     * @see org.kuali.kfs.sys.service.TaxRegionService#getSalesTaxRegions(java.lang.String)
     */
    public List<TaxRegion> getSalesTaxRegions(String postalCode) {

        List<TaxRegion> salesTaxRegions = new ArrayList<TaxRegion>();

        PostalCode postalCodeObj = postalCodeService.getByPostalCodeInDefaultCountry(postalCode);
        if(ObjectUtils.isNotNull(postalCodeObj)) {
            salesTaxRegions.addAll(getPostalCodeTaxRegions(postalCodeObj.getPostalCode(), postalCodeObj.getPostalCountryCode(), false));
            salesTaxRegions.addAll(getStateTaxRegions(postalCodeObj.getPostalStateCode(), postalCodeObj.getPostalCountryCode(), false));
            salesTaxRegions.addAll(getCountyTaxRegions(postalCodeObj.getCountyCode(), postalCodeObj.getPostalStateCode(), postalCodeObj.getPostalCountryCode(), false));
        }

        return salesTaxRegions;
    }

    /**
     * @see org.kuali.kfs.sys.service.TaxRegionService#getUseTaxRegions(java.lang.String)
     */
    public List<TaxRegion> getUseTaxRegions(String postalCode) {

        List<TaxRegion> useTaxRegions = new ArrayList<TaxRegion>();

        PostalCode postalCodeObj = postalCodeService.getByPostalCodeInDefaultCountry(postalCode);
        useTaxRegions.addAll(getPostalCodeTaxRegions(postalCodeObj.getPostalCode(), postalCodeObj.getPostalCountryCode(), true));
        useTaxRegions.addAll(getStateTaxRegions(postalCodeObj.getPostalStateCode(), postalCodeObj.getPostalCountryCode(), true));
        useTaxRegions.addAll(getCountyTaxRegions(postalCodeObj.getCountyCode(), postalCodeObj.getPostalStateCode(), postalCodeObj.getPostalCountryCode(), true));

        return useTaxRegions;
    }

    /**
     * This method returns a list of tax regions that match postal code and country code.
     * 
     * @param postalCode postal code
     * @param postalCountryCode country code
     * @param useTaxOnly determines if only (use tax = true) tax regions are returned 
     * @return
     */
    protected List<TaxRegion> getPostalCodeTaxRegions(String postalCode, String postalCountryCode, boolean useTaxOnly) {

        List<TaxRegion> postalCodeTaxRegions = new ArrayList<TaxRegion>();

        if (StringUtils.isNotEmpty(postalCode)) {
            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put("postalCode", postalCode);
            criteria.put("postalCountryCode", postalCountryCode);
            criteria.put("active", true);
            if (useTaxOnly) {
                criteria.put("taxRegion.taxRegionUseTaxIndicator", useTaxOnly);
            }

            List<TaxRegionPostalCode> taxRegionPostalCodes = (List<TaxRegionPostalCode>) businessObjectService.findMatching(TaxRegionPostalCode.class, criteria);
            for (TaxRegionPostalCode taxRegionPostalCode : taxRegionPostalCodes) {
                postalCodeTaxRegions.add(taxRegionPostalCode.getTaxRegion());
            }
        }
        return postalCodeTaxRegions;
    }

    /**
     * This method returns a list of tax regions that match state code and country code.
     * 
     * @param stateCode state code
     * @param postalCountryCode country code
     * @param useTaxOnly determines if only (use tax = true) tax regions are returned
     * @return
     */
    protected List<TaxRegion> getStateTaxRegions(String stateCode, String postalCountryCode, boolean useTaxOnly) {

        List<TaxRegion> stateTaxRegions = new ArrayList<TaxRegion>();

        if (StringUtils.isNotEmpty(stateCode)) {

            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put("stateCode", stateCode);
            criteria.put("postalCountryCode", postalCountryCode);
            criteria.put("active", true);
            if (useTaxOnly) {
                criteria.put("taxRegion.taxRegionUseTaxIndicator", useTaxOnly);
            }

            List<TaxRegionState> taxRegionStates = (List<TaxRegionState>) businessObjectService.findMatching(TaxRegionState.class, criteria);
            for (TaxRegionState taxRegionState : taxRegionStates) {
                stateTaxRegions.add(taxRegionState.getTaxRegion());
            }
        }
        return stateTaxRegions;
    }

    /**
     * This method returns a list of tax regions that match county code, state code, and country code
     * @param countyCode county code
     * @param stateCode state code
     * @param postalCountryCode country code
     * @param useTaxOnly determines if only (use tax = true) tax regions are returned
     * @return
     */
    protected List<TaxRegion> getCountyTaxRegions(String countyCode, String stateCode, String postalCountryCode, boolean useTaxOnly) {

        List<TaxRegion> countyTaxRegions = new ArrayList<TaxRegion>();
        if (StringUtils.isNotEmpty(countyCode)) {
            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put("countyCode", countyCode);
            criteria.put("stateCode", stateCode);
            criteria.put("postalCountryCode", postalCountryCode);
            criteria.put("active", true);
            if (useTaxOnly) {
                criteria.put("taxRegion.taxRegionUseTaxIndicator", useTaxOnly);
            }

            List<TaxRegionCounty> taxRegionCounties = (List<TaxRegionCounty>) businessObjectService.findMatching(TaxRegionCounty.class, criteria);
            for (TaxRegionCounty taxRegionCounty : taxRegionCounties) {
                countyTaxRegions.add(taxRegionCounty.getTaxRegion());
            }
        }
        return countyTaxRegions;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public PostalCodeService getPostalCodeService() {
        return postalCodeService;
    }

    public void setPostalCodeService(PostalCodeService postalCodeService) {
        this.postalCodeService = postalCodeService;
    }
}
