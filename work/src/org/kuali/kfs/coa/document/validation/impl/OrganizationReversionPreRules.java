/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/document/validation/impl/OrganizationReversionPreRules.java,v $
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
package org.kuali.module.chart.rules;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.OrganizationExtension;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.bo.OrganizationReversionDetail;

/**
 * This class...
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class OrganizationReversionPreRules extends MaintenancePreRulesBase {


    public OrganizationReversionPreRules() {

    }

    protected boolean doCustomPreRules(MaintenanceDocument document) {

        OrganizationReversion orgRev = (OrganizationReversion)document.getNewMaintainableObject().getBusinessObject();
        // copy year and chart to detail records
        copyKeyAttributesToDetail( orgRev );
        
        return true;
    }

    protected void copyKeyAttributesToDetail( OrganizationReversion orgRev ) {
        if ( orgRev.getUniversityFiscalYear() != null && orgRev.getUniversityFiscalYear().intValue() != 0 
                && StringUtils.isNotBlank( orgRev.getChartOfAccountsCode() ) ) {
            // loop over detail records, copying their details
            for ( OrganizationReversionDetail dtl : orgRev.getOrganizationReversionDetail() ) {
                dtl.setChartOfAccountsCode( orgRev.getChartOfAccountsCode() );
                dtl.setUniversityFiscalYear( orgRev.getUniversityFiscalYear() );
                // load the object, if possible
                if ( StringUtils.isNotBlank( dtl.getOrganizationReversionObjectCode() ) ) {
                    dtl.refreshReferenceObject( "organizationReversionObject" );
                }
            }
        }
        
    }

}
