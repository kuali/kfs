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
import static org.kuali.kfs.module.tem.TemConstants.TemProfileParameters.VALID_KIM_TYPE_AFFILIATION_BY_TRAVER_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TemProfileParameters.VALID_TRAVELER_TYPE_BY_CUSTOMER_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.EMPLOYEE_TRAVELER_TYPE_CODES;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.kuali.kfs.module.tem.TemPropertyConstants.TemProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileAddress;
import org.kuali.kfs.module.tem.businessobject.TemProfileEmergencyContact;
import org.kuali.kfs.module.tem.businessobject.TemProfileFromCustomer;
import org.kuali.kfs.module.tem.businessobject.TemProfileFromKimPerson;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.kfs.module.tem.identity.TemOrganizationHierarchyRoleTypeService;
import org.kuali.kfs.module.tem.service.TemRoleService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.ChartOrgHolderImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.address.EntityAddressContract;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenship;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.personal.EntityBioDemographics;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.beans.BeanUtils;

/**
 *
 */
public class TravelerServiceImpl implements TravelerService {

    protected ParameterService parameterService;
    protected PersonService personService;
    protected IdentityManagementService identityManagementService;
    protected BusinessObjectService businessObjectService;
    protected DateTimeService dateTimeService;
    protected TemRoleService temRoleService;
    protected ChartService chartService;
    protected OrganizationService organizationService;
    protected RoleService roleService;
    protected AccountsReceivableModuleService accountsReceivableModuleService;

    protected static Logger LOG = Logger.getLogger(TravelerServiceImpl.class);

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
        retval.setStateCode(person.getAddressStateProvinceCode());
        retval.setZipCode(person.getAddressPostalCode());
        retval.setCityName(person.getAddressCity());
        retval.setCountryCode(person.getAddressCountryCode());
        retval.setEmailAddress(person.getEmailAddress());
        retval.setPhoneNumber(person.getPhoneNumber());
        retval.setTravelerTypeCode(EMP_TRAVELER_TYP_CD);
        return retval;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelerService#canIncludeProfileInSearch(org.kuali.kfs.module.tem.businessobject.TemProfile, java.lang.String, org.kuali.rice.kim.bo.Person, boolean, boolean, boolean, boolean, boolean)
     */
    @Override
    public boolean canIncludeProfileInSearch(TemProfile profile, String docType, Person user, boolean isProfileAdmin, boolean isAssignedArranger, boolean isOrgArranger, boolean isArrangerDoc, boolean isRiskManagement) {
        boolean canInclude = false;

        //arrange doc, risk management or user look up self
        if(isArrangerDoc || isRiskManagement || user.getPrincipalId().equals(profile.getPrincipalId())) {
            return true;
        }

        if(isProfileAdmin || isOrgArranger) {
            //pull the org they are responsible for and filter on that
            final String roleName = isOrgArranger ?
                TemConstants.TEM_ORGANIZATION_PROFILE_ARRANGER :
                TemConstants.TEM_PROFILE_ADMIN;
            canInclude |= isArrangeeByOrganizationByRole(user.getPrincipalId(), profile, roleName);
        }

        //check in arranger details if it does not already have the authority to view the profile
        if(!canInclude && isAssignedArranger) {
            //pull the arranger's profiles it is responsible for
            if (ObjectUtils.isNotNull(docType)){
                canInclude |= getTemRoleService().isTravelDocumentArrangerForProfile(docType, user.getPrincipalId(), profile.getProfileId());
            }else{
                // arranger for a non docType specific search, look up without the doctype comparison
                canInclude |= getTemRoleService().isArrangerForProfile(user.getPrincipalId(), profile.getProfileId());
            }
        }

        return canInclude;
    }

    /**
     * Checks both the organization approver and profile admin roles to see if the given principal can arrange for the role
     * @param principalId the principal id to check
     * @param profile the profile to see if the principal can be their arranger
     * @return true if the principal can be an arranger for the profile, false otherwise
     */
    @Override
    public boolean isArrangeeByOrganization(String principalId, TemProfile profile) {
        return isArrangeeByOrganizationByRole(principalId, profile, TemConstants.TEM_ORGANIZATION_PROFILE_ARRANGER) ||
                isArrangeeByOrganizationByRole(principalId, profile, TemConstants.TEM_PROFILE_ADMIN);
    }

    /**
     * Determines if the given principal id represents a user who can arrange trips for the given profile, by the power granted by the given role
     * @param principalId the principal id to see if they can act as arrangers
     * @param profile the profile to act as an arranger for
     * @param roleName the role it is expected the principal should be in
     * @return true if the principal can arrange, false otherwise
     */
    protected boolean isArrangeeByOrganizationByRole(String principalId, TemProfile profile, String roleName) {
        Organization org = profile.getHomeDeptOrg();
        final ChartOrgHolder chartOrg = getOrganizationForUser(principalId, roleName);
        if(ObjectUtils.isNotNull(chartOrg)) {
            final String roleChartOfAccountsCode = chartOrg.getChartOfAccountsCode();
            final String roleOrganizationCode = chartOrg.getOrganizationCode();
            return isParentOrg(org.getChartOfAccountsCode(), org.getOrganizationCode(), roleChartOfAccountsCode, roleOrganizationCode, true);
        }
        return false; // they're not in the role to begin with
    }

    @Override
    public void convertTemProfileToTravelerDetail(TemProfile profile, TravelerDetail detail){
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
            else {
                detail.setPrincipalName(KFSConstants.EMPTY_STRING);
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
            detail.setNonResidentAlien(profile.getNonResidentAlien());

            //reset traveler detail's emergency contact list
            detail.resetEmergencyContacts();

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

    /**
     * @see org.kuali.kfs.module.tem.service.TravelerService#copyTravelerDetail(org.kuali.kfs.module.tem.businessobject.TravelerDetail, java.lang.String)
     */
    @Override
    public TravelerDetail copyTravelerDetail(TravelerDetail travelerDetail, String documentNumber) {

        TravelerDetail newTravelerDetail = new TravelerDetail();

        //dateOfBirth actually doesn't belong to TravelDetail (only in Profile) so skipping it as it cause error in copyProperties
        BeanUtils.copyProperties(travelerDetail, newTravelerDetail, new String[]{TemProfileProperties.DATE_OF_BIRTH});
        newTravelerDetail.setId(null);
        newTravelerDetail.setVersionNumber(null);
        newTravelerDetail.setDocumentNumber(documentNumber);
        newTravelerDetail.setEmergencyContacts(copyTravelerDetailEmergencyContact(travelerDetail.getEmergencyContacts(), documentNumber));

        return newTravelerDetail;
    }


    /**
     *
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#copyTravelerDetailEmergencyContact(java.util.List, java.lang.String)
     */
    @Override
    public List<TravelerDetailEmergencyContact> copyTravelerDetailEmergencyContact(List<TravelerDetailEmergencyContact> emergencyContacts, String documentNumber) {
        List<TravelerDetailEmergencyContact> newEmergencyContacts = new ArrayList<TravelerDetailEmergencyContact>();
        if (emergencyContacts != null) {
            for (TravelerDetailEmergencyContact emergencyContact : emergencyContacts){
                TravelerDetailEmergencyContact newEmergencyContact = new TravelerDetailEmergencyContact();
                BeanUtils.copyProperties(emergencyContact, newEmergencyContact);
                newEmergencyContact.setDocumentNumber(documentNumber);
                newEmergencyContact.setVersionNumber(new Long(1));
                newEmergencyContact.setObjectId(null);
                newEmergencyContact.setId(null);
                newEmergencyContacts.add(newEmergencyContact);
            }
        }
        return newEmergencyContacts;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelerService#convertToTemProfileFromKim(org.kuali.rice.kim.bo.Person)
     */
    @Override
    public TemProfileFromKimPerson convertToTemProfileFromKim(final Person person) {
        TemProfileFromKimPerson retval = new TemProfileFromKimPerson();
        BeanUtils.copyProperties(person, retval);
        return retval;
    }

    @Override
    public boolean isEmployee(final TravelerDetail traveler) {
        final String param = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, EMPLOYEE_TRAVELER_TYPE_CODES);
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

    /**
     * @see org.kuali.kfs.module.tem.service.TravelerService#convertToTemProfileFromCustomer(org.kuali.kfs.integration.ar.AccountsReceivableCustomer)
     */
    @Override
    public TemProfileFromCustomer convertToTemProfileFromCustomer(AccountsReceivableCustomer person) {
        TemProfileFromCustomer retval = new TemProfileFromCustomer();

        final AccountsReceivableCustomerAddress address = getAddressFor(person);

        BeanUtils.copyProperties(person, retval);
        BeanUtils.copyProperties(address, retval);

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
            if (ObjectUtils.isNull(addressId) && TemConstants.CUSTOMER_PRIMARY_ADDRESS_TYPE_CODE.equals(address.getCustomerAddressTypeCode())) {
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
    public TemProfileAddress convertToTemProfileAddressFromKimAddress(final EntityAddressContract address) {
    	TemProfileAddress retval = new TemProfileAddress();

    	retval.setStreetAddressLine1(address.getLine1());
        retval.setStreetAddressLine2(address.getLine2());
        retval.setStateCode(address.getStateProvinceCode());
        retval.setZipCode(address.getPostalCode());
        retval.setCityName(address.getCity());
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
    public PersonService getPersonService() {
        if ( personService == null ) {
            personService = SpringContext.getBean(PersonService.class);
        }
        return personService;
    }

    /**
     * Copies relevant data from {@link TemProfile} to {@link Customer}
     * @see org.kuali.kfs.module.tem.service.TravelerService#copyTemProfileToCustomer(org.kuali.kfs.module.tem.businessobject.TemProfile, org.kuali.kfs.integration.ar.AccountsReceivableCustomer)
     */
    @Override
    public void copyTemProfileToCustomer(TemProfile profile, AccountsReceivableCustomer customer) {
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
        	customerAddress = getAddressFor(customer, profile.getTemProfileAddress().getCustomerAddressIdentifier());
        } else {
        	customerAddress = getAddressFor(customer);
        }
        if (customerAddress == null){
            customerAddress = getAccountsReceivableModuleService().createCustomerAddress();
            customerAddress.setCustomerAddressTypeCode(TemConstants.CUSTOMER_PRIMARY_ADDRESS_TYPE_CODE);
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
     * @see org.kuali.kfs.module.tem.service.TravelerService#populateTemProfile(org.kuali.kfs.module.tem.businessobject.TemProfile)
     */
    @Override
    public void populateTemProfile(TemProfile profile) {
        if(profile != null){
            if (!StringUtils.isBlank(profile.getPrincipalId())){
                Person person = getPersonService().getPerson(profile.getPrincipalId());
                profile.setPrincipal(person);
                Entity kimEntity = identityManagementService.getEntityByPrincipalId(profile.getPrincipalId());
                profile.setKimEntityInfo(kimEntity);
                copyKimDataToTemProfile(profile, profile.getPrincipal(), profile.getKimEntityInfo());
            } else if (ObjectUtils.isNotNull(profile.getCustomer())){
                copyCustomerToTemProfile(profile, profile.getCustomer());
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelerService#copyCustomerToTemProfile(TemProfile, AccountsReceivableCustomer)
     */
    @Override
    public void copyCustomerToTemProfile(TemProfile profile, AccountsReceivableCustomer customer) {
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

        profile.setEmailAddress(address.getCustomerEmailAddress());
        profile.setPhoneNumber(customer.getCustomerPhoneNumber());
        profile.setEmployeeId("None");
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelerService#copyKimDataToTemProfile(org.kuali.kfs.module.tem.businessobject.TemProfile, org.kuali.rice.kim.bo.Person, org.kuali.rice.kim.bo.entity.dto.KimEntityInfo)
     */
    @Override
    public void copyKimDataToTemProfile(TemProfile profile, Person principal, Entity kimEntity) {
        // copy principal data
        if (ObjectUtils.isNotNull(kimEntity)) {
            profile.setFirstName(principal.getFirstName().toUpperCase());
            profile.setMiddleName(principal.getMiddleName().toUpperCase());
            profile.setLastName(principal.getLastName().toUpperCase());
        }

        TemProfileAddress profileAddress = new TemProfileAddress();

        if(ObjectUtils.isNotNull(profile.getTemProfileAddress())) {
        	profileAddress = profile.getTemProfileAddress();
        }

        profileAddress.setProfileId(profile.getProfileId());

        profileAddress.setStreetAddressLine1(StringUtils.upperCase(principal.getAddressLine1Unmasked()));
        profileAddress.setStreetAddressLine2(StringUtils.upperCase(principal.getAddressLine2Unmasked()));
        profileAddress.setCityName(StringUtils.upperCase(principal.getAddressCity()));
        profileAddress.setStateCode(StringUtils.upperCase(principal.getAddressStateProvinceCodeUnmasked()));
        profileAddress.setZipCode(principal.getAddressPostalCodeUnmasked());
        profileAddress.setCountryCode(StringUtils.upperCase(principal.getAddressCountryCodeUnmasked()));

        profile.setTemProfileAddress(profileAddress);
        profile.setEmailAddress(StringUtils.upperCase(principal.getEmailAddressUnmasked()));
        profile.setPhoneNumber(principal.getPhoneNumberUnmasked());

        String primaryDeptCode[] = principal.getPrimaryDepartmentCode().split("-");
        if(primaryDeptCode != null && primaryDeptCode.length == 2){
            profile.setHomeDeptChartOfAccountsCode(primaryDeptCode[0]);
            profile.setHomeDeptOrgCode(primaryDeptCode[1]);
        }

        profile.refreshReferenceObject("homeDeptOrg");
        profile.setEmployeeId(principal.getEmployeeId());

        // Copy kim info to profile
        if(ObjectUtils.isNotNull(kimEntity)) {
        if (ObjectUtils.isNotNull(kimEntity.getBioDemographics())) {

            String birthDate = kimEntity.getBioDemographics().getBirthDate();
            java.util.Date parsedBirthDate = new java.util.Date();
            try {
                parsedBirthDate = new SimpleDateFormat(EntityBioDemographics.BIRTH_DATE_FORMAT).parse(birthDate);
            } catch (ParseException pe) {
                LOG.error("Error parsing EntityBioDemographics birth date: '" + birthDate + "'", pe);
            }
            Date dateOfBirth = new Date(parsedBirthDate.getTime());
            profile.setDateOfBirth(dateOfBirth);
            profile.setGender(kimEntity.getBioDemographics().getGenderCode());


        }
        List<EntityCitizenship> citizenships = kimEntity.getCitizenships();
        if (ObjectUtils.isNotNull(citizenships) && citizenships.size() > 0) {
            profile.setCitizenship(citizenships.get(0).getCountryCode());
        }
        }




    }



    /**
     * @see org.kuali.kfs.module.tem.service.TravelerService#isCustomerEmployee(org.kuali.kfs.integration.ar.AccountsReceivableCustomer)
     */
    @Override
    public boolean isCustomerEmployee(AccountsReceivableCustomer person) {
        List<String> empParams = new ArrayList<String>(getParameterService().getParameterValuesAsString(TemProfile.class, VALID_TRAVELER_TYPE_BY_CUSTOMER_TYPE));
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
     * @see org.kuali.kfs.module.tem.service.TravelerService#isKimPersonEmployee(org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean isKimPersonEmployee(Person person) {
        List<String> empParams = new ArrayList<String>(getParameterService().getParameterValuesAsString(TemProfile.class, VALID_KIM_TYPE_AFFILIATION_BY_TRAVER_TYPE));
        List<String> empCodes = new ArrayList<String>();
        List<String> nonEmpCodes = new ArrayList<String>();
        splitCodes(empCodes, nonEmpCodes, empParams);

        //for KIM we need the affiliation type in the entity
        Entity kimEntity = identityManagementService.getEntityByPrincipalId(person.getPrincipalId());
        for(EntityAffiliation affiliation: kimEntity.getAffiliations()) {
            if(empCodes.contains(affiliation.getAffiliationType().getCode())) {
                return true;
            } else if(nonEmpCodes.contains(affiliation.getAffiliationType().getCode())) {
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

    private boolean compareAddress(AccountsReceivableCustomerAddress customerAddress, TemProfile temProfile) {
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
            LOG.debug("No chart/org qualifications passed into isParentOrg()");
            return false;
        }
        if (ObjectUtils.isNull(roleChartCode) && ObjectUtils.isNull(roleOrgCode)) {
            LOG.debug("Call to "+this.getClass().getName()+" with no organization role qualifiers; both chart and organization code are null.  Please ensure that qualification data has organization information for this role.");
            return false;
        }
        if (ObjectUtils.isNull(roleOrgCode)) {
            return roleChartCode.equals(chartCode)
                    || (descendHierarchy && chartService.isParentChart(chartCode, roleChartCode));
        }
        final boolean parentOrg = (roleChartCode.equals(chartCode) && roleOrgCode.equals(orgCode))
                || (descendHierarchy && organizationService.isParentOrganization(chartCode, orgCode, roleChartCode, roleOrgCode));
        return parentOrg;
    }

    protected ChartOrgHolder getOrganizationForUser(String principalId, String roleName) {
        if (principalId == null) {
            return null;
        }
        Map<String,String> qualification = new HashMap<String,String>(1);
        qualification.put(TemOrganizationHierarchyRoleTypeService.PERFORM_QUALIFIER_MATCH, "false");
        List<Map<String,String>> roleQualifiers = getRoleService().getRoleQualifersForPrincipalByNamespaceAndRolename(principalId, TemConstants.PARAM_NAMESPACE, roleName, qualification);
        if ((roleQualifiers != null) && !roleQualifiers.isEmpty()) {
            final ChartOrgHolder chartOrg = new ChartOrgHolderImpl(roleQualifiers.get(0).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), roleQualifiers.get(0).get(KfsKimAttributes.ORGANIZATION_CODE));
            return chartOrg;
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

    public TemRoleService getTemRoleService() {
        return temRoleService;
    }

    public void setTemRoleService(TemRoleService temRoleService) {
        this.temRoleService = temRoleService;
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
    public RoleService getRoleService() {
        return roleService;
    }

    /**
     * Sets the roleManagementService attribute value.
     * @param roleManagementService The roleManagementService to set.
     */
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
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