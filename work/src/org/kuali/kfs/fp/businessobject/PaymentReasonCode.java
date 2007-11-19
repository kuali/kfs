/*
 * Copyright 2005-2006 The Kuali Foundation.
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

package org.kuali.module.financial.bo;

import org.kuali.core.bo.KualiCodeBase;

/**
 * This class is used to represent a payment reason code.
 */
public class PaymentReasonCode extends KualiCodeBase {

    private String description;

    /**
     * Default no-arg constructor.
     */
    public PaymentReasonCode() {

    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }


    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
