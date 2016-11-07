/*
 * Copyright 2009 The Kuali Foundation.
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
package edu.arizona.kfs.sys.document.validation.impl;

import org.kuali.kfs.sys.document.validation.Validation;

import java.util.List;

public class AccountingLineValuesAllowedValidationHutch extends org.kuali.kfs.sys.document.validation.impl.AccountingLineValuesAllowedValidationHutch {

    // KITT-296
    private Validation globalTransactionValidation;

    /**
     * Returns a list of all the validations the hutch has to pass in order to pass as a whole.
     *
     * @return a List of Validations
     */
    protected List<Validation> getValidationGauntlet() {
        List<Validation> gauntlet = super.getValidationGauntlet();
        // KITT-296
        if (globalTransactionValidation != null) {
            gauntlet.add(globalTransactionValidation);
        }
        return gauntlet;
    }

    /**
     * KITT-296
     * Gets the globalTransactionValidation attribute.
     *
     * @return Returns the globalTransactionValidation.
     */
    public Validation getGlobalTransactionValidation() {
        return globalTransactionValidation;
    }

    /**
     * KITT-296
     * Sets the globalTransactionValidation attribute value.
     *
     * @param globalTransactionValidation the globalTransactionValidation to set.
     */
    public void setGlobalTransactionValidation(Validation globalTransactionValidation) {
        this.globalTransactionValidation = globalTransactionValidation;
    }

}
