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

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.GlobalBusinessObject;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetail;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * 
 */
public class SubObjectCodeGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject, MutableInactivatable {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubObjectCodeGlobal.class);

    protected String documentNumber;
    protected Integer universityFiscalYear;
    protected String chartOfAccountsCode;
    protected String financialSubObjectCode;
    protected String financialSubObjectCodeName;
    protected String financialSubObjectCodeShortName;
    protected boolean active;

    protected DocumentHeader financialDocument;
    protected SystemOptions universityFiscal;
    protected Chart chartOfAccounts;

    protected List<SubObjectCodeGlobalDetail> subObjCdGlobalDetails;
    protected List<AccountGlobalDetail> accountGlobalDetails;

    /**
     * Default constructor.
     */
    public SubObjectCodeGlobal() {


        subObjCdGlobalDetails = new ArrayList<SubObjectCodeGlobalDetail>();
        accountGlobalDetails = new ArrayList<AccountGlobalDetail>();

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
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
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }


    /**
     * Gets the financialSubObjectCodeName attribute.
     * 
     * @return Returns the financialSubObjectCodeName
     */
    public String getFinancialSubObjectCodeName() {
        return financialSubObjectCodeName;
    }

    /**
     * Sets the financialSubObjectCodeName attribute.
     * 
     * @param financialSubObjectCodeName The financialSubObjectCodeName to set.
     */
    public void setFinancialSubObjectCodeName(String financialSubObjectCodeName) {
        this.financialSubObjectCodeName = financialSubObjectCodeName;
    }


    /**
     * Gets the financialSubObjectCodeShortName attribute.
     * 
     * @return Returns the financialSubObjectCodeShortName
     */
    public String getFinancialSubObjectCodeShortName() {
        return financialSubObjectCodeShortName;
    }

    /**
     * Sets the financialSubObjectCodeShortName attribute.
     * 
     * @param financialSubObjectCodeShortName The financialSubObjectCodeShortName to set.
     */
    public void setFinancialSubObjectCodeShortName(String financialSubObjectCdshortNm) {
        this.financialSubObjectCodeShortName = financialSubObjectCdshortNm;
    }


    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
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
    public void setFinancialDocument(DocumentHeader financialDocument) {
        this.financialDocument = financialDocument;
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
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    public List<SubObjectCodeGlobalDetail> getSubObjCdGlobalDetails() {
        return subObjCdGlobalDetails;
    }

    public void setSubObjCdGlobalDetails(List<SubObjectCodeGlobalDetail> subObjCdGlobalDetails) {
        this.subObjCdGlobalDetails = subObjCdGlobalDetails;
    }

    public List<AccountGlobalDetail> getAccountGlobalDetails() {
        return accountGlobalDetails;
    }

    public void setAccountGlobalDetails(List<AccountGlobalDetail> accountGlobalDetails) {
        this.accountGlobalDetails = accountGlobalDetails;
    }

    /**
     * @see org.kuali.rice.krad.document.GlobalBusinessObject#getGlobalChangesToDelete()
     */
    public List<PersistableBusinessObject> generateDeactivationsToPersist() {
        return null;
    }

    /**
     * This returns a list of Sub Object Codes to Update and/or Add
     * 
     * @see org.kuali.rice.krad.document.GlobalBusinessObject#applyGlobalChanges()
     */
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
        LOG.debug("applyGlobalChanges");
        List result = new ArrayList();

        // Iterate through Account/Object Code combinations; create new or update as necessary

        for (SubObjectCodeGlobalDetail subObjCdGlobalDetail : subObjCdGlobalDetails) {

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

                        SubObjectCode subObjCd = (SubObjectCode) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SubObjectCode.class, pk);
                        if (subObjCd == null) {
                            subObjCd = new SubObjectCode(this.universityFiscalYear, this.chartOfAccountsCode, accountNumber, financialObjectCode, this.financialSubObjectCode);
                        }
                        populate(subObjCd, accountGlobalDetail, subObjCdGlobalDetail);
                        result.add(subObjCd);
                    }
                }
            }
        }

        return result;
    }

    public void populate(SubObjectCode old, AccountGlobalDetail accountGlobalDetail, SubObjectCodeGlobalDetail subObjCdGlobalDetail) {
        old.setFinancialSubObjectCodeName(update(old.getFinancialSubObjectCodeName(), financialSubObjectCodeName));
        old.setFinancialSubObjectCdshortNm(update(old.getFinancialSubObjectCdshortNm(), financialSubObjectCodeShortName));
        old.setActive(update(old.isActive(), active));
    }


    /**
     * This method returns newvalue iff it is not empty
     * 
     * @param oldValue
     * @param newValue
     * @return
     */
    protected String update(String oldValue, String newValue) {
        if (newValue == null || newValue.length() == 0) {
            return oldValue;
        }
        return newValue;
    }

    protected boolean update(boolean oldValue, boolean newValue) {
        return newValue;
    }


    public boolean isPersistable() {
        return true;
    }

    public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
        ArrayList<GlobalBusinessObjectDetail> details = new ArrayList<GlobalBusinessObjectDetail>(accountGlobalDetails.size() + subObjCdGlobalDetails.size());
        details.addAll(accountGlobalDetails);
        details.addAll(subObjCdGlobalDetails);
        return details;
    }

    /**
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List<Collection<PersistableBusinessObject>> managedLists = super.buildListOfDeletionAwareLists();

        managedLists.add( new ArrayList<PersistableBusinessObject>( getAccountGlobalDetails() ) );
        managedLists.add( new ArrayList<PersistableBusinessObject>( getSubObjCdGlobalDetails() ) );

        return managedLists;
    }
}
