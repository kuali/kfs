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
package org.kuali.module.labor.bo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineOverride;
import org.kuali.kfs.bo.AccountingLineOverride.COMPONENT;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.service.AccountPresenceService;

public class LaborAccountingLineOverride{
    
    /**
     * On the given AccountingLine, converts override input checkboxes from a Struts Form into a persistable override code.
     * 
     * @param line
     */
    public static void populateFromInput(AccountingLine line) {
        // todo: this logic won't work if a single account checkbox might also stands for NON_FRINGE_ACCOUNT_USED; needs thought
        
        Set<Integer> overrideInputComponents = new HashSet<Integer>();
        if (line.getAccountExpiredOverride()) {
            overrideInputComponents.add(COMPONENT.EXPIRED_ACCOUNT);
        }
        if (line.isObjectBudgetOverride()) {
            overrideInputComponents.add(COMPONENT.NON_BUDGETED_OBJECT);
        }
        if (line.getNonFringeAccountOverride()) {
            overrideInputComponents.add(COMPONENT.NON_FRINGE_ACCOUNT_USED);
        }
        
        Integer[] inputComponentArray = overrideInputComponents.toArray(new Integer[overrideInputComponents.size()]);
        line.setOverrideCode(AccountingLineOverride.valueOf(inputComponentArray).getCode());
    }

    /**
     * Prepares the given AccountingLine in a Struts Action for display by a JSP. This means converting the override code to
     * checkboxes for display and input, as well as analysing the accounting line and determining which override checkboxes are
     * needed.
     * 
     * @param line
     */
    public static void processForOutput(AccountingLine line) {
        AccountingLineOverride fromCurrentCode = AccountingLineOverride.valueOf(line.getOverrideCode());
        AccountingLineOverride needed = determineNeededOverrides(line);
        line.setAccountExpiredOverride(fromCurrentCode.hasComponent(COMPONENT.EXPIRED_ACCOUNT));
        line.setAccountExpiredOverrideNeeded(needed.hasComponent(COMPONENT.EXPIRED_ACCOUNT));
        line.setObjectBudgetOverride(fromCurrentCode.hasComponent(COMPONENT.NON_BUDGETED_OBJECT));
        line.setObjectBudgetOverrideNeeded(needed.hasComponent(COMPONENT.NON_BUDGETED_OBJECT));
        line.setNonFringeAccountOverride(fromCurrentCode.hasComponent(COMPONENT.NON_FRINGE_ACCOUNT_USED));
        line.setNonFringeAccountOverrideNeeded(needed.hasComponent(COMPONENT.NON_FRINGE_ACCOUNT_USED));
    }
    
    /**
     * Determines what overrides the given line needs.
     * 
     * @param line
     * @return what overrides the given line needs.
     */
    public static AccountingLineOverride determineNeededOverrides(AccountingLine line) {
        Set<Integer> neededOverrideComponents = new HashSet<Integer>();
        if (AccountingLineOverride.needsExpiredAccountOverride(line.getAccount())) {
            neededOverrideComponents.add(COMPONENT.EXPIRED_ACCOUNT);
        }
        
        if (AccountingLineOverride.needsObjectBudgetOverride(line.getAccount(), line.getObjectCode())) {
            neededOverrideComponents.add(COMPONENT.NON_BUDGETED_OBJECT);
        }

        if (AccountingLineOverride.needsNonFringAccountOverride(line.getAccount())) {
            neededOverrideComponents.add(COMPONENT.NON_FRINGE_ACCOUNT_USED);
        }
        
        Integer[] inputComponentArray = neededOverrideComponents.toArray(new Integer[neededOverrideComponents.size()]);
        return AccountingLineOverride.valueOf(inputComponentArray);
    } 
}
