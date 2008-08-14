/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.authorization;

import org.kuali.rice.kns.document.authorization.DocumentActionFlags;

/**
 * Simple bean used to indicate which operations are allowed for the current user on the associated document.
 */
public class FinancialSystemDocumentActionFlags extends DocumentActionFlags {
    private boolean hasAmountTotal;


    /**
     * Default constructor.
     */
    public FinancialSystemDocumentActionFlags() {
        super();
    }

    /**
     * Copy constructor.
     * 
     * @param flags
     */
    public FinancialSystemDocumentActionFlags(DocumentActionFlags flags) {
        super(flags);

        if (flags instanceof FinancialSystemDocumentActionFlags) {
            FinancialSystemDocumentActionFlags mflags = (FinancialSystemDocumentActionFlags) flags;
            this.hasAmountTotal = mflags.hasAmountTotal;
        }
    }

    /**
     * @return String
     */
    public String getClassName() {
        return this.getClass().getName();
    }

    /**
     * Gets the hasAmountTotal attribute. 
     * @return Returns the hasAmountTotal.
     */
    public boolean isHasAmountTotal() {
        return hasAmountTotal;
    }

    /**
     * Sets the hasAmountTotal attribute value.
     * @param hasAmountTotal The hasAmountTotal to set.
     */
    public void setHasAmountTotal(boolean hasAmountTotal) {
        this.hasAmountTotal = hasAmountTotal;
    }

    /**
     * Debugging method, simplifies comparing another instance of this class to this one
     * 
     * @param other
     * @return String
     */
    public String diff(DocumentActionFlags other) {
        StringBuffer s = new StringBuffer(super.diff(other));

        // hasAmountTotal explicitly unaccounted for as was the case in DocumentActionFlags pre-extraction from Rice back to KFS

        return s.toString();
    }
}
