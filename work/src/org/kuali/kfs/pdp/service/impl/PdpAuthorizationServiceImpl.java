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
package org.kuali.kfs.pdp.service.impl;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.service.PdpAuthorizationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.services.IdentityManagementService;

public class PdpAuthorizationServiceImpl implements PdpAuthorizationService {

    private IdentityManagementService identityManagementService;

    /**
     * @see org.kuali.kfs.pdp.service.PdpAuthorizationService#hasCancelPayment(java.lang.String)
     */
    @Override
    public boolean hasCancelPaymentPermission(String principalId) {
        return getIdentityManagementService().isAuthorized(principalId, KFSConstants.CoreModuleNamespaces.PDP, PdpConstants.PermissionNames.CANCEL_PAYMENT, null);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PdpAuthorizationService#hasFormat(java.lang.String, java.lang.String)
     */
    @Override
    public boolean hasFormatPermission(String principalId) {
        return getIdentityManagementService().isAuthorized(principalId, KFSConstants.CoreModuleNamespaces.PDP, PdpConstants.PermissionNames.FORMAT, null);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PdpAuthorizationService#hasHoldPayment(java.lang.String)
     */
    @Override
    public boolean hasHoldPaymentPermission(String principalId) {
        return getIdentityManagementService().isAuthorized(principalId, KFSConstants.CoreModuleNamespaces.PDP, PdpConstants.PermissionNames.HOLD_PAYMENT_REMOVE_NON_TAX_PAYMENT_HOLD, null);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PdpAuthorizationService#hasRemoveFormatLock(java.lang.String, java.lang.String)
     */
    @Override
    public boolean hasRemoveFormatLockPermission(String principalId) {
        return getIdentityManagementService().isAuthorized(principalId, KFSConstants.CoreModuleNamespaces.PDP, PdpConstants.PermissionNames.REMOVE_FORMAT_LOCK, null);
    }


    /**
     * @see org.kuali.kfs.pdp.service.PdpAuthorizationService#hasRemovePaymentTaxHold(java.lang.String)
     */
    @Override
    public boolean hasRemovePaymentTaxHoldPermission(String principalId) {
        return getIdentityManagementService().isAuthorized(principalId, KFSConstants.CoreModuleNamespaces.PDP, PdpConstants.PermissionNames.REMOVE_PAYMENT_TAX_HOLD, null);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PdpAuthorizationService#hasSetAsImmediatePay(java.lang.String, java.lang.String)
     */
    @Override
    public boolean hasSetAsImmediatePayPermission(String principalId) {
        return getIdentityManagementService().isAuthorized(principalId, KFSConstants.CoreModuleNamespaces.PDP, PdpConstants.PermissionNames.SET_AS_IMMEDIATE_PAY, null);
    }

    public IdentityManagementService getIdentityManagementService() {
        if ( identityManagementService == null ) {
            identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        }
        return identityManagementService;
    }

}
