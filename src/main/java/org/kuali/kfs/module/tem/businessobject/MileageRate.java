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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Mileage Rate
 *
 */
@Entity
@Table(name="TEM_MILEAGE_RT_T")
public class MileageRate extends PersistableBusinessObjectBase {

    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + "MileageRate";

    private Integer id;
    private String expenseTypeCode;
    private BigDecimal rate;
    private Date activeFromDate;
    private Date activeToDate;

    private ExpenseType expenseType;

    @Id
    @GeneratedValue(generator="TEM_MILEAGE_RT_ID_SEQ")
    @SequenceGenerator(name="TEM_MILEAGE_RT_ID_SEQ",sequenceName="TEM_MILEAGE_RT_ID_SEQ", allocationSize=5)
    @Column(name="id",nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="rt",precision=19,scale=2,nullable=false)
    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Column(name="actv_from_dt",nullable=false)
    public Date getActiveFromDate() {
        return activeFromDate;
    }

    public void setActiveFromDate(Date activeFromDate) {
        this.activeFromDate = activeFromDate;
    }

    @Column(name="actv_to_dt",nullable=false)
    public Date getActiveToDate() {
        return activeToDate;
    }

    public void setActiveToDate(Date activeToDate) {
        this.activeToDate = activeToDate;
    }

    @Column(name="exp_typ_cd",nullable=false)
    public String getExpenseTypeCode() {
        return expenseTypeCode;
    }

    public void setExpenseTypeCode(String expenseTypeCode) {
        this.expenseTypeCode = expenseTypeCode;
    }

    @ManyToOne
    @JoinColumn(name = "EXP_TYP_CD")
    public ExpenseType getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType = expenseType;
    }

    protected LinkedHashMap<String,String> toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String,String> map = new LinkedHashMap<String,String>();
        map.put("id", Integer.toString(id));
        if (rate != null) {
            map.put("rate", rate.toString());
        }

        return map;
    }

    public String getCodeAndRate(){
        return getExpenseTypeCode() + KFSConstants.BLANK_SPACE + KFSConstants.DASH + KFSConstants.BLANK_SPACE + TemConstants.DOLLAR_SIGN + this.getRate().toString();
    }

}
