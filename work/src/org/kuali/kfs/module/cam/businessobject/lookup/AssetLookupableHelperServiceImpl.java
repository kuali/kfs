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

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
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
        /** TODO make the below KFSConstants **/
        
        actions.append(getMaintenanceUrl(bo, "edit"));
        actions.append("&nbsp;&nbsp;");
        actions.append(getMaintenanceUrl(bo, "copy"));
        actions.append("&nbsp;&nbsp;");
        actions.append("seperate");
        actions.append("&nbsp;&nbsp;");
        actions.append("payment");
        actions.append("&nbsp;&nbsp;");
        actions.append("retire");
        actions.append("&nbsp;&nbsp;");
        actions.append("transfer");
        actions.append("&nbsp;&nbsp;");
        actions.append("loan / return");
        actions.append("&nbsp;&nbsp;");
        actions.append("fabrication");
        actions.append("&nbsp;&nbsp;");
        actions.append("merge");
        
        return actions.toString();
    }
}
