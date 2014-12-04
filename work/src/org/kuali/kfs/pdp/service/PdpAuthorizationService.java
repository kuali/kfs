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
