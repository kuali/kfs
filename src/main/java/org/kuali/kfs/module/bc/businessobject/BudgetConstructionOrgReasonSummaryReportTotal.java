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
package org.kuali.kfs.module.bc.businessobject;

import java.math.BigDecimal;

/**
 * Budget Construction Organization Account Funding Detail Report Business Object.
 */
public class BudgetConstructionOrgReasonSummaryReportTotal {

    //person
    private String personPositionNumber;
    private String personFiscalYearTag;
    private Integer personCsfNormalMonths;
    private Integer personCsfPayMonths;
    private Integer personCsfAmount;
    private BigDecimal personCsfPercent;
    private Integer personSalaryNormalMonths;
    private Integer personSalaryAmount;
    private BigDecimal personSalaryPercent;
    private BigDecimal personSalaryFte;
    private String personTiFlag;
    private Integer personAmountChange;
    private BigDecimal personPercentChange;
    
    //org
    private BigDecimal newFte;
    private Integer newTotalAmount;
    private Integer newAverageAmount;
    private BigDecimal conFte;
    private Integer conTotalBaseAmount;
    private Integer conTotalRequestAmount;
    private Integer conAverageBaseAmount;
    private Integer conAverageRequestAmount;
    private Integer conAveragechange;
    private BigDecimal conPercentChange;
    
    private BudgetConstructionSalarySocialSecurityNumber budgetConstructionSalarySocialSecurityNumber;
    
    /**
     * Gets the budgetConstructionSalarySocialSecurityNumber attribute. 
     * @return Returns the budgetConstructionSalarySocialSecurityNumber.
     */
    public BudgetConstructionSalarySocialSecurityNumber getBudgetConstructionSalarySocialSecurityNumber() {
        return budgetConstructionSalarySocialSecurityNumber;
    }

    /**
     * Sets the budgetConstructionSalarySocialSecurityNumber attribute value.
     * @param budgetConstructionSalarySocialSecurityNumber The budgetConstructionSalarySocialSecurityNumber to set.
     */
    public void setBudgetConstructionSalarySocialSecurityNumber(BudgetConstructionSalarySocialSecurityNumber budgetConstructionSalarySocialSecurityNumber) {
        this.budgetConstructionSalarySocialSecurityNumber = budgetConstructionSalarySocialSecurityNumber;
    }

    public Integer getConAverageBaseAmount() {
        return conAverageBaseAmount;
    }

    public void setConAverageBaseAmount(Integer conAverageBaseAmount) {
        this.conAverageBaseAmount = conAverageBaseAmount;
    }

    public Integer getConAveragechange() {
        return conAveragechange;
    }

    public void setConAveragechange(Integer conAveragechange) {
        this.conAveragechange = conAveragechange;
    }

    public Integer getConAverageRequestAmount() {
        return conAverageRequestAmount;
    }

    public void setConAverageRequestAmount(Integer conAverageRequestAmount) {
        this.conAverageRequestAmount = conAverageRequestAmount;
    }

    public BigDecimal getConFte() {
        return conFte;
    }

    public void setConFte(BigDecimal conFte) {
        this.conFte = conFte;
    }

    public BigDecimal getConPercentChange() {
        return conPercentChange;
    }

    public void setConPercentChange(BigDecimal conPercentChange) {
        this.conPercentChange = conPercentChange;
    }

    public Integer getConTotalBaseAmount() {
        return conTotalBaseAmount;
    }

    public void setConTotalBaseAmount(Integer conTotalBaseAmount) {
        this.conTotalBaseAmount = conTotalBaseAmount;
    }

    public Integer getConTotalRequestAmount() {
        return conTotalRequestAmount;
    }

    public void setConTotalRequestAmount(Integer conTotalRequestAmount) {
        this.conTotalRequestAmount = conTotalRequestAmount;
    }

    public Integer getNewAverageAmount() {
        return newAverageAmount;
    }

    public void setNewAverageAmount(Integer newAverageAmount) {
        this.newAverageAmount = newAverageAmount;
    }

    public BigDecimal getNewFte() {
        return newFte;
    }

    public void setNewFte(BigDecimal newFte) {
        this.newFte = newFte;
    }

    public Integer getNewTotalAmount() {
        return newTotalAmount;
    }

    public void setNewTotalAmount(Integer newTotalAmount) {
        this.newTotalAmount = newTotalAmount;
    }

    public Integer getPersonAmountChange() {
        return personAmountChange;
    }

    public void setPersonAmountChange(Integer personAmountChange) {
        this.personAmountChange = personAmountChange;
    }

    public Integer getPersonCsfAmount() {
        return personCsfAmount;
    }

    public void setPersonCsfAmount(Integer personCsfAmount) {
        this.personCsfAmount = personCsfAmount;
    }

    public Integer getPersonCsfNormalMonths() {
        return personCsfNormalMonths;
    }

    public void setPersonCsfNormalMonths(Integer personCsfNormalMonths) {
        this.personCsfNormalMonths = personCsfNormalMonths;
    }

    public Integer getPersonCsfPayMonths() {
        return personCsfPayMonths;
    }

    public void setPersonCsfPayMonths(Integer personCsfPayMonths) {
        this.personCsfPayMonths = personCsfPayMonths;
    }

    public BigDecimal getPersonCsfPercent() {
        return personCsfPercent;
    }

    public void setPersonCsfPercent(BigDecimal personCsfPercent) {
        this.personCsfPercent = personCsfPercent;
    }

    public String getPersonFiscalYearTag() {
        return personFiscalYearTag;
    }

    public void setPersonFiscalYearTag(String personFiscalYearTag) {
        this.personFiscalYearTag = personFiscalYearTag;
    }

    public BigDecimal getPersonPercentChange() {
        return personPercentChange;
    }

    public void setPersonPercentChange(BigDecimal personPercentChange) {
        this.personPercentChange = personPercentChange;
    }

    public String getPersonPositionNumber() {
        return personPositionNumber;
    }

    public void setPersonPositionNumber(String personPositionNumber) {
        this.personPositionNumber = personPositionNumber;
    }

    public Integer getPersonSalaryAmount() {
        return personSalaryAmount;
    }

    public void setPersonSalaryAmount(Integer personSalaryAmount) {
        this.personSalaryAmount = personSalaryAmount;
    }

    public BigDecimal getPersonSalaryFte() {
        return personSalaryFte;
    }

    public void setPersonSalaryFte(BigDecimal personSalaryFte) {
        this.personSalaryFte = personSalaryFte;
    }

    public Integer getPersonSalaryNormalMonths() {
        return personSalaryNormalMonths;
    }

    public void setPersonSalaryNormalMonths(Integer personSalaryNormalMonths) {
        this.personSalaryNormalMonths = personSalaryNormalMonths;
    }

    public BigDecimal getPersonSalaryPercent() {
        return personSalaryPercent;
    }

    public void setPersonSalaryPercent(BigDecimal personSalaryPercent) {
        this.personSalaryPercent = personSalaryPercent;
    }

    public String getPersonTiFlag() {
        return personTiFlag;
    }

    public void setPersonTiFlag(String personTiFlag) {
        this.personTiFlag = personTiFlag;
    }

    }
