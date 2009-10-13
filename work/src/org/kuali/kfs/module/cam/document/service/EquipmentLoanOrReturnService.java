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
