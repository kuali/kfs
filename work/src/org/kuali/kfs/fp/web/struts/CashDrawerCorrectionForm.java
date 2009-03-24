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
package org.kuali.kfs.fp.web.struts;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.rice.kns.web.struts.form.KualiForm;

public class CashDrawerCorrectionForm extends KualiForm {
    private CashDrawer cashDrawer;
    private String campusCode;

    public CashDrawerCorrectionForm() {
        super();
        cashDrawer = new CashDrawer();
    }

    /**
     * Gets the cashDrawer attribute.
     * 
     * @return Returns the cashDrawer.
     */
    public CashDrawer getCashDrawer() {
        return cashDrawer;
    }

    /**
     * Sets the cashDrawer attribute value.
     * 
     * @param cashDrawer The cashDrawer to set.
     */
    public void setCashDrawer(CashDrawer cashDrawer) {
        this.cashDrawer = cashDrawer;
    }

    /**
     * Gets the campusCode attribute.
     * 
     * @return Returns the campusCode.
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute value.
     * 
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String workgroupName) {
        this.campusCode = workgroupName;
    }

}
