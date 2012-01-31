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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;

/**
 * Fixture class for InvoiceSuspensionCategory
 */
public enum InvoiceSuspensionCategoryFixture {
    INV_SUSPEN_CTGR1("1111", "AAAA"), INV_SUSPEN_CTGR2("2222", "BBBB");

    private String documentNumber;
    private String suspensionCategoryCode;

    private InvoiceSuspensionCategoryFixture(String documentNumber, String suspensionCategoryCode) {

        this.documentNumber = documentNumber;
        this.suspensionCategoryCode = suspensionCategoryCode;
    }

    public InvoiceSuspensionCategory createInvoiceSuspensionCategory() {
        InvoiceSuspensionCategory invoiceSuspensionCategory = new InvoiceSuspensionCategory();
        invoiceSuspensionCategory.setDocumentNumber(this.documentNumber);
        invoiceSuspensionCategory.setSuspensionCategoryCode(suspensionCategoryCode);
        return invoiceSuspensionCategory;

    }
}
