/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;


public abstract class EndowmentSecurityDetailsDocumentBase extends EndowmentTransactionLinesDocumentBase implements EndowmentSecurityDetailsDocument {

    private EndowmentTransactionSecurity fromTransactionSecurity;
    private EndowmentTransactionSecurity toTransactionSecurity;

    /**
     * Gets the fromTransactionSecurity attribute.
     * 
     * @return Returns the fromTransactionSecurity.
     */
    public EndowmentTransactionSecurity getFromTransactionSecurity() {
        return fromTransactionSecurity;
    }

    /**
     * Sets the fromTransactionSecurity attribute value.
     * 
     * @param fromTransactionSecurity The fromTransactionSecurity to set.
     */
    public void setFromTransactionSecurity(EndowmentTransactionSecurity fromTransactionSecurity) {
        this.fromTransactionSecurity = fromTransactionSecurity;
    }

    /**
     * Gets the toTransactionSecurity attribute.
     * 
     * @return Returns the toTransactionSecurity.
     */
    public EndowmentTransactionSecurity getToTransactionSecurity() {
        return toTransactionSecurity;
    }

    /**
     * Sets the toTransactionSecurity attribute value.
     * 
     * @param toTransactionSecurity The toTransactionSecurity to set.
     */
    public void setToTransactionSecurity(EndowmentTransactionSecurity toTransactionSecurity) {
        this.toTransactionSecurity = toTransactionSecurity;
    }


}
