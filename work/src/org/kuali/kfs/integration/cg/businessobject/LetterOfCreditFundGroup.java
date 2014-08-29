/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.integration.cg.businessobject;

import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFundGroup;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;


/**
 * Integration Class for LetterOfCreditFundGroup
 */
public class LetterOfCreditFundGroup implements ContractsAndGrantsLetterOfCreditFundGroup, MutableInactivatable {

    private String letterOfCreditFundGroupCode;
    private String letterOfCreditFundGroupDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public LetterOfCreditFundGroup() {
    }

    /**
     * Gets the letterOfCreditFundGroupCode attribute.
     *
     * @return Returns the letterOfCreditFundGroupCode
     */
    @Override
    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }

    /**
     * Sets the letterOfCreditFundGroupCode attribute.
     *
     * @param letterOfCreditFundGroupCode The letterOfCreditFundGroupCode to set.
     */
    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }


    /**
     * Gets the letterOfCreditFundGroupDescription attribute.
     *
     * @return Returns the letterOfCreditFundGroupDescription
     */
    @Override
    public String getLetterOfCreditFundGroupDescription() {
        return letterOfCreditFundGroupDescription;
    }

    /**
     * Sets the letterOfCreditFundGroupDescription attribute.
     *
     * @param letterOfCreditFundGroupDescription The letterOfCreditFundGroupDescription to set.
     */
    public void setLetterOfCreditFundGroupDescription(String letterOfCreditFundGroupDescription) {
        this.letterOfCreditFundGroupDescription = letterOfCreditFundGroupDescription;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void refresh() {
    }

}
