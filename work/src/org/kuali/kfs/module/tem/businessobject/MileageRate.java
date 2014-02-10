/*
 * Copyright 2010 The Kuali Foundation.
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
