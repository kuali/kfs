/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;

/**
 * Fixture class for InvoiceSuspensionCategory
 */
public enum InvoiceSuspensionCategoryFixture {
    INV_SUSPEN_CTGR1("1111", "AAAA"),
    INV_SUSPEN_CTGR2("2222", "BBBB"),
    INV_SUSPEN_CTGR3("2222", "3"),
    INV_SUSPEN_CTGR4("2222", "4");

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
