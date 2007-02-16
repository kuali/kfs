/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.kfs.lookup.keyvalues;

import java.util.Comparator;

import org.kuali.kfs.bo.Options;

public class FiscalYearComparator implements Comparator {

    public FiscalYearComparator() {
    }

    public int compare(Object o1, Object o2) {

        Options fiscalYear1 = (Options) o1;
        Options fiscalYear2 = (Options) o2;

        return fiscalYear2.getUniversityFiscalYear().compareTo(fiscalYear1.getUniversityFiscalYear());
    }

}
