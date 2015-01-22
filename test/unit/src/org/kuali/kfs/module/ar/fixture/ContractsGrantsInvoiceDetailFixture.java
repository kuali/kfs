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

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for ContractsGrantsInvoiceDetail
 */
public enum ContractsGrantsInvoiceDetailFixture {
    INV_DTL1(new Long(2341), "6320", "SAL", KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, false),
    INV_DTL2(new Long(2350), "6321", "SAL", KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, false),
    INV_DTL3(new Long(2350), "6322", "EMPB", KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, false),
    INV_DTL4(new Long(2355), "6324", "SAL", new KualiDecimal(320.00), KualiDecimal.ZERO, new KualiDecimal(340.00), new KualiDecimal(-20.00), KualiDecimal.ZERO, false),
    INV_DTL5(new Long(2356), "6325", "EMPB", new KualiDecimal(320.00), KualiDecimal.ZERO, new KualiDecimal(340.00), new KualiDecimal(-20.00), KualiDecimal.ZERO, false),
    INV_DTL6(new Long(2357), "6326", "OIC", new KualiDecimal(320.00), KualiDecimal.ZERO, new KualiDecimal(340.00), new KualiDecimal(-20.00), KualiDecimal.ZERO, true),
    INV_DTL7(new Long(2357), "6326", null, new KualiDecimal(300.00), KualiDecimal.ZERO, new KualiDecimal(300.00), new KualiDecimal(0.00), KualiDecimal.ZERO, true),
    INV_DTL8(new Long(2358), "6367", "SAL", KualiDecimal.ZERO, new KualiDecimal(5.0), KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, false),
    INV_DTL9(new Long(2359), "6328", "EMPB", KualiDecimal.ZERO, new KualiDecimal(7.0), KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, false),;

    private Long invoiceDetailIdentifier;
    private String documentNumber;
    private String categoryCode;
    private KualiDecimal budget = KualiDecimal.ZERO;
    private KualiDecimal expenditures = KualiDecimal.ZERO;
    private KualiDecimal cumulative = KualiDecimal.ZERO;
    private KualiDecimal balance = KualiDecimal.ZERO;
    private KualiDecimal billed = KualiDecimal.ZERO;
    private boolean indirectCostIndicator;

    private ContractsGrantsInvoiceDetailFixture(Long invoiceDetailIdentifier, String documentNumber, String categoryCode, KualiDecimal budget, KualiDecimal expenditures, KualiDecimal cumulative, KualiDecimal balance, KualiDecimal billed, boolean indirectCostIndicator) {

        this.invoiceDetailIdentifier = invoiceDetailIdentifier;
        this.documentNumber = documentNumber;
        this.categoryCode = categoryCode;
        this.budget = budget;
        this.expenditures = expenditures;
        this.cumulative = cumulative;
        this.balance = balance;
        this.billed = billed;
        this.indirectCostIndicator = indirectCostIndicator;
    }

    public ContractsGrantsInvoiceDetail createInvoiceDetail() {
        ContractsGrantsInvoiceDetail invoiceDetail = new ContractsGrantsInvoiceDetail();
        invoiceDetail.setInvoiceDetailIdentifier(this.invoiceDetailIdentifier);

        invoiceDetail.setDocumentNumber(this.documentNumber);
        invoiceDetail.setCategoryCode(this.categoryCode);
        invoiceDetail.refreshReferenceObject(ArPropertyConstants.COST_CATEGORY);
        invoiceDetail.setTotalBudget(this.budget);
        invoiceDetail.setInvoiceAmount(this.expenditures);
        invoiceDetail.setCumulativeExpenditures(this.cumulative);
        invoiceDetail.setTotalPreviouslyBilled(this.billed);
        invoiceDetail.setIndirectCostIndicator(this.indirectCostIndicator);

        return invoiceDetail;
    }
}
