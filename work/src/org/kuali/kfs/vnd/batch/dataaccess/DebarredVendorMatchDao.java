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
package org.kuali.kfs.vnd.batch.dataaccess;

import java.util.List;

import org.kuali.kfs.vnd.businessobject.DebarredVendorMatch;
import org.kuali.kfs.vnd.businessobject.VendorDetail;

public interface DebarredVendorMatchDao {
    /**
     * If the exact same match already exists in the Vendor exclude match table, this
     * method will find that match and return
     * @param match the specified match to compare with.
     * @return the already existing match
     */
    public DebarredVendorMatch getPreviousVendorExcludeConfirmation(DebarredVendorMatch match);
    
    /**
     * This method returns the list of already debarred vendors in the system, but unmatched with
     * EPLS exclude vendors
     * @return list of already debarred vendors
     */
    public List<VendorDetail> getDebarredVendorsUnmatched();
    
    /**
     * This method returns a particular debarred vendor match, given the debarred vendor id
     * @param debarred vendor id
     * @return corresponding debarred vendor match
     */
    public DebarredVendorMatch getDebarredVendor(int debarredVendorId);
}
