/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.labor.util;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.bo.CorrectionCriteria;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.web.optionfinder.LaborOriginEntryFieldFinder;

/**
 * This class provides utility methods for the Labor correction document
 */
public class CorrectionDocumentUtils {
    /**
     * Returns whether an origin entry matches the passed in criteria. If both the criteria and actual value are both String types
     * and are empty, null, or whitespace only, then they will match.
     * 
     * @param cc correction criteria to test against origin entry
     * @param oe origin entry to test
     * @return true if origin entry matches the passed in criteria
     */
    public static boolean laborEntryMatchesCriteria(CorrectionCriteria cc, OriginEntryFull oe) {
        LaborOriginEntryFieldFinder loeff = new LaborOriginEntryFieldFinder();
        LaborOriginEntry loe = (LaborOriginEntry) oe;
        Object fieldActualValue = loe.getFieldValue(cc.getCorrectionFieldName());
        String fieldTestValue = StringUtils.isBlank(cc.getCorrectionFieldValue()) ? "" : cc.getCorrectionFieldValue();
        String fieldType = loeff.getFieldType(cc.getCorrectionFieldName());
        String fieldActualValueString = org.kuali.module.gl.util.CorrectionDocumentUtils.convertToString(fieldActualValue, fieldType);

        if ("String".equals(fieldType) && StringUtils.isBlank(fieldActualValueString)) {
            fieldActualValueString = "";
        }

        if ("eq".equals(cc.getCorrectionOperatorCode())) {
            return fieldActualValueString.equals(fieldTestValue);
        }
        else if ("ne".equals(cc.getCorrectionOperatorCode())) {
            return (!fieldActualValueString.equals(fieldTestValue));
        }
        else if ("sw".equals(cc.getCorrectionOperatorCode())) {
            return fieldActualValueString.startsWith(fieldTestValue);
        }
        else if ("ew".equals(cc.getCorrectionOperatorCode())) {
            return fieldActualValueString.endsWith(fieldTestValue);
        }
        else if ("ct".equals(cc.getCorrectionOperatorCode())) {
            return (fieldActualValueString.indexOf(fieldTestValue) > -1);
        }
        throw new IllegalArgumentException("Unknown operator: " + cc.getCorrectionOperatorCode());
    }
    
    /**
     * Returns whether the labor entry matches any of the criteria groups
     * 
     * @param entry labor origin entry
     * @param groups collection of correction change group
     * @return true if labor origin entry matches any of the criteria groups
     */
    public static boolean doesLaborEntryMatchAnyCriteriaGroups(OriginEntryFull entry, Collection<CorrectionChangeGroup> groups) {
        boolean anyGroupMatch = false;
        for (CorrectionChangeGroup ccg : groups) {
            int matches = 0;
            for (CorrectionCriteria cc : ccg.getCorrectionCriteria()) {
                if (CorrectionDocumentUtils.laborEntryMatchesCriteria(cc, entry)) {
                    matches++;
                }
            }

            // If they all match, change it
            if (matches == ccg.getCorrectionCriteria().size()) {
                anyGroupMatch = true;
                break;
            }
        }
        return anyGroupMatch;
    }
}
