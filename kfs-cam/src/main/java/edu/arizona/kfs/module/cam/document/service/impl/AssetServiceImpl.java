package edu.arizona.kfs.module.cam.document.service.impl;

import edu.arizona.kfs.module.cam.CamsConstants;
import edu.arizona.kfs.module.cam.document.service.AssetService;

import org.kuali.kfs.integration.cam.businessobject.Asset;

public class AssetServiceImpl extends org.kuali.kfs.module.cam.document.service.impl.AssetServiceImpl implements AssetService {
	
    public String getNewAssetConditionCode() {
        return getParameterService().getParameterValueAsString(Asset.class, CamsConstants.Asset.NEW_CONDITION_CODE_PARM);
    }
}
