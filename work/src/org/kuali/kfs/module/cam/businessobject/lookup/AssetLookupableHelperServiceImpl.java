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
package org.kuali.module.cams.lookup;

import java.util.Properties;

import org.kuali.RiceConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.util.UrlFactory;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;

/**
 * This class overrids the base getActionUrls method
 */
public class AssetLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * Custom action urls for Asset.
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getActionUrls(org.kuali.core.bo.BusinessObject)
     */
    @Override
    public String getActionUrls(BusinessObject bo) {
        StringBuffer actions = new StringBuffer();

        /** TODO per authorization don't show some links **/
        /** TODO per Asset status don't show some links **/
        
        actions.append(getMaintenanceUrl(bo, KFSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL));
        actions.append("&nbsp;&nbsp;");
        actions.append(getMaintenanceUrl(bo, KFSConstants.MAINTENANCE_COPY_METHOD_TO_CALL));
        actions.append("&nbsp;&nbsp;");
        actions.append(CamsConstants.MAINTENANCE_TAG_METHOD_TO_CALL);
        actions.append("&nbsp;&nbsp;");
        actions.append(CamsConstants.MAINTENANCE_SEPERATE_METHOD_TO_CALL);
        actions.append("&nbsp;&nbsp;");
        actions.append(CamsConstants.MAINTENANCE_PAYMENT_METHOD_TO_CALL);
        actions.append("&nbsp;&nbsp;");
        actions.append(getMaintenanceUrl(bo, CamsConstants.MAINTENANCE_RETIRE_METHOD_TO_CALL));
        actions.append("&nbsp;&nbsp;");
        actions.append(CamsConstants.MAINTENANCE_TRANSFER_METHOD_TO_CALL);
        actions.append("&nbsp;&nbsp;");
        actions.append(CamsConstants.MAINTENANCE_LOAN_METHOD_TO_CALL);
        actions.append("&nbsp;&nbsp;");
        actions.append(CamsConstants.MAINTENANCE_FABRICATION_METHOD_TO_CALL);
        actions.append("&nbsp;&nbsp;");
        actions.append(CamsConstants.MAINTENANCE_MERGE_METHOD_TO_CALL);
        
        return actions.toString();
    }
    
    /**
     * Custom maintenance Urls. In addition to edit / copy CAMs has a lot of urls that are essentially an edit, pass different
     * doc types and hence hide certain sections. The different doc types are also important for possible posts to the GL.
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getMaintenanceUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getMaintenanceUrl(BusinessObject businessObject, String methodToCall) {
        Asset asset = (Asset) businessObject;
        
        Properties parameters = new Properties();
        parameters.put(RiceConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL);
        parameters.put(RiceConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, businessObject.getClass().getName());

        // Asset PK
        parameters.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
        
        if (methodToCall.equals(CamsConstants.MAINTENANCE_RETIRE_METHOD_TO_CALL)) {
            parameters.put(CamsPropertyConstants.Asset.DOCUMENT_TYPE_CODE, CamsConstants.DocumentTypes.ASSET_RETIREMENT);
            
            String url = UrlFactory.parameterizeUrl(RiceConstants.MAINTENANCE_ACTION, parameters);
            url = "<a href=\"" + url + "\">" + CamsConstants.MAINTENANCE_RETIRE_METHOD_TO_CALL + "</a>";
            return url;
        }
        else {
            // Regular maintenance document functionality
            return super.getMaintenanceUrl(businessObject, methodToCall);
        }
    }
}
