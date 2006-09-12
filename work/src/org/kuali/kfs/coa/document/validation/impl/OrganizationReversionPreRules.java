/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
