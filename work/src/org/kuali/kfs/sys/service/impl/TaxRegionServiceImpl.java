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

import org.kuali.kfs.sys.businessobject.PostalCode;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.businessobject.TaxRegionCounty;
import org.kuali.kfs.sys.businessobject.TaxRegionPostalCode;
import org.kuali.kfs.sys.businessobject.TaxRegionState;
import org.kuali.kfs.sys.service.PostalCodeService;
import org.kuali.kfs.sys.service.TaxRegionService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TaxRegionServiceImpl implements TaxRegionService {
    
    private BusinessObjectService businessObjectService;
    private PostalCodeService postalCodeService;

    public List<TaxRegion> getSalesTaxRegions(String postalCode) {
        
        List<TaxRegion> salesTaxRegions = new ArrayList<TaxRegion>();
        
        PostalCode postalCodeObj = postalCodeService.getByPrimaryId(postalCode);        
        salesTaxRegions.addAll(getPostalCodeTaxRegions( postalCodeObj.getPostalZipCode(), false));
        salesTaxRegions.addAll(getStateTaxRegions( postalCodeObj.getPostalStateCode(), false));
        salesTaxRegions.addAll(getCountyTaxRegions( postalCodeObj.getCountyCode(), false));
        
        return salesTaxRegions;
    }

    public List<TaxRegion> getUseTaxRegions(String postalCode) {
        
        List<TaxRegion> useTaxRegions = new ArrayList<TaxRegion>();
        
        PostalCode postalCodeObj = postalCodeService.getByPrimaryId(postalCode);        
        useTaxRegions.addAll(getPostalCodeTaxRegions( postalCodeObj.getPostalZipCode(), true));
        useTaxRegions.addAll(getStateTaxRegions( postalCodeObj.getPostalStateCode(), true));
        useTaxRegions.addAll(getCountyTaxRegions( postalCodeObj.getCountyCode(), true));
        
        return useTaxRegions;
    }
    
    protected List<TaxRegion> getPostalCodeTaxRegions( String postalCode, boolean useTaxOnly ){
        
        List<TaxRegion> postalCodeTaxRegions = new ArrayList<TaxRegion>();
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("postalCode", postalCode);
        //criteria.put("countryCode", countryCode);
        if( useTaxOnly ){
            criteria.put("taxRegion.taxRegionUseTaxIndicator", new Boolean(useTaxOnly));
        }
        
        List<TaxRegionPostalCode> taxRegionPostalCodes =( List<TaxRegionPostalCode> )businessObjectService.findMatching(TaxRegionPostalCode.class, criteria);
        for( TaxRegionPostalCode taxRegionPostalCode : taxRegionPostalCodes ){
            postalCodeTaxRegions.add(taxRegionPostalCode.getTaxRegion());
        }        
        return postalCodeTaxRegions;
    }
    
    protected List<TaxRegion> getStateTaxRegions( String stateCode, boolean useTaxOnly ){
        
        List<TaxRegion> stateTaxRegions = new ArrayList<TaxRegion>();
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("stateCode", stateCode);
        //criteria.put("countryCode", countryCode);
        if( useTaxOnly ){
            criteria.put("taxRegion.taxRegionUseTaxIndicator", new Boolean(useTaxOnly));
        }
        
        List<TaxRegionState> taxRegionStates =( List<TaxRegionState> )businessObjectService.findMatching(TaxRegionState.class, criteria);
        for( TaxRegionState taxRegionState : taxRegionStates ){
            stateTaxRegions.add(taxRegionState.getTaxRegion());
        }        
        return stateTaxRegions;
    }    
    
    protected List<TaxRegion> getCountyTaxRegions( String countyCode, boolean useTaxOnly ){
        
        List<TaxRegion> countyTaxRegions = new ArrayList<TaxRegion>();
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("countyCode", countyCode);
        //criteria.put("countryCode", countryCode);
        if( useTaxOnly ){
            criteria.put("taxRegion.taxRegionUseTaxIndicator", new Boolean(useTaxOnly));
        }
        
        List<TaxRegionCounty> taxRegionCounties =( List<TaxRegionCounty> )businessObjectService.findMatching(TaxRegionCounty.class, criteria);
        for( TaxRegionCounty taxRegionState : taxRegionCounties ){
            countyTaxRegions.add(taxRegionState.getTaxRegion());
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
