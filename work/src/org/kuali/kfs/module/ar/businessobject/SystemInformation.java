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
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherDocumentationLocation;
import org.kuali.kfs.fp.businessobject.PaymentReasonCode;
import org.kuali.kfs.integration.ar.AccountsReceivableSystemInformation;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;
import org.kuali.rice.location.framework.state.StateEbo;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class SystemInformation extends PersistableBusinessObjectBase implements MutableInactivatable, AccountsReceivableSystemInformation, FiscalYearBasedBusinessObject {

    protected Integer universityFiscalYear;
    protected String processingChartOfAccountCode;
    protected String processingOrganizationCode;
    protected String universityFederalEmployerIdentificationNumber;
    protected String discountObjectCode;
    protected String universityClearingChartOfAccountsCode;
    protected String universityClearingAccountNumber;
    protected String universityClearingSubAccountNumber;
    protected String universityClearingObjectCode;
    protected String universityClearingSubObjectCode;
    protected String creditCardObjectCode;
    protected String lockboxNumber;
    protected boolean active;
    protected String organizationRemitToAddressName;
    protected String organizationRemitToLine1StreetAddress;
    protected String organizationRemitToLine2StreetAddress;
    protected String organizationRemitToCityName;
    protected String organizationRemitToStateCode;
    protected String organizationRemitToZipCode;
    protected String organizationCheckPayableToName;
    protected String financialDocumentInitiatorIdentifier;
//    protected String wireChartOfAccountsCode;
//    protected String wireAccountNumber;
//    protected String wireSubAccountNumber;
//    protected String wireObjectCode;
//    protected String wireSubObjectCode;

    protected ObjectCode creditCardFinancialObject;
    protected SubObjectCode universityClearingSubObject;
    protected ObjectCode universityClearingObject;
    protected ObjectCode discountFinancialObject;
    protected Organization processingOrganization;
    protected Chart processingChartOfAccount;
    protected Account universityClearingAccount;
    protected Chart universityClearingChartOfAccounts;
    protected SubAccount universityClearingSubAccount;
    protected ObjectCode universityFiscalYearObject;
    protected StateEbo organizationRemitToState;
//    protected Chart wireChartOfAccounts;
//    protected Account wireAccount;
//    protected SubAccount wireSubAccount;
//    protected ObjectCode wireObject;
//    protected SubObjectCode wireSubObject;
    protected Person financialDocumentInitiator;
    protected SystemOptions universityFiscal;
    protected PostalCodeEbo orgRemitToZipCode;
    /* Start TEM REFUND Merge */
    private PaymentReasonCode refundPaymentReason;
    private ObjectCode refundObjectCode;
//    private DisbursementVoucherDocumentationLocation refundDocumentationLocation;
    /* End TEM REFUND Merge */
    /* Start TEM REFUND Merge */
    private String refundPaymentReasonCode;
    private String refundFinancialObjectCode;
    private String refundDocumentationLocationCode;
/* End TEM REFUND Merge */

	public Person getFinancialDocumentInitiator() {
	    financialDocumentInitiator = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(financialDocumentInitiatorIdentifier, financialDocumentInitiator);
        return financialDocumentInitiator;
    }

    public void setFinancialDocumentInitiator(Person financialDocumentInitiator) {
        this.financialDocumentInitiator = financialDocumentInitiator;
    }

    public String getRefundPaymentReasonCode() {
        return refundPaymentReasonCode;
    }

    public void setRefundPaymentReasonCode(String refundPaymentReasonCode) {
        this.refundPaymentReasonCode = refundPaymentReasonCode;
    }

    public String getRefundFinancialObjectCode() {
        return refundFinancialObjectCode;
    }

    public void setRefundFinancialObjectCode(String refundFinancialObjectCode) {
        this.refundFinancialObjectCode = refundFinancialObjectCode;
    }

    public String getRefundDocumentationLocationCode() {
        return refundDocumentationLocationCode;
    }

    public void setRefundDocumentationLocationCode(String refundDocumentationLocationCode) {
        this.refundDocumentationLocationCode = refundDocumentationLocationCode;
    }

    /**
	 * Default constructor.
	 */
	public SystemInformation() {

	}

	/**
	 * Gets the universityFiscalYear attribute.
	 *
	 * @return Returns the universityFiscalYear
	 *
	 */
	@Override
    public Integer getUniversityFiscalYear() {
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 *
	 * @param universityFiscalYear The universityFiscalYear to set.
	 *
	 */
	@Override
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}

	/**
	 * Gets the processingChartOfAccountCode attribute.
	 *
	 * @return Returns the processingChartOfAccountCode
	 *
	 */
	@Override
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
	@Override
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
     * Gets the universityFederalEmployerIdentificationNumber attribute.
     *
     * @return Returns the universityFederalEmployerIdentificationNumber
     *
     */
    public String getUniversityFederalEmployerIdentificationNumber() {
        return universityFederalEmployerIdentificationNumber;
    }

    /**
     * Sets the universityFederalEmployerIdentificationNumber attribute.
     *
     * @param universityFederalEmployerIdentificationNumber The universityFederalEmployerIdentificationNumber to set.
     *
     */
    public void setUniversityFederalEmployerIdentificationNumber(String universityFederalEmployerIdentificationNumber) {
        this.universityFederalEmployerIdentificationNumber = universityFederalEmployerIdentificationNumber;
    }

	/**
	 * Gets the discountObjectCode attribute.
	 *
	 * @return Returns the discountObjectCode
	 *
	 */
	public String getDiscountObjectCode() {
		return discountObjectCode;
	}

	/**
	 * Sets the discountObjectCode attribute.
	 *
	 * @param discountObjectCode The discountObjectCode to set.
	 *
	 */
	public void setDiscountObjectCode(String refundFinancialObjectCode) {
		this.discountObjectCode = refundFinancialObjectCode;
	}

	/**
	 * Gets the universityClearingChartOfAccountsCode attribute.
	 *
	 * @return Returns the universityClearingChartOfAccountsCode
	 *
	 */
	public String getUniversityClearingChartOfAccountsCode() {
		return universityClearingChartOfAccountsCode;
	}

	/**
	 * Sets the universityClearingChartOfAccountsCode attribute.
	 *
	 * @param universityClearingChartOfAccountsCode The universityClearingChartOfAccountsCode to set.
	 *
	 */
	public void setUniversityClearingChartOfAccountsCode(String universityClearingChartOfAccountsCode) {
		this.universityClearingChartOfAccountsCode = universityClearingChartOfAccountsCode;
	}


	/**
	 * Gets the universityClearingAccountNumber attribute.
	 *
	 * @return Returns the universityClearingAccountNumber
	 *
	 */
	public String getUniversityClearingAccountNumber() {
		return universityClearingAccountNumber;
	}

	/**
	 * Sets the universityClearingAccountNumber attribute.
	 *
	 * @param universityClearingAccountNumber The universityClearingAccountNumber to set.
	 *
	 */
	public void setUniversityClearingAccountNumber(String universityClearingAccountNumber) {
		this.universityClearingAccountNumber = universityClearingAccountNumber;
	}


	/**
	 * Gets the universityClearingSubAccountNumber attribute.
	 *
	 * @return Returns the universityClearingSubAccountNumber
	 *
	 */
	public String getUniversityClearingSubAccountNumber() {
		return universityClearingSubAccountNumber;
	}

	/**
	 * Sets the universityClearingSubAccountNumber attribute.
	 *
	 * @param universityClearingSubAccountNumber The universityClearingSubAccountNumber to set.
	 *
	 */
	public void setUniversityClearingSubAccountNumber(String universityClearingSubAccountNumber) {
		this.universityClearingSubAccountNumber = universityClearingSubAccountNumber;
	}


	/**
	 * Gets the universityClearingObjectCode attribute.
	 *
	 * @return Returns the universityClearingObjectCode
	 *
	 */
	public String getUniversityClearingObjectCode() {
		return universityClearingObjectCode;
	}

	/**
	 * Sets the universityClearingObjectCode attribute.
	 *
	 * @param universityClearingObjectCode The universityClearingObjectCode to set.
	 *
	 */
	public void setUniversityClearingObjectCode(String universityClearingObjectCode) {
		this.universityClearingObjectCode = universityClearingObjectCode;
	}


	/**
	 * Gets the universityClearingSubObjectCode attribute.
	 *
	 * @return Returns the universityClearingSubObjectCode
	 *
	 */
	public String getUniversityClearingSubObjectCode() {
		return universityClearingSubObjectCode;
	}

	/**
	 * Sets the universityClearingSubObjectCode attribute.
	 *
	 * @param universityClearingSubObjectCode The universityClearingSubObjectCode to set.
	 *
	 */
	public void setUniversityClearingSubObjectCode(String universityClearingSubObjectCode) {
		this.universityClearingSubObjectCode = universityClearingSubObjectCode;
	}


	/**
	 * Gets the creditCardObjectCode attribute.
	 *
	 * @return Returns the creditCardObjectCode
	 *
	 */
	public String getCreditCardObjectCode() {
		return creditCardObjectCode;
	}

	/**
	 * Sets the creditCardObjectCode attribute.
	 *
	 * @param creditCardObjectCode The creditCardObjectCode to set.
	 *
	 */
	public void setCreditCardObjectCode(String creditCardObjectCode) {
		this.creditCardObjectCode = creditCardObjectCode;
	}


	/**
	 * Gets the lockboxNumber attribute.
	 *
	 * @return Returns the lockboxNumber
	 *
	 */
	public String getLockboxNumber() {
		return lockboxNumber;
	}

	/**
	 * Sets the lockboxNumber attribute.
	 *
	 * @param lockboxNumber The lockboxNumber to set.
	 *
	 */
	public void setLockboxNumber(String lockboxNumber) {
		this.lockboxNumber = lockboxNumber;
	}

    /**
     * Gets the active attribute.
     *
     * @return Returns the active
     *
     */
    @Override
    public boolean isActive() {
        return active;
    }

	/**
	 * Gets the active attribute.
	 *
	 * @return Returns the active
	 *
	 */
	public boolean getActive() {
		return active;
	}

	/**
	 * Sets the active attribute.
	 *
	 * @param active The active to set.
	 *
	 */
	@Override
    public void setActive(boolean active) {
		this.active = active;
	}

	/**
     * Gets the financialDocumentInitiatorIdentifier attribute.
     * @return Returns the financialDocumentInitiatorIdentifier.
     */
    public String getFinancialDocumentInitiatorIdentifier() {
        return financialDocumentInitiatorIdentifier;
    }

    /**
     * Sets the financialDocumentInitiatorIdentifier attribute value.
     * @param financialDocumentInitiatorIdentifier The financialDocumentInitiatorIdentifier to set.
     */
    public void setFinancialDocumentInitiatorIdentifier(String financialDocumentInitiatorIdentifier) {
        this.financialDocumentInitiatorIdentifier = financialDocumentInitiatorIdentifier;
    }

    /**
     * Gets the organizationCheckPayableToName attribute.
     * @return Returns the organizationCheckPayableToName.
     */
    public String getOrganizationCheckPayableToName() {
        return organizationCheckPayableToName;
    }

    /**
     * Sets the organizationCheckPayableToName attribute value.
     * @param organizationCheckPayableToName The organizationCheckPayableToName to set.
     */
    public void setOrganizationCheckPayableToName(String organizationCheckPayableToName) {
        this.organizationCheckPayableToName = organizationCheckPayableToName;
    }

    /**
     * Gets the organizationRemitToAddressName attribute.
     * @return Returns the organizationRemitToAddressName.
     */
    public String getOrganizationRemitToAddressName() {
        return organizationRemitToAddressName;
    }

    /**
     * Sets the organizationRemitToAddressName attribute value.
     * @param organizationRemitToAddressName The organizationRemitToAddressName to set.
     */
    public void setOrganizationRemitToAddressName(String organizationRemitToAddressName) {
        this.organizationRemitToAddressName = organizationRemitToAddressName;
    }

    /**
     * Gets the organizationRemitToCityName attribute.
     * @return Returns the organizationRemitToCityName.
     */
    public String getOrganizationRemitToCityName() {
        return organizationRemitToCityName;
    }

    /**
     * Sets the organizationRemitToCityName attribute value.
     * @param organizationRemitToCityName The organizationRemitToCityName to set.
     */
    public void setOrganizationRemitToCityName(String organizationRemitToCityName) {
        this.organizationRemitToCityName = organizationRemitToCityName;
    }

    /**
     * Gets the organizationRemitToLine1StreetAddress attribute.
     * @return Returns the organizationRemitToLine1StreetAddress.
     */
    public String getOrganizationRemitToLine1StreetAddress() {
        return organizationRemitToLine1StreetAddress;
    }

    /**
     * Sets the organizationRemitToLine1StreetAddress attribute value.
     * @param organizationRemitToLine1StreetAddress The organizationRemitToLine1StreetAddress to set.
     */
    public void setOrganizationRemitToLine1StreetAddress(String organizationRemitToLine1StreetAddress) {
        this.organizationRemitToLine1StreetAddress = organizationRemitToLine1StreetAddress;
    }

    /**
     * Gets the organizationRemitToLine2StreetAddress attribute.
     * @return Returns the organizationRemitToLine2StreetAddress.
     */
    public String getOrganizationRemitToLine2StreetAddress() {
        return organizationRemitToLine2StreetAddress;
    }

    /**
     * Sets the organizationRemitToLine2StreetAddress attribute value.
     * @param organizationRemitToLine2StreetAddress The organizationRemitToLine2StreetAddress to set.
     */
    public void setOrganizationRemitToLine2StreetAddress(String organizationRemitToLine2StreetAddress) {
        this.organizationRemitToLine2StreetAddress = organizationRemitToLine2StreetAddress;
    }

    /**
     * Gets the organizationRemitToStateCode attribute.
     * @return Returns the organizationRemitToStateCode.
     */
    public String getOrganizationRemitToStateCode() {
        return organizationRemitToStateCode;
    }

    /**
     * Sets the organizationRemitToStateCode attribute value.
     * @param organizationRemitToStateCode The organizationRemitToStateCode to set.
     */
    public void setOrganizationRemitToStateCode(String organizationRemitToStateCode) {
        this.organizationRemitToStateCode = organizationRemitToStateCode;
    }

    /**
     * Gets the organizationRemitToZipCode attribute.
     * @return Returns the organizationRemitToZipCode.
     */
    public String getOrganizationRemitToZipCode() {
        return organizationRemitToZipCode;
    }

    /**
     * Sets the organizationRemitToZipCode attribute value.
     * @param organizationRemitToZipCode The organizationRemitToZipCode to set.
     */
    public void setOrganizationRemitToZipCode(String organizationRemitToZipCode) {
        this.organizationRemitToZipCode = organizationRemitToZipCode;
    }

//    /**
//     * Gets the wireAccountNumber attribute.
//     * @return Returns the wireAccountNumber.
//     */
//    public String getWireAccountNumber() {
//        return wireAccountNumber;
//    }
//
//    /**
//     * Sets the wireAccountNumber attribute value.
//     * @param wireAccountNumber The wireAccountNumber to set.
//     */
//    public void setWireAccountNumber(String wireAccountNumber) {
//        this.wireAccountNumber = wireAccountNumber;
//    }
//
//    /**
//     * Gets the wireChartOfAccountsCode attribute.
//     * @return Returns the wireChartOfAccountsCode.
//     */
//    public String getWireChartOfAccountsCode() {
//        return wireChartOfAccountsCode;
//    }
//
//    /**
//     * Sets the wireChartOfAccountsCode attribute value.
//     * @param wireChartOfAccountsCode The wireChartOfAccountsCode to set.
//     */
//    public void setWireChartOfAccountsCode(String wireChartOfAccountsCode) {
//        this.wireChartOfAccountsCode = wireChartOfAccountsCode;
//    }
//
//    /**
//     * Gets the wireObjectCode attribute.
//     * @return Returns the wireObjectCode.
//     */
//    public String getWireObjectCode() {
//        return wireObjectCode;
//    }
//
//    /**
//     * Sets the wireObjectCode attribute value.
//     * @param wireObjectCode The wireObjectCode to set.
//     */
//    public void setWireObjectCode(String wireObjectCode) {
//        this.wireObjectCode = wireObjectCode;
//    }
//
//    /**
//     * Gets the wireSubAccountNumber attribute.
//     * @return Returns the wireSubAccountNumber.
//     */
//    public String getWireSubAccountNumber() {
//        return wireSubAccountNumber;
//    }
//
//    /**
//     * Sets the wireSubAccountNumber attribute value.
//     * @param wireSubAccountNumber The wireSubAccountNumber to set.
//     */
//    public void setWireSubAccountNumber(String wireSubAccountNumber) {
//        this.wireSubAccountNumber = wireSubAccountNumber;
//    }
//
//    /**
//     * Gets the wireSubObjectCode attribute.
//     * @return Returns the wireSubObjectCode.
//     */
//    public String getWireSubObjectCode() {
//        return wireSubObjectCode;
//    }
//
//    /**
//     * Sets the wireSubObjectCode attribute value.
//     * @param wireSubObjectCode The wireSubObjectCode to set.
//     */
//    public void setWireSubObjectCode(String wireSubObjectCode) {
//        this.wireSubObjectCode = wireSubObjectCode;
//    }

	/**
	 * Gets the creditCardFinancialObject attribute.
	 *
	 * @return Returns the creditCardFinancialObject
	 *
	 */
	public ObjectCode getCreditCardFinancialObject() {
		return creditCardFinancialObject;
	}

	/**
	 * Sets the creditCardFinancialObject attribute.
	 *
	 * @param creditCardFinancialObject The creditCardFinancialObject to set.
	 * @deprecated
	 */
	@Deprecated
    public void setCreditCardFinancialObject(ObjectCode creditCardFinancialObject) {
		this.creditCardFinancialObject = creditCardFinancialObject;
	}

	/**
	 * Gets the universityClearingSubObject attribute.
	 *
	 * @return Returns the universityClearingSubObject
	 *
	 */
	public SubObjectCode getUniversityClearingSubObject() {
		return universityClearingSubObject;
	}

	/**
	 * Sets the universityClearingSubObject attribute.
	 *
	 * @param universityClearingSubObject The universityClearingSubObject to set.
	 * @deprecated
	 */
	@Deprecated
    public void setUniversityClearingSubObject(SubObjectCode universityClearingSubObject) {
		this.universityClearingSubObject = universityClearingSubObject;
	}

	/**
	 * Gets the universityClearingObject attribute.
	 *
	 * @return Returns the universityClearingObject
	 *
	 */
	public ObjectCode getUniversityClearingObject() {
		return universityClearingObject;
	}

	/**
	 * Sets the universityClearingObject attribute.
	 *
	 * @param universityClearingObject The universityClearingObject to set.
	 * @deprecated
	 */
	@Deprecated
    public void setUniversityClearingObject(ObjectCode universityClearingObject) {
		this.universityClearingObject = universityClearingObject;
	}

	/**
	 * Gets the discountFinancialObject attribute.
	 *
	 * @return Returns the discountFinancialObject
	 *
	 */
	public ObjectCode getDiscountFinancialObject() {
		return discountFinancialObject;
	}

	/**
	 * Sets the discountFinancialObject attribute.
	 *
	 * @param discountFinancialObject The discountFinancialObject to set.
	 * @deprecated
	 */
	@Deprecated
    public void setDiscountFinancialObject(ObjectCode refundFinancialObject) {
		this.discountFinancialObject = refundFinancialObject;
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
	 * Gets the universityClearingAccount attribute.
	 *
	 * @return Returns the universityClearingAccount
	 *
	 */
	public Account getUniversityClearingAccount() {
		return universityClearingAccount;
	}

	/**
	 * Sets the universityClearingAccount attribute.
	 *
	 * @param universityClearingAccount The universityClearingAccount to set.
	 * @deprecated
	 */
	@Deprecated
    public void setUniversityClearingAccount(Account universityClearingAccount) {
		this.universityClearingAccount = universityClearingAccount;
	}

	/**
	 * Gets the universityClearingChartOfAccounts attribute.
	 *
	 * @return Returns the universityClearingChartOfAccounts
	 *
	 */
	public Chart getUniversityClearingChartOfAccounts() {
		return universityClearingChartOfAccounts;
	}

	/**
	 * Sets the universityClearingChartOfAccounts attribute.
	 *
	 * @param universityClearingChartOfAccounts The universityClearingChartOfAccounts to set.
	 * @deprecated
	 */
	@Deprecated
    public void setUniversityClearingChartOfAccounts(Chart universityClearingChartOfAccounts) {
		this.universityClearingChartOfAccounts = universityClearingChartOfAccounts;
	}

    /**
     * Gets the universityClearingSubAccount attribute.
     * @return Returns the universityClearingSubAccount.
     */
    public SubAccount getUniversityClearingSubAccount() {
        return universityClearingSubAccount;
    }

    /**
     * Sets the universityClearingSubAccount attribute value.
     * @param universityClearingSubAccount The universityClearingSubAccount to set.
     * @deprecated
     */
    @Deprecated
    public void setUniversityClearingSubAccount(SubAccount universityClearingSubAccount) {
        this.universityClearingSubAccount = universityClearingSubAccount;
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
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, KFSConstants.COUNTRY_CODE_UNITED_STATES);/*RICE20_REFACTORME*/
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

//    /**
//     * Gets the wireAccount attribute.
//     * @return Returns the wireAccount.
//     */
//    public Account getWireAccount() {
//        return wireAccount;
//    }
//
//    /**
//     * Sets the wireAccount attribute value.
//     * @param wireAccount The wireAccount to set.
//     * @deprecated
//     */
//    public void setWireAccount(Account wireAccount) {
//        this.wireAccount = wireAccount;
//    }
//
//    /**
//     * Gets the wireChartOfAccounts attribute.
//     * @return Returns the wireChartOfAccounts.
//     */
//    public Chart getWireChartOfAccounts() {
//        return wireChartOfAccounts;
//    }
//
//    /**
//     * Sets the wireChartOfAccounts attribute value.
//     * @param wireChartOfAccounts The wireChartOfAccounts to set.
//     * @deprecated
//     */
//    public void setWireChartOfAccounts(Chart wireChartOfAccounts) {
//        this.wireChartOfAccounts = wireChartOfAccounts;
//    }
//
//    /**
//     * Gets the wireObject attribute.
//     * @return Returns the wireObject.
//     */
//    public ObjectCode getWireObject() {
//        return wireObject;
//    }
//
//    /**
//     * Sets the wireObject attribute value.
//     * @param wireObject The wireObject to set.
//     * @deprecated
//     */
//    public void setWireObject(ObjectCode wireObject) {
//        this.wireObject = wireObject;
//    }
//
//    /**
//     * Gets the wireSubAccount attribute.
//     * @return Returns the wireSubAccount.
//     */
//    public SubAccount getWireSubAccount() {
//        return wireSubAccount;
//    }
//
//    /**
//     * Sets the wireSubAccount attribute value.
//     * @param wireSubAccount The wireSubAccount to set.
//     * @deprecated
//     */
//    public void setWireSubAccount(SubAccount wireSubAccount) {
//        this.wireSubAccount = wireSubAccount;
//    }
//
//    /**
//     * Gets the wireSubObject attribute.
//     * @return Returns the wireSubObject.
//     */
//    public SubObjectCode getWireSubObject() {
//        return wireSubObject;
//    }
//
//    /**
//     * Sets the wireSubObject attribute value.
//     * @param wireSubObject The wireSubObject to set.
//     * @deprecated
//     */
//    public void setWireSubObject(SubObjectCode wireSubObject) {
//        this.wireSubObject = wireSubObject;
//    }

    public PaymentReasonCode getRefundPaymentReason() {
        return refundPaymentReason;
    }

    public void setRefundPaymentReason(PaymentReasonCode refundPaymentReason) {
        this.refundPaymentReason = refundPaymentReason;
    }

    public ObjectCode getRefundObjectCode() {
        return refundObjectCode;
    }

    public void setRefundObjectCode(ObjectCode refundObjectCode) {
        this.refundObjectCode = refundObjectCode;
    }

    /**
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("processingChartOfAccountCode", this.processingChartOfAccountCode);
        m.put("processingOrganizationCode", this.processingOrganizationCode);
	    return m;
    }

	@Override
    public String toString() {
	    return ((this.universityFiscalYear == null) ? "" : this.universityFiscalYear + "-") +
	            this.processingChartOfAccountCode + "-" + this.processingOrganizationCode;
	}

    public ObjectCode getUniversityFiscalYearObject() {
        return universityFiscalYearObject;
    }

    public void setUniversityFiscalYearObject(ObjectCode universityFiscalYearObject) {
        this.universityFiscalYearObject = universityFiscalYearObject;
    }

    /**
     * This method (a hack by any other name...) returns a string so that an organization options can have a link to view its own
     * inquiry page after a look up
     *
     * @return the String "View System Information"
     */
    public String getSystemInformationViewer() {
        return "View System Information";
    }

    /**
     * Gets the universityFiscal attribute.
     *
     * @return Returns the universityFiscal.
     */
    public SystemOptions getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal attribute value.
     *
     * @param universityFiscal The universityFiscal to set.
     */
    public void setUniversityFiscal(SystemOptions universityFiscal) {
        this.universityFiscal = universityFiscal;
    }

    /**
     * Gets the orgRemitToZipCode attribute.
     * @return Returns the orgRemitToZipCode.
     */
    public PostalCodeEbo getOrgRemitToZipCode() {
        if ( StringUtils.isBlank(organizationRemitToZipCode) ) {
            orgRemitToZipCode = null;
        } else {
            if ( orgRemitToZipCode == null || !StringUtils.equals( orgRemitToZipCode.getCode(),organizationRemitToZipCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(PostalCodeEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, KFSConstants.COUNTRY_CODE_UNITED_STATES);/*RICE20_REFACTORME*/
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, organizationRemitToZipCode);
                    orgRemitToZipCode = moduleService.getExternalizableBusinessObject(PostalCodeEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }

        return orgRemitToZipCode;
    }

    /**
     * Sets the orgRemitToZipCode attribute value.
     * @param orgRemitToZipCode The orgRemitToZipCode to set.
     */
    public void setOrgRemitToZipCode(PostalCodeEbo orgRemitToZipCode) {
        this.orgRemitToZipCode = orgRemitToZipCode;
    }
}
