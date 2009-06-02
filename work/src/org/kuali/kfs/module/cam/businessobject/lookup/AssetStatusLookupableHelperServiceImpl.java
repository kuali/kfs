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
package org.kuali.kfs.module.cam.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cam.CapitalAssetManagementAsset;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetStatus;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;

/**
 * This class overrids the base getActionUrls method
 */
public class AssetStatusLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetStatusLookupableHelperServiceImpl.class);

    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {        
        List<AssetStatus> assetStatuses = (List<AssetStatus>)super.getSearchResultsHelper(fieldValues, unbounded);

        //If the lookup was invoked from a document, then hide Under Construction Asset Status
        if (StringUtils.isNotBlank(fieldValues.get(KNSConstants.DOC_NUM))){
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