/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;
import java.util.List;
import org.kuali.rice.kns.util.TypedArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.kns.bo.KualiCodeBase;
import org.kuali.kfs.module.endow.businessobject.TypeRestrictionCode;
import org.kuali.kfs.module.endow.businessobject.TypeFeeMethod;
import org.kuali.kfs.module.endow.businessobject.CashSweepModel;
import org.kuali.kfs.module.endow.businessobject.AutomatedCashInvestmentModel;
import org.kuali.kfs.sys.KFSConstants;

/**
 * Business Object for Type Code.
 */
public class TypeCode extends KualiCodeBase {
    private static final Logger LOG = Logger.getLogger(TypeCode.class);

    private String incomeRestrictionCode;
    private String principalRestrictionCode;
    private Integer cashSweepModelId;
    private Integer incomeACIModelId;
    private Integer principalACIModelId;

    private TypeRestrictionCode typeRestrictionCodeForIncomeRestrictionCode;
    private TypeRestrictionCode typeRestrictionCodeForPrincipalRestrictionCode;
    private CashSweepModel cashSweepModel;
    private AutomatedCashInvestmentModel automatedCashInvestmentModelForPrincipalACIModelId;
    private AutomatedCashInvestmentModel automatedCashInvestmentModelForIncomeACIModelId;

    private List<TypeFeeMethod> typeFeeMethods;

    /**
     * Constructs a TypeCode object
     */
    public TypeCode() {
        super();
        typeFeeMethods = new TypedArrayList(TypeFeeMethod.class);
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.TYPE_CODE, super.code);
        return m;
    }

    /**
     * This method gets incomeRestrictionCode
     * 
     * @return incomeRestrictionCode
     */
    public String getIncomeRestrictionCode() {
        return incomeRestrictionCode;
    }

    /**
     * This method sets incomeRestrictionCode.
     * 
     * @param incomeRestrictionCode
     */
    public void setIncomeRestrictionCode(String incomeRestrictionCode) {
        this.incomeRestrictionCode = incomeRestrictionCode;
    }

    /**
     * This method gets the principalRestrictionCode.
     * 
     * @return principalRestrictionCode
     */
    public String getPrincipalRestrictionCode() {
        return principalRestrictionCode;
    }

    /**
     * This method sets the principalRestrictionCode.
     * 
     * @param principalRestrictionCode
     */
    public void setPrincipalRestrictionCode(String principalRestrictionCode) {
        this.principalRestrictionCode = principalRestrictionCode;
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
     * This method gets the typeRestrictionCodeForIncomeRestrictionCode.
     * 
     * @return typeRestrictionCodeForIncomeRestrictionCode
     */
    public TypeRestrictionCode getTypeRestrictionCodeForPrincipalRestrictionCode() {
        return typeRestrictionCodeForPrincipalRestrictionCode;
    }

    /**
     * This method sets the typeRestrictionCodeForPrincipalRestrictionCode.
     * 
     * @param typeRestrictionCodeForPrincipalRestrictionCode
     */
    public void setTypeRestrictionCodeForPrincipalRestrictionCode(TypeRestrictionCode typeRestrictionCodeForPrincipalRestrictionCode) {
        this.typeRestrictionCodeForPrincipalRestrictionCode = typeRestrictionCodeForPrincipalRestrictionCode;
    }

    /**
     * This method gets the typeRestrictionCodeForIncomeRestrictionCode.
     * 
     * @return typeRestrictionCodeForIncomeRestrictionCode
     */
    public TypeRestrictionCode getTypeRestrictionCodeForIncomeRestrictionCode() {
        return typeRestrictionCodeForIncomeRestrictionCode;
    }

    /**
     * This method sets the typeRestrictionCodeForIncomeRestrictionCode.
     * 
     * @param typeRestrictionCodeForIncomeRestrictionCode
     */
    public void setTypeRestrictionCodeForIncomeRestrictionCode(TypeRestrictionCode typeRestrictionCodeForIncomeRestrictionCode) {
        this.typeRestrictionCodeForIncomeRestrictionCode = typeRestrictionCodeForIncomeRestrictionCode;
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
     * Gets the typeRestrictionCodeForIncomeRestrictionCode description.
     * 
     * @return typeRestrictionCodeForIncomeRestrictionCode description
     */
    public String getTypeRestrictionCodeForIncomeRestrictionCodeDesc() {

        if (typeRestrictionCodeForIncomeRestrictionCode != null) {
            return typeRestrictionCodeForIncomeRestrictionCode.getCodeAndDescription();
        }
        else
            return KFSConstants.EMPTY_STRING;
    }

    /**
     * Gets the typeRestrictionCodeForPrincipalRestrictionCode description.
     * 
     * @return typeRestrictionCodeForPrincipalRestrictionCode description
     */
    public String getTypeRestrictionCodeForPrincipalRestrictionCodeDesc() {

        if (typeRestrictionCodeForPrincipalRestrictionCode != null) {
            return typeRestrictionCodeForPrincipalRestrictionCode.getCodeAndDescription();
        }
        else
            return KFSConstants.EMPTY_STRING;
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
     * @see org.kuali.rice.kns.bo.KualiCodeBase#getCodeAndDescription()
     */
    @Override
    public String getCodeAndDescription() {
        if (StringUtils.isEmpty(code)) {
            return KFSConstants.EMPTY_STRING;
        }
        return super.getCodeAndDescription();
    }

}
