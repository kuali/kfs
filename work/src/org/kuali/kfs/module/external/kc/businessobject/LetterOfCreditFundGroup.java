/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.businessobject;

import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFundGroup;

public class LetterOfCreditFundGroup implements ContractsAndGrantsLetterOfCreditFundGroup {

    private String letterOfCreditFundGroupCode;
    private String letterOfCreditFundGroupDescription;

    @Override
    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }

    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }

    @Override
    public String getLetterOfCreditFundGroupDescription() {
        return letterOfCreditFundGroupDescription;
    }

    public void setLetterOfCreditFundGroupDescription(String letterOfCreditFundGroupDescription) {
        this.letterOfCreditFundGroupDescription = letterOfCreditFundGroupDescription;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void setActive(boolean active) { }

    @Override
    public void refresh() { }
}
