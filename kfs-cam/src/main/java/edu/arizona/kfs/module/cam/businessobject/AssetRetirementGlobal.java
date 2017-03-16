package edu.arizona.kfs.module.cam.businessobject;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class AssetRetirementGlobal extends org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal {
    private static final String VALID_ASSET_RETIREMENT_STATUSES_BY_ASSET_STATUS = "VALID_ASSET_RETIREMENT_STATUSES_BY_ASSET_STATUS";
    
    @Override
    protected void setAssetForPersist(Asset asset, List<PersistableBusinessObject> persistables, AssetRetirementService retirementService) {
        String invStatusCode = asset.getInventoryStatusCode();
        super.setAssetForPersist(asset, persistables, retirementService);
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        //Look up parameter to see the new status to use
        Map<String, String> inventoryStatusMap = new HashMap<String, String>();
        Collection<String> wholeValues = parameterService.getParameterValuesAsString(AssetRetirementGlobal.class, VALID_ASSET_RETIREMENT_STATUSES_BY_ASSET_STATUS);

        for (String wholeValue : wholeValues) {
            String[] tokens = wholeValue.split("=");
            String valueStr = tokens[0];

            String values = tokens[1];
            String[] valueTokens = values.split(",");
            for (String str : valueTokens) {
                String keyValue = str;
                inventoryStatusMap.put(keyValue, valueStr);
            }
        }
        
        String newInvStatusCode = inventoryStatusMap.get(invStatusCode);
        if(StringUtils.isNotBlank(newInvStatusCode)) {
            asset.setInventoryStatusCode(newInvStatusCode);
        }
    }
}
