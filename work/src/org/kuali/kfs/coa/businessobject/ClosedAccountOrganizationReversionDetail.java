/*
 * Copyright 2009 The Kuali Foundation
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
