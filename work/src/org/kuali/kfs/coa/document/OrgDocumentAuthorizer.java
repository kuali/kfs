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

import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.core.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceDocumentAuthorizerBase;
import org.kuali.core.exceptions.ApplicationParameterException;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.util.SpringServiceLocator;

/**
 * Org/Organization specific authorization rules.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class OrgDocumentAuthorizer extends MaintenanceDocumentAuthorizerBase {

    private static final Logger LOG = Logger.getLogger(OrgDocumentAuthorizer.class);

    /**
     * Constructs a OrgDocumentAuthorizer.java.
     */
    public OrgDocumentAuthorizer() {
        super();
    }

    /**
     * 
     * This method returns the set of authorization restrictions (if any) that apply to this Org in this context.
     * 
     * @param document
     * @param user
     * @return
     * 
     */
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, KualiUser user) {

        MaintenanceDocumentAuthorizations auths = new MaintenanceDocumentAuthorizations();

        // if the user is the system supervisor, then do nothing, dont apply
        // any restrictions
        if (user.isSupervisorUser()) {
            return auths;
        }

        // get the group name that we need here - ORG
        KualiConfigurationService configService;
        configService = SpringServiceLocator.getKualiConfigurationService();
        KualiGroup group = null;
        try {
            String groupName = configService.getApplicationParameterValue(Constants.ChartApcParms.GROUP_CHART_MAINT_EDOCS, Constants.ChartApcParms.ORG_PLANT_WORKGROUP_PARM_NAME);

            // create a new KualiGroup instance with that name
            KualiGroupService groupService = SpringServiceLocator.getKualiGroupService();
            try {
                group = groupService.getByGroupName(groupName);
            }
            catch (GroupNotFoundException ex) {
                LOG.error("The group by name '" + groupName + "' was not " + "found in the KualiGroupService.  This is a configuration error, and " + "authorization/business-rules cannot be processed without this.", ex);
            }
        }
        catch (ApplicationParameterException ex) {
            LOG.error("unable to load application parameter for org plant workgroup", ex);
        }

        // if the user is NOT a member of the special group, then mark all the
        // ICR & CS fields read-only.
        if (group == null || !user.isMember(group)) {
            auths.addReadonlyAuthField("organizationPlantChartCode");
            auths.addReadonlyAuthField("organizationPlantAccountNumber");
            auths.addReadonlyAuthField("campusPlantChartCode");
            auths.addReadonlyAuthField("campusPlantAccountNumber");
        }
        return auths;
    }
}
