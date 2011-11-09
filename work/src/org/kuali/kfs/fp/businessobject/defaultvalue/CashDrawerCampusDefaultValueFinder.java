/*
 * Copyright 2009 The Kuali Foundation
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
