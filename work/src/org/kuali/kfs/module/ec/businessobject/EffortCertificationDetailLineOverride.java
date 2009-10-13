/*
 * Copyright 2008 The Kuali Foundation
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
