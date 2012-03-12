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
