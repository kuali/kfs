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
package org.kuali.kfs.fp.businessobject.defaultvalue;

import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * Finds the campus of the currently logged in user and uses that as the campus code
 */
public class CashDrawerCampusDefaultValueFinder implements ValueFinder {

    /**
     * Uses CashReceiptService#getCashReceiptVerificationUnitForUser to find the current user's
     * cash receipt campus and returns that value as the cash drawer campus.
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        final CashReceiptService cashReceiptService = SpringContext.getBean(CashReceiptService.class);
        return cashReceiptService.getCashReceiptVerificationUnitForUser(GlobalVariables.getUserSession().getPerson());
    }

}
