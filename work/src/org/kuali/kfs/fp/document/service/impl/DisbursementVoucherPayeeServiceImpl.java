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
package org.kuali.kfs.fp.document.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementPayee;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorType;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.authorization.DocumentAuthorizer;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService; import java.util.ArrayList;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * implementing the service methods defined in DisbursementVoucherPayeeService
 * 
 * @see DisbursementVoucherPayeeService
 */
public class DisbursementVoucherPayeeServiceImpl implements DisbursementVoucherPayeeService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherPayeeServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private DocumentService documentService;
    private ParameterService parameterService;
    private VendorService vendorService;

    public final static String addressPattern = "{0}, {1}, {2} {3}";

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#getPayeeTypeDescription(java.lang.String)
     */
    public String getPayeeTypeDescription(String payeeTypeCode) {
        String payeeTypeDescription = StringUtils.EMPTY;

        if (KFSConstants.PaymentPayeeTypes.EMPLOYEE.equals(payeeTypeCode)) {
            payeeTypeDescription = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.NON_VENDOR_EMPLOYEE_PAYEE_TYPE_LABEL_PARM_NM);
        }
        else if (KFSConstants.PaymentPayeeTypes.VENDOR.equals(payeeTypeCode)) {
            payeeTypeDescription = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.PO_AND_DV_PAYEE_TYPE_LABEL_PARM_NM);
        }
        else if (KFSConstants.PaymentPayeeTypes.REVOLVING_FUND_VENDOR.equals(payeeTypeCode)) {
            payeeTypeDescription = this.getVendorTypeDescription(VendorConstants.VendorTypes.REVOLVING_FUND);
        }
        else if (KFSConstants.PaymentPayeeTypes.SUBJECT_PAYMENT_VENDOR.equals(payeeTypeCode)) {
            payeeTypeDescription = this.getVendorTypeDescription(VendorConstants.VendorTypes.SUBJECT_PAYMENT);
        }
        else if (KFSConstants.PaymentPayeeTypes.CUSTOMER.equals(payeeTypeCode)) {
            payeeTypeDescription = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.PAYEE_TYPE_NAME);
        }

        return payeeTypeDescription;
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#isEmployee(org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail)
     */
    @Override
    public boolean isEmployee(DisbursementVoucherPayeeDetail dvPayeeDetail) {
        // If is vendor, then check vendor employee flag
        if (this.isVendor(dvPayeeDetail)) {
            VendorDetail vendor = vendorService.getByVendorNumber(dvPayeeDetail.getDisbVchrPayeeIdNumber());
            return vendor == null ? false : vendorService.isVendorInstitutionEmployee(vendor.getVendorHeaderGeneratedIdentifier());
        }

        String payeeTypeCode = dvPayeeDetail.getDisbursementVoucherPayeeTypeCode();
        return KFSConstants.PaymentPayeeTypes.EMPLOYEE.equals(payeeTypeCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#isEmployee(org.kuali.kfs.fp.businessobject.DisbursementPayee)
     */
    public boolean isEmployee(DisbursementPayee payee) {
        // If is vendor, then check vendor employee flag
        if (this.isVendor(payee)) {
            VendorDetail vendor = vendorService.getByVendorNumber(payee.getPayeeIdNumber());
            return vendor == null ? false : vendorService.isVendorInstitutionEmployee(vendor.getVendorHeaderGeneratedIdentifier());
        }

        String payeeTypeCode = payee.getPayeeTypeCode();
        return KFSConstants.PaymentPayeeTypes.EMPLOYEE.equals(payeeTypeCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#isVendor(org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail)
     */
    public boolean isVendor(DisbursementVoucherPayeeDetail dvPayeeDetail) {
        String payeeTypeCode = dvPayeeDetail.getDisbursementVoucherPayeeTypeCode();
        return DisbursementVoucherConstants.VENDOR_PAYEE_TYPE_CODES.contains(payeeTypeCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#isVendor(org.kuali.kfs.fp.businessobject.DisbursementPayee)
     */
    public boolean isVendor(DisbursementPayee payee) {
        String payeeTypeCode = payee.getPayeeTypeCode();
        return DisbursementVoucherConstants.VENDOR_PAYEE_TYPE_CODES.contains(payeeTypeCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#isPayeeIndividualVendor(org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail)
     */
    public boolean isPayeeIndividualVendor(DisbursementVoucherPayeeDetail dvPayeeDetail) {
        return this.isVendor(dvPayeeDetail) ? this.isPayeeIndividualVendor(dvPayeeDetail.getDisbVchrPayeeIdNumber()) : false;
    }
    
	/* Start TEM Refund Merge */
    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#isTaxReviewRequired(String)
     */
    public boolean isTaxReviewRequired(String payeeTaxControlCode) {
        ParameterService paramService = SpringContext.getBean(ParameterService.class);
        List<String> taxControlCodes = new ArrayList<String>( paramService.getParameterValuesAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.TAX_CONTROL_CODES_REQUIRING_TAX_REVIEW_PARM_NM) );
        
        return taxControlCodes != null && taxControlCodes.contains(payeeTaxControlCode);
    }
    /* End TEM Refund Merge */
    
    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#isPayeeIndividualVendor(org.kuali.kfs.fp.businessobject.DisbursementPayee)
     */
    public boolean isPayeeIndividualVendor(DisbursementPayee payee) {
        return this.isVendor(payee) ? this.isPayeeIndividualVendor(payee.getPayeeIdNumber()) : false;
    }
    
    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#getVendorOwnershipTypeCode(org.kuali.kfs.fp.businessobject.DisbursementPayee)
     */
    public String getVendorOwnershipTypeCode(DisbursementPayee payee) {
        if(ObjectUtils.isNull(payee) || !this.isVendor(payee)) {
            return null;
        }
        
        VendorDetail vendor = vendorService.getByVendorNumber(payee.getPayeeIdNumber());
        return vendor == null ? null : vendor.getVendorHeader().getVendorOwnershipCode();
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#checkPayeeAddressForChanges(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    public void checkPayeeAddressForChanges(DisbursementVoucherDocument dvDoc) {
        Map<String, String> pks = new HashMap<String, String>();
        pks.put("documentNumber", dvDoc.getDocumentNumber());

        DisbursementVoucherDocument savedDv = (DisbursementVoucherDocument) businessObjectService.findByPrimaryKey(DisbursementVoucherDocument.class, pks);
        DisbursementVoucherPayeeDetail newPayeeDetail = dvDoc.getDvPayeeDetail();
        DisbursementVoucherPayeeDetail oldPayeeDetail = savedDv.getDvPayeeDetail();

        if (ObjectUtils.isNotNull(oldPayeeDetail) && ObjectUtils.isNotNull(newPayeeDetail)) {
            if (!oldPayeeDetail.hasSameAddress(newPayeeDetail)) {// Addresses don't match, so let's start the recording of
                // changes

                // Put a note on the document to record the change to the address
                try {
                    String noteText = buildPayeeChangedNoteText(newPayeeDetail, oldPayeeDetail);

                    int noteMaxSize = dataDictionaryService.getAttributeMaxLength("Note", "noteText");

                    // Break up the note into multiple pieces if the note is too large to fit in the database field.
                    while (noteText.length() > noteMaxSize) {
                        int fromIndex = 0;
                        fromIndex = noteText.lastIndexOf(';', noteMaxSize);

                        String noteText1 = noteText.substring(0, fromIndex);
                        Note note1 = documentService.createNoteFromDocument(dvDoc, noteText1);
                        dvDoc.addNote(note1);
                        noteText = noteText.substring(fromIndex);
                    }

                    Note note = documentService.createNoteFromDocument(dvDoc, noteText);
                    dvDoc.addNote(note);
                }
                catch (Exception e) {
                    LOG.error("Exception while attempting to create or add note: " + e);
                }

                // Send out FYIs to all previous approvers so they're aware of the changes to the address
                try {
                    Set<Person> priorApprovers = dvDoc.getAllPriorApprovers();

                    String initiatorUserId = dvDoc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
                    setupFYIs(dvDoc, priorApprovers, initiatorUserId);
                }
                catch (WorkflowException we) {
                    LOG.error("Exception while attempting to retrieve all prior approvers from workflow: " + we);
                }
                catch (Exception unfe) {
                    LOG.error("Exception while attempting to retrieve all prior approvers for a disbursement voucher: " + unfe);
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#getFieldConversionBetweenPayeeAndVendor()
     */
    @Override
    public Map<String, String> getFieldConversionBetweenPayeeAndVendor() {
        Map<String, String> fieldConversionMap = new HashMap<String, String>();

        fieldConversionMap.put(KFSPropertyConstants.TAX_NUMBER, VendorPropertyConstants.VENDOR_TAX_NUMBER);

        fieldConversionMap.put(KFSPropertyConstants.VENDOR_NAME, VendorPropertyConstants.VENDOR_NAME);
        fieldConversionMap.put(KFSPropertyConstants.VENDOR_NUMBER, VendorPropertyConstants.VENDOR_NUMBER);

        fieldConversionMap.put(KFSPropertyConstants.PERSON_FIRST_NAME, VendorPropertyConstants.VENDOR_FIRST_NAME);
        fieldConversionMap.put(KFSPropertyConstants.PERSON_LAST_NAME, VendorPropertyConstants.VENDOR_LAST_NAME);

        fieldConversionMap.put(KFSPropertyConstants.ACTIVE, KFSPropertyConstants.ACTIVE_INDICATOR);

        return fieldConversionMap;
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#getFieldConversionBetweenPayeeAndPerson()
     */
    @Override
    public Map<String, String> getFieldConversionBetweenPayeeAndPerson() {
        Map<String, String> fieldConversionMap = new HashMap<String, String>();

        //fieldConversionMap.put(KFSPropertyConstants.TAX_NUMBER, KIMPropertyConstants.Person.EXTERNAL_ID);

        fieldConversionMap.put(KFSPropertyConstants.PERSON_FIRST_NAME, KIMPropertyConstants.Person.FIRST_NAME);
        fieldConversionMap.put(KFSPropertyConstants.PERSON_LAST_NAME, KIMPropertyConstants.Person.LAST_NAME);

        fieldConversionMap.put(KFSPropertyConstants.EMPLOYEE_ID, KIMPropertyConstants.Person.EMPLOYEE_ID);
        fieldConversionMap.put(KFSPropertyConstants.ACTIVE, KIMPropertyConstants.Person.ACTIVE);

        return fieldConversionMap;
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#getPayeeFromVendor(org.kuali.kfs.vnd.businessobject.VendorDetail)
     */
    @Override
    public DisbursementPayee getPayeeFromVendor(VendorDetail vendorDetail) {
        DisbursementPayee disbursementPayee = new DisbursementPayee();

        disbursementPayee.setActive(vendorDetail.isActiveIndicator());

        disbursementPayee.setPayeeIdNumber(vendorDetail.getVendorNumber());
        disbursementPayee.setPayeeName(vendorDetail.getAltVendorName());
        disbursementPayee.setTaxNumber(vendorDetail.getVendorHeader().getVendorTaxNumber());

        String vendorTypeCode = vendorDetail.getVendorHeader().getVendorTypeCode();
        String payeeTypeCode = getVendorPayeeTypeCodeMapping().get(vendorTypeCode);
        disbursementPayee.setPayeeTypeCode(payeeTypeCode);

        String vendorAddress = MessageFormat.format(addressPattern, vendorDetail.getDefaultAddressLine1(), vendorDetail.getDefaultAddressCity(), vendorDetail.getDefaultAddressStateCode(), vendorDetail.getDefaultAddressCountryCode());
        disbursementPayee.setAddress(vendorAddress);

        return disbursementPayee;
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#getPayeeFromPerson(org.kuali.rice.kim.bo.Person)
     */
    @Override
    public DisbursementPayee getPayeeFromPerson(Person person) {
        DisbursementPayee disbursementPayee = new DisbursementPayee();

        disbursementPayee.setActive(person.isActive());

        disbursementPayee.setPayeeIdNumber(person.getEmployeeId());
        disbursementPayee.setPrincipalId(person.getPrincipalId());

        disbursementPayee.setPayeeName(person.getName());
        disbursementPayee.setTaxNumber(KFSConstants.BLANK_SPACE);

        disbursementPayee.setPayeeTypeCode(KFSConstants.PaymentPayeeTypes.EMPLOYEE);

        String personAddress = MessageFormat.format(addressPattern, person.getAddressLine1(), person.getAddressCity(), person.getAddressStateProvinceCode(), person.getAddressCountryCode());
        disbursementPayee.setAddress(personAddress);

        return disbursementPayee;
    }

    /**
     * Creates text for a note which records changes to the payee
     * @param newPayeeDetail the changed payee detail
     * @param oldPayeeDetail the original payee detail
     * @return the string for a note
     */
    protected String buildPayeeChangedNoteText(DisbursementVoucherPayeeDetail newPayeeDetail, DisbursementVoucherPayeeDetail oldPayeeDetail) {
        StringBuilder noteText = new StringBuilder();
        String valueLabel = "";
        try {
            noteText.append("The following changes were made to the payee address: ");

            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_LINE1_ADDR);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeLine1Addr(), newPayeeDetail.getDisbVchrPayeeLine1Addr()));

            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_LINE2_ADDR);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeLine2Addr(), newPayeeDetail.getDisbVchrPayeeLine2Addr()));

            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_CITY_NAME);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeCityName(), newPayeeDetail.getDisbVchrPayeeCityName()));

            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_STATE_CODE);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeStateCode(), newPayeeDetail.getDisbVchrPayeeStateCode()));

            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_ZIP_CODE);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeZipCode(), newPayeeDetail.getDisbVchrPayeeZipCode()));

            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_COUNTRY_CODE);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeCountryCode(), newPayeeDetail.getDisbVchrPayeeCountryCode()));
        }
        catch (Exception ex) {
            LOG.error("Error while attempting to build out note text for payee address change note: " + ex);
        }

        return noteText.toString();
    }

    /**
     * This method...
     * 
     * @param valueName
     * @param oldValue
     * @param newValue
     * @return
     */
    protected String buildAddressValueDifferenceText(String valueName, String oldValue, String newValue) {
        // Nothing to log if values are still the same
        if (StringUtils.equals(oldValue, newValue)) {
            return "";
        }

        StringBuilder text = new StringBuilder();

        text.append(valueName).append(" was changed from ");
        text.append(oldValue == null ? "(no value entered)" : oldValue).append(" to ");
        text.append(newValue).append("; ");

        return text.toString();
    }

    /**
     * Creates FYI requests to previous approvers
     * 
     * @param dvDoc the document where the payee address has changed
     * @param priorApprovers the previous approvers
     * @param initiatorUserId the id of the initiator
     */
    protected void setupFYIs(DisbursementVoucherDocument dvDoc, Set<Person> priorApprovers, String initiatorUserId) {
        List<AdHocRoutePerson> adHocRoutePersons = dvDoc.getAdHocRoutePersons();
        final FinancialSystemTransactionalDocumentAuthorizerBase documentAuthorizer = getDocumentAuthorizer(dvDoc);
        
        // Add FYI for each approver who has already approved the document
        for (Person approver : priorApprovers) {
            if (documentAuthorizer.canReceiveAdHoc(dvDoc, approver, KewApiConstants.ACTION_REQUEST_FYI_REQ)) {
                String approverPersonUserId = approver.getPrincipalName();
                adHocRoutePersons.add(buildFyiRecipient(approverPersonUserId));
            }
        }

        // Add FYI for initiator
        adHocRoutePersons.add(buildFyiRecipient(initiatorUserId));
    }
    
    /**
     * Constructs a document authorizer for this class
     * @return the document authorizer for this class
     */
    protected FinancialSystemTransactionalDocumentAuthorizerBase getDocumentAuthorizer(DisbursementVoucherDocument dvDoc) {
        final String docTypeName = dataDictionaryService.getDocumentTypeNameByClass(dvDoc.getClass());
        Class<? extends DocumentAuthorizer> documentAuthorizerClass = dataDictionaryService.getDataDictionary().getDocumentEntry(docTypeName).getDocumentAuthorizerClass();
        
        FinancialSystemTransactionalDocumentAuthorizerBase documentAuthorizer = null;
        try {
            documentAuthorizer = (FinancialSystemTransactionalDocumentAuthorizerBase)documentAuthorizerClass.newInstance();
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Could not construct document authorizer: "+documentAuthorizerClass.getName(), ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("Could not construct document authorizer: "+documentAuthorizerClass.getName(), iae);
        }
        
        return documentAuthorizer;
    }

    /**
     * This method...
     * 
     * @param userId
     * @return
     */
    protected AdHocRoutePerson buildFyiRecipient(String userId) {
        AdHocRoutePerson adHocRoutePerson = new AdHocRoutePerson();
        adHocRoutePerson.setActionRequested(KewApiConstants.ACTION_REQUEST_FYI_REQ);
        adHocRoutePerson.setId(userId);
        return adHocRoutePerson;
    }

    // get the description of the vendor type with the given vendor type code
    protected String getVendorTypeDescription(String vendorTypeCode) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(KFSPropertyConstants.VENDOR_TYPE_CODE, vendorTypeCode);

        VendorType vendorType = (VendorType) businessObjectService.findByPrimaryKey(VendorType.class, primaryKeys);
        return ObjectUtils.isNotNull(vendorType) ? vendorType.getVendorTypeDescription() : StringUtils.EMPTY;
    }

    // determine whether the given payee id number is associated with an individual vendor
    protected boolean isPayeeIndividualVendor(String payeeIdNumber) {
        List<String> individualOwnerShipTypeCodes = new ArrayList<String>( parameterService.getParameterValuesAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.INDIVIDUAL_OWNERSHIP_TYPES_PARM_NM) );

        VendorDetail vendor = vendorService.getByVendorNumber(payeeIdNumber);
        if (vendor != null && individualOwnerShipTypeCodes != null) {
            return individualOwnerShipTypeCodes.contains(vendor.getVendorHeader().getVendorOwnershipCode());
        }

        return false;
    }

 // do mapping between vendor type code and payee type code
    private static Map<String, String> getVendorPayeeTypeCodeMapping() {
        Map<String, String> payeeVendorTypeCodeMapping = new HashMap<String, String>();

        payeeVendorTypeCodeMapping.put(VendorConstants.VendorTypes.PURCHASE_ORDER, KFSConstants.PaymentPayeeTypes.VENDOR);
        payeeVendorTypeCodeMapping.put(VendorConstants.VendorTypes.DISBURSEMENT_VOUCHER, KFSConstants.PaymentPayeeTypes.VENDOR);
        payeeVendorTypeCodeMapping.put(VendorConstants.VendorTypes.REVOLVING_FUND, KFSConstants.PaymentPayeeTypes.REVOLVING_FUND_VENDOR);
        payeeVendorTypeCodeMapping.put(VendorConstants.VendorTypes.SUBJECT_PAYMENT, KFSConstants.PaymentPayeeTypes.SUBJECT_PAYMENT_VENDOR);

        return payeeVendorTypeCodeMapping;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * 
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the vendorService attribute value.
     * @param vendorService The vendorService to set.
     */
    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }
}
