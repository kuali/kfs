/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.kuali.core.bo.BusinessObject;

/**
 * This class...
 * 
 * @author Kuali Nervous System Team ()
 */
public abstract class CodeDescriptionFormatterBase implements CodeDescriptionFormatter {

    public static final String DEFAULT_DESCRIPTION = "(description unavailable)";

    /**
     * The output string will probably be larger than the default StringBuffer size of 10, so lets avoid 1 memory allocation and
     * copy
     */
    public static final int INIT_BUFFER_SIZE = 20;

    /**
     * @see org.kuali.module.financial.util.CodeDescriptionFormatter#getFormattedStringWithDescriptions(java.util.Set,
     *      java.lang.String, java.lang.String)
     */
    public String getFormattedStringWithDescriptions(Set values, String startConjunction, String endConjunction) {
        Map<String, BusinessObject> valueToBOMap = getValuesToBusinessObjectsMap(values);
        StringBuffer buf = new StringBuffer();

        Iterator valuesIter = values.iterator();

        if (valuesIter.hasNext()) {
            if (startConjunction != null && !"".equals(startConjunction)) {
                buf.append(startConjunction).append(" ");
            }
            String currValue = (String) valuesIter.next();
            buf.append(currValue).append(", ");

            BusinessObject bo = valueToBOMap.get(currValue);
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

            BusinessObject bo = valueToBOMap.get(currValue);
            buf.append(bo == null ? getDefaultDescription() : getDescriptionOfBO(bo));
        }
        return buf.toString();
    }

    /**
     * Returns a Map such that the values in the values set will map to the appropriate BO
     * 
     * There may be mappings for values that are not in the parameter set
     * 
     * Use this method sparingly, as it will likely cause an access to the DB
     * 
     * It may be desirable to use the values to limit the breadth of the search, and it is up to the implementation to decide
     * whether to use it to do so.
     * 
     * @param values a set of values to limit the retrieval from (optional feature), may be null
     * 
     * @return a map from value string to BO
     */
    protected abstract Map<String, BusinessObject> getValuesToBusinessObjectsMap(Set values);

    /**
     * Returns the description of a BO
     * 
     * @param bo
     * @return
     */
    protected abstract String getDescriptionOfBO(BusinessObject bo);

    protected String getDefaultDescription() {
        return DEFAULT_DESCRIPTION;
    }
}
