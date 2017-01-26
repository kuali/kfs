package edu.arizona.kfs.pdp.batch.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.pdp.batch.service.ShippingInvoiceProcessService;
import edu.arizona.kfs.pdp.businessobject.ShippingAccount;
import edu.arizona.kfs.pdp.businessobject.ShippingInvoice;
import edu.arizona.kfs.pdp.businessobject.ShippingInvoiceTracking;
import edu.arizona.kfs.pdp.dataaccess.ShippingInvoiceDao;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSParameterKeyConstants;

/**
 * This is the default implementation of the ShippingInvoiceProcessService interface.
 * 
 * @see edu.arizona.kfs.pdp.batch.service.ShippingInvoiceProcessService
 */
@Transactional
public class ShippingInvoiceProcessServiceImpl implements ShippingInvoiceProcessService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ShippingInvoiceProcessServiceImpl.class);
    
    protected BusinessObjectService businessObjectService;
    protected ParameterService parameterService;
    protected DateTimeService dateTimeService;
    protected AccountService accountService;
    protected ShippingInvoiceDao shippingInvoiceDao;
    
    protected class KFSAccountingInfo {
        String accountNbr;
        String financialObjectCode;
        String chartOfAccountsCode;
        String subAccountNumber;
        String financialSubObjectCode;
        String projCode;
        String organizationReferenceId;
        
        public KFSAccountingInfo() {
            accountNbr = "";
            financialObjectCode = "";
            chartOfAccountsCode = "";
            subAccountNumber = "";
            financialSubObjectCode = "";
            projCode = "";
            organizationReferenceId = "";
        }
        
        public String getAccountNbr() {
            return accountNbr;
        }
       
        public void setAccountNbr(String accountNbr) {
            this.accountNbr = accountNbr;
        }
       
        public String getFinancialObjectCode() {
            return financialObjectCode;
        }
       
        public void setFinancialObjectCode(String financialObjectCode) {
            this.financialObjectCode = financialObjectCode;
        }
      
        public String getChartOfAccountsCode() {
            return chartOfAccountsCode;
        }
       
        public void setChartOfAccountsCode(String chartOfAccountsCode) {
            this.chartOfAccountsCode = chartOfAccountsCode;
        }
              
        public String getSubAccountNumber() {
            return subAccountNumber;
        }
      
        public void setSubAccountNumber(String subAccountNumber) {
            this.subAccountNumber = subAccountNumber;
        }
       
        public String getFinancialSubObjectCode() {
            return financialSubObjectCode;
        }
       
        public void setFinancialSubObjectCode(String financialSubObjectCode) {
            this.financialSubObjectCode = financialSubObjectCode;
        }
      
        public String getProjCode() {
            return projCode;
        }
       
        public void setProjCode(String projCode) {
            this.projCode = projCode;
        }
       
        public String getOrganizationReferenceId() {
            return organizationReferenceId;
        }
       
        public void setOrganizationReferenceId(String organizationReferenceId) {
            this.organizationReferenceId = organizationReferenceId;
        }
    }
    
    /**
     * This method retrieves a collection of temporary shipping invoice records by shipping company and calls
     * helper methods to creates transactions for each tracking element in each invoice.
     * 
     * It adds these payments to a batch file that is uploaded for processing
     * 
     * @return True if the shipping invoice records were processed successfully.  If any problem occurs while
     * creating the documents, a runtime exception will be thrown.
     * 
     * @see edu.arizona.kfs.pdp.batch.service#ShippingInvoiceProcessService#updateShippingInvoiceRecords()
     */
    public boolean processShippingInvoiceRecords( Date processRunDate ) {
                
        List<String> companies = shippingInvoiceDao.getShippingInvoiceCompanies();
        List<ShippingInvoice> shippingInvoices;
        
        // process invoices based on shipping company
        for (String company : companies) {
            KualiInteger count = KualiInteger.ZERO;
            KualiDecimal totalAmount = KualiDecimal.ZERO;
            
            // create new batch 
            Batch batch = createBatch(company, processRunDate);
            
            // create new payment group with this info 
            PaymentGroup paymentGroup = buildPaymentGroup(company, batch);
                                    
            // load shipping records from temp tables
            shippingInvoices = new ArrayList<ShippingInvoice>();    
            shippingInvoices = retrieveShippingInvoices(company);
            
            for (ShippingInvoice shippingInvoice: shippingInvoices) {            
                processInvoice(shippingInvoice, batch, paymentGroup);
                count = batch.getPaymentCount().add(new KualiInteger(1));
                batch.setPaymentCount(count);
                totalAmount = batch.getPaymentTotalAmount().add(shippingInvoice.getTotalInvoiceCharge());
                batch.setPaymentTotalAmount(totalAmount);
                businessObjectService.save(batch);
            }
            
            // check that the total payment amount does not exceed the configured threshold amount of the customer
            checkCustomerThreshold(batch);            
               
        }
        
        return true;
    }
    
    /**
     * This method creates a Batch instance and populates it with the information provided.
     * 
     * @param shippingCompany The shipping company used to retrieve a customer profile to be set on the batch.
     * @param processRunDate The date the batch was submitted and the date the customer profile was generated.
     * @return A fully populated batch instance.
     */
    protected Batch createBatch(String shippingCompany, Date processRunDate) {
    	final Map<String, Object> map = new HashMap<String, Object>();
        map.put(PdpPropertyConstants.SUB_UNIT_CODE, shippingCompany);
        map.put("active", Boolean.TRUE);

        Collection<CustomerProfile> customerProfiles = businessObjectService.findMatching(CustomerProfile.class, Collections.unmodifiableMap(map));
        if (customerProfiles.isEmpty()) {
        	throw new IllegalArgumentException("Unable to find customer profile for " + shippingCompany);
        }

        // Create the batch for this company
        Batch batch = new Batch();  
        batch.setCustomerProfile(customerProfiles.iterator().next());        
        batch.setCustomerFileCreateTimestamp(new Timestamp(processRunDate.getTime()));
        batch.setFileProcessTimestamp(new Timestamp(processRunDate.getTime()));
        batch.setPaymentFileName(KFSConstants.SHIPPING_FILE_NAME);
        batch.setSubmiterUserId(KFSConstants.SYSTEM_USER);

        // Set these for now, we will update them later
        batch.setPaymentCount(KualiInteger.ZERO);
        batch.setPaymentTotalAmount(KualiDecimal.ZERO);

        businessObjectService.save(batch);

        return batch;
    }
            
    /**
     * This method constructs a PaymentGroup for a shipping company's invoices and associates it with the batch
     * provided.
     * It gets the vendor information using the DUNS number from the VENDOR_DUNS_NUMBER_BY_SHIPPING_COMPANIES parm.
     * 
     * @param shippingCompany
     * @param batch The batch that the payment group will be associated with.
     * @return
     */
    protected PaymentGroup buildPaymentGroup(String shippingCompany, Batch batch) {
        LOG.debug("buildPaymentGroup() for " + shippingCompany);
        String vendorDunsNumber = parameterService.getParameterValueAsString(ShippingAccount.class, KFSParameterKeyConstants.ShippingConstants.VENDOR_DUNS_NUMBER_BY_SHIPPING_COMPANIES, shippingCompany);
        
        if (StringUtils.isBlank( vendorDunsNumber ) ) {
            throw new IllegalArgumentException( "No vendor DUNS number could be found for shipping company: " + shippingCompany + " -- Unable to create payment group.");
        }        
        vendorDunsNumber = vendorDunsNumber.split("=")[1]; // Discard the 'FEDX=' keep just the vendorDunsNumber                
        VendorDetail vendorDetail = SpringContext.getBean(VendorService.class).getVendorByDunsNumber(vendorDunsNumber);
        if ( vendorDetail == null ) {
            throw new IllegalArgumentException( "No vendor with the given DUNS number (" + vendorDunsNumber + ") could be found for shipping company: " + shippingCompany + " -- Unable to create payment group.");
        }
        
        VendorHeader vendorHeader = (VendorHeader) businessObjectService.findBySinglePrimaryKey(VendorHeader.class, vendorDetail.getVendorHeaderGeneratedIdentifier());              
            
        PaymentGroup paymentGroup = new PaymentGroup();
        paymentGroup.setPayeeName(vendorDetail.getVendorName());
        paymentGroup.setPayeeId(vendorDetail.getVendorNumber());
        paymentGroup.setPayeeIdTypeCd(PdpConstants.PayeeIdTypeCodes.VENDOR_ID);
        
        if (vendorHeader.getVendorOwnershipCategoryCode() != null) {
            paymentGroup.setPayeeOwnerCd(vendorHeader.getVendorOwnershipCategoryCode());
        }
        
        List<VendorAddress> vendorAddresses = vendorDetail.getVendorAddresses();
        for (VendorAddress address: vendorAddresses) {
            String addressType = address.getVendorAddressType().getVendorAddressTypeCode();
            if (addressType != null) {
                if (VendorConstants.AddressTypes.REMIT.equalsIgnoreCase(addressType)) {
                    paymentGroup.setLine1Address(address.getVendorLine1Address());
                    paymentGroup.setLine2Address(address.getVendorLine2Address());
                    paymentGroup.setCity(address.getVendorCityName());
                    paymentGroup.setState(address.getVendorStateCode());
                    paymentGroup.setZipCd(address.getVendorZipCode());                   
                    paymentGroup.setCountry(address.getVendorCountryCode());
                }
            }
        }
        paymentGroup.setCombineGroups(Boolean.TRUE);
        paymentGroup.setCampusAddress(Boolean.FALSE);
        paymentGroup.setPaymentDate(dateTimeService.getCurrentSqlDate());
        paymentGroup.setProcessImmediate(Boolean.FALSE);       
        paymentGroup.setPaymentStatusCode(PdpConstants.PaymentStatusCodes.OPEN);
        PaymentStatus paymentStatus = (PaymentStatus) businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, PdpConstants.PaymentStatusCodes.OPEN);
        paymentGroup.setPaymentStatus(paymentStatus);                
        paymentGroup.setPymtAttachment(Boolean.FALSE);
        paymentGroup.setPymtSpecialHandling(Boolean.FALSE);
        paymentGroup.setEmployeeIndicator(Boolean.FALSE);
        paymentGroup.setNraPayment(Boolean.FALSE);
        paymentGroup.setTaxablePayment(Boolean.FALSE);
        
        paymentGroup.setBatch(batch);
                      
        return paymentGroup;
    }

    /**
     * This method retrieves a list of shipping invoice records for the specified shipping company from the database.
     * 
     * @param shippingCo
     * @return List containing shipping invoice records for shippingCo.
     */
    protected List<ShippingInvoice> retrieveShippingInvoices(String shippingCo) {       
    	if (StringUtils.isBlank(shippingCo)) {
            throw new IllegalArgumentException("shippingCo is null or blank");
		}
    	
    	// retrieve shipping invoice records from shipping invoice table, order by invoice number
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("shippingCompany", shippingCo);        
        List<ShippingInvoice> loadedShippingInvoices = (List<ShippingInvoice>) businessObjectService.findMatchingOrderBy(ShippingInvoice.class, map, "invoiceNumber", true);
        return loadedShippingInvoices;
    }    
    
    /**
     * This method processes a shipping invoice. It iterates through each tracking element in order to 
     * get the PDP payment information by calling helper methods.
     * 
     * By calling helper methods, it creates records in PDP payment tables 
     * (PaymentDetail, PaymentAccountDetail and PaymentNoteText).
     * 
     * @param shippingInvoice
     * @param batch associated with the paymentGroup
     * @param paymentGroup associated with the invoice  
     * @return batch The batch associated with the payments in the shippingInvoice.
     */
    protected boolean processInvoice(ShippingInvoice shippingInvoice, Batch batch, PaymentGroup paymentGroup) {
        List<ShippingInvoiceTracking> trackingElements = new ArrayList<ShippingInvoiceTracking>();    
        KFSAccountingInfo accountingInfo;
                
        trackingElements = shippingInvoice.getInvoiceTrackingElements();
        
        for (ShippingInvoiceTracking trackingElement : trackingElements) {
            accountingInfo = getKFSAccountingInfo(shippingInvoice, trackingElement);
            PaymentDetail paymentDetail = buildPaymentDetail(batch, shippingInvoice, trackingElement, accountingInfo);
            paymentGroup.addPaymentDetails(paymentDetail);
        }
        
        businessObjectService.save(paymentGroup);
        
        return true;
    }    
    
    /**
     * This method creates records in the PaymentDetail tables (includes PaymentAccountDetail and PaymentNoteText)
     * for a shipping invoice's tracking element.
     * If any accounting info is missing, it gets the defualt values from the customer profile.
     * 
     * @param batch
     * @param shippingInvoice
     * @param trackingElement
     * @param accountingInfo for the invoice and tracking element
     * @return A populated PaymentDetail object
     */
    protected PaymentDetail buildPaymentDetail(Batch batch, ShippingInvoice shippingInvoice, ShippingInvoiceTracking trackingElement,
            KFSAccountingInfo accountingInfo) {
        CustomerProfile customer = batch.getCustomerProfile();
        PaymentDetail paymentDetail = new PaymentDetail();
        
        // set values in payment detail
        paymentDetail.setInvoiceNbr(shippingInvoice.getInvoiceNumber());
        paymentDetail.setInvoiceDate(shippingInvoice.getInvoiceDate());
        paymentDetail.setOrigInvoiceAmount(trackingElement.getNetCharge());  // changed for KITT-1350
        paymentDetail.setNetPaymentAmount(trackingElement.getNetCharge());
        paymentDetail.setInvTotDiscountAmount(KualiDecimal.ZERO);
        paymentDetail.setInvTotOtherCreditAmount(KualiDecimal.ZERO);
        paymentDetail.setInvTotOtherDebitAmount(KualiDecimal.ZERO);
        paymentDetail.setInvTotShipAmount(KualiDecimal.ZERO);
        paymentDetail.setPrimaryCancelledPayment(Boolean.FALSE);
        paymentDetail.setFinancialSystemOriginCode(KFSConstants.SHIPPING_INVOICE_ORIGIN_CODE);
        // setting the customer document number to the tracking number, since we don't have a real KFS document
        paymentDetail.setCustPaymentDocNbr(trackingElement.getTrackingNumber());
        paymentDetail.setFinancialDocumentTypeCode(DisbursementVoucherConstants.DOCUMENT_TYPE_CHECKACH);

        // set values in payment account detail
        PaymentAccountDetail pad = new PaymentAccountDetail();
        if (StringUtils.isNotEmpty(accountingInfo.getChartOfAccountsCode())) {
            pad.setFinChartCode(accountingInfo.getChartOfAccountsCode());
        }
        else {            
            pad.setFinChartCode(customer.getDefaultChartCode());
        }
        if (StringUtils.isNotEmpty(accountingInfo.getAccountNbr())) {
            pad.setAccountNbr(accountingInfo.getAccountNbr());
        }
        else {
            pad.setAccountNbr(customer.getDefaultAccountNumber());
        }
        if (StringUtils.isNotEmpty(accountingInfo.getSubAccountNumber())) {
            pad.setSubAccountNbr(accountingInfo.getSubAccountNumber());
        }
        else {
            pad.setSubAccountNbr(KFSConstants.getDashSubAccountNumber());
        }
        pad.setFinObjectCode(accountingInfo.getFinancialObjectCode());        
        if (StringUtils.isNotEmpty(accountingInfo.getFinancialSubObjectCode())) {
            pad.setFinSubObjectCode(accountingInfo.getFinancialSubObjectCode());
        }
        else {
            pad.setFinSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
        if (StringUtils.isNotEmpty(accountingInfo.getOrganizationReferenceId())) {
            pad.setOrgReferenceId(accountingInfo.getOrganizationReferenceId());
        }
        else {
            pad.setOrgReferenceId(" ");
        }
        if (StringUtils.isNotEmpty(accountingInfo.getProjCode())) {
            pad.setProjectCode(accountingInfo.getProjCode());
        }
        else {
            pad.setProjectCode(KFSConstants.getDashProjectCode());
        }
        pad.setAccountNetAmount(trackingElement.getNetCharge());
        paymentDetail.addAccountDetail(pad);
        
        // set values in payment notes
        int line = 0;
        PaymentNoteText pnt = new PaymentNoteText();
        pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
        pnt.setCustomerNoteText("Invoice creation date: " + shippingInvoice.getInvoiceDate());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating invoice creation date note: " + pnt.getCustomerNoteText());
        }
        paymentDetail.addNote(pnt);
        
        pnt = new PaymentNoteText();
        pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
        pnt.setCustomerNoteText("Tracking number: " + trackingElement.getTrackingNumber());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating tracking number note: " + pnt.getCustomerNoteText());
        }
        paymentDetail.addNote(pnt);

        pnt = new PaymentNoteText();
        pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
        pnt.setCustomerNoteText("Transaction reference number: " + shippingInvoice.getTransactionRefNumber());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating transaction reference number note: " + pnt.getCustomerNoteText());
        }
        paymentDetail.addNote(pnt);
        
        return paymentDetail;
    }    
    
    /**
     * This method gets the KFS accounting string associated with the account for a tracking element.
     * 1. Examine the reference number in each tracking element in each invoice for a valid account.
     * 2. Examine bill-to-account number in invoice to use if reference number is invalid.
     * 3. Use KFS accounting info from Shipping Account table unless no matching record.
     * 4. Get KFS accounting info from default parameters if no matching Shipping Account record.
     *  
     * @param shippingInvoice
     * @param trackingElement
     * 
     * @return KFSAccountingInfo object
     */
    private KFSAccountingInfo getKFSAccountingInfo(ShippingInvoice shippingInvoice, ShippingInvoiceTracking trackingElement) {
        // This method examines the reference number and the bill-to-account number to find a valid account number.
        int NUM_REFNBR_PIECES = 2;
        StringTokenizer tokenizer;
        String accountString;
        String accountChart;
        String accountNbr;
        Account account = null;
        String financialObjectCode = parameterService.getParameterValueAsString(ShippingAccount.class, KFSParameterKeyConstants.ShippingConstants.DEFAULT_TRANSACTION_OBJECT_CODE);
        
        KFSAccountingInfo accountingInfo = new KFSAccountingInfo();
                
        // examine reference number
        accountString = StringUtils.defaultString(trackingElement.getReferenceNumber()); // switch to empty-string if null 
        tokenizer = new StringTokenizer(accountString, "-");
        if (tokenizer.countTokens() == NUM_REFNBR_PIECES) {    // if ref number is in format "chart-acctNum"
            accountChart = tokenizer.nextToken();
            accountNbr = tokenizer.nextToken();
        }
        else {
            accountNbr = accountString;
            accountChart = "";
        }
        
        account = accountService.getByPrimaryId(accountChart, accountNbr);
        if ( account != null && (account.isClosed() || account.isExpired()) ) {
        	account = accountService.getUnexpiredContinuationAccountOrNull(account);
        }
        
        if (ObjectUtils.isNotNull(account)) {    // if reference number is a valid account
            accountingInfo.setAccountNbr(account.getAccountNumber());
            accountingInfo.setChartOfAccountsCode(account.getChartOfAccountsCode());
            accountingInfo.setFinancialObjectCode(financialObjectCode);  
            }
            
        else {  // examine bill-to-account number
            ShippingAccount shipAccount = getShippingAccount(shippingInvoice.getBillToAccountNumber());
        
            // if valid shipping account
            if (ObjectUtils.isNotNull(shipAccount) && shipAccount.isActive()) {
                account = shipAccount.getAccount();
                if ( ObjectUtils.isNotNull(account) && (account.isClosed() || account.isExpired()) ) {
                    account = accountService.getUnexpiredContinuationAccountOrNull(account);
                }
                
                accountingInfo.setAccountNbr(account.getAccountNumber());
                accountingInfo.setChartOfAccountsCode(account.getChartOfAccountsCode());

                // get rest of KFS accounting string from shipping account
                accountingInfo.setFinancialObjectCode(shipAccount.getFinancialObjectCode());
                accountingInfo.setSubAccountNumber(shipAccount.getSubAccountNumber());
                accountingInfo.setFinancialSubObjectCode(shipAccount.getFinancialSubObjectCode());
                accountingInfo.setProjCode(shipAccount.getProjCode());
                accountingInfo.setOrganizationReferenceId(shipAccount.getOrganizationReferenceId());
            }
            else {    // get KFS accounting string from defaults in parameters
                accountingInfo.setFinancialObjectCode(financialObjectCode);                
            }
        }
        
        return accountingInfo;       
    }
            
    private ShippingAccount getShippingAccount(String shippingAccountNbr) {
        Map<String, String> pkMap = new HashMap<String, String>();
        pkMap.put("shippingAccountNumber", shippingAccountNbr);
        ShippingAccount shipAccount = (ShippingAccount) businessObjectService.findByPrimaryKey(ShippingAccount.class, pkMap);
        
        return shipAccount;
    }
        
    /**
     * This method checks that the total payment amount does not exceed the configured threshold amount of the customer.
     * 
     *  So, nothing really happens here, this validation was never successfully adapted to a fully batch process where there is no feedback to the user through the UI.
     *  There should be a warning message on an email or report indicating the threshold was exceeded. I spoke to Mary Baum (10/20/16) and we agreed to leave as is currently coded.
     *  I am leaving this non-functioning code here in case there is a future request to make this validation actually work.        
     * 
     * @param batch that is associated with a set of invoices
     */    
    private void checkCustomerThreshold(Batch batch) {
    	     	
        KualiDecimal totalPaymentAmt = batch.getPaymentTotalAmount();
        CustomerProfile customer = batch.getCustomerProfile();
        KualiDecimal threshold = customer.getPaymentThresholdAmount();
        
        if (totalPaymentAmt.compareTo(threshold) > 0) {
//        addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_DETAIL_THRESHOLD, testAmount.toString(), customer.getPaymentThresholdAmount().toString());       	
        }
    }
          
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
      
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
       
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
      
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
            
    public void setShippingInvoiceDao(ShippingInvoiceDao shippingInvoiceDao) {
        this.shippingInvoiceDao = shippingInvoiceDao;
    }
}
