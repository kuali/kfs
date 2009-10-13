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
package org.kuali.kfs.module.bc.businessobject.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.lookup.KualiLookupableImpl;

/**
 * Lookupable impl for lookups using the temp list action and jsp. Corrects maintenance new link.
 */
public class TempListLookupableImpl extends KualiLookupableImpl {

    /**
     * @see org.kuali.core.lookup.KualiLookupableImpl#getCreateNewUrl()
     */
    @Override
    public String getCreateNewUrl() {
        String url = super.getCreateNewUrl();
        url = StringUtils.replace(url, KFSConstants.MAINTENANCE_ACTION, KFSConstants.RICE_PATH_PREFIX + KFSConstants.MAINTENANCE_ACTION);
        url = StringUtils.replace(url, "images/", KFSConstants.RICE_PATH_PREFIX + "images/");
        
        return url;
    }

}
