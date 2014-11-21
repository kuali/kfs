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
package org.kuali.kfs.module.ec.businessobject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride.COMPONENT;

/**
 * to handle Effort certification detail line override
 */
public class EffortCertificationDetailLineOverride {
    
    public static final List<String> REFRESH_FIELDS = Arrays.asList(new String[]{ KFSPropertyConstants.ACCOUNT});

    /**
     * convert override input checkboxes from a Struts Form into an override code.
     * 
     * @param detailLine the given detail line
     */
    public static void populateFromInput(EffortCertificationDetail detailLine) {
        Set<Integer> overrideInputComponents = new HashSet<Integer>();
        if (detailLine.isAccountExpiredOverride()) {
            overrideInputComponents.add(COMPONENT.EXPIRED_ACCOUNT);
        }

        Integer[] inputComponentArray = overrideInputComponents.toArray(new Integer[overrideInputComponents.size()]);
        detailLine.setOverrideCode(AccountingLineOverride.valueOf(inputComponentArray).getCode());
    }

    /**
     * prepare the given detail line in a Struts Action for display by a JSP. This means converting the override code to
     * checkboxes for display and input, as well as analyzing the accounting line and determining which override checkboxes are
     * needed.
     * 
     * @param detailLine the given detail line
     */
    public static void processForOutput(EffortCertificationDetail detailLine) {
        AccountingLineOverride fromCurrentCode = AccountingLineOverride.valueOf(detailLine.getOverrideCode());
        AccountingLineOverride needed = determineNeededOverrides(detailLine);
        detailLine.setAccountExpiredOverride(fromCurrentCode.hasComponent(COMPONENT.EXPIRED_ACCOUNT));
        detailLine.setAccountExpiredOverrideNeeded(needed.hasComponent(COMPONENT.EXPIRED_ACCOUNT));
    }

    /**
     * determine whether the given detail line has any attribute with override
     * 
     * @param detailLine the given detail line
     * @return what overrides the given line needs.
     */
    public static AccountingLineOverride determineNeededOverrides(EffortCertificationDetail detailLine) {
        Set<Integer> neededOverrideComponents = new HashSet<Integer>();
        if (AccountingLineOverride.needsExpiredAccountOverride(detailLine.getAccount())) {
            neededOverrideComponents.add(COMPONENT.EXPIRED_ACCOUNT);
        }

        Integer[] inputComponentArray = neededOverrideComponents.toArray(new Integer[neededOverrideComponents.size()]);
        return AccountingLineOverride.valueOf(inputComponentArray);
    }
}
