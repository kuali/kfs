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
