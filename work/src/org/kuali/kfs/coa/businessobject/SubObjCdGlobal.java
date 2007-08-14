/*
 * Copyright 2007 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.GlobalBusinessObject;
import org.kuali.core.bo.GlobalBusinessObjectDetail;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;

/**
 * 
 */
public class SubObjCdGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubObjCdGlobal.class);

    private String documentNumber;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String financialSubObjectCode;
    private String financialSubObjectCodeName;
    private String financialSubObjectCodeShortName;
    private boolean financialSubObjectActiveIndicator;

    private DocumentHeader financialDocument;
    private Options universityFiscal;
    private Chart chartOfAccounts;
    
    private List<SubObjCdGlobalDetail> subObjCdGlobalDetails;
    private List<AccountGlobalDetail> accountGlobalDetails;
    
    /**
     * Default constructor.
     */
    public SubObjCdGlobal() {


        subObjCdGlobalDetails = new TypedArrayList(SubObjCdGlobalDetail.class);
        accountGlobalDetails = new TypedArrayList(AccountGlobalDetail.class);

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     * 
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     * 
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     * 
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     * 
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode
     * 
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     * 
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }


    /**
     * Gets the financialSubObjectCodeName attribute.
     * 
     * @return Returns the financialSubObjectCodeName
     * 
     */
    public String getFinancialSubObjectCodeName() {
        return financialSubObjectCodeName;
    }

    /**
     * Sets the financialSubObjectCodeName attribute.
     * 
     * @param financialSubObjectCodeName The financialSubObjectCodeName to set.
     * 
     */
    public void setFinancialSubObjectCodeName(String financialSubObjectCodeName) {
        this.financialSubObjectCodeName = financialSubObjectCodeName;
    }


    /**
     * Gets the financialSubObjectCodeShortName attribute.
     * 
     * @return Returns the financialSubObjectCodeShortName
     * 
     */
    public String getFinancialSubObjectCodeShortName() {
        return financialSubObjectCodeShortName;
    }

    /**
     * Sets the financialSubObjectCodeShortName attribute.
     * 
     * @param financialSubObjectCodeShortName The financialSubObjectCodeShortName to set.
     * 
     */
    public void setFinancialSubObjectCodeShortName(String financialSubObjectCdshortNm) {
        this.financialSubObjectCodeShortName = financialSubObjectCdshortNm;
    }


    /**
     * Gets the financialSubObjectActiveIndicator attribute.
     * 
     * @return Returns the financialSubObjectActiveIndicator
     * 
     */
    public boolean isFinancialSubObjectActiveIndicator() {
        return financialSubObjectActiveIndicator;
    }


    /**
     * Sets the financialSubObjectActiveIndicator attribute.
     * 
     * @param financialSubObjectActiveIndicator The financialSubObjectActiveIndicator to set.
     * 
     */
    public void setFinancialSubObjectActiveIndicator(boolean financialSubObjectActiveIndicator) {
        this.financialSubObjectActiveIndicator = financialSubObjectActiveIndicator;
    }


    /**
     * Gets the financialDocument attribute.
     * 
     * @return Returns the financialDocument
     * 
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
    public void setFinancialDocument(DocumentHeader financialDocument) {
        this.financialDocument = financialDocument;
    }

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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    public List<SubObjCdGlobalDetail> getSubObjCdGlobalDetails() {
        return subObjCdGlobalDetails;
    }

    public void setSubObjCdGlobalDetails(List<SubObjCdGlobalDetail> subObjCdGlobalDetails) {
        this.subObjCdGlobalDetails = subObjCdGlobalDetails;
    }

    public List<AccountGlobalDetail> getAccountGlobalDetails() {
        return accountGlobalDetails;
    }

    public void setAccountGlobalDetails(List<AccountGlobalDetail> accountGlobalDetails) {
        this.accountGlobalDetails = accountGlobalDetails;
    }

    /**
     * @see org.kuali.core.document.GlobalBusinessObject#getGlobalChangesToDelete()
     */
    public List<PersistableBusinessObject> generateDeactivationsToPersist() {
        return null;
    }

    /**
     * This returns a list of Sub Object Codes to Update and/or Add
     * 
     * @see org.kuali.core.document.GlobalBusinessObject#applyGlobalChanges()
     */
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
        LOG.debug("applyGlobalChanges");
        List result = new ArrayList();

        // Iterate through Account/Object Code combinations; create new or update as necessary

        for (SubObjCdGlobalDetail subObjCdGlobalDetail : subObjCdGlobalDetails) {
            
            String financialObjectCode = subObjCdGlobalDetail.getFinancialObjectCode();
            
            if (financialObjectCode != null && financialObjectCode.length() > 0) {

                for (AccountGlobalDetail accountGlobalDetail : accountGlobalDetails) {

                    Map pk = new HashMap();
        
                    String accountNumber = accountGlobalDetail.getAccountNumber();
        
                    if (accountNumber != null && accountNumber.length() > 0) {
                        pk.put("UNIV_FISCAL_YR", this.universityFiscalYear);
                        pk.put("FIN_COA_CD", this.chartOfAccountsCode);
                        pk.put("ACCOUNT_NBR", accountNumber);
                        pk.put("FIN_OBJECT_CD", financialObjectCode);
                        pk.put("FIN_SUB_OBJ_CD", this.financialSubObjectCode);
        
                        SubObjCd subObjCd = (SubObjCd) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SubObjCd.class, pk);
                        if (subObjCd == null) {
                            subObjCd = new SubObjCd(this.universityFiscalYear, this.chartOfAccountsCode, accountNumber, financialObjectCode, this.financialSubObjectCode);
                        }
                        populate(subObjCd, accountGlobalDetail, subObjCdGlobalDetail);
                        result.add(subObjCd);
                    }
                }
            }
        }

        return result;
    }

    public void populate(SubObjCd old, AccountGlobalDetail accountGlobalDetail, SubObjCdGlobalDetail subObjCdGlobalDetail) {
        old.setFinancialSubObjectCodeName(update(financialSubObjectCodeName, old.getFinancialSubObjectCodeName()));
        old.setFinancialSubObjectCdshortNm(update(financialSubObjectCodeShortName, old.getFinancialSubObjectCdshortNm()));
        old.setFinancialSubObjectActiveIndicator(update(financialSubObjectActiveIndicator, old.isFinancialSubObjectActiveIndicator()));
    }


    /**
     * 
     * This method returns newvalue iff it is not empty
     * 
     * @param oldValue
     * @param newValue
     * @return
     */
    private String update(String oldValue, String newValue) {
        if (newValue == null || newValue.length() == 0) {
            return oldValue;
        }
        return newValue;
    }

    private boolean update(boolean oldValue, boolean newValue) {
        return newValue;
    }


    public boolean isPersistable() {
        return true;
    }

    public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
        ArrayList<GlobalBusinessObjectDetail> details = new ArrayList<GlobalBusinessObjectDetail>( accountGlobalDetails.size() + subObjCdGlobalDetails.size() );
        details.addAll( accountGlobalDetails );
        details.addAll( subObjCdGlobalDetails );
        return details;
    }
    
    /**
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List<List> managedLists = super.buildListOfDeletionAwareLists();

        managedLists.add(getAccountGlobalDetails());
        managedLists.add(getSubObjCdGlobalDetails());

        return managedLists;
    }
}
