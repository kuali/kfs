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
/*
 * Created on Jul 22, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.kuali.module.pdp.form.customerprofile;

import org.apache.struts.action.ActionForm;

/**
 * @author delyea To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class CustomerBankForm extends ActionForm {
    private String disbursementDescription;
    private String disbursementTypeCode;
    private Integer bankId;

    public CustomerBankForm() {
    }

    /**
     * @return Returns the disbursementTypeCode.
     */
    public String getDisbursementTypeCode() {
        return disbursementTypeCode;
    }

    /**
     * @param disbursementTypeCode The disbursementTypeCode to set.
     */
    public void setDisbursementTypeCode(String disbursementTypeCode) {
        this.disbursementTypeCode = disbursementTypeCode;
    }

    /**
     * @return Returns the bankId.
     */
    public Integer getBankId() {
        return bankId;
    }

    /**
     * @return Returns the disbursementDescription.
     */
    public String getDisbursementDescription() {
        return disbursementDescription;
    }

    /**
     * @param bankId The bankId to set.
     */
    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    /**
     * @param disbursementDescription The disbursementDescription to set.
     */
    public void setDisbursementDescription(String disbursementDescription) {
        this.disbursementDescription = disbursementDescription;
    }

}
