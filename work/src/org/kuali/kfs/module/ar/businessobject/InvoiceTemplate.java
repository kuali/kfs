/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

/**
 * Defines Invoice Template used for generation of Contracts and Grants Invoice PDFs.
 */

public class InvoiceTemplate extends TemplateBase implements MutableInactivatable {

    private String invoiceTemplateCode;
    private String invoiceTemplateDescription;

    public String getInvoiceTemplateCode() {
        return invoiceTemplateCode;
    }

    public void setInvoiceTemplateCode(String invoiceTemplateCode) {
        this.invoiceTemplateCode = invoiceTemplateCode;
    }

    public String getInvoiceTemplateDescription() {
        return invoiceTemplateDescription;
    }

    public void setInvoiceTemplateDescription(String invoiceTemplateDescription) {
        this.invoiceTemplateDescription = invoiceTemplateDescription;
    }

}
