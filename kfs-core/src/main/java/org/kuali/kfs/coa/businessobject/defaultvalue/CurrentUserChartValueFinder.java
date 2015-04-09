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
package org.kuali.kfs.coa.businessobject.defaultvalue;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * This class represents a value finder that returns the currently logged in user's default chart of accounts code.
 */
public class CurrentUserChartValueFinder implements ValueFinder {

    /**
     * This method returns the current user's default chart of accounts code.
     * 
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if (currentUser != null) {
            return SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(currentUser, KFSConstants.ParameterNamespaces.CHART).getChartOfAccountsCode();
        }
        else {
            return KFSConstants.EMPTY_STRING;
        }
    }

}

