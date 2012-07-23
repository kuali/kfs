/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.tem.service.impl;

import static org.kuali.kfs.module.tem.TemConstants.EMP_TRAVELER_TYP_CD;
import static org.kuali.kfs.module.tem.TemConstants.NONEMP_TRAVELER_TYP_CD;
import static org.kuali.kfs.module.tem.TemConstants.TemProfileParameters.AR_CUSTOMER_TYPE_TO_TRAVELER_TYPE_CROSSWALK;
import static org.kuali.kfs.module.tem.TemConstants.TemProfileParameters.KIM_AFFILIATION_TYPE_TO_TRAVELER_TYPE_CROSSWALK;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.EMPLOYEE_TRAVELER_TYPE_CODES;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerAddress;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileAddress;
import org.kuali.kfs.module.tem.businessobject.TemProfileEmergencyContact;
import org.kuali.kfs.module.tem.businessobject.TemProfileFromCustomer;
import org.kuali.kfs.module.tem.businessobject.TemProfileFromKimPerson;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService;
import org.kuali.kfs.module.tem.identity.TemOrganizationHierarchyRoleTypeService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.dto.KimEntityAffiliationInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityCitizenshipInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityInfo;
import org.kuali.rice.kim.bo.entity.impl.KimEntityAddressImpl;
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class TravelerServiceImpl implements TravelerService {
    private ParameterService parameterService;
    private PersonService<Person> personService;
    private TravelArrangerDocumentService arrangerDocumentService;
    private IdentityManagementService identityManagementService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private ChartService chartService;
    private OrganizationService organizationService;
    private RoleManagementService roleManagementService;
    private AccountsReceivableModuleService accountsReceivableModuleService;
    
    private static Logger LOG = Logger.getLogger(TravelerServiceImpl.class);

    /**
     * Creates a {@link TravelerDetail} from a {@link Person} instance
     *
     * @param person to create {@link TravelerDetail} instance from
     * @return a new {@link Traveler} detail instance
     */
    @Override
    public TravelerDetail convertToTraveler(final Person person) {
        TravelerDetail retval = new TravelerDetail();
        retval.setPrincipalId(person.getPrincipalId());
        retval.setPrincipalName(person.getPrincipalName());
        retval.setFirstName(person.getFirstName());
        retval.setLastName(person.getLastName());
        retval.setStreetAddressLine1(person.getAddressLine1());
        retval.setStreetAddressLine2(person.getAddressLine2());
        retval.setStateCode(person.getAddressStateCode());
        retval.setZipCode(person.getAddressPostalCode());
        retval.setCityName(person.getAddressCityName());
        retval.setCountryCode(person.getAddressCountryCode());
        retval.setEmailAddress(person.getEmailAddress());
        retval.setPhoneNumber(person.getPhoneNumber());
        retval.setTravelerTypeCode(EMP_TRAVELER_TYP_CD);
        return retval;
    }
    
    @Override
    public boolean canIncludeProfileInSearch(TEMProfile profile, Person user, boolean isProfileAdmin, boolean isAssignedArranger, boolean isOrgArranger, boolean isArrangerDoc, boolean isRiskManagement) {
        boolean canInclude = false;
        if(isArrangerDoc || isRiskManagement) {
            return true;
        }
        if(isProfileAdmin || isOrgArranger) {
            //pull the org they are responsible for and filter on that
            Organization org = profile.getHomeDeptOrg();
            String roleName; 
            if(isOrgArranger) {
                roleName = TemConstants.TEM_ORGANIZATION_PROFILE_ARRANGER;
            } else {
                roleName = TemConstants.TEM_PROFILE_ADMIN;
            }
            String roleQualifiers[] = this.getOrganizationForUser(user.getPrincipalId(), roleName);
            if(ObjectUtils.isNotNull(roleQualifiers)) {
                String roleChartOfAccountsCode = roleQualifiers[0];
                String roleOrganizationCode = roleQualifiers[1];
                canInclude |= isParentOrg(org.getChartOfAccountsCode(), org.getOrganizationCode(), roleChartOfAccountsCode, roleOrganizationCode, true);
            } 
            
            
        } 
        if(isAssignedArranger) {
            //pull the arranger's profiles it is responsible for
            canInclude |= getArrangerDocumentService().isArrangerForProfile(user.getPrincipalId(), profile.getProfileId());
        }
        if(user.getPrincipalId().equals(profile.getPrincipalId())) {
            canInclude = true;
        }
        
        return canInclude;
    }
    
    @Override
    public void convertTEMProfileToTravelerDetail(TEMProfile profile, TravelerDetail detail){
        if(profile != null){
            if(detail.getId() == null){
                SequenceAccessorService sas = SpringContext.getBean(SequenceAccessorService.class);
                long id = sas.getNextAvailableSequenceNumber(TemConstants.TEM_TRAVELER_DETAIL_SEQ_NAME);        
                detail.setId((int)id);
            }
            
            detail.setFirstName(profile.getFirstName());
            detail.setLastName(profile.getLastName());
            detail.setStreetAddressLine1(profile.getTemProfileAddress().getStreetAddressLine1());
            detail.setStreetAddressLine2(profile.getTemProfileAddress().getStreetAddressLine2());
            detail.setStateCode(profile.getTemProfileAddress().getStateCode());
            detail.setCityName(profile.getTemProfileAddress().getCityName());
            detail.setZipCode(profile.getTemProfileAddress().getZipCode());
            detail.setCountryCode(profile.getTemProfileAddress().getCountryCode());
            detail.setPrincipalId(profile.getPrincipalId());
            if (ObjectUtils.isNotNull(profile.getPrincipal())) {
                detail.setPrincipalName(profile.getPrincipal().getPrincipalName());
            }
            detail.setCustomer(profile.getCustomer());
            detail.setTravelerType(profile.getTravelerType());
            detail.setTravelerTypeCode(profile.getTravelerTypeCode());
            detail.setCustomerNumber(profile.getCustomerNumber());
            detail.setEmailAddress(profile.getEmailAddress());
            detail.setPhoneNumber(profile.getPhoneNumber());
            detail.setDateOfBirth(profile.getDateOfBirth());
            detail.setGender(profile.getGender());
            detail.setCitizenship(profile.getCitizenship());
            detail.setDriversLicenseExpDate(profile.getDriversLicenseExpDate());
            detail.setDriversLicenseNumber(profile.getDriversLicenseNumber());
            detail.setDriversLicenseState(profile.getDriversLicenseState());
            detail.setNotifyTAFinal(profile.isNotifyTAFinal());
            detail.setNotifyTAStatusChange(profile.isNotifyTAStatusChange());
            detail.setNotifyTERFinal(profile.isNotifyTERFinal());
            detail.setNotifyTERStatusChange(profile.isNotifyTERStatusChange());
            
            if (ObjectUtils.isNotNull(profile.getEmergencyContacts())){
                int count = 1;
                for(TemProfileEmergencyContact profileContact : profile.getEmergencyContacts()){
                    TravelerDetailEmergencyContact contact = new TravelerDetailEmergencyContact(profileContact);
                    contact.setDocumentNumber(detail.getDocumentNumber());
                    contact.setFinancialDocumentLineNumber(count);
                    contact.setTravelerDetailId(detail.getId());
                    detail.getEmergencyContacts().add(contact);
                    count++;
                }            
            }        
        }
    }

    @Override
    public TravelerDetail copyTraveler(TravelerDetail fromTraveler, String documentNumber) {
        TravelerDetail detail = new TravelerDetail();
        if(fromTraveler != null){
            if(detail.getId() == null){
                SequenceAccessorService sas = SpringContext.getBean(SequenceAccessorService.class);
                long id = sas.getNextAvailableSequenceNumber(TemConstants.TEM_TRAVELER_DETAIL_SEQ_NAME);        
                detail.setId((int)id);
            }
            
            detail.setFirstName(fromTraveler.getFirstName());
            detail.setLastName(fromTraveler.getLastName());
            detail.setStreetAddressLine1(fromTraveler.getStreetAddressLine1());
            detail.setStreetAddressLine2(fromTraveler.getStreetAddressLine2());
            detail.setStateCode(fromTraveler.getStateCode());
            detail.setCityName(fromTraveler.getCityName());
            detail.setZipCode(fromTraveler.getZipCode());
            detail.setCountryCode(fromTraveler.getCountryCode());
            detail.setPrincipalId(fromTraveler.getPrincipalId());
            detail.setPrincipalName(fromTraveler.getPrincipalName());
            detail.setCustomer(fromTraveler.getCustomer());
            detail.setTravelerType(fromTraveler.getTravelerType());
            detail.setTravelerTypeCode(fromTraveler.getTravelerTypeCode());
            detail.setCustomerNumber(fromTraveler.getCustomerNumber());
            detail.setEmailAddress(fromTraveler.getEmailAddress());
            detail.setPhoneNumber(fromTraveler.getPhoneNumber());
            detail.setDateOfBirth(fromTraveler.getDateOfBirth());
            detail.setGender(fromTraveler.getGender());
            detail.setCitizenship(fromTraveler.getCitizenship());
            detail.setDriversLicenseExpDate(fromTraveler.getDriversLicenseExpDate());
            detail.setDriversLicenseNumber(fromTraveler.getDriversLicenseNumber());
            detail.setDriversLicenseState(fromTraveler.getDriversLicenseState());
            detail.setNotifyTAFinal(fromTraveler.isNotifyTAFinal());
            detail.setNotifyTAStatusChange(fromTraveler.isNotifyTAStatusChange());
            detail.setNotifyTERFinal(fromTraveler.isNotifyTERFinal());
            detail.setNotifyTERStatusChange(fromTraveler.isNotifyTERStatusChange());

            if (ObjectUtils.isNotNull(fromTraveler.getEmergencyContacts())){
                int count = 1;
                for(TravelerDetailEmergencyContact fromContact : fromTraveler.getEmergencyContacts()){
                    TravelerDetailEmergencyContact contact = new TravelerDetailEmergencyContact();
                    contact.setPrimary(fromContact.isPrimary());
                    contact.setContactRelationTypeCode(fromContact.getContactRelationTypeCode());
                    contact.setContactRelationType(fromContact.getContactRelationType());
                    contact.setContactName(fromContact.getContactName());
                    contact.setPhoneNumber(fromContact.getPhoneNumber());
                    contact.setEmailAddress(fromContact.getEmailAddress());
                    contact.setDocumentNumber(documentNumber);
                    contact.setFinancialDocumentLineNumber(count);
                    contact.setTravelerDetailId(detail.getId());
                    detail.getEmergencyContacts().add(contact);
                    count++;
                }            
            }        
}
        return detail;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelerService#convertToTemProfileFromKim(org.kuali.rice.kim.bo.Person)
     */
    @Override
    public TemProfileFromKimPerson convertToTemProfileFromKim(final Person person) {
        TemProfileFromKimPerson retval = new TemProfileFromKimPerson();
        
        try {
            BeanUtils.copyProperties(retval, person);
        }
        catch (IllegalAccessException ex) {
            LOG.error("IllegalAccessException in convertToTemProfileFromKim - BeanUtils.copyProperties.", ex);
        }
        catch (InvocationTargetException ex) {
            LOG.error("InvocationTargetException in convertToTemProfileFromKim - BeanUtils.copyProperties.", ex);
        }        
        
        return retval;
    }

    @Override
    public boolean isEmployee(final TravelerDetail traveler) {
        final String param = getParameterService().getParameterValue(TemParameterConstants.TEM_DOCUMENT.class, EMPLOYEE_TRAVELER_TYPE_CODES);
        List<String> employeeTypes = StrTokenizer.getCSVInstance(param).getTokenList();
        
        return employeeTypes.contains(StringUtils.defaultString(traveler.getTravelerTypeCode()));
    }

    /**
     * Creates a {@link TravelerDetail} from a {@link Person} instance
     *
     * @param customer to create {@link TravelerDetail} instance from
     * @return a new {@link Traveler} detail instance
     */
    @Override
    public TravelerDetail convertToTraveler(final AccountsReceivableCustomer customer) {
        TravelerDetail retval = new TravelerDetail();

        final AccountsReceivableCustomerAddress address = getAddressFor(customer);
        final String[] names = customer.getCustomerName().split(" ");
        final String firstName = names[0];
        final String lastName = names[names.length - 1];
       
        retval.setCustomerNumber(customer.getCustomerNumber());
        retval.refreshReferenceObject(TemPropertyConstants.CUSTOMER);
        retval.setFirstName(firstName);
        retval.setLastName(lastName);
        retval.setStreetAddressLine1(address.getCustomerLine1StreetAddress());
        retval.setStreetAddressLine2(address.getCustomerLine2StreetAddress());
        retval.setStateCode(address.getCustomerStateCode());
        retval.setZipCode(address.getCustomerZipCode());
        retval.setCityName(address.getCustomerCityName());
        retval.setCountryCode(address.getCustomerCountryCode());
        retval.setEmailAddress(address.getCustomerEmailAddress());
        retval.setPhoneNumber(customer.getCustomerPhoneNumber());
        retval.setTravelerTypeCode(NONEMP_TRAVELER_TYP_CD);

        return retval;
    }
    
    @Override
    public TemProfileFromCustomer convertToTemProfileFromCustomer(AccountsReceivableCustomer person) {
        TemProfileFromCustomer retval = new TemProfileFromCustomer();
    
        final AccountsReceivableCustomerAddress address = getAddressFor(person);
                     
        try {
            BeanUtils.copyProperties(retval, person);
            BeanUtils.copyProperties(retval, address);
        }
        catch (IllegalAccessException ex) {
            LOG.error("IllegalAccessException in convertToTemProfileFromCustomer - BeanUtils.copyProperties.", ex);
        }
        catch (InvocationTargetException ex) {
            LOG.error("InvocationTargetException in convertToTemProfileFromCustomer - BeanUtils.copyProperties.", ex);
        } 
        
        return retval;
    }

    /**
     * Dig up the primary address for a {@link Customer}
     *
     * @param customer to get address for
     * @return {@link CustomerAddress} instance
     */
    protected AccountsReceivableCustomerAddress getAddressFor(final AccountsReceivableCustomer customer) {
        return getAddressFor(customer, null);
    }
    
    /**
     * Dig up the primary address for a {@link Customer}
     *
     * @param customer to get address for
     * @return {@link CustomerAddress} instance
     */
    protected AccountsReceivableCustomerAddress getAddressFor(final AccountsReceivableCustomer customer, Integer addressId) {
        for (final AccountsReceivableCustomerAddress address : customer.getAccountsReceivableCustomerAddresses()) {
            if (ObjectUtils.isNull(addressId) && "P".equals(address.getAccountsReceivableCustomerAddressType().getCustomerAddressTypeCode())) {
                return address;
            } else if (address.getCustomerAddressIdentifier().equals(addressId)) {
            	return address;
            }
        }
        return null;
    }
    
    @Override
    public TemProfileAddress convertToTemProfileAddressFromCustomer(AccountsReceivableCustomerAddress customerAddress) {
    	TemProfileAddress retval = new TemProfileAddress();
    
    	retval.setStreetAddressLine1(customerAddress.getCustomerLine1StreetAddress());
        retval.setStreetAddressLine2(customerAddress.getCustomerLine2StreetAddress());
        retval.setStateCode(customerAddress.getCustomerStateCode());
        retval.setZipCode(customerAddress.getCustomerZipCode());
        retval.setCityName(customerAddress.getCustomerCityName());
        retval.setCountryCode(customerAddress.getCustomerCountryCode());
        retval.setCustomerNumber(customerAddress.getCustomerNumber());
        retval.setCustomerAddressIdentifier(customerAddress.getCustomerAddressIdentifier());
        
        return retval;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.service.TravelerService#convertToTemProfileAddressFromKimAddress(org.kuali.rice.kim.bo.Person)
     */
    @Override
    public TemProfileAddress convertToTemProfileAddressFromKimAddress(final KimEntityAddressImpl address) {
    	TemProfileAddress retval = new TemProfileAddress();
        
    	retval.setStreetAddressLine1(address.getLine1());
        retval.setStreetAddressLine2(address.getLine2());
        retval.setStateCode(address.getStateCode());
        retval.setZipCode(address.getPostalCode());
        retval.setCityName(address.getCityName());
        retval.setCountryCode(address.getCountryCode());
        
        return retval;
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(final ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the personService attribute. 
     * @return Returns the personService.
     */
    public PersonService<Person> getPersonService() {
        if ( personService == null ) {
            personService = SpringContext.getBean(PersonService.class);
        }
        return personService;
    }
    
    /**
     * Copies relevant data from {@link TemProfile} to {@link Customer}
     * @see org.kuali.kfs.module.tem.service.TravelerService#copyTEMProfileToCustomer(org.kuali.kfs.module.tem.businessobject.TEMProfile, org.kuali.kfs.integration.ar.AccountsReceivableCustomer)
     */
    @Override
    public void copyTEMProfileToCustomer(TEMProfile profile, AccountsReceivableCustomer customer) {
        String tempName = profile.getFirstName() + " " + (StringUtils.isEmpty(profile.getMiddleName()) ? "" : profile.getMiddleName() + " ") + profile.getLastName();
        if (tempName.length() > 40){
            tempName = profile.getFirstName() + " " + profile.getLastName();
            while (tempName.length() > 40){
                tempName = tempName.substring(0, tempName.length()-1);
            }
        }
        customer.setCustomerName(tempName);
        customer.setCustomerEmailAddress(profile.getEmailAddress());
        customer.setCustomerPhoneNumber(profile.getPhoneNumber());
        AccountsReceivableCustomerAddress customerAddress = null;
        if(ObjectUtils.isNotNull(profile.getTemProfileAddress()) && ObjectUtils.isNotNull(profile.getTemProfileAddress().getCustomerAddressIdentifier())) {
        	customerAddress = (AccountsReceivableCustomerAddress) getAddressFor(customer, profile.getTemProfileAddress().getCustomerAddressIdentifier());
        } else {
        	customerAddress = (AccountsReceivableCustomerAddress) getAddressFor(customer);
        }
        if (customerAddress == null){
            customerAddress = getAccountsReceivableModuleService().createCustomerAddress();
            customerAddress.setCustomerAddressTypeCode("P");
            customerAddress.setCustomerAddressName(tempName);
            customer.setCustomerAddressChangeDate(dateTimeService.getCurrentSqlDate());
        } else {
        	if (compareAddress(customerAddress, profile)) {
                customer.setCustomerAddressChangeDate(dateTimeService.getCurrentSqlDate());
            }
        }
        customerAddress.setCustomerLine1StreetAddress(profile.getTemProfileAddress().getStreetAddressLine1());
        customerAddress.setCustomerLine2StreetAddress(profile.getTemProfileAddress().getStreetAddressLine2());
        customerAddress.setCustomerCityName(profile.getTemProfileAddress().getCityName());
        customerAddress.setCustomerStateCode(profile.getTemProfileAddress().getStateCode());
        customerAddress.setCustomerZipCode(profile.getTemProfileAddress().getZipCode());
        customerAddress.setCustomerCountryCode(profile.getTemProfileAddress().getCountryCode());
        customerAddress.setCustomerEmailAddress(profile.getEmailAddress());
        
        if (customer.getAccountsReceivableCustomerAddresses() == null){
            customer.setAccountsReceivableCustomerAddresses(new ArrayList<AccountsReceivableCustomerAddress>());
        }
        if (customer.getAccountsReceivableCustomerAddresses().size() == 0){
            List<AccountsReceivableCustomerAddress> customerAddresses = customer.getAccountsReceivableCustomerAddresses();
            customerAddresses.add(customerAddress);
            customer.setAccountsReceivableCustomerAddresses(customerAddresses);
        }
        
        customer.setCustomerRecordAddDate(dateTimeService.getCurrentSqlDate());
        customer.setCustomerLastActivityDate(dateTimeService.getCurrentSqlDate());
        customer.setCustomerBirthDate(profile.getDateOfBirth());
      
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelerService#populateTEMProfile(org.kuali.kfs.module.tem.businessobject.TEMProfile)
     */
    @Override
    public void populateTEMProfile(TEMProfile profile) {
        if(profile != null){
            if (!StringUtils.isBlank(profile.getPrincipalId())){
                PersonImpl person = (PersonImpl) getPersonService().getPerson(profile.getPrincipalId());
                profile.setPrincipal(person);
                KimEntityInfo kimEntityInfo = identityManagementService.getEntityInfoByPrincipalId(profile.getPrincipalId());
                profile.setKimEntityInfo(kimEntityInfo);
                copyKimDataToTEMProfile(profile, profile.getPrincipal(), profile.getKimEntityInfo());
            } else if (!StringUtils.isBlank(profile.getCustomerNumber())){
                copyCustomerToTEMProfile(profile, profile.getCustomer());
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelerService#copyCustomerToTEMProfile(org.kuali.kfs.module.tem.businessobject.TEMProfile, org.kuali.kfs.integration.ar.AccountsReceivableCustomer)
     */
    @Override
    public void copyCustomerToTEMProfile(TEMProfile profile, AccountsReceivableCustomer customer) {
        String[] customerNames = customer.getCustomerName().split(" ");
        if (customerNames.length == 1){
            profile.setFirstName(customerNames[0]);
        }
        else if (customerNames.length == 2){
            profile.setFirstName(customerNames[0]);
            profile.setLastName(customerNames[1]);
        }
        else if (customerNames.length == 3){
            profile.setFirstName(customerNames[0]);
            profile.setMiddleName(customerNames[1]);
            profile.setLastName(customerNames[2]);
        }
        else{
            profile.setFirstName(customerNames[0]);
            profile.setMiddleName(customerNames[1]);
            profile.setLastName(customerNames[2]);
            for (int i=3;i<customerNames.length;i++){
                profile.setLastName(profile.getLastName() + " " + customerNames[i]);
            }
            
        }
        AccountsReceivableCustomerAddress address = customer.getPrimaryAddress();
        TemProfileAddress profileAddress = new TemProfileAddress();
        
        if(ObjectUtils.isNotNull(profile.getTemProfileAddress())) {
        	profileAddress = profile.getTemProfileAddress();
        }
        
        profileAddress.setProfileId(profile.getProfileId());
        
        profileAddress.setStreetAddressLine1(address.getCustomerLine1StreetAddress());
        profileAddress.setStreetAddressLine2(address.getCustomerLine2StreetAddress());
        profileAddress.setCityName(address.getCustomerCityName());
        profileAddress.setStateCode(address.getCustomerStateCode());
        profileAddress.setZipCode(address.getCustomerZipCode());
        profileAddress.setCountryCode(address.getCustomerCountryCode());
        
        profile.setTemProfileAddress(profileAddress);
        
        profile.setEmailAddress(customer.getCustomerEmailAddress());
        profile.setPhoneNumber(customer.getCustomerPhoneNumber());
        profile.setEmployeeId("None");
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelerService#copyPrincipalToTEMProfile(org.kuali.kfs.module.tem.businessobject.TEMProfile, org.kuali.kfs.integration.ar.AccountsReceivableCustomer)
     */
    @Override
    public void copyKimDataToTEMProfile(TEMProfile profile, PersonImpl principal, KimEntityInfo kimEntityInfo) {
        // copy principal data
        profile.setFirstName(principal.getFirstName().toUpperCase());
        profile.setMiddleName(principal.getMiddleName().toUpperCase());
        profile.setLastName(principal.getLastName().toUpperCase());
        
        TemProfileAddress profileAddress = new TemProfileAddress();
        
        if(ObjectUtils.isNotNull(profile.getTemProfileAddress())) {
        	profileAddress = profile.getTemProfileAddress();
        }
        
        profileAddress.setProfileId(profile.getProfileId());
        
        profileAddress.setStreetAddressLine1(principal.getAddressLine1Unmasked().toUpperCase());
        profileAddress.setStreetAddressLine2(principal.getAddressLine2Unmasked().toUpperCase());
        profileAddress.setCityName(principal.getAddressCityName().toUpperCase());
        profileAddress.setStateCode(principal.getAddressStateCodeUnmasked().toUpperCase());
        profileAddress.setZipCode(principal.getAddressPostalCodeUnmasked());
        profileAddress.setCountryCode(principal.getAddressCountryCodeUnmasked().toUpperCase());
        
        profile.setTemProfileAddress(profileAddress);
        profile.setEmailAddress(principal.getEmailAddressUnmasked().toUpperCase());
        profile.setPhoneNumber(principal.getPhoneNumberUnmasked());
        
        String primaryDeptCode[] = principal.getPrimaryDepartmentCode().split("-");
        if(primaryDeptCode != null && primaryDeptCode.length == 2){
            profile.setHomeDeptChartOfAccountsCode(primaryDeptCode[0]);
            profile.setHomeDeptOrgCode(primaryDeptCode[1]);
        }
        
        profile.refreshReferenceObject("homeDeptOrg");
        profile.setEmployeeId(principal.getEmployeeId());
        
        // copy kim info
        if (ObjectUtils.isNotNull(kimEntityInfo.getBioDemographics())) {
            Date dob = new Date(kimEntityInfo.getBioDemographics().getBirthDate().getTime());
            profile.setDateOfBirth(dob);
            profile.setGender(kimEntityInfo.getBioDemographics().getGenderCode());
        }
        List<KimEntityCitizenshipInfo> citizenships = kimEntityInfo.getCitizenships();
        if (ObjectUtils.isNotNull(citizenships) && citizenships.size() > 0) {
            profile.setCitizenship(citizenships.get(0).getCountryCode());
        }
    }
    
    

    /**
     * @see org.kuali.kfs.module.tem.service.TravelerService#isCustomerEmployee(org.kuali.kfs.integration.ar.AccountsReceivableCustomer)
     */
    @Override
    public boolean isCustomerEmployee(AccountsReceivableCustomer person) {
        List<String> empParams = getParameterService().getParameterValues(TemParameterConstants.TEM_PROFILE.class, AR_CUSTOMER_TYPE_TO_TRAVELER_TYPE_CROSSWALK);
        List<String> empCodes = new ArrayList<String>();
        List<String> nonEmpCodes = new ArrayList<String>();
        splitCodes(empCodes, nonEmpCodes, empParams);
        
        if(empCodes.contains(person.getCustomerTypeCode())) {
            return true;
        } else if(nonEmpCodes.contains(person.getCustomerTypeCode())) {
            return false;
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelerService#isKimPersonEmployee(org.kuali.rice.kim.bo.impl.PersonImpl)
     */
    @Override
    public boolean isKimPersonEmployee(PersonImpl person) {
        List<String> empParams = getParameterService().getParameterValues(TemParameterConstants.TEM_PROFILE.class, KIM_AFFILIATION_TYPE_TO_TRAVELER_TYPE_CROSSWALK);
        List<String> empCodes = new ArrayList<String>();
        List<String> nonEmpCodes = new ArrayList<String>();
        splitCodes(empCodes, nonEmpCodes, empParams);
        
        //for KIM we need the affiliation type in the entity
        KimEntityInfo kimEntityInfo = identityManagementService.getEntityInfoByPrincipalId(person.getPrincipalId());
        for(KimEntityAffiliationInfo info: kimEntityInfo.getAffiliations()) {
            if(empCodes.contains(info.getAffiliationTypeCode())) {
                return true;
            } else if(nonEmpCodes.contains(info.getAffiliationTypeCode())) {
                return false;
            }
        }
        
        return false;
    }
    
    private void splitCodes(List<String> empCodes, List<String> nonEmpCodes, List<String> empParams) {
        for(String param: empParams) {
            String [] splitParams = param.split("=");
            String typeCode = splitParams[1];
            if(typeCode.equals(TemConstants.EMP_TRAVELER_TYP_CD)) {
                empCodes.add(splitParams[0]);
            } else if(typeCode.equals(TemConstants.NONEMP_TRAVELER_TYP_CD)) {
                nonEmpCodes.add(splitParams[0]);
            }
        }
    }
    
    private boolean compareAddress(AccountsReceivableCustomerAddress customerAddress, TEMProfile temProfile) {
    	if(!StringUtils.equalsIgnoreCase(customerAddress.getCustomerLine1StreetAddress(), temProfile.getTemProfileAddress().getStreetAddressLine1())) {
    		return true;
    	}
    	if(!StringUtils.equalsIgnoreCase(customerAddress.getCustomerLine2StreetAddress(), temProfile.getTemProfileAddress().getStreetAddressLine2())) {
    		return true;
    	}
    	if(!StringUtils.equalsIgnoreCase(customerAddress.getCustomerCityName(), temProfile.getTemProfileAddress().getCityName())) {
    		return true;
    	}
    	if(!StringUtils.equalsIgnoreCase(customerAddress.getCustomerStateCode(), temProfile.getTemProfileAddress().getStateCode())) {
    		return true;
    	}
    	if(!StringUtils.equalsIgnoreCase(customerAddress.getCustomerZipCode(), temProfile.getTemProfileAddress().getZipCode())) {
    		return true;
    	}
    	if(!StringUtils.equalsIgnoreCase(customerAddress.getCustomerCountryCode(), temProfile.getTemProfileAddress().getCountryCode())) {
    		return true;
    	}
    	if(!StringUtils.equalsIgnoreCase(customerAddress.getCustomerEmailAddress(), temProfile.getEmailAddress())) {
    		return true;
    	}
        
        return false;
    }

    @Override
    public boolean isParentOrg(String chartCode, String orgCode, String roleChartCode, String roleOrgCode, boolean descendHierarchy) {
        if ( StringUtils.isBlank(chartCode) || StringUtils.isBlank(orgCode) ) {
            debug("No chart/org qualifications passed into isParentOrg()");
            return false;
        }
        if (ObjectUtils.isNull(roleChartCode) && ObjectUtils.isNull(roleOrgCode)) {
            debug("Call to "+this.getClass().getName()+" with no organization role qualifiers; both chart and organization code are null.  Please ensure that qualification data has organization information for this role.");
            return false;
        }
        if (ObjectUtils.isNull(roleOrgCode)) {
            return roleChartCode.equals(chartCode) 
                    || (descendHierarchy && chartService.isParentChart(chartCode, roleChartCode));
        }
        return (roleChartCode.equals(chartCode) && roleOrgCode.equals(orgCode)) 
                || (descendHierarchy && organizationService.isParentOrganization(chartCode, orgCode, roleChartCode, roleOrgCode));
    }
    
    protected String[] getOrganizationForUser(String principalId, String roleName) {
        if (principalId == null) {
            return null;
        }
        AttributeSet qualification = new AttributeSet(1);
        qualification.put(TemOrganizationHierarchyRoleTypeService.PERFORM_QUALIFIER_MATCH, "false");
        List<AttributeSet> roleQualifiers = getRoleManagementService().getRoleQualifiersForPrincipal(principalId, TemConstants.PARAM_NAMESPACE, roleName, qualification); 
        if ((roleQualifiers != null) && !roleQualifiers.isEmpty()) {
            String[] qualifiers = new String[2];
            qualifiers[0] = roleQualifiers.get(0).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            qualifiers[1] = roleQualifiers.get(0).get(KfsKimAttributes.ORGANIZATION_CODE);
            return qualifiers;
        }
        return null;
    }
    /**
     * Gets the identityManagementService attribute. 
     * @return Returns the identityManagementService.
     */
    public IdentityManagementService getIdentityManagementService() {
        return identityManagementService;
    }

    /**
     * Sets the identityManagementService attribute value.
     * @param identityManagementService The identityManagementService to set.
     */
    public void setIdentityManagementService(IdentityManagementService identityManagementService) {
        this.identityManagementService = identityManagementService;
    }

	/**
	 * Gets the businessObjectService attribute. 
	 * @return Returns the businessObjectService.
	 */
	public BusinessObjectService getBusinessObjectService() {
		return businessObjectService;
	}

	/**
	 * Sets the businessObjectService attribute value.
	 * @param businessObjectService The businessObjectService to set.
	 */
	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}

	/**
	 * Sets the dateTimeService attribute value.
	 * @param dateTimeService The dateTimeService to set.
	 */
	public void setDateTimeService(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

	/**
	 * Gets the dateTimeService attribute. 
	 * @return Returns the dateTimeService.
	 */
	public DateTimeService getDateTimeService() {
		return dateTimeService;
	}

    /**
     * Gets the arrangerDocumentService attribute. 
     * @return Returns the arrangerDocumentService.
     */
    public TravelArrangerDocumentService getArrangerDocumentService() {
        return arrangerDocumentService;
    }

    /**
     * Sets the arrangerDocumentService attribute value.
     * @param arrangerDocumentService The arrangerDocumentService to set.
     */
    public void setArrangerDocumentService(TravelArrangerDocumentService arrangerDocumentService) {
        this.arrangerDocumentService = arrangerDocumentService;
    }
	
    public ChartService getChartService() {
        return chartService;
    }

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    public OrganizationService getOrganizationService() {
        return organizationService;
    }

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /**
     * Gets the roleManagementService attribute. 
     * @return Returns the roleManagementService.
     */
    public RoleManagementService getRoleManagementService() {
        return roleManagementService;
    }

    /**
     * Sets the roleManagementService attribute value.
     * @param roleManagementService The roleManagementService to set.
     */
    public void setRoleManagementService(RoleManagementService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }
    
    protected AccountsReceivableModuleService getAccountsReceivableModuleService() {
        if (accountsReceivableModuleService == null) {
            this.accountsReceivableModuleService = SpringContext.getBean(AccountsReceivableModuleService.class);
        }
        
        return accountsReceivableModuleService;
    }

    public void setAccountsReceivableModuleService(AccountsReceivableModuleService accountsReceivableModuleService) {
        this.accountsReceivableModuleService = accountsReceivableModuleService;
    }    
    
}