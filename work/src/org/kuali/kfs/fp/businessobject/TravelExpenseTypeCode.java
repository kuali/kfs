/*
 * Copyright 2005-2006 The Kuali Foundation
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

package org.kuali.kfs.fp.businessobject;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.KualiCodeBase;

/**
 * This class is used to represent a travel expense type code business object.
 */
public class TravelExpenseTypeCode extends KualiCodeBase implements MutableInactivatable {
    boolean prepaidExpense;

    /**
     * Default no-arg constructor.
     */
    public TravelExpenseTypeCode() {

    }

    /**
     * @return Returns the prepaidExpense.
     */
    public boolean isPrepaidExpense() {
        return prepaidExpense;
    }

    /**
     * @param prepaidExpense The prepaidExpense to set.
     */
    public void setPrepaidExpense(boolean prepaidExpense) {
        this.prepaidExpense = prepaidExpense;
    }
}
