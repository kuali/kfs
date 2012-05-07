/*
 * Copyright 2007-2009 The Kuali Foundation
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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;
import org.kuali.rice.location.framework.state.StateEbo;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class OrganizationOptions extends PersistableBusinessObjectBase {

	protected String chartOfAccountsCode;
	protected String organizationCode;
	protected String processingChartOfAccountCode;
	protected String processingOrganizationCode;
	protected String printInvoiceIndicator;
	protected String organizationPaymentTermsText;
	protected String organizationMessageText;
	protected String organizationRemitToAddressName;
	protected String organizationRemitToLine1StreetAddress;
	protected String organizationRemitToLine2StreetAddress;
	protected String organizationRemitToCityName;
	protected String organizationRemitToStateCode;
	protected String organizationRemitToZipCode;
    protected String organizationRemitToCountryCode = KFSConstants.COUNTRY_CODE_UNITED_STATES;
	protected String organizationPhoneNumber;
	protected String organization800PhoneNumber;
	protected String organizationFaxNumber;
	protected String universityName;
	protected String organizationCheckPayableToName;
    protected String organizationPostalZipCode;
    protected String organizationPostalCountryCode;

    protected Organization organization;
	protected Chart chartOfAccounts;
	protected Chart processingChartOfAccount;
	protected Organization processingOrganization;
    protected StateEbo organizationRemitToState;
    protected PrintInvoiceOptions printInvoiceOptions;
    protected PostalCodeEbo orgPostalZipCode;
    protected PostalCodeEbo orgRemitToZipCode;
    protected CountryEbo orgPostalCountryCode;

    protected transient SystemInformation systemInformationForAddress;
    protected transient SystemInformation systemInformationForAddressName;
    protected static transient ParameterService parameterService;
    protected static transient SystemInformationService systemInformationService;
    protected static transient UniversityDateService universityDateService;


    /**
	 * Default constructor.
	 */
	public OrganizationOptions() {

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
	 * Gets the processingChartOfAccountCode attribute.
	 *
	 * @return Returns the processingChartOfAccountCode
	 *
	 */
	public String getProcessingChartOfAccountCode() {
		return processingChartOfAccountCode;
	}

	/**
	 * Sets the processingChartOfAccountCode attribute.
	 *
	 * @param processingChartOfAccountCode The processingChartOfAccountCode to set.
	 *
	 */
	public void setProcessingChartOfAccountCode(String processingChartOfAccountCode) {
		this.processingChartOfAccountCode = processingChartOfAccountCode;
	}


	/**
	 * Gets the processingOrganizationCode attribute.
	 *
	 * @return Returns the processingOrganizationCode
	 *
	 */
	public String getProcessingOrganizationCode() {
		return processingOrganizationCode;
	}

	/**
	 * Sets the processingOrganizationCode attribute.
	 *
	 * @param processingOrganizationCode The processingOrganizationCode to set.
	 *
	 */
	public void setProcessingOrganizationCode(String processingOrganizationCode) {
		this.processingOrganizationCode = processingOrganizationCode;
	}

	/**
	 * Gets the printInvoiceIndicator attribute.
	 *
	 * @return Returns the printInvoiceIndicator
	 *
	 */
	public String getPrintInvoiceIndicator() {
		return printInvoiceIndicator;
	}

	/**
	 * Sets the printInvoiceIndicator attribute.
	 *
	 * @param printInvoiceIndicator The printInvoiceIndicator to set.
	 *
	 */
	public void setPrintInvoiceIndicator(String printInvoiceIndicator) {
		this.printInvoiceIndicator = printInvoiceIndicator;
	}


	/**
	 * Gets the organizationPaymentTermsText attribute.
	 *
	 * @return Returns the organizationPaymentTermsText
	 *
	 */
	public String getOrganizationPaymentTermsText() {
		return organizationPaymentTermsText;
	}

	/**
	 * Sets the organizationPaymentTermsText attribute.
	 *
	 * @param organizationPaymentTermsText The organizationPaymentTermsText to set.
	 *
	 */
	public void setOrganizationPaymentTermsText(String organizationPaymentTermsText) {
		this.organizationPaymentTermsText = organizationPaymentTermsText;
	}


	/**
	 * Gets the organizationMessageText attribute.
	 *
	 * @return Returns the organizationMessageText
	 *
	 */
	public String getOrganizationMessageText() {
		return organizationMessageText;
	}

	/**
	 * Sets the organizationMessageText attribute.
	 *
	 * @param organizationMessageText The organizationMessageText to set.
	 *
	 */
	public void setOrganizationMessageText(String organizationMessageText) {
		this.organizationMessageText = organizationMessageText;
	}


	/**
	 * Gets the organizationRemitToAddressName attribute.
	 *
	 * @return Returns the organizationRemitToAddressName
	 *
	 */
	public String getOrganizationRemitToAddressName() {
	    final SystemInformation systemInfo = getSystemInformationForRemitToAddressName();
	    if(systemInfo != null) {
	        return systemInfo.getOrganizationRemitToAddressName();
	    }
	    return organizationRemitToAddressName;
	}

	/**
	 * Sets the organizationRemitToAddressName attribute.
	 *
	 * @param organizationRemitToAddressName The organizationRemitToAddressName to set.
	 *
	 */
	public void setOrganizationRemitToAddressName(String organizationRemitToAddressName) {
		this.organizationRemitToAddressName = organizationRemitToAddressName;
	}


	/**
	 * Gets the organizationRemitToLine1StreetAddress attribute.
	 *
	 * @return Returns the organizationRemitToLine1StreetAddress
	 *
	 */
	public String getOrganizationRemitToLine1StreetAddress() {
	    final SystemInformation systemInfo = getSystemInformationForRemitToAddress();
        if(systemInfo != null) {
            return systemInfo.getOrganizationRemitToLine1StreetAddress();
        }
        return organizationRemitToLine1StreetAddress;
	}

	/**
	 * Sets the organizationRemitToLine1StreetAddress attribute.
	 *
	 * @param organizationRemitToLine1StreetAddress The organizationRemitToLine1StreetAddress to set.
	 *
	 */
	public void setOrganizationRemitToLine1StreetAddress(String organizationRemitToLine1StreetAddress) {
		this.organizationRemitToLine1StreetAddress = organizationRemitToLine1StreetAddress;
	}


	/**
	 * Gets the organizationRemitToLine2StreetAddress attribute.
	 *
	 * @return Returns the organizationRemitToLine2StreetAddress
	 *
	 */
	public String getOrganizationRemitToLine2StreetAddress() {
	    final SystemInformation systemInfo = getSystemInformationForRemitToAddress();
        if(systemInfo != null) {
            return systemInfo.getOrganizationRemitToLine2StreetAddress();
        }
        return organizationRemitToLine2StreetAddress;
	}

	/**
	 * Sets the organizationRemitToLine2StreetAddress attribute.
	 *
	 * @param organizationRemitToLine2StreetAddress The organizationRemitToLine2StreetAddress to set.
	 *
	 */
	public void setOrganizationRemitToLine2StreetAddress(String organizationRemitToLine2StreetAddress) {
		this.organizationRemitToLine2StreetAddress = organizationRemitToLine2StreetAddress;
	}


	/**
	 * Gets the organizationRemitToCityName attribute.
	 *
	 * @return Returns the organizationRemitToCityName
	 *
	 */
	public String getOrganizationRemitToCityName() {
	    final SystemInformation systemInfo = getSystemInformationForRemitToAddress();
        if(systemInfo != null) {
            return systemInfo.getOrganizationRemitToCityName();
        }
		return organizationRemitToCityName;
	}

	/**
	 * Sets the organizationRemitToCityName attribute.
	 *
	 * @param organizationRemitToCityName The organizationRemitToCityName to set.
	 *
	 */
	public void setOrganizationRemitToCityName(String organizationRemitToCityName) {
		this.organizationRemitToCityName = organizationRemitToCityName;
	}


	/**
	 * Gets the organizationRemitToStateCode attribute.
	 *
	 * @return Returns the organizationRemitToStateCode
	 *
	 */
	public String getOrganizationRemitToStateCode() {
	    final SystemInformation systemInfo = getSystemInformationForRemitToAddress();
        if(systemInfo != null) {
            return systemInfo.getOrganizationRemitToStateCode();
        }
		return organizationRemitToStateCode;
	}

	/**
	 * Sets the organizationRemitToStateCode attribute.
	 *
	 * @param organizationRemitToStateCode The organizationRemitToStateCode to set.
	 *
	 */
	public void setOrganizationRemitToStateCode(String organizationRemitToStateCode) {
		this.organizationRemitToStateCode = organizationRemitToStateCode;
	}


	/**
	 * Gets the organizationRemitToZipCode attribute.
	 *
	 * @return Returns the organizationRemitToZipCode
	 *
	 */
	public String getOrganizationRemitToZipCode() {
        final SystemInformation systemInfo = getSystemInformationForRemitToAddress();
        if(systemInfo != null) {
            return systemInfo.getOrganizationRemitToZipCode();
        }
		return organizationRemitToZipCode;
	}

	/**
	 * Sets the organizationRemitToZipCode attribute.
	 *
	 * @param organizationRemitToZipCode The organizationRemitToZipCode to set.
	 *
	 */
	public void setOrganizationRemitToZipCode(String organizationRemitToZipCode) {
		this.organizationRemitToZipCode = organizationRemitToZipCode;
	}

	/**
	 * Gets the organizationPhoneNumber attribute.
	 *
	 * @return Returns the organizationPhoneNumber
	 *
	 */
	public String getOrganizationPhoneNumber() {
		return organizationPhoneNumber;
	}

	/**
	 * Sets the organizationPhoneNumber attribute.
	 *
	 * @param organizationPhoneNumber The organizationPhoneNumber to set.
	 *
	 */
	public void setOrganizationPhoneNumber(String organizationPhoneNumber) {
		this.organizationPhoneNumber = organizationPhoneNumber;
	}


	/**
	 * Gets the organization800PhoneNumber attribute.
	 *
	 * @return Returns the organization800PhoneNumber
	 *
	 */
	public String getOrganization800PhoneNumber() {
		return organization800PhoneNumber;
	}

	/**
	 * Sets the organization800PhoneNumber attribute.
	 *
	 * @param organization800PhoneNumber The organization800PhoneNumber to set.
	 *
	 */
	public void setOrganization800PhoneNumber(String organization800PhoneNumber) {
		this.organization800PhoneNumber = organization800PhoneNumber;
	}


	/**
	 * Gets the organizationFaxNumber attribute.
	 *
	 * @return Returns the organizationFaxNumber
	 *
	 */
	public String getOrganizationFaxNumber() {
		return organizationFaxNumber;
	}

	/**
	 * Sets the organizationFaxNumber attribute.
	 *
	 * @param organizationFaxNumber The organizationFaxNumber to set.
	 *
	 */
	public void setOrganizationFaxNumber(String organizationFaxNumber) {
		this.organizationFaxNumber = organizationFaxNumber;
	}


	/**
	 * Gets the universityName attribute.
	 *
	 * @return Returns the universityName
	 *
	 */
	public String getUniversityName() {
		return universityName;
	}

	/**
	 * Sets the universityName attribute.
	 *
	 * @param universityName The universityName to set.
	 *
	 */
	public void setUniversityName(String universityName) {
		this.universityName = universityName;
	}


	/**
	 * Gets the organizationCheckPayableToName attribute.
	 *
	 * @return Returns the organizationCheckPayableToName
	 *
	 */
	public String getOrganizationCheckPayableToName() {
	    final SystemInformation systemInfo = getSystemInformationForRemitToAddress();
        if(systemInfo != null) {
            return systemInfo.getOrganizationCheckPayableToName();
        }
		return organizationCheckPayableToName;
	}

	/**
	 * Sets the organizationCheckPayableToName attribute.
	 *
	 * @param organizationCheckPayableToName The organizationCheckPayableToName to set.
	 *
	 */
	public void setOrganizationCheckPayableToName(String organizationCheckPayableToName) {
		this.organizationCheckPayableToName = organizationCheckPayableToName;
	}


	/**
	 * Gets the organization attribute.
	 *
	 * @return Returns the organization
	 *
	 */
	public Organization getOrganization() {
		return organization;
	}

	/**
	 * Sets the organization attribute.
	 *
	 * @param organization The organization to set.
	 * @deprecated
	 */
	@Deprecated
    public void setOrganization(Organization organization) {
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
	@Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

	/**
	 * Gets the processingChartOfAccount attribute.
	 *
	 * @return Returns the processingChartOfAccount
	 *
	 */
	public Chart getProcessingChartOfAccount() {
		return processingChartOfAccount;
	}

	/**
	 * Sets the processingChartOfAccount attribute.
	 *
	 * @param processingChartOfAccount The processingChartOfAccount to set.
	 * @deprecated
	 */
	@Deprecated
    public void setProcessingChartOfAccount(Chart processingChartOfAccount) {
		this.processingChartOfAccount = processingChartOfAccount;
	}

	/**
	 * Gets the processingOrganization attribute.
	 *
	 * @return Returns the processingOrganization
	 *
	 */
	public Organization getProcessingOrganization() {
		return processingOrganization;
	}

	/**
	 * Sets the processingOrganization attribute.
	 *
	 * @param processingOrganization The processingOrganization to set.
	 * @deprecated
	 */
	@Deprecated
    public void setProcessingOrganization(Organization processingOrganization) {
		this.processingOrganization = processingOrganization;
	}

    /**
     * Gets the organizationRemitToState attribute.
     * @return Returns the organizationRemitToState.
     */
    public StateEbo getOrganizationRemitToState() {
        if ( StringUtils.isBlank(organizationRemitToStateCode) ) {
            organizationRemitToState = null;
        } else {
            if ( organizationRemitToState == null || !StringUtils.equals( organizationRemitToState.getCode(),organizationRemitToStateCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(StateEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, organizationRemitToStateCode);
                    organizationRemitToState = moduleService.getExternalizableBusinessObject(StateEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return organizationRemitToState;
    }

    /**
     * Sets the organizationRemitToState attribute value.
     * @param organizationRemitToState The organizationRemitToState to set.
     * @deprecated
     */
    @Deprecated
    public void setOrganizationRemitToState(StateEbo organizationRemitToState) {
        this.organizationRemitToState = organizationRemitToState;
    }


    /**
     * Gets the printOption attribute.
     * @return Returns the printOption.
     */
    public PrintInvoiceOptions getPrintInvoiceOptions() {
        return printInvoiceOptions;
    }

    /**
     * Sets the printOption attribute value.
     * @param printOption The printOption to set.
     */
    public void setPrintInvoiceOptions(PrintInvoiceOptions printInvoiceOptions) {
        this.printInvoiceOptions = printInvoiceOptions;
    }

    /**
     * This method (a hack by any other name...) returns a string so that an organization options can have a link to view its own
     * inquiry page after a look up
     *
     * @return the String "View Organization Options"
     */
    public String getOrganizationOptionsViewer() {
        return "View Organization Options";
    }

    public String getOrganizationPostalZipCode() {
        return organizationPostalZipCode;
    }

    public String getOrganizationPostalCountryCode() {
        return organizationPostalCountryCode;
    }

    public void setOrganizationPostalCountryCode(String organizationPostalCountryCode) {
        this.organizationPostalCountryCode = organizationPostalCountryCode;
    }

    public void setOrganizationPostalZipCode(String organizationPostalZipCode) {
        this.organizationPostalZipCode = organizationPostalZipCode;
    }

    public PostalCodeEbo getOrgPostalZipCode() {
        if ( StringUtils.isBlank(organizationPostalZipCode) || StringUtils.isBlank(organizationPostalCountryCode) ) {
            orgPostalZipCode = null;
        } else {
            if ( orgPostalZipCode == null || !StringUtils.equals( orgPostalZipCode.getCode(),organizationPostalZipCode)
                    || !StringUtils.equals(orgPostalZipCode.getCountryCode(), organizationPostalCountryCode )) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(PostalCodeEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, organizationPostalCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, organizationPostalZipCode);
                    orgPostalZipCode = moduleService.getExternalizableBusinessObject(PostalCodeEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return orgPostalZipCode;
    }

    public void setOrgPostalZipCode(PostalCodeEbo orgPostalZipCode) {
        this.orgPostalZipCode = orgPostalZipCode;
    }

    public PostalCodeEbo getOrgRemitToZipCode() {
        if ( StringUtils.isBlank(organizationRemitToZipCode) || StringUtils.isBlank(organizationRemitToCountryCode) ) {
            orgRemitToZipCode = null;
        } else {
            if ( orgRemitToZipCode == null || !StringUtils.equals( orgRemitToZipCode.getCode(),organizationRemitToZipCode)
                    || !StringUtils.equals(orgRemitToZipCode.getCountryCode(), organizationPostalCountryCode )) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(PostalCodeEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, organizationPostalCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, organizationRemitToZipCode);
                    orgRemitToZipCode = moduleService.getExternalizableBusinessObject(PostalCodeEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return orgRemitToZipCode;
    }

    public void setOrgRemitToZipCode(PostalCodeEbo orgRemitToZipCode) {
        this.orgRemitToZipCode = orgRemitToZipCode;
    }

    public CountryEbo getOrgPostalCountryCode() {
        if ( StringUtils.isBlank(organizationPostalCountryCode) ) {
            orgPostalCountryCode = null;
        } else {
            if ( orgPostalCountryCode == null || !StringUtils.equals( orgPostalCountryCode.getCode(),organizationPostalCountryCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CountryEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, organizationPostalCountryCode);
                    orgPostalCountryCode = moduleService.getExternalizableBusinessObject(CountryEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return orgPostalCountryCode;
    }

    public void setOrgPostalCountryCode(CountryEbo orgPostalCountryCode) {
        this.orgPostalCountryCode = orgPostalCountryCode;
    }

    /**
     * @return the related SystemInformation object with the address to use as remit to address, if the remit to address is not editable on this OrganizationOptions business object
     */
    protected SystemInformation getSystemInformationForRemitToAddress() {
        if (!isRemitToAddressEditable()) {
            if (systemInformationForAddressName == null) {
                final Integer currentFiscalYear = getUniversityDateService().getCurrentFiscalYear();
                systemInformationForAddressName = getSystemInformationService()
                    .getByProcessingChartOrgAndFiscalYear(processingChartOfAccountCode, processingOrganizationCode, currentFiscalYear);
            }
            return systemInformationForAddressName;
        }
        return null;
    }

    /**
     * @return the related SystemInformation object with the address to use as remit to address name, if the remit to address name is not editable on this OrganizationOptions business object
     */
    protected SystemInformation getSystemInformationForRemitToAddressName() {
        if (!isRemitToAddressNameEditable()) {
            if (systemInformationForAddress == null) {
                final Integer currentFiscalYear = getUniversityDateService().getCurrentFiscalYear();
                systemInformationForAddress = getSystemInformationService()
                    .getByProcessingChartOrgAndFiscalYear(processingChartOfAccountCode, processingOrganizationCode, currentFiscalYear);
            }
            return systemInformationForAddress;
        }
        return null;
    }

    /**
     * @return true if the name for the remit to address is editable, false otherwise
     */
    protected boolean isRemitToAddressNameEditable() {
        return getParameterService().getParameterValueAsBoolean(OrganizationOptions.class, ArConstants.REMIT_TO_NAME_EDITABLE_IND);
    }

    /**
     * @return true if the remit to address is editable, false otherwise
     */
    protected boolean isRemitToAddressEditable() {
        return getParameterService().getParameterValueAsBoolean(OrganizationOptions.class, ArConstants.REMIT_TO_ADDRESS_EDITABLE_IND);
    }

    /**
     * @return the default implementation of the ParameterService
     */
    protected ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }

    /**
     * @return the default implementation of the SystemInformationService
     */
    protected SystemInformationService getSystemInformationService() {
        if (systemInformationService == null) {
            systemInformationService = SpringContext.getBean(SystemInformationService.class);
        }
        return systemInformationService;
    }

    /**
     * @return the default implementation of the UniversityDateService
     */
    protected UniversityDateService getUniversityDateService() {
        if (universityDateService == null) {
            universityDateService = SpringContext.getBean(UniversityDateService.class);
        }
        return universityDateService;
    }

    public String getOrganizationRemitToCountryCode() {
        return organizationRemitToCountryCode;
    }

    public void setOrganizationRemitToCountryCode(String organizationRemitToCountryCode) {
        this.organizationRemitToCountryCode = organizationRemitToCountryCode;
    }
}
