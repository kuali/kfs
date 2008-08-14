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
 * TransactionalDocument-specific flags used for authorization checks.
 */
final public class FinancialSystemTransactionalDocumentActionFlags extends FinancialSystemDocumentActionFlags {
    private boolean canErrorCorrect;

    /**
     * Default constructor.
     */
    public FinancialSystemTransactionalDocumentActionFlags() {
    }

    /**
     * Copy constructor.
     * 
     * @param flags
     */
    public FinancialSystemTransactionalDocumentActionFlags(DocumentActionFlags flags) {
        super(flags);

        if (flags instanceof FinancialSystemTransactionalDocumentActionFlags) {
            FinancialSystemTransactionalDocumentActionFlags mflags = (FinancialSystemTransactionalDocumentActionFlags) flags;
            this.canErrorCorrect = mflags.canErrorCorrect;
        }
    }

    /**
     * @return boolean
     */
    public boolean getCanErrorCorrect() {
        return canErrorCorrect;
    }

    /**
     * @param canErrorCorrect
     */
    public void setCanErrorCorrect(boolean canErrorCorrect) {
        this.canErrorCorrect = canErrorCorrect;
    }

    /**
     * Debugging method, simplifies comparing another instance of this class to this one
     * 
     * @param other
     * @return String
     */
    public String diff(DocumentActionFlags other) {
        StringBuffer s = new StringBuffer(super.diff(other));

        if (other instanceof FinancialSystemTransactionalDocumentActionFlags) {
            FinancialSystemTransactionalDocumentActionFlags tother = (FinancialSystemTransactionalDocumentActionFlags) other;

            if (canErrorCorrect != tother.canErrorCorrect) {
                s.append("canErrorCorrect=(" + canErrorCorrect + "," + tother.canErrorCorrect + ")");
            }
        }

        return s.toString();
    }
}
