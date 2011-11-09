/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.web;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.krad.bo.PersistableBusinessObject;

/**
 * This class...
 */
public abstract class CodeDescriptionFormatterBase implements CodeDescriptionFormatter {

    public static final String DEFAULT_DESCRIPTION = "(description unavailable)";

    /**
     * The output string will probably be larger than the default StringBuffer size of 10, so lets avoid 1 memory allocation and
     * copy
     */
    public static final int INIT_BUFFER_SIZE = 20;

    /**
     * @see org.kuali.kfs.sys.document.webCodeDescriptionFormatter#getFormattedStringWithDescriptions(java.util.Set,
     *      java.lang.String, java.lang.String)
     */
    public String getFormattedStringWithDescriptions(Set values, String startConjunction, String endConjunction) {
        Map<String, PersistableBusinessObject> valueToBOMap = getValuesToBusinessObjectsMap(values);
        StringBuffer buf = new StringBuffer();

        Iterator valuesIter = values.iterator();

        if (valuesIter.hasNext()) {
            if (startConjunction != null && !"".equals(startConjunction)) {
                buf.append(startConjunction).append(" ");
            }
            String currValue = (String) valuesIter.next();
            buf.append(currValue).append(", ");

            PersistableBusinessObject bo = valueToBOMap.get(currValue);
            buf.append(bo == null ? getDefaultDescription() : getDescriptionOfBO(bo));
        }
        else {
            buf.append("(none)");
        }

        while (valuesIter.hasNext()) {
            buf.append("; ");

            String currValue = (String) valuesIter.next();
            if (!valuesIter.hasNext()) {
                // no more values after this, it's time to put the end conjunction
                buf.append(endConjunction).append(" ");
            }

            buf.append(currValue).append(", ");

            PersistableBusinessObject bo = valueToBOMap.get(currValue);
            buf.append(bo == null ? getDefaultDescription() : getDescriptionOfBO(bo));
        }
        return buf.toString();
    }

    /**
     * Returns a Map such that the values in the values set will map to the appropriate BO There may be mappings for values that are
     * not in the parameter set Use this method sparingly, as it will likely cause an access to the DB It may be desirable to use
     * the values to limit the breadth of the search, and it is up to the implementation to decide whether to use it to do so.
     * 
     * @param values a set of values to limit the retrieval from (optional feature), may be null
     * @return a map from value string to BO
     */
    protected abstract Map<String, PersistableBusinessObject> getValuesToBusinessObjectsMap(Set values);

    /**
     * Returns the description of a BO
     * 
     * @param bo
     * @return
     */
    protected abstract String getDescriptionOfBO(PersistableBusinessObject bo);

    protected String getDefaultDescription() {
        return DEFAULT_DESCRIPTION;
    }
}
