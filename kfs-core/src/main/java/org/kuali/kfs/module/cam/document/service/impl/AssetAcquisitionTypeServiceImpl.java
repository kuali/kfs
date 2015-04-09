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
