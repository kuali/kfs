/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.service.PermissionService;
import org.kuali.module.chart.bo.Org;

/**
 * This class...
 */
public class PointOfViewOrgValuesFinder extends KeyValuesBase {

    private List pointOfViewOrgKeyLabels;

    /**
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        PermissionService permissionService = SpringContext.getBean(PermissionService.class);
        try {
            List<Org> pointOfViewOrgs = permissionService.getOrgReview(GlobalVariables.getUserSession().getNetworkId());
            pointOfViewOrgKeyLabels = new ArrayList();
            pointOfViewOrgKeyLabels.add(new KeyLabelPair("", ""));
            for (Iterator iter = pointOfViewOrgs.iterator(); iter.hasNext();) {
                Org element = (Org) iter.next();
                pointOfViewOrgKeyLabels.add(new KeyLabelPair(element.getChartOfAccountsCode() + "-" + element.getOrganizationCode(), element.getChartOfAccountsCode() + "-" + element.getOrganizationCode()));
            }
        }
        catch (Exception e) {
            pointOfViewOrgKeyLabels = null;
        }

        return pointOfViewOrgKeyLabels;
    }

}
