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
