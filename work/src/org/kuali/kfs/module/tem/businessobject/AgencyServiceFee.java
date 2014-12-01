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
package org.kuali.kfs.module.tem.businessobject;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.KualiCodeBase;

@Entity
@Table(name = "TEM_AGENCY_SRVC_FEE_T")
public class AgencyServiceFee extends KualiCodeBase implements MutableInactivatable {

    private String creditChartCode;
    private String creditAccountNumber;
    private String creditObjectCode;
    private KualiDecimal serviceFee;

    private Chart chart;
    private Account account;
    private ObjectCode objectCode;

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("code", getCode());
        map.put("name", getName());
        map.put("creditChartCode", creditChartCode);
        map.put("creditAccountNumber", creditAccountNumber);
        map.put("creditObjectCode", creditObjectCode);
        map.put("serviceFee", serviceFee);

        return map;
    }

    /**
     * Gets the creditChartCode attribute.
     * @return Returns the creditChartCode.
     */
    @Column(name = "CHART_CD", length = 2, nullable = false)
    public String getCreditChartCode() {
        return creditChartCode;
    }

    /**
     * Sets the creditChartCode attribute value.
     * @param creditChartCode The creditChartCode to set.
     */
    public void setCreditChartCode(String creditChartCode) {
        this.creditChartCode = creditChartCode;
    }

    /**
     * Gets the creditAccountNumber attribute.
     * @return Returns the creditAccountNumber.
     */
    @Column(name = "ACCT_NBR", length = 7, nullable = false)
    public String getCreditAccountNumber() {
        return creditAccountNumber;
    }

    /**
     * Sets the creditAccountNumber attribute value.
     * @param creditAccountNumber The creditAccountNumber to set.
     */
    public void setCreditAccountNumber(String creditAccountNumber) {
        this.creditAccountNumber = creditAccountNumber;
    }

    /**
     * Gets the creditObjectCode attribute.
     * @return Returns the creditObjectCode.
     */
    @Column(name = "FIN_OBJECT_CD", length = 4, nullable = false)
    public String getCreditObjectCode() {
        return creditObjectCode;
    }

    /**
     * Sets the creditObjectCode attribute value.
     * @param creditObjectCode The creditObjectCode to set.
     */
    public void setCreditObjectCode(String creditObjectCode) {
        this.creditObjectCode = creditObjectCode;
    }

    /**
     * Gets the serviceFee attribute.
     * @return Returns the serviceFee.
     */
    @Column(name = "SRVC_FEE", nullable = true)
    public KualiDecimal getServiceFee() {
        return serviceFee;
    }

    /**
     * Sets the serviceFee attribute value.
     * @param serviceFee The serviceFee to set.
     */
    public void setServiceFee(KualiDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }

    /**
     * Gets the chart attribute.
     * @return Returns the chart
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute.
     * @param chart The chart to set.
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * Gets the account attribute.
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute value.
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the objectCode attribute.
     * @return Returns the objectCode.
     */
    public ObjectCode getObjectCode() {
        return objectCode;
    }

    /**
     * Sets the objectCode attribute value.
     * @param objectCode The objectCode to set.
     */
    public void setObjectCode(ObjectCode objectCode) {
        this.objectCode = objectCode;
    }
}
