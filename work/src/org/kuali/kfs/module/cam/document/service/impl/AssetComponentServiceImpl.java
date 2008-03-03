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
package org.kuali.module.cams.service.impl;

import org.kuali.module.cams.bo.AssetComponent;
import org.kuali.module.cams.dao.AssetComponentDao;
import org.kuali.module.cams.service.AssetComponentService;
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


    public Integer getMaxSequenceNumber(AssetComponent assetComponent) {
        return this.assetComponentDao.getMaxSquenceNumber(assetComponent);
    }

}
