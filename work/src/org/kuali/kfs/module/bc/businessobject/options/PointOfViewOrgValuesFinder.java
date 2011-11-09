/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.bc.businessobject.options;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionProcessorService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class...
 */
public class PointOfViewOrgValuesFinder extends KeyValuesBase {

    private List pointOfViewOrgKeyLabels;

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        Person person = GlobalVariables.getUserSession().getPerson();
        List<Organization> pointOfViewOrgs = SpringContext.getBean(BudgetConstructionProcessorService.class).getProcessorOrgs(person);
        pointOfViewOrgKeyLabels = new ArrayList();
        pointOfViewOrgKeyLabels.add(new ConcreteKeyValue("", ""));
        for (Iterator iter = pointOfViewOrgs.iterator(); iter.hasNext();) {
            Organization element = (Organization) iter.next();
            pointOfViewOrgKeyLabels.add(new ConcreteKeyValue(element.getChartOfAccountsCode() + "-" + element.getOrganizationCode(), element.getChartOfAccountsCode() + "-" + element.getOrganizationCode()));
        }

        return pointOfViewOrgKeyLabels;
    }

}
