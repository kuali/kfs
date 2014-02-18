/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFundGroup;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class ContractsGrantsSuspendedInvoiceDetailReport extends TransientBusinessObjectBase {

    private String suspensionCategoryCode;
    private String documentNumber;
    private String fundManagerPrincipalId;
    private String projectDirectorPrincipalId;
    private String fundManagerPrincipalName;
    private String projectDirectorPrincipalName;
    private String letterOfCreditFundGroupCode;
    private KualiDecimal awardTotal;

    private Person awardFundManager;
    private Person awardProjectDirector;
    private SuspensionCategory suspensionCategory;
    private ContractsAndGrantsLetterOfCreditFundGroup letterOfCreditFundGroup;


    /**
     * @return the letterOfCreditFundGroup
     */
    public ContractsAndGrantsLetterOfCreditFundGroup getLetterOfCreditFundGroup() {
        return letterOfCreditFundGroup = (ContractsAndGrantsLetterOfCreditFundGroup) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsLetterOfCreditFundGroup.class).retrieveExternalizableBusinessObjectIfNecessary(this, letterOfCreditFundGroup, "letterOfCreditFundGroup");
    }

    /**
     * @param letterOfCreditFundGroup the letterOfCreditFundGroup to set
     */
    public void setLetterOfCreditFundGroup(ContractsAndGrantsLetterOfCreditFundGroup letterOfCreditFundGroup) {
        this.letterOfCreditFundGroup = letterOfCreditFundGroup;
    }

    /**
     * @return the suspensionCategory
     */
    public SuspensionCategory getSuspensionCategory() {
        return suspensionCategory;
    }

    /**
     * @param suspensionCategory the suspensionCategory to set
     */
    public void setSuspensionCategory(SuspensionCategory suspensionCategory) {
        this.suspensionCategory = suspensionCategory;
    }

    /**
     * @return the fundManagerPrincipalName
     */
    public String getFundManagerPrincipalName() {
        return fundManagerPrincipalName;
    }

    /**
     * @param fundManagerPrincipalName the fundManagerPrincipalName to set
     */
    public void setFundManagerPrincipalName(String fundManagerPrincipalName) {
        this.fundManagerPrincipalName = fundManagerPrincipalName;
    }

    /**
     * @return the projectDirectorPrincipalName
     */
    public String getProjectDirectorPrincipalName() {
        return projectDirectorPrincipalName;
    }

    /**
     * @param projectDirectorPrincipalName the projectDirectorPrincipalName to set
     */
    public void setProjectDirectorPrincipalName(String projectDirectorPrincipalName) {
        this.projectDirectorPrincipalName = projectDirectorPrincipalName;
    }

    /**
     * @return the suspensionCategoryCode
     */
    public String getSuspensionCategoryCode() {
        return suspensionCategoryCode;
    }

    /**
     * @param suspensionCategoryCode the suspensionCategoryCode to set
     */
    public void setSuspensionCategoryCode(String suspensionCategoryCode) {
        this.suspensionCategoryCode = suspensionCategoryCode;
    }

    /**
     * @return Returns the awardTotal.
     */
    public KualiDecimal getAwardTotal() {
        return awardTotal;
    }

    public void setAwardTotal(KualiDecimal awardTotal) {
        this.awardTotal = awardTotal;
    }


    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @return the fundManagerPrincipalId
     */
    public String getFundManagerPrincipalId() {
        return fundManagerPrincipalId;
    }

    /**
     * @return Returns the accountFiscalOfficerSystemIdentifier.
     */
    public String getFundManagerPrincipalIdForSearching() {
        return getFundManagerPrincipalId();
    }

    /**
     * @param fundManagerPrincipalId the fundManagerPrincipalId to set
     */
    public void setFundManagerPrincipalId(String fundManagerPrincipalId) {
        this.fundManagerPrincipalId = fundManagerPrincipalId;
    }

    /**
     * @return the projectDirectorPrincipalId
     */
    public String getProjectDirectorPrincipalId() {
        return projectDirectorPrincipalId;
    }

    /**
     * @param projectDirectorPrincipalId the projectDirectorPrincipalId to set
     */
    public void setProjectDirectorPrincipalId(String projectDirectorPrincipalId) {
        this.projectDirectorPrincipalId = projectDirectorPrincipalId;
    }

    /**
     * @return Returns the accountFiscalOfficerSystemIdentifier.
     */
    public String getProjectDirectorPrincipalIdForSearching() {
        return getProjectDirectorPrincipalId();
    }

    /**
     * @return Retrieves the award's Fund Manager using Person Service.
     */
    public Person getAwardFundManager() {
        PersonService personService = SpringContext.getBean(PersonService.class);
        awardFundManager = personService.getPerson(fundManagerPrincipalId);

        return awardFundManager;
    }


    /**
     * Sets the awardFundManager attribute value.
     * 
     * @param awardFundManager The awardFundManager to set.
     */
    public void setAwardFundManager(Person awardFundManager) {
        this.awardFundManager = awardFundManager;
    }

    /**
     * @return Retrieves the award's Project Director using Person Service.
     */
    public Person getAwardProjectDirector() {
        PersonService personService = SpringContext.getBean(PersonService.class);
        awardProjectDirector = personService.getPerson(projectDirectorPrincipalId);
        return awardProjectDirector;
    }


    /**
     * Sets the awardProjectDirector attribute value.
     * 
     * @param awardProjectDirector The awardProjectDirector to set.
     */
    public void setAwardProjectDirector(Person awardProjectDirector) {
        this.awardProjectDirector = awardProjectDirector;
    }

    /**
     * @return the letterOfCreditFundGroupCode
     */
    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }

    /**
     * @param letterOfCreditFundGroupCode the letterOfCreditFundGroupCode to set
     */
    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, this.documentNumber);
        m.put("suspensionCategoryCode", this.suspensionCategoryCode);
        m.put("fundManagerPrincipalId", this.fundManagerPrincipalId);
        m.put("projectDirectorPrincipalId", this.projectDirectorPrincipalId);
        m.put("projectDirectorPrincipalName", this.projectDirectorPrincipalName);
        m.put("fundManagerPrincipalName", this.fundManagerPrincipalName);
        m.put("projectDirectorPrincipalName", this.projectDirectorPrincipalName);
        m.put("letterOfCreditFundGroupCode", this.letterOfCreditFundGroupCode);
        if (this.awardTotal != null) {
            m.put("awardTotal", this.awardTotal.toString());
        }

        return m;
    }
}
