/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.web.comparator;

import org.displaytag.model.Cell;
import org.kuali.rice.krad.comparator.NumericValueComparator;

import java.io.Serializable;
import java.util.Comparator;

public class NumericCellComparator implements Comparator, Serializable {

    static final long serialVersionUID = 3449202365486147519L;

    public int compare(Object o1, Object o2) {

        // null guard. non-null value is greater. equal if both are null
        if (null == o1 || null == o2) {
            return (null == o1 && null == o2) ? 0 : ((null == o1) ? -1 : 1);
        }

        String numericCompare1 = CellComparatorHelper.getSanitizedStaticValue((Cell) o1);
        String numericCompare2 = CellComparatorHelper.getSanitizedStaticValue((Cell) o2);

        return NumericValueComparator.getInstance().compare(numericCompare1, numericCompare2);
    }

}
