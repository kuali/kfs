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

import org.kuali.module.cams.bo.AssetPayment;

public interface AssetPaymentService {

    /**
     * Finds out the maximum value of payment sequence for an asset
     * 
     * @param assetPayment Asset Payment
     * @return Maximum sequence value of asset payment within an asset
     */
    Integer getMaxSequenceNumber(AssetPayment assetPayment);

    /**
     * Checks if asset payment is federally funder or not
     * 
     * @param assetPayment Payment record
     * @return True if financial object sub type code indicates federal contribution
     */
    boolean isPaymentFederalContribution(AssetPayment assetPayment);

    /**
     * Checks active status of financial object of the payment
     * 
     * @param assetPayment Payment record
     * @return True if object is active
     */
    boolean isPaymentFinancialObjectActive(AssetPayment assetPayment);
}
