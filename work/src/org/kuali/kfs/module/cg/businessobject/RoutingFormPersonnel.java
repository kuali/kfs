/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.bo.PostalZipCode;
import org.kuali.kfs.bo.State;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.service.ChartUserService;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.budget.bo.BudgetUser;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RoutingFormPersonnel extends PersistableBusinessObjectBase {

	private String documentNumber;
    private Integer routingFormPersonSequenceNumber;
    private String personUniversalIdentifier;
    private String chartOfAccountsCode;
	private String organizationCode;
	private KualiInteger personCreditPercent;
	private KualiInteger personFinancialAidPercent;
	private String personRoleCode;
	private String personPrefixText;
	private String personSuffixText;
	private String personPositionTitle;
	private String personDivisionText;
	private String personLine1Address;
	private String personLine2Address;
	private String personCityName;
	private String personCountyName;
	private String personStateCode;
	private String personCountryCode;
	private String personZipCode;
	private String personPhoneNumber;
	private String personFaxNumber;
	private String personEmailAddress;
    private String personRoleText;
    private boolean personToBeNamedIndicator;
    
    private Org organization;
	private Chart chartOfAccounts;
    private State personState;
    private PostalZipCode personZip;
    private Country personCountry;
    private PersonRole personRole;
//    private RoutingFormDocument routingFormDocument;
    private UniversalUser user;
    
	/**
	 * Default constructor.
	 */
	public RoutingFormPersonnel() {

	}

    /**
     * Default constructor.
     */
    public RoutingFormPersonnel(BudgetUser budgetUser, String documentNumber, String personRoleCode) {
        this.documentNumber = documentNumber;
        this.personRoleCode = personRoleCode;
        this.personUniversalIdentifier = budgetUser.getPersonUniversalIdentifier();
        
        
        this.chartOfAccountsCode = budgetUser.getFiscalCampusCode();
        this.organizationCode = budgetUser.getPrimaryDepartmentCode();
        this.personPrefixText = budgetUser.getPersonNamePrefixText();
        this.personSuffixText = budgetUser.getPersonNameSuffixText();;
        this.personRoleText = budgetUser.getRole();
        this.personToBeNamedIndicator = budgetUser.getPersonUniversalIdentifier() == null;
    }

    /**
     * Sets several fields in RoutingFormPersonnel person based upon the contained UniversalUserService's ChartUserService:
     * <ul>
     * <li>chart</li>
     * <li>org</li>
     * <li>email address</li>
     * <li>campus address</li>
     * <li>phone number</li>
     * </ul>
     */
    public void populateWithUserServiceFields() {
        // retrieve services and refresh UniversalUser objects (it's empty after returning from a kul:lookup)
        UniversalUserService universalUserService = SpringServiceLocator.getUniversalUserService();
        ChartUserService chartUserService = SpringServiceLocator.getChartUserService();
        UniversalUser user = universalUserService.updateUniversalUserIfNecessary(this.getPersonUniversalIdentifier(), this.getUser());
        
        // set chart / org for new person
        this.setChartOfAccountsCode(chartUserService.getDefaultChartOfAccountsCode( (ChartUser)user.getModuleUser(ChartUser.MODULE_ID) ));
        this.setOrganizationCode(chartUserService.getDefaultOrganizationCode( (ChartUser)user.getModuleUser(ChartUser.MODULE_ID) ));
        
        // set email address, campus address, and phone
        this.setPersonEmailAddress(user.getPersonEmailAddress());
        this.setPersonLine1Address(user.getPersonCampusAddress());
        this.setPersonPhoneNumber(user.getPersonLocalPhoneNumber());
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
     * Gets the routingFormPersonSequenceNumber attribute. 
     * @return Returns the routingFormPersonSequenceNumber.
     */
    public Integer getRoutingFormPersonSequenceNumber() {
        return routingFormPersonSequenceNumber;
    }

    /**
     * Sets the routingFormPersonSequenceNumber attribute value.
     * @param routingFormPersonSequenceNumber The routingFormPersonSequenceNumber to set.
     */
    public void setRoutingFormPersonSequenceNumber(Integer routingFormPersonSequenceNumber) {
        this.routingFormPersonSequenceNumber = routingFormPersonSequenceNumber;
    }    
    
    /**
     * Gets the personUniversalIdentifier attribute.
     * 
     * @return Returns the personUniversalIdentifier
     * 
     */
    public String getPersonUniversalIdentifier() { 
        return personUniversalIdentifier;
    }

    /**
     * Sets the personUniversalIdentifier attribute.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     * 
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
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
	 * Gets the organizationCode attribute.
	 * 
	 * @return Returns the organizationCode
	 * 
	 */
	public String getOrganizationCode() { 
		return organizationCode;
	}

	/**
	 * Sets the organizationCode attribute.
	 * 
	 * @param organizationCode The organizationCode to set.
	 * 
	 */
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}


	/**
	 * Gets the personCreditPercent attribute.
	 * 
	 * @return Returns the personCreditPercent
	 * 
	 */
	public KualiInteger getPersonCreditPercent() { 
		return personCreditPercent;
	}

	/**
	 * Sets the personCreditPercent attribute.
	 * 
	 * @param personCreditPercent The personCreditPercent to set.
	 * 
	 */
	public void setPersonCreditPercent(KualiInteger personCreditPercent) {
		this.personCreditPercent = personCreditPercent;
	}


	/**
	 * Gets the personFinancialAidPercent attribute.
	 * 
	 * @return Returns the personFinancialAidPercent
	 * 
	 */
	public KualiInteger getPersonFinancialAidPercent() { 
		return personFinancialAidPercent;
	}

	/**
	 * Sets the personFinancialAidPercent attribute.
	 * 
	 * @param personFinancialAidPercent The personFinancialAidPercent to set.
	 * 
	 */
	public void setPersonFinancialAidPercent(KualiInteger personFinancialAidPercent) {
		this.personFinancialAidPercent = personFinancialAidPercent;
	}


	/**
	 * Gets the personRoleCode attribute.
	 * 
	 * @return Returns the personRoleCode
	 * 
	 */
	public String getPersonRoleCode() { 
		return personRoleCode;
	}

	/**
	 * Sets the personRoleCode attribute.
	 * 
	 * @param personRoleCode The personRoleCode to set.
	 * 
	 */
	public void setPersonRoleCode(String personRoleCode) {
		this.personRoleCode = personRoleCode;
	}


	/**
	 * Gets the personPrefixText attribute.
	 * 
	 * @return Returns the personPrefixText
	 * 
	 */
	public String getPersonPrefixText() { 
		return personPrefixText;
	}

	/**
	 * Sets the personPrefixText attribute.
	 * 
	 * @param personPrefixText The personPrefixText to set.
	 * 
	 */
	public void setPersonPrefixText(String personPrefixText) {
		this.personPrefixText = personPrefixText;
	}


	/**
	 * Gets the personSuffixText attribute.
	 * 
	 * @return Returns the personSuffixText
	 * 
	 */
	public String getPersonSuffixText() { 
		return personSuffixText;
	}

	/**
	 * Sets the personSuffixText attribute.
	 * 
	 * @param personSuffixText The personSuffixText to set.
	 * 
	 */
	public void setPersonSuffixText(String personSuffixText) {
		this.personSuffixText = personSuffixText;
	}


	/**
	 * Gets the personPositionTitle attribute.
	 * 
	 * @return Returns the personPositionTitle
	 * 
	 */
	public String getPersonPositionTitle() { 
		return personPositionTitle;
	}

	/**
	 * Sets the personPositionTitle attribute.
	 * 
	 * @param personPositionTitle The personPositionTitle to set.
	 * 
	 */
	public void setPersonPositionTitle(String personPositionTitle) {
		this.personPositionTitle = personPositionTitle;
	}


	/**
	 * Gets the personDivisionText attribute.
	 * 
	 * @return Returns the personDivisionText
	 * 
	 */
	public String getPersonDivisionText() { 
		return personDivisionText;
	}

	/**
	 * Sets the personDivisionText attribute.
	 * 
	 * @param personDivisionText The personDivisionText to set.
	 * 
	 */
	public void setPersonDivisionText(String personDivisionText) {
		this.personDivisionText = personDivisionText;
	}


	/**
	 * Gets the personLine1Address attribute.
	 * 
	 * @return Returns the personLine1Address
	 * 
	 */
	public String getPersonLine1Address() { 
		return personLine1Address;
	}

	/**
	 * Sets the personLine1Address attribute.
	 * 
	 * @param personLine1Address The personLine1Address to set.
	 * 
	 */
	public void setPersonLine1Address(String personLine1Address) {
		this.personLine1Address = personLine1Address;
	}


	/**
	 * Gets the personLine2Address attribute.
	 * 
	 * @return Returns the personLine2Address
	 * 
	 */
	public String getPersonLine2Address() { 
		return personLine2Address;
	}

	/**
	 * Sets the personLine2Address attribute.
	 * 
	 * @param personLine2Address The personLine2Address to set.
	 * 
	 */
	public void setPersonLine2Address(String personLine2Address) {
		this.personLine2Address = personLine2Address;
	}


	/**
	 * Gets the personCityName attribute.
	 * 
	 * @return Returns the personCityName
	 * 
	 */
	public String getPersonCityName() { 
		return personCityName;
	}

	/**
	 * Sets the personCityName attribute.
	 * 
	 * @param personCityName The personCityName to set.
	 * 
	 */
	public void setPersonCityName(String personCityName) {
		this.personCityName = personCityName;
	}


	/**
	 * Gets the personCountyName attribute.
	 * 
	 * @return Returns the personCountyName
	 * 
	 */
	public String getPersonCountyName() { 
		return personCountyName;
	}

	/**
	 * Sets the personCountyName attribute.
	 * 
	 * @param personCountyName The personCountyName to set.
	 * 
	 */
	public void setPersonCountyName(String personCountyName) {
		this.personCountyName = personCountyName;
	}


	/**
	 * Gets the personStateCode attribute.
	 * 
	 * @return Returns the personStateCode
	 * 
	 */
	public String getPersonStateCode() { 
		return personStateCode;
	}

	/**
	 * Sets the personStateCode attribute.
	 * 
	 * @param personStateCode The personStateCode to set.
	 * 
	 */
	public void setPersonStateCode(String personStateCode) {
		this.personStateCode = personStateCode;
	}


	/**
	 * Gets the personCountryCode attribute.
	 * 
	 * @return Returns the personCountryCode
	 * 
	 */
	public String getPersonCountryCode() { 
		return personCountryCode;
	}

	/**
	 * Sets the personCountryCode attribute.
	 * 
	 * @param personCountryCode The personCountryCode to set.
	 * 
	 */
	public void setPersonCountryCode(String personCountryCode) {
		this.personCountryCode = personCountryCode;
	}


	/**
	 * Gets the personZipCode attribute.
	 * 
	 * @return Returns the personZipCode
	 * 
	 */
	public String getPersonZipCode() { 
		return personZipCode;
	}

	/**
	 * Sets the personZipCode attribute.
	 * 
	 * @param personZipCode The personZipCode to set.
	 * 
	 */
	public void setPersonZipCode(String personZipCode) {
		this.personZipCode = personZipCode;
	}


	/**
	 * Gets the personPhoneNumber attribute.
	 * 
	 * @return Returns the personPhoneNumber
	 * 
	 */
	public String getPersonPhoneNumber() { 
		return personPhoneNumber;
	}

	/**
	 * Sets the personPhoneNumber attribute.
	 * 
	 * @param personPhoneNumber The personPhoneNumber to set.
	 * 
	 */
	public void setPersonPhoneNumber(String personPhoneNumber) {
		this.personPhoneNumber = personPhoneNumber;
	}


	/**
	 * Gets the personFaxNumber attribute.
	 * 
	 * @return Returns the personFaxNumber
	 * 
	 */
	public String getPersonFaxNumber() { 
		return personFaxNumber;
	}

	/**
	 * Sets the personFaxNumber attribute.
	 * 
	 * @param personFaxNumber The personFaxNumber to set.
	 * 
	 */
	public void setPersonFaxNumber(String personFaxNumber) {
		this.personFaxNumber = personFaxNumber;
	}


	/**
	 * Gets the personEmailAddress attribute.
	 * 
	 * @return Returns the personEmailAddress
	 * 
	 */
	public String getPersonEmailAddress() { 
		return personEmailAddress;
	}

	/**
	 * Sets the personEmailAddress attribute.
	 * 
	 * @param personEmailAddress The personEmailAddress to set.
	 * 
	 */
	public void setPersonEmailAddress(String personEmailAddress) {
		this.personEmailAddress = personEmailAddress;
	}


	/**
	 * Gets the organization attribute.
	 * 
	 * @return Returns the organization
	 * 
	 */
	public Org getOrganization() { 
		return organization;
	}

	/**
	 * Sets the organization attribute.
	 * 
	 * @param organization The organization to set.
	 * @deprecated
	 */
	public void setOrganization(Org organization) {
		this.organization = organization;
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
     * Gets the personCountry attribute. 
     * @return Returns the personCountry.
     */
    public Country getPersonCountry() {
        return personCountry;
    }

    /**
     * Sets the personCountry attribute value.
     * @param personCountry The personCountry to set.
     * @deprecated
     */
    public void setPersonCountry(Country personCountry) {
        this.personCountry = personCountry;
    }

    /**
     * Gets the personState attribute. 
     * @return Returns the personState.
     */
    public State getPersonState() {
        return personState;
    }

    /**
     * Sets the personState attribute value.
     * @param personState The personState to set.
     * @deprecated
     */
    public void setPersonState(State personState) {
        this.personState = personState;
    }

    /**
     * Gets the personZip attribute. 
     * @return Returns the personZip.
     */
    public PostalZipCode getPersonZip() {
        return personZip;
    }

    /**
     * Sets the personZip attribute value.
     * @param personZip The personZip to set.
     * @deprecated
     */
    public void setPersonZip(PostalZipCode personZip) {
        this.personZip = personZip;
    }

    /**
     * Gets the personRole attribute. 
     * @return Returns the personRole.
     */
    public PersonRole getPersonRole() {
        return personRole;
    }

    /**
     * Sets the personRole attribute value.
     * @param personRole The personRole to set.
     * @deprecated
     */
    public void setPersonRole(PersonRole personRole) {
        this.personRole = personRole;
    }
//    
//    /**
//     * Gets the routingFormDocument attribute. 
//     * @return Returns the routingFormDocument.
//     */
//    public RoutingFormDocument getRoutingFormDocument() {
//        return routingFormDocument;
//    }
//
//    /**
//     * Sets the routingFormDocument attribute value.
//     * @param routingFormDocument The routingFormDocument to set.
//     * @deprecated
//     */
//    public void setRoutingFormDocument(RoutingFormDocument routingFormDocument) {
//        this.routingFormDocument = routingFormDocument;
//    }
//    
    /**
     * Gets the user attribute. 
     * @return Returns the user.
     */
    public UniversalUser getUser() {
        return user;
    }

    /**
     * Sets the user attribute value.
     * @param user The user to set.
     * @deprecated
     */
    public void setUser(UniversalUser user) {
        this.user = user;
    }

    /**
     * Gets the personRoleText attribute. 
     * @return Returns the personRoleText.
     */
    public String getPersonRoleText() {
        return personRoleText;
    }

    /**
     * Sets the personRoleText attribute value.
     * @param personRoleText The personRoleText to set.
     */
    public void setPersonRoleText(String personRoleText) {
        this.personRoleText = personRoleText;
    }

    /**
     * Gets the personToBeNamedIndicator attribute. 
     * @return Returns the personToBeNamedIndicator.
     */
    public boolean isPersonToBeNamedIndicator() {
        return personToBeNamedIndicator;
    }

    /**
     * Sets the personToBeNamedIndicator attribute value.
     * @param personToBeNamedIndicator The personToBeNamedIndicator to set.
     */
    public void setPersonToBeNamedIndicator(boolean personToBeNamedIndicator) {
        this.personToBeNamedIndicator = personToBeNamedIndicator;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.personUniversalIdentifier != null) {
            m.put("personUniversalIdentifier", this.personUniversalIdentifier.toString());
        }
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    public boolean isProjectDirector() {
       KualiConfigurationService kualiConfigurationService = SpringServiceLocator.getKualiConfigurationService();
       final String PERSON_ROLE_CODE_PD = kualiConfigurationService.getApplicationParameterValue(KraConstants.KRA_DEVELOPMENT_GROUP, "KraRoutingFormPersonRoleCodeProjectDirector");
        
       return PERSON_ROLE_CODE_PD.equals(this.getPersonRoleCode());
    }
    
}
