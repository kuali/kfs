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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.gl.OJBUtility;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * This class represents the transient balance inquiry attributes which is typically used as a "dummy business object"
 */
public class TransientContractsGrantsAttributes extends TransientBusinessObjectBase {

    private String invoiceReportOption;

    /**
     * Constructs a DummyBusinessObject.java.
     */
    public TransientContractsGrantsAttributes() {
        super();
        this.invoiceReportOption = "";
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return (LinkedHashMap) OJBUtility.buildPropertyMap(this);
    }

    public String getInvoiceReportOption() {
        return invoiceReportOption;
    }

    public void setInvoiceReportOption(String invoiceReportOption) {
        this.invoiceReportOption = invoiceReportOption;
    }

}
