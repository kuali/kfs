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
package org.kuali.kfs.module.cam.document.service.impl;

import org.kuali.kfs.module.cam.businessobject.AssetComponent;
import org.kuali.kfs.module.cam.document.dataaccess.AssetComponentDao;
import org.kuali.kfs.module.cam.document.service.AssetComponentService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AssetComponentServiceImpl implements AssetComponentService {
    AssetComponentDao assetComponentDao;


    public AssetComponentDao getAssetComponentDao() {
        return assetComponentDao;
    }


    public void setAssetComponentDao(AssetComponentDao assetComponentDao) {
        this.assetComponentDao = assetComponentDao;
    }


    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetComponentService#getMaxSequenceNumber(org.kuali.kfs.module.cam.businessobject.AssetComponent)
     */
    public Integer getMaxSequenceNumber(AssetComponent assetComponent) {
        return this.assetComponentDao.getMaxSquenceNumber(assetComponent);
    }

}
