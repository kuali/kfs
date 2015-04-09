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
package org.kuali.kfs.module.ar.businessobject;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFundGroup;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;

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
    private ContractsAndGrantsLetterOfCreditFundGroup letterOfCreditFundGroup;

    /**
     * @return the letterOfCreditFundGroup
     */
    public ContractsAndGrantsLetterOfCreditFundGroup getLetterOfCreditFundGroup() {
        return letterOfCreditFundGroup = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsLetterOfCreditFundGroup.class).retrieveExternalizableBusinessObjectIfNecessary(this, letterOfCreditFundGroup, "letterOfCreditFundGroup");
    }

    /**
     * @param letterOfCreditFundGroup the letterOfCreditFundGroup to set
     */
    public void setLetterOfCreditFundGroup(ContractsAndGrantsLetterOfCreditFundGroup letterOfCreditFundGroup) {
        this.letterOfCreditFundGroup = letterOfCreditFundGroup;
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

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (ObjectUtils.isNotNull(obj)) {
            if (this.getClass().equals(obj.getClass())) {
                ContractsGrantsSuspendedInvoiceDetailReport other = (ContractsGrantsSuspendedInvoiceDetailReport) obj;
                return (StringUtils.equalsIgnoreCase(this.suspensionCategoryCode, other.suspensionCategoryCode) &&
                        StringUtils.equalsIgnoreCase(this.documentNumber, other.documentNumber));
            }
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ObjectUtil.generateHashCode(this, Arrays.asList(ArPropertyConstants.SuspensionCategoryReportFields.SUSPENSION_CATEGORY_CODE, KFSPropertyConstants.DOCUMENT_NUMBER));
    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
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
