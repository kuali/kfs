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
