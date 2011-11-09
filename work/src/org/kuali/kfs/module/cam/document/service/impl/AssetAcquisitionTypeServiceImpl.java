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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType;
import org.kuali.kfs.module.cam.document.service.AssetAcquisitionTypeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AssetAcquisitionTypeServiceImpl implements AssetAcquisitionTypeService {
    BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetAcquisitionTypeService#doesAcquisitionTypeHaveIncomeAssetObjectCode(java.lang.String)
     */
    public boolean hasIncomeAssetObjectCode(String acquisitionTypeCode) {
        Map<String, Object> pkKeys = new HashMap<String, Object>();
        pkKeys.put(CamsPropertyConstants.AssetAcquisitionType.ACQUISITION_TYPE_CODE, acquisitionTypeCode);
        AssetAcquisitionType assetAcquisitionType = (AssetAcquisitionType) getBusinessObjectService().findByPrimaryKey(AssetAcquisitionType.class, pkKeys);

        return StringUtils.isNotBlank(assetAcquisitionType.getIncomeAssetObjectCode());
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
