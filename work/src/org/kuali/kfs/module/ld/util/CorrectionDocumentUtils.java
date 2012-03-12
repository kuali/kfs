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
package org.kuali.kfs.module.ld.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.CorrectionChangeGroup;
import org.kuali.kfs.gl.businessobject.CorrectionCriteria;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.options.LaborOriginEntryFieldFinder;
import org.kuali.rice.core.api.util.type.KualiDecimal;

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
        String fieldActualValueString = org.kuali.kfs.gl.document.CorrectionDocumentUtils.convertToString(fieldActualValue, fieldType);

        if ("String".equals(fieldType) || "sw".equals(cc.getCorrectionOperatorCode()) || "ew".equals(cc.getCorrectionOperatorCode()) || "ct".equals(cc.getCorrectionOperatorCode())) {
            return org.kuali.kfs.gl.document.CorrectionDocumentUtils.compareStringData(cc, fieldTestValue, fieldActualValueString);
        }
        int compareTo = 0;
        try {
            if (fieldActualValue == null) {
                return false;
            }
            if ("Integer".equals(fieldType)) {
                compareTo = ((Integer) fieldActualValue).compareTo(Integer.parseInt(fieldTestValue));
            }
            if ("KualiDecimal".equals(fieldType)) {
                compareTo = ((KualiDecimal) fieldActualValue).compareTo(new KualiDecimal(Double.parseDouble(fieldTestValue)));
            }
            if ("BigDecimal".equals(fieldType)) {
                compareTo = ((BigDecimal) fieldActualValue).compareTo(new BigDecimal(Double.parseDouble(fieldTestValue)));

            }
            if ("Date".equals(fieldType)) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                compareTo = ((Date) fieldActualValue).compareTo(df.parse(fieldTestValue));
            }
        }
        catch (Exception e) {
            // any exception while parsing data return false
            return false;
        }
        return org.kuali.kfs.gl.document.CorrectionDocumentUtils.compareTo(compareTo, cc.getCorrectionOperatorCode());
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
