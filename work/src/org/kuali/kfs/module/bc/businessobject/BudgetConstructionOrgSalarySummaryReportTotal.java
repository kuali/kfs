/*
 * Copyright 2007-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.bc.businessobject;

import java.math.BigDecimal;

/**
 * Budget Construction Organization Account Funding Detail Report Business Object.
 */
public class BudgetConstructionOrgSalarySummaryReportTotal {

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

   

    public BudgetConstructionSalarySocialSecurityNumber getBudgetConstructionSalarySocialSecurityNumber() {
        return budgetConstructionSalarySocialSecurityNumber;
    }

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
