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
package org.kuali.module.cams.service;

import org.kuali.module.cams.document.EquipmentLoanOrReturnDocument;

public interface EquipmentLoanOrReturnService {

    
    /**
     * Checks if asset payment is federally funder or not
     * 
     * @param assetPayment Payment record
     * @return True if financial object sub type code indicates federal contribution
     */
    boolean canBeLoaned(EquipmentLoanOrReturnDocument document);    
    /**
     * This method is called when the work flow document is reached its final approval
     * <ol>
     * <li>Gets the latest asset details from DB</li>
     * <li>Save asset owner data</li>
     * <li>Save location changes </li>
     * <li>Save organization changes</li>
     * <li>Create offset payments</li>
     * <li>Create new payments</li>
     * <li>Update original payments</li>
     * </ol>
     */
    void processApprovedEquipmentLoanOrReturn(EquipmentLoanOrReturnDocument document);

}
