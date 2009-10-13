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
package org.kuali.kfs.sys.service;

import java.util.List;

import org.kuali.kfs.sys.businessobject.TaxRegion;

public interface TaxRegionService {
    
    /**
     * This method returns a list of tax regions based on postal code.
     * 
     * @param postalCode
     * @return
     */
    List<TaxRegion> getSalesTaxRegions( String postalCode );
    
    /**
     * This method returns a list of tax regions based on postal code.  This only includes tax regions where
     * the tax indicator is set to true.
     * 
     * @param postalCode
     * @return
     */
    List<TaxRegion> getUseTaxRegions( String postalCode );    
}
