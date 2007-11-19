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
package org.kuali.module.gl.service.impl;

import org.kuali.module.gl.service.ScrubberProcessObjectCodeOverride;

/**
 * The default implementation of ScrubberProcessObjectCodeOverride
 */
public class ScrubberProcessObjectCodeOverrideImpl implements ScrubberProcessObjectCodeOverride {

    /**
     * Potentially overrides the object code for origin entries the scrubber generates
     * 
     * @param originEntryObjectLevelCode the current level code of the object code that could potentially be overriden
     * @param originEntryObjectCode current originEntryObjectCode
     * @return the overriden object code, or the same object code if overriding wasn't necessary
     * @see org.kuali.module.gl.service.ScrubberProcessObjectCodeOverride#getOriginEntryObjectCode(java.lang.String, java.lang.String)
     */
    public String getOriginEntryObjectCode(String originEntryObjectLevelCode, String originEntryObjectCode) {
        boolean done = false;

        // IU Specific Rules
        if ((!done) && ("BENF".equals(originEntryObjectLevelCode) && ("9956".equals(originEntryObjectCode) || 5700 > Integer.valueOf(originEntryObjectCode).intValue()))) { // BENEFITS
            originEntryObjectCode = "9956"; // TRSFRS_OF_FUNDS_FRINGE_BENF
            done = true;
        }

        if ((!done) && ("FINA".equals(originEntryObjectLevelCode) && ("9954".equals(originEntryObjectCode) || "5400".equals(originEntryObjectCode)))) {
            // STUDENT_FINANCIAL_AID - TRSFRS_OF_FUNDS_FEE_REM - GRADUATE_FEE_REMISSIONS
            originEntryObjectCode = "9954"; // TRSFRS_OF_FUNDS_CAPITAL
            done = true;
        }

        return originEntryObjectCode;
    }
}
