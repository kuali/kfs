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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * This class represents the transient CGB attributes and is typically used as a "dummy business object"
 */
public class TransientContractsGrantsBillingAttributes extends TransientBusinessObjectBase {

    private String invoiceReportOption = StringUtils.EMPTY;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return OJBUtility.buildPropertyMap(this);
    }

    public String getInvoiceReportOption() {
        return invoiceReportOption;
    }

    public void setInvoiceReportOption(String invoiceReportOption) {
        this.invoiceReportOption = invoiceReportOption;
    }

}
