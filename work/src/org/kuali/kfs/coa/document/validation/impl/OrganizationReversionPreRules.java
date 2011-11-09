/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.businessobject.OrganizationReversionDetail;
import org.kuali.kfs.coa.service.OrganizationReversionService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * PreRules checks for the {@link OrganizationReversion} that needs to occur while still in the Struts processing. This includes defaults
 */
public class OrganizationReversionPreRules extends MaintenancePreRulesBase {

    private transient OrganizationReversionService organizationReversionService;
    public OrganizationReversionPreRules() {

    }

    /**
     * This calls the {@link OrganizationReversionPreRules#copyKeyAttributesToDetail(OrganizationReversion)}
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules(MaintenanceDocument document) {

        OrganizationReversion orgRev = (OrganizationReversion) document.getNewMaintainableObject().getBusinessObject();
        // copy year and chart to detail records
        copyKeyAttributesToDetail(orgRev);
        copyDefaultObjectcodeIfNoCarryForwardByObjectCode(orgRev);
        return true;
    }

    /**
     * 
     * This copies the chart of accounts, and the fiscal year from the parent {@link OrganizationReversion} to the 
     * {@link OrganizationReversionDetail} objects and refreshes the reference object on them if the values have 
     * been filled out
     * @param orgRev
     */
    protected void copyKeyAttributesToDetail(OrganizationReversion orgRev) {
        if (orgRev.getUniversityFiscalYear() != null && orgRev.getUniversityFiscalYear().intValue() != 0 && StringUtils.isNotBlank(orgRev.getChartOfAccountsCode())) {
            // loop over detail records, copying their details
            for (OrganizationReversionDetail dtl : orgRev.getOrganizationReversionDetail()) {
                dtl.setChartOfAccountsCode(orgRev.getChartOfAccountsCode());
                dtl.setUniversityFiscalYear(orgRev.getUniversityFiscalYear());
                // load the object, if possible
                if (StringUtils.isNotBlank(dtl.getOrganizationReversionObjectCode())) {
                    dtl.refreshReferenceObject("organizationReversionObject");
                }
            }
        }

    }
    
    /**
     * 
     * This copies the object code from system parameter to the Organization Reversion object code field if the 
     * "Carry Forward by Object Code" indicator is set to true
     * @param orgRev
     */
    protected void copyDefaultObjectcodeIfNoCarryForwardByObjectCode(OrganizationReversion orgRev) {
        if (orgRev.isCarryForwardByObjectCodeIndicator() == true) return; 
        
        if (organizationReversionService == null) {
            organizationReversionService = SpringContext.getBean(OrganizationReversionService.class);
        }
        String objectCode = organizationReversionService.getOrganizationReversionDetaiFromSystemParameters();
        
        List<OrganizationReversionDetail> details = orgRev.getOrganizationReversionDetail();
        for (OrganizationReversionDetail dtl : details) {
            if (ObjectUtils.isNull(dtl.getOrganizationReversionObjectCode()))  {
                dtl.setOrganizationReversionObjectCode(objectCode);
            }
        }
    }       
}
