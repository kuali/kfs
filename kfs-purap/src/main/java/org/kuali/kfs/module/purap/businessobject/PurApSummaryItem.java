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
package org.kuali.kfs.module.purap.businessobject;

import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Purap Summary Item Business Object.
 */
public class PurApSummaryItem extends PurApItemBase {

    private KualiDecimal estimatedEncumberanceAmount;   

    public PurApSummaryItem() {
        this(KualiDecimal.ZERO);
    }

    public PurApSummaryItem(KualiDecimal estimatedEncumberanceAmount) {
        this.estimatedEncumberanceAmount = estimatedEncumberanceAmount;
    }

    public KualiDecimal getEstimatedEncumberanceAmount() {
        return estimatedEncumberanceAmount;
    }

    public void setEstimatedEncumberanceAmount(KualiDecimal estimatedEncumberanceAmount) {
        this.estimatedEncumberanceAmount = estimatedEncumberanceAmount;
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApItem#getAccountingLineClass()
     */
    public Class getAccountingLineClass() {
        return SourceAccountingLine.class;
    }

    public Class getUseTaxClass() {
        return SummaryItemUseTax.class;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApItem#isConsideredEntered()
     */
    public boolean isConsideredEntered() {
        return false;
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApItemBase#resetAccount()
     */
    @Override
    public void resetAccount() {
        // do nothing
    }
}
