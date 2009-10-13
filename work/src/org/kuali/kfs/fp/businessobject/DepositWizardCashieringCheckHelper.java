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
package org.kuali.kfs.fp.businessobject;

/**
 * This class works with the Deposit Wizard form to figure out which cashiering checks are to be included in a given deposit
 */
public class DepositWizardCashieringCheckHelper {
    private Integer sequenceId;

    public DepositWizardCashieringCheckHelper() {
        sequenceId = new Integer(-1);
    }

    /**
     * Gets the sequenceId attribute.
     * 
     * @return Returns the sequenceId.
     */
    public Integer getSequenceId() {
        return sequenceId;
    }

    /**
     * Sets the sequenceId attribute value.
     * 
     * @param sequenceId The sequenceId to set.
     */
    public void setSequenceId(Integer sequenceId) {
        this.sequenceId = sequenceId;
    }
}
