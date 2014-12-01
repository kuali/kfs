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
