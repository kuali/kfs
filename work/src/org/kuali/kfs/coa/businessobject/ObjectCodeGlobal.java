/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.kfs.coa.businessobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.GlobalBusinessObject;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetail;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 *
 */
public class ObjectCodeGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectCodeGlobal.class);

    protected String documentNumber;
    protected Integer universityFiscalYear;
    protected String chartOfAccountsCode;
    protected String financialObjectCode;
    protected String financialObjectCodeName;
    protected String financialObjectCodeShortName;
    protected String financialObjectLevelCode;
    protected String reportsToChartOfAccountsCode;
    protected String reportsToFinancialObjectCode;
    protected String financialObjectTypeCode;
    protected String financialObjectSubTypeCode;
    protected String historicalFinancialObjectCode;
    protected boolean financialObjectActiveIndicator;
    protected String financialBudgetAggregationCd;
    protected String finObjMandatoryTrnfrOrElimCd;
    protected String financialFederalFundedCode;
    protected String nextYearFinancialObjectCode;

    protected DocumentHeader financialDocument;
    protected ObjectCode financialObject;
    protected ObjectCode reportsToFinancialObject;
    protected SystemOptions universityFiscal;
    protected Chart chartOfAccounts;
    protected Chart reportsToChartOfAccounts;
    protected ObjectType financialObjectType;
    protected ObjectSubType financialObjectSubType;
    protected ObjectLevel financialObjectLevel;
    protected BudgetAggregationCode financialBudgetAggregation;
    protected MandatoryTransferEliminationCode finObjMandatoryTrnfrelim;
    protected FederalFundedCode financialFederalFunded;

    protected List<ObjectCodeGlobalDetail> objectCodeGlobalDetails;

    /**
     * Default constructor.
     */
    public ObjectCodeGlobal() {
        objectCodeGlobalDetails = new ArrayList<ObjectCodeGlobalDetail>();
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    @Override
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    @Override
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the universityFiscalYear attribute.
     *
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     *
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the financialObjectCode attribute.
     *
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     *
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }


    /**
     * Gets the financialObjectCodeName attribute.
     *
     * @return Returns the financialObjectCodeName
     */
    public String getFinancialObjectCodeName() {
        return financialObjectCodeName;
    }

    /**
     * Sets the financialObjectCodeName attribute.
     *
     * @param financialObjectCodeName The financialObjectCodeName to set.
     */
    public void setFinancialObjectCodeName(String financialObjectCodeName) {
        this.financialObjectCodeName = financialObjectCodeName;
    }


    /**
     * Gets the financialObjectCodeShortName attribute.
     *
     * @return Returns the financialObjectCodeShortName
     */
    public String getFinancialObjectCodeShortName() {
        return financialObjectCodeShortName;
    }

    /**
     * Sets the financialObjectCodeShortName attribute.
     *
     * @param financialObjectCodeShortName The financialObjectCodeShortName to set.
     */
    public void setFinancialObjectCodeShortName(String financialObjectCodeShortName) {
        this.financialObjectCodeShortName = financialObjectCodeShortName;
    }


    /**
     * Gets the financialObjectLevelCode attribute.
     *
     * @return Returns the financialObjectLevelCode
     */
    public String getFinancialObjectLevelCode() {
        return financialObjectLevelCode;
    }

    /**
     * Sets the financialObjectLevelCode attribute.
     *
     * @param financialObjectLevelCode The financialObjectLevelCode to set.
     */
    public void setFinancialObjectLevelCode(String financialObjectLevelCode) {
        this.financialObjectLevelCode = financialObjectLevelCode;
    }


    /**
     * Gets the reportsToChartOfAccountsCode attribute.
     *
     * @return Returns the reportsToChartOfAccountsCode
     */
    public String getReportsToChartOfAccountsCode() {
        return reportsToChartOfAccountsCode;
    }

    /**
     * Sets the reportsToChartOfAccountsCode attribute.
     *
     * @param reportsToChartOfAccountsCode The reportsToChartOfAccountsCode to set.
     */
    public void setReportsToChartOfAccountsCode(String reportsToChartOfAccountsCode) {
        this.reportsToChartOfAccountsCode = reportsToChartOfAccountsCode;
    }


    /**
     * Gets the reportsToFinancialObjectCode attribute.
     *
     * @return Returns the reportsToFinancialObjectCode
     */
    public String getReportsToFinancialObjectCode() {
        return reportsToFinancialObjectCode;
    }

    /**
     * Sets the reportsToFinancialObjectCode attribute.
     *
     * @param reportsToFinancialObjectCode The reportsToFinancialObjectCode to set.
     */
    public void setReportsToFinancialObjectCode(String reportsToFinancialObjectCode) {
        this.reportsToFinancialObjectCode = reportsToFinancialObjectCode;
    }


    /**
     * Gets the financialObjectTypeCode attribute.
     *
     * @return Returns the financialObjectTypeCode
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute.
     *
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }


    /**
     * Gets the financialObjectSubTypeCode attribute.
     *
     * @return Returns the financialObjectSubTypeCode
     */
    public String getFinancialObjectSubTypeCode() {
        return financialObjectSubTypeCode;
    }

    /**
     * Sets the financialObjectSubTypeCode attribute.
     *
     * @param financialObjectSubTypeCode The financialObjectSubTypeCode to set.
     */
    public void setFinancialObjectSubTypeCode(String financialObjectSubTypeCode) {
        this.financialObjectSubTypeCode = financialObjectSubTypeCode;
    }


    /**
     * Gets the historicalFinancialObjectCode attribute.
     *
     * @return Returns the historicalFinancialObjectCode
     */
    public String getHistoricalFinancialObjectCode() {
        return historicalFinancialObjectCode;
    }

    /**
     * Sets the historicalFinancialObjectCode attribute.
     *
     * @param historicalFinancialObjectCode The historicalFinancialObjectCode to set.
     */
    public void setHistoricalFinancialObjectCode(String historicalFinancialObjectCode) {
        this.historicalFinancialObjectCode = historicalFinancialObjectCode;
    }


    /**
     * Gets the financialObjectActiveIndicator attribute.
     *
     * @return Returns the financialObjectActiveIndicator
     */
    public boolean isFinancialObjectActiveIndicator() {
        return financialObjectActiveIndicator;
    }


    /**
     * Sets the financialObjectActiveIndicator attribute.
     *
     * @param financialObjectActiveIndicator The financialObjectActiveIndicator to set.
     */
    public void setFinancialObjectActiveIndicator(boolean financialObjectActiveIndicator) {
        this.financialObjectActiveIndicator = financialObjectActiveIndicator;
    }


    /**
     * Gets the financialBudgetAggregationCd attribute.
     *
     * @return Returns the financialBudgetAggregationCd
     */
    public String getFinancialBudgetAggregationCd() {
        return financialBudgetAggregationCd;
    }

    /**
     * Sets the financialBudgetAggregationCd attribute.
     *
     * @param financialBudgetAggregationCd The financialBudgetAggregationCd to set.
     */
    public void setFinancialBudgetAggregationCd(String financialBudgetAggregationCd) {
        this.financialBudgetAggregationCd = financialBudgetAggregationCd;
    }


    /**
     * Gets the finObjMandatoryTrnfrOrElimCd attribute.
     *
     * @return Returns the finObjMandatoryTrnfrOrElimCd
     */
    public String getFinObjMandatoryTrnfrOrElimCd() {
        return finObjMandatoryTrnfrOrElimCd;
    }

    /**
     * Sets the finObjMandatoryTrnfrOrElimCd attribute.
     *
     * @param finObjMandatoryTrnfrOrElimCd The finObjMandatoryTrnfrOrElimCd to set.
     */
    public void setFinObjMandatoryTrnfrOrElimCd(String finObjMandatoryTrnfrOrElimCd) {
        this.finObjMandatoryTrnfrOrElimCd = finObjMandatoryTrnfrOrElimCd;
    }


    /**
     * Gets the financialFederalFundedCode attribute.
     *
     * @return Returns the financialFederalFundedCode
     */
    public String getFinancialFederalFundedCode() {
        return financialFederalFundedCode;
    }

    /**
     * Sets the financialFederalFundedCode attribute.
     *
     * @param financialFederalFundedCode The financialFederalFundedCode to set.
     */
    public void setFinancialFederalFundedCode(String financialFederalFundedCode) {
        this.financialFederalFundedCode = financialFederalFundedCode;
    }


    /**
     * Gets the nextYearFinancialObjectCode attribute.
     *
     * @return Returns the nextYearFinancialObjectCode
     */
    public String getNextYearFinancialObjectCode() {
        return nextYearFinancialObjectCode;
    }

    /**
     * Sets the nextYearFinancialObjectCode attribute.
     *
     * @param nextYearFinancialObjectCode The nextYearFinancialObjectCode to set.
     */
    public void setNextYearFinancialObjectCode(String nextYearFinancialObjectCode) {
        this.nextYearFinancialObjectCode = nextYearFinancialObjectCode;
    }


    /**
     * Gets the financialDocument attribute.
     *
     * @return Returns the financialDocument
     */
    public DocumentHeader getFinancialDocument() {
        return financialDocument;
    }

    /**
     * Sets the financialDocument attribute.
     *
     * @param financialDocument The financialDocument to set.
     * @deprecated
     */
    @Deprecated
    public void setFinancialDocument(DocumentHeader financialDocument) {
        this.financialDocument = financialDocument;
    }

    /**
     * Gets the financialObject attribute.
     *
     * @return Returns the financialObject
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject attribute.
     *
     * @param financialObject The financialObject to set.
     * @deprecated
     */
    @Deprecated
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the reportsToFinancialObject attribute.
     *
     * @return Returns the reportsToFinancialObject
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
    @Deprecated
    public void setReportsToFinancialObject(ObjectCode reportsToFinancialObject) {
        this.reportsToFinancialObject = reportsToFinancialObject;
    }

    /**
     * Gets the universityFiscal attribute.
     *
     * @return Returns the universityFiscal
     */
    public SystemOptions getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal attribute.
     *
     * @param universityFiscal The universityFiscal to set.
     * @deprecated
     */
    @Deprecated
    public void setUniversityFiscal(SystemOptions universityFiscal) {
        this.universityFiscal = universityFiscal;
    }

    /**
     * Gets the chartOfAccounts attribute.
     *
     * @return Returns the chartOfAccounts
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
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the reportsToChartOfAccounts attribute.
     *
     * @return Returns the reportsToChartOfAccounts
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
    @Deprecated
    public void setReportsToChartOfAccounts(Chart reportsToChartOfAccounts) {
        this.reportsToChartOfAccounts = reportsToChartOfAccounts;
    }

    /**
     * This method returns the FinancialObjectSubType attribute.
     *
     * @return Returns FinancialObjectSubType attribute.
     */
    public ObjectSubType getFinancialObjectSubType() {
        return financialObjectSubType;
    }

    /**
     * This method sets the FinancialObjectSubType attribute.
     *
     * @param financialObjectSubType The financialObjectSubType to set.
     * @deprecated
     */
    @Deprecated
    public void setFinancialObjectSubType(ObjectSubType financialObjectSubType) {
        this.financialObjectSubType = financialObjectSubType;
    }

    /**
     * This method returns the FinancialObjectType
     *
     * @return an ObjectType for this ObjectCodeGlobal.
     */
    public ObjectType getFinancialObjectType() {
        return financialObjectType;
    }

    /**
     * This method sets a FinancialObjectType
     *
     * @param financialObjectType the ObjectType to set.
     * @deprecated
     */
    @Deprecated
    public void setFinancialObjectType(ObjectType financialObjectType) {
        this.financialObjectType = financialObjectType;
    }

    /**
     * This method returns the FinancialBudgetAggregation
     *
     * @return the FinancialBudgetAggregation
     */
    public BudgetAggregationCode getFinancialBudgetAggregation() {
        return financialBudgetAggregation;
    }

    /**
     * This method sets a FinancialBudgetAggregation
     *
     * @param financialBudgetAggregation the BudgetAggregationCode to set
     * @deprecated
     */
    @Deprecated
    public void setFinancialBudgetAggregation(BudgetAggregationCode financialBudgetAggregation) {
        this.financialBudgetAggregation = financialBudgetAggregation;
    }

    /**
     * This method the financial federal funded code for this ObjectCodeGlobal
     *
     * @return the Federal Funded Code
     */
    public FederalFundedCode getFinancialFederalFunded() {
        return financialFederalFunded;
    }

    /**
     * This method sets FinancialFederalFunded
     *
     * @param financialFederalFunded the FederalFundedCode to set
     * @deprecated
     */
    @Deprecated
    public void setFinancialFederalFunded(FederalFundedCode financialFederalFunded) {
        this.financialFederalFunded = financialFederalFunded;
    }

    /**
     * This method returns the Object Level
     *
     * @return ObjectLevel
     */
    public ObjectLevel getFinancialObjectLevel() {
        return financialObjectLevel;
    }

    /**
     * This method sets the FinancialObjectLevel
     *
     * @param financialObjectLevel the ObjLevel to set
     * @deprecated
     */
    @Deprecated
    public void setFinancialObjectLevel(ObjectLevel financialObjectLevel) {
        this.financialObjectLevel = financialObjectLevel;
    }

    /**
     * This method returns the Mandatory Transfer or Elimination Code
     *
     * @return the MandatoryTransferEliminationCode
     */
    public MandatoryTransferEliminationCode getFinObjMandatoryTrnfrelim() {
        return finObjMandatoryTrnfrelim;
    }

    /**
     * This method pretty much sets the FinObjMandatoryTrnfrelm (the Mandatory Transfer or Elimination Code)
     *
     * @param finObjMandatoryTrnfrelim the MandatoryTransferEliminationCode to set
     * @deprecated
     */
    @Deprecated
    public void setFinObjMandatoryTrnfrelim(MandatoryTransferEliminationCode finObjMandatoryTrnfrelim) {
        this.finObjMandatoryTrnfrelim = finObjMandatoryTrnfrelim;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String,String> m = new LinkedHashMap<String,String>();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    public List<ObjectCodeGlobalDetail> getObjectCodeGlobalDetails() {
        return objectCodeGlobalDetails;
    }

    public void setObjectCodeGlobalDetails(List<ObjectCodeGlobalDetail> objectCodeGlobalDetails) {
        this.objectCodeGlobalDetails = objectCodeGlobalDetails;
    }

    /**
     * @see org.kuali.rice.krad.document.GlobalBusinessObject#getGlobalChangesToDelete()
     */
    @Override
    public List<PersistableBusinessObject> generateDeactivationsToPersist() {
        return null;
    }

    /**
     * This returns a list of Object Codes to Update and/or Add
     *
     * @see org.kuali.rice.krad.document.GlobalBusinessObject#applyGlobalChanges()
     */
    @Override
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
        LOG.debug("applyGlobalChanges");
        List<PersistableBusinessObject> persistables = new ArrayList<PersistableBusinessObject>();

        // Iterate through Object Codes; create new or update as necessary
        // Set reports-to Chart to appropriate value

        for (ObjectCodeGlobalDetail detail : objectCodeGlobalDetails) {

            Map pk = new HashMap();

            Integer fiscalYear = detail.getUniversityFiscalYear();
            String chart = detail.getChartOfAccountsCode();

            if (fiscalYear != null && chart != null && chart.length() > 0) {
                pk.put("UNIV_FISCAL_YR", fiscalYear);
                pk.put("FIN_COA_CD", chart);
                pk.put("FIN_OBJECT_CD", financialObjectCode);

                ObjectCode objectCode = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ObjectCode.class, pk);
                if (objectCode == null) {
                    objectCode = new ObjectCode(fiscalYear, chart, financialObjectCode);
                    objectCode.setFinancialObjectActiveCode(true);
                }
                populate(objectCode, detail);
                Map<String, String> hierarchy = SpringContext.getBean(ChartService.class).getReportsToHierarchy();
                objectCode.setReportsToChartOfAccountsCode(hierarchy.get(chart));

                persistables.add(objectCode);
            }
        }

        return persistables;
    }

    public void populate(ObjectCode old, ObjectCodeGlobalDetail detail) {

        old.setFinancialObjectCodeName(update(financialObjectCodeName, old.getFinancialObjectCodeName()));
        old.setFinancialObjectCodeShortName(update(financialObjectCodeShortName, old.getFinancialObjectCodeShortName()));

        old.setFinancialObjectLevelCode(update(financialObjectLevelCode, old.getFinancialObjectLevelCode()));
        old.setFinancialObjectTypeCode(update(financialObjectTypeCode, old.getFinancialObjectTypeCode()));
        old.setFinancialObjectSubTypeCode(update(financialObjectSubTypeCode, old.getFinancialObjectSubTypeCode()));
        old.setHistoricalFinancialObjectCode(update(historicalFinancialObjectCode, old.getHistoricalFinancialObjectCode()));
        old.setFinancialObjectActiveCode(update(financialObjectActiveIndicator, old.isFinancialObjectActiveCode()));
        old.setFinancialBudgetAggregationCd(update(financialBudgetAggregationCd, old.getFinancialBudgetAggregationCd()));
        old.setFinObjMandatoryTrnfrelimCd(update(finObjMandatoryTrnfrOrElimCd, old.getFinObjMandatoryTrnfrelimCd()));
        old.setFinancialFederalFundedCode(update(financialFederalFundedCode, old.getFinancialFederalFundedCode()));
        old.setNextYearFinancialObjectCode(update(nextYearFinancialObjectCode, old.getNextYearFinancialObjectCode()));
        old.setReportsToFinancialObjectCode(update(reportsToFinancialObjectCode, old.getReportsToFinancialObjectCode()));
    }


    /**
     * This method returns newvalue iff it is not empty
     *
     * @param oldValue
     * @param newValue
     * @return
     */
    protected String update(String newValue, String oldValue) {
        if (newValue == null || newValue.length() == 0) {
            return oldValue;
        }
        return newValue;
    }

    protected boolean update(boolean newValue, boolean oldValue) {
        return newValue;
    }


    @Override
    public boolean isPersistable() {
        return true;
    }

    @Override
    public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
        return getObjectCodeGlobalDetails();
    }

    /**
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List<Collection<PersistableBusinessObject>> buildListOfDeletionAwareLists() {
        List<Collection<PersistableBusinessObject>> managedLists = super.buildListOfDeletionAwareLists();

        managedLists.add( new ArrayList<PersistableBusinessObject>( getObjectCodeGlobalDetails() ) );

        return managedLists;
    }
}
