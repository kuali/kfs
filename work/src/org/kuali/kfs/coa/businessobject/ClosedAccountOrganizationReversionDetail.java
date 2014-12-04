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
package org.kuali.kfs.coa.businessobject;

import org.kuali.kfs.sys.KFSConstants;

/**
 * Returns OrganizationReversionCategoryInformation for a closed account - which is to say that only
 * returns "R2" as the organization reversion strategy code
 */
public class ClosedAccountOrganizationReversionDetail implements OrganizationReversionCategoryInfo {
    private OrganizationReversionCategoryInfo organizationReversionDetail;
    
    /**
     * Constructs a ClosedAccountOrganizationReversionDetail.java.
     * @param organizationReversionDetail the decorated organization reversion detail
     */
    protected ClosedAccountOrganizationReversionDetail(OrganizationReversionCategoryInfo organizationReversionDetail) {
        this.organizationReversionDetail = organizationReversionDetail;
    }

    /**
     * Always returns R2
     * @see org.kuali.kfs.coa.businessobject.OrganizationReversionCategoryInfo#getOrganizationReversionCode()
     */
    public String getOrganizationReversionCode() {
        return KFSConstants.RULE_CODE_R2;
    }

    /**
     * 
     * @see org.kuali.kfs.coa.businessobject.OrganizationReversionCategoryInfo#getOrganizationReversionObjectCode()
     */
    public String getOrganizationReversionObjectCode() {
        return organizationReversionDetail.getOrganizationReversionObjectCode();
    }

}
