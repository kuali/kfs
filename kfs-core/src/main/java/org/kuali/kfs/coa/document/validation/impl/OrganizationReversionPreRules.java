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
