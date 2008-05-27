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
     * This method is called when the work flow document is reached its final approval
     * <ol>
     * <li>Gets the latest equipmentLoaOrReturn details from DB</li>
     * <li>Save asset data</li>
     * <li>Save borrower's location changes </li>
     * <li>Save store at location changes</li>
     * </ol>
     */
    void processApprovedEquipmentLoanOrReturn(EquipmentLoanOrReturnDocument document);

}
