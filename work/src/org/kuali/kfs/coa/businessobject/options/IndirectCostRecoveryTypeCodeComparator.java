/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.coa.businessobject.options;

import java.util.Comparator;

import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryType;

/**
 * This class allows us to compare two {@link ICRTypeCode}
 */
public class IndirectCostRecoveryTypeCodeComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        IndirectCostRecoveryType icrType1 = (IndirectCostRecoveryType) o1;
        IndirectCostRecoveryType icrType2 = (IndirectCostRecoveryType) o2;
        int icrTypeComp = icrType1.getCode().compareTo(icrType2.getCode());

        return icrTypeComp;
    }

}
