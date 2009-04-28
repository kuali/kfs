/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.AssetLock;
import org.kuali.kfs.module.cam.service.AssetLockService;
import org.kuali.kfs.sys.context.SpringContext;

public class CapitalAssetManagementModuleServiceImpl implements CapitalAssetManagementModuleService {
    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#storeAssetLocks(java.util.List, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public boolean storeAssetLocks(List<Long> capitalAssetNumbers, String documentNumber, String documentType, String lockingInformation) {
        List<AssetLock> assetLocks = getAssetLockService().buildAssetLockHelper(capitalAssetNumbers, documentNumber, documentType, StringUtils.isBlank(lockingInformation) ? CamsConstants.defaultLockingInformation : lockingInformation);
        return getAssetLockService().checkAndSetAssetLocks(assetLocks);
    }

    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#deleteAssetLocks(java.lang.String, java.lang.String)
     */
    public void deleteAssetLocks(String documentNumber, String lockingInformation) {
        getAssetLockService().deleteAssetLocks(documentNumber, StringUtils.isBlank(lockingInformation) ? CamsConstants.defaultLockingInformation : lockingInformation);
    }

    protected AssetLockService getAssetLockService() {
        return SpringContext.getBean(AssetLockService.class);
    }

    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#isAssetLockedByDocument(java.lang.String,
     *      java.lang.String)
     */
    public boolean isAssetLockedByDocument(String documentNumber, String lockingInformation) {
        return getAssetLockService().isAssetLockedByDocument(documentNumber, StringUtils.isBlank(lockingInformation) ? CamsConstants.defaultLockingInformation : lockingInformation);
    }
}
