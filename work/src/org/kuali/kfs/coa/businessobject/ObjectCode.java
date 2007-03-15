/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.KualiCode;
import org.kuali.core.bo.Summarizable;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.impl.PersistenceStructureServiceImpl;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.codes.BudgetAggregationCode;
import org.kuali.module.chart.bo.codes.FederalFundedCode;
import org.kuali.module.chart.bo.codes.MandatoryTransferEliminationCode;
import org.kuali.module.gl.bo.SufficientFundRebuild;

/**
 * 
 */
public class ObjectCode extends PersistableBusinessObjectBase implements Summarizable {
    
    
    static {
        PersistenceStructureServiceImpl.referenceConversionMap.put(ObjectCode.class, ObjectCodeCurrent.class);
    }
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectCode.class);

    private static final long serialVersionUID = -965833141452795485L;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String financialObjectCode;
    private String financialObjectCodeName;
    private String financialObjectCodeShortName;
    private String historicalFinancialObjectCode;
    private boolean financialObjectActiveCode;
    private String financialObjectLevelCode;
    private String reportsToChartOfAccountsCode;
    private String reportsToFinancialObjectCode;
    private String financialObjectTypeCode;
    private String financialObjectSubTypeCode;
    private String financialBudgetAggregationCd;
    private String nextYearFinancialObjectCode;
    private String finObjMandatoryTrnfrelimCd;
    private String financialFederalFundedCode;

    private transient BudgetAggregationCode financialBudgetAggregation;
    private transient MandatoryTransferEliminationCode finObjMandatoryTrnfrelim;
    private transient FederalFundedCode financialFederalFunded;
    private transient Options universityFiscal;
    private transient ObjectCode nextYearFinancialObject;
    private transient ObjLevel financialObjectLevel;
    private transient Chart chartOfAccounts;
    private transient Chart reportsToChartOfAccounts;
    private transient ObjectCode reportsToFinancialObject;
    private transient ObjectType financialObjectType;
    private transient ObjSubTyp financialObjectSubType;
    private transient ObjectCode objectCode;

    /**
     * Default no-arg constructor.
     */
    public ObjectCode() {
        // initialize the object fiscal year to the current fiscal year
        // universityFiscalYear = SpringServiceLocator.getDateTimeService().getCurrentFiscalYear();

        // construct the referenced objects for the calling of the referencing object
        this.financialObjectLevel = new ObjLevel();
        this.financialObjectType = new ObjectType();
    }

    /**
     * 
     * Constructs a ObjectCode.java with the given defaults; this way, it is not necessary to use any deprecated setters.
     * 
     * @param fiscalYear
     * @param chart
     * @param financialObjectCode - an active object code
     */
    public ObjectCode(Integer fiscalYear, String chart, String financialObjectCode) {
        this.universityFiscalYear = fiscalYear;
        this.chartOfAccountsCode = chart;
        this.financialObjectCode = financialObjectCode;
        this.financialObjectActiveCode = true;
    }

    /**
     * This method is only for use by the framework
     */
    public void setUniversityFiscalYear(Integer i) {
        this.universityFiscalYear = i;
    }

    /**
     * Gets the financialFederalFunded attribute.
     * 
     * @return Returns the financialFederalFunded.
     */
    public FederalFundedCode getFinancialFederalFunded() {
        return financialFederalFunded;
    }

    /**
     * Sets the financialFederalFunded attribute value.
     * 
     * @param financialFederalFunded The financialFederalFunded to set.
     * @deprecated
     */
    public void setFinancialFederalFunded(FederalFundedCode financialFederalFunded) {
        this.financialFederalFunded = financialFederalFunded;
    }

    /**
     * Gets the finObjMandatoryTrnfrelim attribute.
     * 
     * @return Returns the finObjMandatoryTrnfrelimCd.
     */
    public MandatoryTransferEliminationCode getFinObjMandatoryTrnfrelim() {
        return finObjMandatoryTrnfrelim;
    }


    /**
     * Sets the finObjMandatoryTrnfrelim attribute value.
     * 
     * @param finObjMandatoryTrnfrelim The finObjMandatoryTrnfrelim to set.
     * @deprecated
     */
    public void setFinObjMandatoryTrnfrelim(MandatoryTransferEliminationCode finObjMandatoryTrnfrelim) {
        this.finObjMandatoryTrnfrelim = finObjMandatoryTrnfrelim;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode
     * 
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     * 
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialObjectCodeName attribute.
     * 
     * @return Returns the financialObjectCodeName
     * 
     */
    public String getFinancialObjectCodeName() {
        return financialObjectCodeName;
    }

    /**
     * Sets the financialObjectCodeName attribute.
     * 
     * @param financialObjectCodeName The financialObjectCodeName to set.
     * 
     */
    public void setFinancialObjectCodeName(String financialObjectCodeName) {
        this.financialObjectCodeName = financialObjectCodeName;
    }

    /**
     * Gets the financialObjectCodeShortName attribute.
     * 
     * @return Returns the financialObjectCodeShortName
     * 
     */
    public String getFinancialObjectCodeShortName() {
        return financialObjectCodeShortName;
    }

    /**
     * Sets the financialObjectCodeShortName attribute.
     * 
     * @param financialObjectCodeShortName The financialObjectCodeShortName to set.
     * 
     */
    public void setFinancialObjectCodeShortName(String financialObjectCodeShortName) {
        this.financialObjectCodeShortName = financialObjectCodeShortName;
    }

    /**
     * Gets the historicalFinancialObjectCode attribute.
     * 
     * @return Returns the historicalFinancialObjectCode
     * 
     */
    public String getHistoricalFinancialObjectCode() {
        return historicalFinancialObjectCode;
    }

    /**
     * Sets the historicalFinancialObjectCode attribute.
     * 
     * @param historicalFinancialObjectCode The historicalFinancialObjectCode to set.
     * 
     */
    public void setHistoricalFinancialObjectCode(String historicalFinancialObjectCode) {
        this.historicalFinancialObjectCode = historicalFinancialObjectCode;
    }

    /**
     * Gets the financialObjectActiveCode attribute.
     * 
     * @return Returns the financialObjectActiveCode
     * 
     */
    public boolean isFinancialObjectActiveCode() {
        return financialObjectActiveCode;
    }

    /**
     * Sets the financialObjectActiveCode attribute.
     * 
     * @param financialObjectActiveCode The financialObjectActiveCode to set.
     * 
     */
    public void setFinancialObjectActiveCode(boolean financialObjectActiveCode) {
        this.financialObjectActiveCode = financialObjectActiveCode;
    }

    /**
     * Gets the financialBudgetAggregationCd attribute.
     * 
     * @return Returns the financialBudgetAggregationCd
     * 
     */
    /*public BudgetAggregationCode getFinancialBudgetAggregation() {
        return financialBudgetAggregation;
    }*/

    /**
     * Sets the financialBudgetAggregationCd attribute.
     * 
     * @param financialBudgetAggregationCd The financialBudgetAggregationCd to set.
     * @deprecated
     */
    /*public void setFinancialBudgetAggregation(BudgetAggregationCode financialBudgetAggregationCd) {
        this.financialBudgetAggregation = financialBudgetAggregationCd;
    }*/

    /**
     * Gets the universityFiscal attribute.
     * 
     * @return Returns the universityFiscal
     * 
     */
    public Options getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal attribute.
     * 
     * @param universityFiscal The universityFiscal to set.
     * @deprecated
     */
    public void setUniversityFiscal(Options universityFiscal) {
        this.universityFiscal = universityFiscal;
    }

    /**
     * Gets the nextYearFinancialObject attribute.
     * 
     * @return Returns the nextYearFinancialObject
     * 
     */
    public ObjectCode getNextYearFinancialObject() {
        return nextYearFinancialObject;
    }

    /**
     * Sets the nextYearFinancialObject attribute.
     * 
     * @param nextYearFinancialObject The nextYearFinancialObject to set.
     * @deprecated
     */
    public void setNextYearFinancialObject(ObjectCode nextYearFinancialObject) {
        this.nextYearFinancialObject = nextYearFinancialObject;
    }

    /**
     * Gets the financialObjectLevel attribute.
     * 
     * @return Returns the financialObjectLevel
     * 
     */
    public ObjLevel getFinancialObjectLevel() {
        return financialObjectLevel;
    }

    /**
     * Sets the financialObjectLevel attribute.
     * 
     * @param financialObjectLevel The financialObjectLevel to set.
     * @deprecated
     */
    public void setFinancialObjectLevel(ObjLevel financialObjectLevel) {
        this.financialObjectLevel = financialObjectLevel;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     * 
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the reportsToChartOfAccounts attribute.
     * 
     * @return Returns the reportsToChartOfAccounts
     * 
     */
    public Chart getReportsToChartOfAccounts() {
        return reportsToChartOfAccounts;
    }

    /**
     * Sets the reportsToChartOfAccounts attribute.
     * 
     * @param reportsToChartOfAccounts The reportsToChartOfAccounts to set.
     * @deprecated
     */
    public void setReportsToChartOfAccounts(Chart reportsToChartOfAccounts) {
        this.reportsToChartOfAccounts = reportsToChartOfAccounts;
    }

    /**
     * Gets the reportsToFinancialObject attribute.
     * 
     * @return Returns the reportsToFinancialObject
     * 
     */
    public ObjectCode getReportsToFinancialObject() {
        return reportsToFinancialObject;
    }

    /**
     * Sets the reportsToFinancialObject attribute.
     * 
     * @param reportsToFinancialObject The reportsToFinancialObject to set.
     * @deprecated
     */
    public void setReportsToFinancialObject(ObjectCode reportsToFinancialObject) {
        this.reportsToFinancialObject = reportsToFinancialObject;
    }

    /**
     * Gets the financialObjectType attribute.
     * 
     * @return Returns the financialObjectType
     * 
     */
    public ObjectType getFinancialObjectType() {
        return financialObjectType;
    }

    /**
     * Sets the financialObjectType attribute.
     * 
     * @param financialObjectType The financialObjectType to set.
     * @deprecated
     */
    public void setFinancialObjectType(ObjectType financialObjectType) {
        this.financialObjectType = financialObjectType;
    }

    /**
     * Gets the financialObjectSubType attribute.
     * 
     * @return Returns the financialObjectSubType
     * 
     */
    public ObjSubTyp getFinancialObjectSubType() {
        return financialObjectSubType;
    }

    /**
     * Sets the financialObjectSubType attribute.
     * 
     * @param financialObjectSubType The financialObjectSubType to set.
     * @deprecated
     */
    public void setFinancialObjectSubType(ObjSubTyp financialObjectSubType) {
        this.financialObjectSubType = financialObjectSubType;
    }

    /**
     */
    public void setChartOfAccountsCode(String string) {
        this.chartOfAccountsCode = string;
    }

    /**
     * 
     */
    public String getChartOfAccountsCode() {
        return this.chartOfAccountsCode;
    }

    /**
     * 
     */
    public Integer getUniversityFiscalYear() {
        return this.universityFiscalYear;
    }

    /**
     * @return Returns the financialBudgetAggregationCd.
     */
    public String getFinancialBudgetAggregationCd() {
        return financialBudgetAggregationCd;
    }

    /**
     * @param financialBudgetAggregationCd The financialBudgetAggregationCd to set.
     */
    public void setFinancialBudgetAggregationCd(String financialBudgetAggregationCd) {
        this.financialBudgetAggregationCd = financialBudgetAggregationCd;
    }

    /**
     * @return Returns the financialObjectLevelCode.
     */
    public String getFinancialObjectLevelCode() {
        return financialObjectLevelCode;
    }

    /**
     * @param financialObjectLevelCode The financialObjectLevelCode to set.
     */
    public void setFinancialObjectLevelCode(String financialObjectLevelCode) {
        this.financialObjectLevelCode = financialObjectLevelCode;
    }

    /**
     * @return Returns the financialObjectSubTypeCode.
     */
    public String getFinancialObjectSubTypeCode() {
        return financialObjectSubTypeCode;
    }

    /**
     * @param financialObjectSubTypeCode The financialObjectSubTypeCode to set.
     */
    public void setFinancialObjectSubTypeCode(String financialObjectSubTypeCode) {
        this.financialObjectSubTypeCode = financialObjectSubTypeCode;
    }

    /**
     * @return Returns the financialObjectTypeCode.
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * @return Returns the nextYearFinancialObjectCode.
     */
    public String getNextYearFinancialObjectCode() {
        return nextYearFinancialObjectCode;
    }

    /**
     * @param nextYearFinancialObjectCode The nextYearFinancialObjectCode to set.
     */
    public void setNextYearFinancialObjectCode(String nextYearFinancialObjectCode) {
        this.nextYearFinancialObjectCode = nextYearFinancialObjectCode;
    }

    /**
     * @return Returns the reportsToChartOfAccountsCode.
     */
    public String getReportsToChartOfAccountsCode() {
        return reportsToChartOfAccountsCode;
    }

    /**
     * @param reportsToChartOfAccountsCode The reportsToChartOfAccountsCode to set.
     */
    public void setReportsToChartOfAccountsCode(String reportsToChartOfAccountsCode) {
        this.reportsToChartOfAccountsCode = reportsToChartOfAccountsCode;
    }

    /**
     * @return Returns the reportsToFinancialObjectCode.
     */
    public String getReportsToFinancialObjectCode() {
        return reportsToFinancialObjectCode;
    }

    /**
     * @param reportsToFinancialObjectCode The reportsToFinancialObjectCode to set.
     */
    public void setReportsToFinancialObjectCode(String reportsToFinancialObjectCode) {
        this.reportsToFinancialObjectCode = reportsToFinancialObjectCode;
    }

    /**
     * @return Returns the financialFederalFundedCode.
     */
    public String getFinancialFederalFundedCode() {
        return financialFederalFundedCode;
    }

    /**
     * @param financialFederalFundedCode The financialFederalFundedCode to set.
     */
    public void setFinancialFederalFundedCode(String financialFederalFundedCode) {
        this.financialFederalFundedCode = financialFederalFundedCode;
    }

    /**
     * @return Returns the finObjMandatoryTrnfrelimCd.
     */
    public String getFinObjMandatoryTrnfrelimCd() {
        return finObjMandatoryTrnfrelimCd;
    }

    /**
     * @param finObjMandatoryTrnfrelimCd The finObjMandatoryTrnfrelimCd to set.
     */
    public void setFinObjMandatoryTrnfrelimCd(String finObjMandatoryTrnfrelimCd) {
        this.finObjMandatoryTrnfrelimCd = finObjMandatoryTrnfrelimCd;
    }

    /**
     * @return Returns the objectCode.
     */
    public ObjectCode getObjectCode() {
        return objectCode;
    }

    /**
     * @param objectCode The objectCode to set.
     * @deprecated
     */
    public void setObjectCode(ObjectCode objectCode) {
        this.objectCode = objectCode;
    }

    public BudgetAggregationCode getFinancialBudgetAggregation() {
        return financialBudgetAggregation;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {

        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("financialObjectCode", this.financialObjectCode);

        return m;
    }

    @Override
    public void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        super.beforeUpdate(persistenceBroker);
        try {
            // KULCOA-549: update the sufficient funds table
            // get the current data from the database
            BusinessObjectService boService = SpringServiceLocator.getBusinessObjectService();
            ObjectCode originalObjectCode = (ObjectCode) boService.retrieve(this);

            if (originalObjectCode != null) {
                if (!originalObjectCode.getFinancialObjectLevelCode().equals(getFinancialObjectLevelCode())) {
                    SufficientFundRebuild sfr = new SufficientFundRebuild();
                    sfr.setAccountFinancialObjectTypeCode(SufficientFundRebuild.REBUILD_OBJECT);
                    sfr.setChartOfAccountsCode(originalObjectCode.getChartOfAccountsCode());
                    sfr.setAccountNumberFinancialObjectCode(originalObjectCode.getFinancialObjectLevelCode());
                    if (boService.retrieve(sfr) == null) {
                        persistenceBroker.store(sfr);
                    }
                    sfr = new SufficientFundRebuild();
                    sfr.setAccountFinancialObjectTypeCode(SufficientFundRebuild.REBUILD_OBJECT);
                    sfr.setChartOfAccountsCode(getChartOfAccountsCode());
                    sfr.setAccountNumberFinancialObjectCode(getFinancialObjectLevelCode());
                    if (boService.retrieve(sfr) == null) {
                        persistenceBroker.store(sfr);
                    }
                }
            }
        }
        catch (Exception ex) {
            LOG.error("Problem updating sufficient funds rebuild table: ", ex);
        }
    }

    public boolean isActive() {
        return this.financialObjectActiveCode;
    }

    public void setActive(boolean a) {
        this.financialObjectActiveCode=a;
    }

    public void setCode(String code) {
        this.chartOfAccountsCode=code;
    }

    public void setName(String name) {
        this.financialObjectCodeName=name;
    }

    public String getCode() {
        return this.financialObjectCode;
    }

    public String getName() {
        return this.financialObjectCodeName;
    }

}