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

import java.math.BigDecimal;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;


/**
 * Requisition Account Business Object.
 */
public class RequisitionAccount extends PurApAccountingLineBase {

    private static final long serialVersionUID = -8655437895493693864L;
 //   protected static final int BIG_DECIMAL_SCALE = 2;

    public RequisitionAccount() {
        this.setSequenceNumber(0);
    }

    public RequisitionItem getRequisitionItem() {
        return super.getPurapItem();
    }

    /**
     *
     * @param requisitionItem The requisitionItem to set.
     * @deprecated
     */
    @Deprecated
    public void setRequisitionItem(RequisitionItem requisitionItem) {
        setPurapItem(requisitionItem);
    }


    @Override
    public BigDecimal getAccountLinePercent() {
        BigDecimal accountLinePercent = super.getAccountLinePercent();
        String defaultDistributionMethod = SpringContext.getBean(ParameterService.class).getParameterValueAsString(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.DISTRIBUTION_METHOD_FOR_ACCOUNTING_LINES);
     
        if(PurapConstants.AccountDistributionMethodCodes.PROPORTIONAL_CODE.equalsIgnoreCase(defaultDistributionMethod)){
            if(accountLinePercent == null || accountLinePercent.compareTo(BigDecimal.ZERO) == 0){
                accountLinePercent = new BigDecimal(100);
                this.setAccountLinePercent(accountLinePercent);
            }

        }
       
        return accountLinePercent;
    }

}
