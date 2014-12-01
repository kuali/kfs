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
package org.kuali.kfs.coa.businessobject.lookup;

import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * This class overrides the base getActionUrls to set it to an empty string
 */
public class IndirectCostRecoveryExclusionTypeLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @returns Empty string because we don't want any action links to be displayed on the lookups results page.
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getActionUrls(org.kuali.rice.krad.bo.BusinessObject)
     */
    public String getActionUrls(BusinessObject businessObject) {
        return "";
    }

}
