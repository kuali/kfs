/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.vnd.batch.service;

import java.util.List;

import org.kuali.kfs.vnd.businessobject.DebarredVendorMatch;
import org.kuali.kfs.vnd.businessobject.VendorDetail;

public interface VendorExcludeService {
    /**
     * This method loads the epls file, retrieves and saves debarred vendors to a temp table in DB. 
     */
    public boolean loadEplsFile();
    
    /**
     * This method retrieves the debarred vendors and match with the vendors in the system. Matches are stored in DB. 
     */
    public boolean matchVendors();
    
    /**
     * This method purges the existing vendor records in MT table. 
     */
    public void purgeOldVendorRecords();
    
    /**
     * This method returns the already debarred vendors in the system, which are not matched with the EPLS excluded vendors. 
     */
    public List<VendorDetail> getDebarredVendorsUnmatched();
    
    /**
     * This method confirms the debarred vendor match record, as a debarred vendor in the DB.  
     */
    public void confirmDebarredVendor(int debarredVendorId);
    
    /**
     * This method denies the debarred vendor match record in the DB.  
     */
    public void denyDebarredVendor(int debarredVendorId);
}
