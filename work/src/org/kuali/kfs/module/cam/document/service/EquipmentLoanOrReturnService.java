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
package org.kuali.kfs.module.cam.document.service;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.EquipmentLoanOrReturnDocument;

public interface EquipmentLoanOrReturnService {

    /**
     * This method is called when the work flow document is reached its final approval
     * <ol>
     * <li>Gets the latest equipmentLoaOrReturn details from DB</li>
     * <li>Save asset data</li>
     * <li>Save borrower's location changes </li>
     * <li>Save store at location changes</li>
     * </ol>
     */
    void processApprovedEquipmentLoanOrReturn(EquipmentLoanOrReturnDocument document);

    /**
     * Identifies the latest equipment loan or return information available for an asset
     * <li>All approved loan/return documents are sorted descending based on the loan date</li>
     * <li>Latest record is used for display on the asset edit screen</li>
     * 
     * @param asset Asset
     */
    void setEquipmentLoanInfo(Asset asset);
    
}
