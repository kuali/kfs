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

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.businessobject.TaxRegionPostalCode;
import org.kuali.kfs.sys.service.TaxRegionService;
import org.kuali.rice.kns.service.BusinessObjectService;

public class TaxRegionServiceImpl implements TaxRegionService {
    
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    private BusinessObjectService businessObjectService;

    public List<TaxRegion> getSalesTaxRegions(Date dateOfTransaction, String postalCode) {
        
        List<TaxRegion> salesTaxRegions = new ArrayList<TaxRegion>();
        
        //get postal codes
        Map<String, String> criteria = new HashMap<String, String>();
        List<TaxRegionPostalCode> taxRegionPostalCodes =( List<TaxRegionPostalCode> )businessObjectService.findMatching(TaxRegionPostalCode.class, criteria);
        
        for( TaxRegionPostalCode taxRegionPostalCode : taxRegionPostalCodes ){
            salesTaxRegions.add(taxRegionPostalCode.getTaxRegion());
        }
        
        //go through state codes
        
        
        return salesTaxRegions;
    }

    public List<TaxRegion> getUseTaxRegions(Date dateOfTransaction, String postalCode) {
        return null;
    }
}
