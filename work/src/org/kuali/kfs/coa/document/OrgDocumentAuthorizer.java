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
package org.kuali.module.chart.document;

import org.kuali.core.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceDocumentAuthorizerBase;
import org.kuali.module.chart.bo.Org;

/**
 * Org/Organization specific authorization rules.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class OrgDocumentAuthorizer extends MaintenanceDocumentAuthorizerBase {

    /**
     * Constructs a OrgDocumentAuthorizer.java.
     */
    public OrgDocumentAuthorizer() {
        super();
    }
    
    /**
     * 
     * This method returns the set of authorization restrictions (if any) that 
     * apply to this Org in this context.
     * 
     * @param document
     * @param user
     * @return
     * 
     */
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, KualiUser user) {
        
        MaintenanceDocumentAuthorizations auths = new MaintenanceDocumentAuthorizations();
        
        //  if the user is the system supervisor, then do nothing, dont apply 
        // any restrictions
        if (user.isSupervisorUser()) {
            return auths;
        }
        
        //  get the chartCode for the Org being touched
        Org org = (Org) document.getNewMaintainableObject().getBusinessObject();
        String chartCode = org.getChartOfAccountsCode();
        
        //  if the user is NOT the chart manager for this Org's chart, then 
        // do not allow them to modify the 4 plant account fields
        if (!user.isManagerForChart(chartCode)) {
            auths.addReadonlyAuthField("organizationPlantChartCode");
            auths.addReadonlyAuthField("organizationPlantAccountNumber");
            auths.addReadonlyAuthField("campusPlantChartCode");
            auths.addReadonlyAuthField("campusPlantAccountNumber");
        }
        return auths;
    }
}
