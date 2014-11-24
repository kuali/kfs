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
