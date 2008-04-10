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

import org.kuali.module.cams.bo.Asset;

public interface EquipmentLoanInfoService {

    /**
     * Identifies the latest equipment loan or return information available for an asset
     * <li>All approved loan/return documents are sorted descending based on the loan date</li>
     * <li>Latest record is used for display on the asset edit screen</li>
     * 
     * @param asset Asset
     */
    void setEquipmentLoanInfo(Asset asset);

}
