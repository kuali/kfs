/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.businessobject.lookup;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.AssetStatus;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This class overrids the base getActionUrls method
 */
public class AssetStatusLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetStatusLookupableHelperServiceImpl.class);

    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {        
        List<AssetStatus> assetStatuses = (List<AssetStatus>)super.getSearchResultsHelper(fieldValues, unbounded);

        //If the lookup was invoked from a document, then hide Under Construction Asset Status
        if (StringUtils.isNotBlank(fieldValues.get(KRADConstants.DOC_NUM))){
            boolean found=false;
            int pos=-1;
            for(AssetStatus assetStatus : assetStatuses) {
                pos++;
                if (assetStatus.getInventoryStatusCode().equals(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_UNDER_CONSTRUCTION)) {
                    found = true;
                    break;
                }
            }

            if (found)
                assetStatuses.remove(pos);
        }
        return assetStatuses;
    }
}
