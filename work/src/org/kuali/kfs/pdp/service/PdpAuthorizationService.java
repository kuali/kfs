/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.pdp.service;

public interface PdpAuthorizationService {

    /**
     * This method checks if person has 'Cancel Payment' permission.
     * @param principalId Principal ID
     * @return true if it has permission, false otherwise
     */
    public boolean hasCancelPaymentPermission(String principalId);

    /**
     * This method checks if the principal has 'Format' permission.
     * @param principalId Principal ID
     * @return true if it has permission, false otherwise
     */
    public boolean hasFormatPermission(String principalId);

    /**
     * This method checks that the principal has the 'Hold Payment / Remove Non-Tax Payment Hold' permission.
     * @param principalId Principal ID
     * @return true if it has permission, false otherwise
     */
    public boolean hasHoldPaymentPermission(String principalId);

    /**
     * This method checks that the principal has the 'Remove Format Lock' permission.
     * @param principalId Principal ID
     * @return true if it has permission, false otherwise
     */
    public boolean hasRemoveFormatLockPermission(String principalId);

    /**
     * This method checks that the principal has 'Remove Payment Tax Hold' permission 
     * @param principalId Principal ID
     * @return true if it has permission, false otherwise
     */
    public boolean hasRemovePaymentTaxHoldPermission(String principalId);

    /**
     * This method checks that the principal has 'Set as Immmediate Pay' permission.
     * @param principalId Principal ID
     * @return true if it has permission, false otherwise
     */
    public boolean hasSetAsImmediatePayPermission(String principalId);

}
