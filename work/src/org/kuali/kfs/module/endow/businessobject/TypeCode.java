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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.KualiCodeBase;

/**
 * Business Object for Type Code.
 */
public class TypeCode extends KualiCodeBase {
    private static final Logger LOG = Logger.getLogger(TypeCode.class);

    private Integer cashSweepModelId;
    private Integer incomeACIModelId;
    private Integer principalACIModelId;

    private CashSweepModel cashSweepModel;
    private AutomatedCashInvestmentModel automatedCashInvestmentModelForPrincipalACIModelId;
    private AutomatedCashInvestmentModel automatedCashInvestmentModelForIncomeACIModelId;

    private List<TypeFeeMethod> typeFeeMethods;

    /**
     * Constructs a TypeCode object
     */
    public TypeCode() {
        super();
        typeFeeMethods = new ArrayList<TypeFeeMethod>();
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.TYPE_CODE, super.code);
        return m;
    }

    /**
     * This method gets the incomeACIModelId.
     * 
     * @return incomeACIModelId
     */
    public Integer getIncomeACIModelId() {
        return incomeACIModelId;
    }

    /**
     * This method sets the incomeACIModelId.
     * 
     * @param incomeACIModelId
     */
    public void setIncomeACIModelId(Integer incomeACIModelId) {
        this.incomeACIModelId = incomeACIModelId;
    }

    /**
     * This method gets the cashSweepModelId
     * 
     * @return cashSweepModelId
     */
    public Integer getCashSweepModelId() {
        return cashSweepModelId;
    }

    /**
     * This method sets the dividendPayDate.
     * 
     * @param dividendPayDate
     */
    public void setCashSweepModelId(Integer cashSweepModelId) {
        this.cashSweepModelId = cashSweepModelId;
    }

    /**
     * This method gets the principalACIModelId.
     * 
     * @return principalACIModelId
     */
    public Integer getPrincipalACIModelId() {
        return principalACIModelId;
    }

    /**
     * This method sets the principalACIModelId.
     * 
     * @param principalACIModelId
     */
    public void setPrincipalACIModelId(Integer principalACIModelId) {
        this.principalACIModelId = principalACIModelId;
    }

    /**
     * This method returns the cashSweepModel.
     * 
     * @return cashSweepModel
     */
    public CashSweepModel getCashSweepModel() {
        return cashSweepModel;
    }

    /**
     * This method sets the cashSweepModel.
     * 
     * @param cashSweepModel
     */
    public void setCashSweepModel(CashSweepModel cashSweepModel) {
        this.cashSweepModel = cashSweepModel;
    }

    /**
     * This method gets the automatedCashInvestmentModelForIncomeACIModelId.
     * 
     * @return automatedCashInvestmentModelForIncomeACIModelId
     */
    public AutomatedCashInvestmentModel getAutomatedCashInvestmentModelForIncomeACIModelId() {
        return automatedCashInvestmentModelForIncomeACIModelId;
    }

    /**
     * This method sets the automatedCashInvestmentModelForIncomeACIModelId.
     * 
     * @param automatedCashInvestmentModelForIncomeACIModelId
     */
    public void setAutomatedCashInvestmentModelForIncomeACIModelId(AutomatedCashInvestmentModel automatedCashInvestmentModelForIncomeACIModelId) {
        this.automatedCashInvestmentModelForIncomeACIModelId = automatedCashInvestmentModelForIncomeACIModelId;
    }

    /**
     * This method gets the automatedCashInvestmentModelForPrincipalACIModelId.
     * 
     * @return automatedCashInvestmentModelForPrincipalACIModelId
     */
    public AutomatedCashInvestmentModel getAutomatedCashInvestmentModelForPrincipalACIModelId() {
        return automatedCashInvestmentModelForPrincipalACIModelId;
    }

    /**
     * This method sets the automatedCashInvestmentModelForPrincipalACIModelId.
     * 
     * @param automatedCashInvestmentModelForPrincipalACIModelId
     */
    public void setAutomatedCashInvestmentModelForPrincipalACIModelId(AutomatedCashInvestmentModel automatedCashInvestmentModelForPrincipalACIModelId) {
        this.automatedCashInvestmentModelForPrincipalACIModelId = automatedCashInvestmentModelForPrincipalACIModelId;
    }

    /**
     * Gets the cashSweepModel description.
     * 
     * @return cashSweepModel description
     */
    public String getCashSweepModelDesc() {

        if (cashSweepModel != null) {
            return cashSweepModel.getCashSweepModelName();
        }
        else
            return KFSConstants.EMPTY_STRING;
    }

    /**
     * Gets the automatedCashInvestmentModelForPrincipalACIModelId description.
     * 
     * @return automatedCashInvestmentModelForPrincipalACIModelId description
     */
    public String getAutomatedCashInvestmentModelForPrincipalACIModelIdDesc() {

        if (automatedCashInvestmentModelForPrincipalACIModelId != null) {
            return automatedCashInvestmentModelForPrincipalACIModelId.getAciModelName();
        }
        else
            return KFSConstants.EMPTY_STRING;
    }

    /**
     * Gets the automatedCashInvestmentModelForIncomeACIModelId description.
     * 
     * @return automatedCashInvestmentModelForIncomeACIModelId description
     */
    public String getAutomatedCashInvestmentModelForIncomeACIModelIdDesc() {

        if (automatedCashInvestmentModelForIncomeACIModelId != null) {
            return automatedCashInvestmentModelForIncomeACIModelId.getAciModelName();
        }
        else
            return KFSConstants.EMPTY_STRING;
    }

    /**
     * Gets the typeFeeMethods attribute.
     * 
     * @return Returns the typeFeeMethods.
     */
    public List<TypeFeeMethod> getTypeFeeMethods() {
        return typeFeeMethods;
    }

    /**
     * Sets the typeFeeMethods attribute value.
     * 
     * @param typeFeeMethod The typeFeeMethods to set.
     */
    public void setTypeFeeMethods(List<TypeFeeMethod> typeFeeMethods) {
        this.typeFeeMethods = typeFeeMethods;
    }

    /**
     * @see org.kuali.rice.krad.bo.KualiCodeBase#getCodeAndDescription()
     */
    @Override
    public String getCodeAndDescription() {
        if (StringUtils.isEmpty(code)) {
            return KFSConstants.EMPTY_STRING;
        }
        return super.getCodeAndDescription();
    }

    /**
     * Gets the code for report
     * 
     * @return code
     */
    public String getCodeForReport() {
        return code;
    }
}
