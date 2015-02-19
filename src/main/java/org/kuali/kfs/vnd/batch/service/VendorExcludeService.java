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
