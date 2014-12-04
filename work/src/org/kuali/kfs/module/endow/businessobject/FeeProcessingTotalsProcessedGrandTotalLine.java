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
package org.kuali.kfs.module.endow.businessobject;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class FeeProcessingTotalsProcessedGrandTotalLine extends TransientBusinessObjectBase {
    protected String feeMethodCode;
    protected String documentNumber;
    protected int linesGenerated = 0;
    protected KualiDecimal totalIncomeAmount = KualiDecimal.ZERO;
    protected KualiDecimal totalPrincipalAmount = KualiDecimal.ZERO;

    public FeeProcessingTotalsProcessedGrandTotalLine() {
        feeMethodCode = "Grand Totals";
    }

    public String getFeeMethodCode() {
        return feeMethodCode;
    }

    public void setFeeMethodCode(String feeMethodCode) {
        this.feeMethodCode = feeMethodCode;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public int getLinesGenerated() {
        return linesGenerated;
    }

    public void setLinesGenerated(int linesGenerated) {
        this.linesGenerated = linesGenerated;
    }

    public KualiDecimal getTotalIncomeAmount() {
        return totalIncomeAmount;
    }

    public void setTotalIncomeAmount(KualiDecimal totalIncomeAmount) {
        this.totalIncomeAmount = totalIncomeAmount;
    }

    public KualiDecimal getTotalPrincipalAmount() {
        return totalPrincipalAmount;
    }

    public void setTotalPrincipalAmount(KualiDecimal totalPrincipalAmount) {
        this.totalPrincipalAmount = totalPrincipalAmount;
    }
}
