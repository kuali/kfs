/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject.options;

import java.util.Comparator;

import org.kuali.kfs.sys.businessobject.SystemOptions;

public class FiscalYearComparator implements Comparator {

    public FiscalYearComparator() {
    }

    public int compare(Object o1, Object o2) {

        SystemOptions fiscalYear1 = (SystemOptions) o1;
        SystemOptions fiscalYear2 = (SystemOptions) o2;

        return fiscalYear2.getUniversityFiscalYear().compareTo(fiscalYear1.getUniversityFiscalYear());
    }

}
