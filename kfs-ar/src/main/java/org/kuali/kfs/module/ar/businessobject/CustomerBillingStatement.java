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

import java.sql.Date;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class CustomerBillingStatement extends PersistableBusinessObjectBase {

    protected String customerNumber;
    protected KualiDecimal previouslyBilledAmount;
    protected Date reportedDate;

    /**
     * @return the customerNumber
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * @param customerNumber the customerNumber to set
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * @return the previouslyBilledAmount
     */
    public KualiDecimal getPreviouslyBilledAmount() {
        return previouslyBilledAmount;
    }

    /**
     * @param previouslyBilledAmount the previouslyBilledAmount to set
     */
    public void setPreviouslyBilledAmount(KualiDecimal previouslyBilledAmount) {
        this.previouslyBilledAmount = previouslyBilledAmount;
    }

    /**
     * @return the reportedDate
     */
    public Date getReportedDate() {
        return reportedDate;
    }

    /**
     * @param reportedDate the reportedDate to set
     */
    public void setReportedDate(Date reportedDate) {
        this.reportedDate = reportedDate;
    }

    /**
     * Gets the default statement format value
     * @return
     */
    public String getStatementFormat() {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        return parameterService.getParameterValueAsString(CustomerBillingStatement.class, ArConstants.DEFAULT_FORMAT);
    }

    /**
     * Gets the default include zero balance customer value
     * @return
     */
    public String getIncludeZeroBalanceCustomers() {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        return parameterService.getParameterValueAsString(CustomerBillingStatement.class, ArConstants.INCLUDE_ZERO_BALANCE_CUSTOMERS);
    }

}
