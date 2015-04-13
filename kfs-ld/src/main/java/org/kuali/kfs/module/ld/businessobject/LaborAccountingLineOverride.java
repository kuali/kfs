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
package org.kuali.kfs.module.ld.businessobject;

import java.util.HashSet;
import java.util.Set;

import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride.COMPONENT;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;

/**
 * Labor business object for Labor Accounting Line Override
 */
public class LaborAccountingLineOverride {

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
     * checkboxes for display and input, as well as analyzing the accounting line and determining which override checkboxes are
     * needed.
     *
     * @param line
     */
    public static void processForOutput(AccountingDocument document, AccountingLine line) {
        AccountingLineOverride fromCurrentCode = AccountingLineOverride.valueOf(line.getOverrideCode());
        AccountingLineOverride needed = determineNeededOverrides(document, line);
        // KFSMI-9133 : updating system to automatically check expired account boxes on the source side
        // of the transaction, since those are read only.  Otherwise, amounts in expired accounts
        // could never be transferred
        line.setAccountExpiredOverrideNeeded(needed.hasComponent(COMPONENT.EXPIRED_ACCOUNT));
        if ( line.getAccountExpiredOverrideNeeded() ) {
            if ( line instanceof SourceAccountingLine ) {
                line.setAccountExpiredOverride(true);
            } else {
                line.setAccountExpiredOverride(fromCurrentCode.hasComponent(COMPONENT.EXPIRED_ACCOUNT));
            }
        }
        line.setObjectBudgetOverride(fromCurrentCode.hasComponent(COMPONENT.NON_BUDGETED_OBJECT));
        line.setObjectBudgetOverrideNeeded(needed.hasComponent(COMPONENT.NON_BUDGETED_OBJECT));
        line.setNonFringeAccountOverride(fromCurrentCode.hasComponent(COMPONENT.NON_FRINGE_ACCOUNT_USED));
        line.setNonFringeAccountOverrideNeeded(needed.hasComponent(COMPONENT.NON_FRINGE_ACCOUNT_USED));
    }

    /**
     * 
     * @deprecated use {@link processForOutput(AccountingDocument document, AccountingLine line)} instead.
     * 
     */
    @Deprecated
    public static void processForOutput(AccountingLine line) {
        AccountingLineOverride fromCurrentCode = AccountingLineOverride.valueOf(line.getOverrideCode());
        AccountingLineOverride needed = determineNeededOverrides(line);
        // KFSMI-9133 : updating system to automatically check expired account boxes on the source side
        // of the transaction, since those are read only.  Otherwise, amounts in expired accounts
        // could never be transferred
        line.setAccountExpiredOverrideNeeded(needed.hasComponent(COMPONENT.EXPIRED_ACCOUNT));
        if ( line.getAccountExpiredOverrideNeeded() ) {
            if ( line instanceof SourceAccountingLine ) {
                line.setAccountExpiredOverride(true);
            } else {
                line.setAccountExpiredOverride(fromCurrentCode.hasComponent(COMPONENT.EXPIRED_ACCOUNT));
            }
        }
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
    public static AccountingLineOverride determineNeededOverrides(AccountingDocument document, AccountingLine line) {
        LaborModuleService laborModuleService = SpringContext.getBean(LaborModuleService.class);
        return laborModuleService.determineNeededOverrides(document, line);
    }

    /**
     * 
     * @deprecated use {@link AccountingLineOverride determineNeededOverrides(AccountingDocument document, AccountingLine line)} instead.
     * 
     */
    @Deprecated
    public static AccountingLineOverride determineNeededOverrides(AccountingLine line) {
        LaborModuleService laborModuleService = SpringContext.getBean(LaborModuleService.class);
        return laborModuleService.determineNeededOverrides(line);
    }

}
