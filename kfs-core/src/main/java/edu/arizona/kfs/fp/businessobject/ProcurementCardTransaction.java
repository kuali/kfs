package edu.arizona.kfs.fp.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ProcurementCardTransaction extends PersistableBusinessObjectBase {

	private Integer transactionSequenceRowNumber;
	private String transactionCreditCardNumber;
	private KualiDecimal financialDocumentTotalAmount;
	private String transactionDebitCreditCode;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String financialObjectCode;
	private String financialSubObjectCode;
	private String projectCode;
	private Date transactionCycleStartDate;
	private Date transactionCycleEndDate;
	private String cardHolderName;
	private Date transactionDate;
	private String transactionReferenceNumber;
	private String transactionMerchantCategoryCode;
	private Date transactionPostingDate;
	private String transactionOriginalCurrencyCode;
	private String transactionBillingCurrencyCode;
	private KualiDecimal transactionOriginalCurrencyAmount;
	private BigDecimal transactionCurrencyExchangeRate;
	private KualiDecimal transactionSettlementAmount;
	private KualiDecimal transactionSalesTaxAmount;
	private boolean transactionTaxExemptIndicator;
	private boolean transactionPurchaseIdentifierIndicator;
	private String transactionPurchaseIdentifierDescription;
	private String transactionUnitContactName;
	private String transactionTravelAuthorizationCode;
	private String transactionPointOfSaleCode;
	private String vendorName;
	private String vendorLine1Address;
	private String vendorLine2Address;
	private String vendorCityName;
	private String vendorStateCode;
	private String vendorZipCode;
	private String vendorOrderNumber;
	private String visaVendorIdentifier;
	private String cardHolderAlternateName;
	private String cardHolderLine1Address;
	private String cardHolderLine2Address;
	private String cardHolderCityName;
	private String cardHolderStateCode;
	private String cardHolderZipCode;
	private String cardHolderWorkPhoneNumber;
	private KualiDecimal cardLimit;
	private KualiDecimal cardCycleAmountLimit;
	private KualiDecimal cardCycleVolumeLimit;
	private String cardStatusCode;
	private String cardNoteText;
 // Procurement Card Level 3 Addendum Header Data
	private String invoiceNumber;
	private Date orderDate;
	private String purchaseTime;
	private String shipPostal;
	private String destinationPostal;
	private String destinationCountryCode;
	private KualiDecimal taxAmount;
	private KualiDecimal taxRate;
	private KualiDecimal discountAmount;
	private KualiDecimal freightAmount;
	private KualiDecimal dutyAmount;
 // Procurement Card Level 3 User Amounts data
	private Date userEffectiveDate;
	private KualiDecimal userAmount;
 // Procurement Card Level 3 Fuel data
	private String oilBrandName;
	private String odometerReading;
	private String fleetId;
	private String messageId;
	private String usage;
	private String fuelServiceType;
	private String fuelProductCd;
	private String productTypeCd;
	private BigDecimal fuelQuantity;
	private Integer fuelUnitOfMeasure;
	private KualiDecimal fuelUnitPrice;
	private KualiDecimal fuelSaleAmount;
	private KualiDecimal fuelDiscountAmount;
	private KualiDecimal taxAmount1;
	private KualiDecimal taxAmount2;
	private KualiDecimal totalAmount;
 // Procurement Card Level 3 Generic data
	private Date genericEffectiveDate;
	private String genericData;
 // Procurement Card Level 3 Lodging data
	private Date arriveDate;
	private Date departureDate;
	private String folioNum;
	private String propertyPhoneNum;
	private String customerServiceNum;
	private KualiDecimal prePaidAmt;
	private KualiDecimal roomRate;
	private KualiDecimal roomTax;
	private String programCode;
	private KualiDecimal callCharges;
	private KualiDecimal foodSvcCharges;
	private KualiDecimal miniBarCharges;
	private KualiDecimal giftShopCharges;
	private KualiDecimal laundryCharges;
	private KualiDecimal healthClubCharges;
	private KualiDecimal movieCharges;
	private KualiDecimal busCtrCharges;
	private KualiDecimal parkingCharges;
	private String otherCode;
	private KualiDecimal otherCharges;
	private KualiDecimal adjustmentAmount;
 // Procurement Card Level 3 Car Rental data
	private Date checkOutDate;
	private String rentalAgreementNum;
	private String renterName;
	private String returnCity;
	private String returnState;
	private String returnCountry;
	private Date returnDate;
	private String returnLocation;
	private String customerSvcNum;
	private String rentalClass;
	private KualiDecimal dailyRate;
	private KualiDecimal weeklyRate;
	private KualiDecimal ratePerMile;
	private Integer maxFreeMiles;
	private Integer totalMiles;
	private KualiDecimal oneWayCharges;
	private KualiDecimal insuranceCharges;
	private KualiDecimal regularCharges;
	private KualiDecimal towingCharges;
	private KualiDecimal extraCharges;
	private KualiDecimal lateReturnFee;
	private String adjustCode;
	private KualiDecimal adjustAmount;
	private String progCode;
	private KualiDecimal phoneCharges;
	private KualiDecimal othrCharges;
	private KualiDecimal totalTaxAmount;
 // Procurement Card Level 3 Transport data
	private String passengerName;
	private Date departDate;
	private String departureCity;
	private String agencyCode;
	private String agencyName;
	private long ticketNumber;
	private String customerCode;
	private Date issueDate;
	private String issuingCarrier;
	private KualiDecimal totalFare;
	private KualiDecimal totalFees;
	private KualiDecimal totalTaxes;
	
	private List<ProcurementCardTranAddItem> procurementCardTranAddItems;
	private List<ProcurementCardTranNonFuel> procurementCardTranNonFuels;
	private List<ProcurementCardTranShipSvc> procurementCardTranShipSvcs;
	private List<ProcurementCardTranTempSvc> procurementCardTranTempSvcs;
	private List<ProcurementCardTranTransportLeg> procurementCardTranTransportLegs;
	
	public ProcurementCardTransaction() {
		procurementCardTranAddItems = new ArrayList<ProcurementCardTranAddItem>();
		procurementCardTranNonFuels = new ArrayList<ProcurementCardTranNonFuel>();
		procurementCardTranShipSvcs = new ArrayList<ProcurementCardTranShipSvc>();
		procurementCardTranTempSvcs = new ArrayList<ProcurementCardTranTempSvc>();
		procurementCardTranTransportLegs = new ArrayList<ProcurementCardTranTransportLeg>();
	}

	public Integer getTransactionSequenceRowNumber() {
		return transactionSequenceRowNumber;
	}

	public void setTransactionSequenceRowNumber(Integer transactionSequenceRowNumber) {
		this.transactionSequenceRowNumber = transactionSequenceRowNumber;
	}

	public String getTransactionCreditCardNumber() {
		return transactionCreditCardNumber;
	}

	public void setTransactionCreditCardNumber(String transactionCreditCardNumber) {
		this.transactionCreditCardNumber = transactionCreditCardNumber;
	}

	public KualiDecimal getFinancialDocumentTotalAmount() {
		return financialDocumentTotalAmount;
	}

	public void setFinancialDocumentTotalAmount(KualiDecimal financialDocumentTotalAmount) {
		this.financialDocumentTotalAmount = financialDocumentTotalAmount;
	}

	public void setFinancialDocumentTotalAmount(String financialDocumentTotalAmount) {
		if(StringUtils.isNotBlank(financialDocumentTotalAmount)) {
			this.financialDocumentTotalAmount = new KualiDecimal(financialDocumentTotalAmount);
		} else {
			this.financialDocumentTotalAmount = KualiDecimal.ZERO;
		}
	}
	
	public String getTransactionDebitCreditCode() {
		return transactionDebitCreditCode;
	}

	public void setTransactionDebitCreditCode(String transactionDebitCreditCode) {
		this.transactionDebitCreditCode = transactionDebitCreditCode;
	}

	public String getChartOfAccountsCode() {
		return chartOfAccountsCode;
	}

	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getSubAccountNumber() {
		return subAccountNumber;
	}

	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
	}

	public String getFinancialObjectCode() {
		return financialObjectCode;
	}

	public void setFinancialObjectCode(String financialObjectCode) {
		this.financialObjectCode = financialObjectCode;
	}

	public String getFinancialSubObjectCode() {
		return financialSubObjectCode;
	}

	public void setFinancialSubObjectCode(String financialSubObjectCode) {
		this.financialSubObjectCode = financialSubObjectCode;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public Date getTransactionCycleStartDate() {
		return transactionCycleStartDate;
	}

	public void setTransactionCycleStartDate(Date transactionCycleStartDate) {
		this.transactionCycleStartDate = transactionCycleStartDate;
	}
	
	public void setTransactionCycleStartDate(String transactionCycleStartDate) {
		if(StringUtils.isNotBlank(transactionCycleStartDate)) {
			this.transactionCycleStartDate = (Date) (new SqlDateConverter()).convert(Date.class, transactionCycleStartDate);
		}
	}

	public Date getTransactionCycleEndDate() {
		return transactionCycleEndDate;
	}

	public void setTransactionCycleEndDate(Date transactionCycleEndDate) {
		this.transactionCycleEndDate = transactionCycleEndDate;
	}
	
	public void setTransactionCycleEndDate(String transactionCycleEndDate) {
		if(StringUtils.isNotBlank(transactionCycleEndDate)) {
			this.transactionCycleEndDate = (Date) (new SqlDateConverter()).convert(Date.class, transactionCycleEndDate);
		}
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionReferenceNumber() {
		return transactionReferenceNumber;
	}

	public void setTransactionReferenceNumber(String transactionReferenceNumber) {
		this.transactionReferenceNumber = transactionReferenceNumber;
	}

	public String getTransactionMerchantCategoryCode() {
		return transactionMerchantCategoryCode;
	}

	public void setTransactionMerchantCategoryCode(String transactionMerchantCategoryCode) {
		this.transactionMerchantCategoryCode = transactionMerchantCategoryCode;
	}

	public Date getTransactionPostingDate() {
		return transactionPostingDate;
	}

	public void setTransactionPostingDate(Date transactionPostingDate) {
		this.transactionPostingDate = transactionPostingDate;
	}

	public String getTransactionOriginalCurrencyCode() {
		return transactionOriginalCurrencyCode;
	}

	public void setTransactionOriginalCurrencyCode(String transactionOriginalCurrencyCode) {
		this.transactionOriginalCurrencyCode = transactionOriginalCurrencyCode;
	}

	public String getTransactionBillingCurrencyCode() {
		return transactionBillingCurrencyCode;
	}

	public void setTransactionBillingCurrencyCode(String transactionBillingCurrencyCode) {
		this.transactionBillingCurrencyCode = transactionBillingCurrencyCode;
	}

	public KualiDecimal getTransactionOriginalCurrencyAmount() {
		return transactionOriginalCurrencyAmount;
	}

	public void setTransactionOriginalCurrencyAmount(KualiDecimal transactionOriginalCurrencyAmount) {
		this.transactionOriginalCurrencyAmount = transactionOriginalCurrencyAmount;
	}
	
	public void setTransactionOriginalCurrencyAmount(String transactionOriginalCurrencyAmount) {
		if(StringUtils.isNotBlank(transactionOriginalCurrencyAmount)) {
			this.transactionOriginalCurrencyAmount = new KualiDecimal(transactionOriginalCurrencyAmount);
		} else {
			this.transactionOriginalCurrencyAmount = KualiDecimal.ZERO;
		}
	}

	public BigDecimal getTransactionCurrencyExchangeRate() {
		return transactionCurrencyExchangeRate;
	}

	public void setTransactionCurrencyExchangeRate(BigDecimal transactionCurrencyExchangeRate) {
		this.transactionCurrencyExchangeRate = transactionCurrencyExchangeRate;
	}

	public void setTransactionCurrencyExchangeRate(String transactionCurrencyExchangeRate) {
		if(StringUtils.isNotBlank(transactionCurrencyExchangeRate)) {
			this.transactionCurrencyExchangeRate = new BigDecimal(transactionCurrencyExchangeRate);
		} else {
			this.transactionCurrencyExchangeRate = new BigDecimal(0);
		}
	}
	
	public KualiDecimal getTransactionSettlementAmount() {
		return transactionSettlementAmount;
	}

	public void setTransactionSettlementAmount(KualiDecimal transactionSettlementAmount) {
		this.transactionSettlementAmount = transactionSettlementAmount;
	}

	public void setTransactionSettlementAmount(String transactionSettlementAmount) {
		if(StringUtils.isNotBlank(transactionSettlementAmount)) {
			this.transactionSettlementAmount = new KualiDecimal(transactionSettlementAmount);
		} else {
			this.transactionSettlementAmount = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getTransactionSalesTaxAmount() {
		return transactionSalesTaxAmount;
	}

	public void setTransactionSalesTaxAmount(KualiDecimal transactionSalesTaxAmount) {
		this.transactionSalesTaxAmount = transactionSalesTaxAmount;
	}

	public void setTransactionSalesTaxAmount(String transactionSalesTaxAmount) {
		if(StringUtils.isNotBlank(transactionSalesTaxAmount)) {
			this.transactionSalesTaxAmount = new KualiDecimal(transactionSalesTaxAmount);
		} else {
			this.transactionSalesTaxAmount = KualiDecimal.ZERO;
		}
	}
	
	public boolean getTransactionTaxExemptIndicator() {
        return transactionTaxExemptIndicator;
    }

	public void setTransactionTaxExemptIndicator(boolean transactionTaxExemptIndicator) {
		this.transactionTaxExemptIndicator = transactionTaxExemptIndicator;
	}

	public void setTransactionTaxExemptIndicator(String transactionTaxExemptIndicator) {
		if(KFSConstants.ACTIVE_INDICATOR.equals(transactionTaxExemptIndicator)) {
			this.transactionTaxExemptIndicator = true;
		} else {
			this.transactionTaxExemptIndicator = false;
		}
	}
		
    public boolean getTransactionPurchaseIdentifierIndicator() {
        return transactionPurchaseIdentifierIndicator;
    }

	public void setTransactionPurchaseIdentifierIndicator(boolean transactionPurchaseIdentifierIndicator) {
		this.transactionPurchaseIdentifierIndicator = transactionPurchaseIdentifierIndicator;
	}
	
	public void setTransactionPurchaseIdentifierIndicator(String transactionPurchaseIdentifierIndicator) {
		if(KFSConstants.ACTIVE_INDICATOR.equals(transactionPurchaseIdentifierIndicator)) {
			this.transactionPurchaseIdentifierIndicator = true;
		} else {
			this.transactionPurchaseIdentifierIndicator = false;
		}
	}

	public String getTransactionPurchaseIdentifierDescription() {
		return transactionPurchaseIdentifierDescription;
	}

	public void setTransactionPurchaseIdentifierDescription(String transactionPurchaseIdentifierDescription) {
		this.transactionPurchaseIdentifierDescription = transactionPurchaseIdentifierDescription;
	}

	public String getTransactionUnitContactName() {
		return transactionUnitContactName;
	}

	public void setTransactionUnitContactName(String transactionUnitContactName) {
		this.transactionUnitContactName = transactionUnitContactName;
	}

	public String getTransactionTravelAuthorizationCode() {
		return transactionTravelAuthorizationCode;
	}

	public void setTransactionTravelAuthorizationCode(String transactionTravelAuthorizationCode) {
		this.transactionTravelAuthorizationCode = transactionTravelAuthorizationCode;
	}

	public String getTransactionPointOfSaleCode() {
		return transactionPointOfSaleCode;
	}

	public void setTransactionPointOfSaleCode(String transactionPointOfSaleCode) {
		this.transactionPointOfSaleCode = transactionPointOfSaleCode;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getVendorLine1Address() {
		return vendorLine1Address;
	}

	public void setVendorLine1Address(String vendorLine1Address) {
		this.vendorLine1Address = vendorLine1Address;
	}

	public String getVendorLine2Address() {
		return vendorLine2Address;
	}

	public void setVendorLine2Address(String vendorLine2Address) {
		this.vendorLine2Address = vendorLine2Address;
	}

	public String getVendorCityName() {
		return vendorCityName;
	}

	public void setVendorCityName(String vendorCityName) {
		this.vendorCityName = vendorCityName;
	}

	public String getVendorStateCode() {
		return vendorStateCode;
	}

	public void setVendorStateCode(String vendorStateCode) {
		this.vendorStateCode = vendorStateCode;
	}

	public String getVendorZipCode() {
		return vendorZipCode;
	}

	public void setVendorZipCode(String vendorZipCode) {
		this.vendorZipCode = vendorZipCode;
	}

	public String getVendorOrderNumber() {
		return vendorOrderNumber;
	}

	public void setVendorOrderNumber(String vendorOrderNumber) {
		this.vendorOrderNumber = vendorOrderNumber;
	}

	public String getVisaVendorIdentifier() {
		return visaVendorIdentifier;
	}

	public void setVisaVendorIdentifier(String visaVendorIdentifier) {
		this.visaVendorIdentifier = visaVendorIdentifier;
	}

	public String getCardHolderAlternateName() {
		return cardHolderAlternateName;
	}

	public void setCardHolderAlternateName(String cardHolderAlternateName) {
		this.cardHolderAlternateName = cardHolderAlternateName;
	}

	public String getCardHolderLine1Address() {
		return cardHolderLine1Address;
	}

	public void setCardHolderLine1Address(String cardHolderLine1Address) {
		this.cardHolderLine1Address = cardHolderLine1Address;
	}

	public String getCardHolderLine2Address() {
		return cardHolderLine2Address;
	}

	public void setCardHolderLine2Address(String cardHolderLine2Address) {
		this.cardHolderLine2Address = cardHolderLine2Address;
	}

	public String getCardHolderCityName() {
		return cardHolderCityName;
	}

	public void setCardHolderCityName(String cardHolderCityName) {
		this.cardHolderCityName = cardHolderCityName;
	}

	public String getCardHolderStateCode() {
		return cardHolderStateCode;
	}

	public void setCardHolderStateCode(String cardHolderStateCode) {
		this.cardHolderStateCode = cardHolderStateCode;
	}

	public String getCardHolderZipCode() {
		return cardHolderZipCode;
	}

	public void setCardHolderZipCode(String cardHolderZipCode) {
		this.cardHolderZipCode = cardHolderZipCode;
	}

	public String getCardHolderWorkPhoneNumber() {
		return cardHolderWorkPhoneNumber;
	}

	public void setCardHolderWorkPhoneNumber(String cardHolderWorkPhoneNumber) {
		this.cardHolderWorkPhoneNumber = cardHolderWorkPhoneNumber;
	}

	public KualiDecimal getCardLimit() {
		return cardLimit;
	}

	public void setCardLimit(KualiDecimal cardLimit) {
		this.cardLimit = cardLimit;
	}
	
	public void setCardLimit(String cardLimit) {
		if (StringUtils.isNotBlank(cardLimit)) {
			this.cardLimit = new KualiDecimal(cardLimit);
		} else {
			this.cardLimit = KualiDecimal.ZERO;
		}
	}

	public KualiDecimal getCardCycleAmountLimit() {
		return cardCycleAmountLimit;
	}

	public void setCardCycleAmountLimit(KualiDecimal cardCycleAmountLimit) {
		this.cardCycleAmountLimit = cardCycleAmountLimit;
	}
	
	public void setCardCycleAmountLimit(String cardCycleAmountLimit) {
		if (StringUtils.isNotBlank(cardCycleAmountLimit)) {
			this.cardCycleAmountLimit = new KualiDecimal(cardCycleAmountLimit);
		} else {
			this.cardCycleAmountLimit = KualiDecimal.ZERO;
		}
	}

	public KualiDecimal getCardCycleVolumeLimit() {
		return cardCycleVolumeLimit;
	}

	public void setCardCycleVolumeLimit(KualiDecimal cardCycleVolumeLimit) {
		this.cardCycleVolumeLimit = cardCycleVolumeLimit;
	}

	public void setCardCycleVolumeLimit(String cardCycleVolumeLimit) {
		if(StringUtils.isNotBlank(cardCycleVolumeLimit)) {
			this.cardCycleVolumeLimit = new KualiDecimal(cardCycleVolumeLimit);
		} else {
			this.cardCycleVolumeLimit = KualiDecimal.ZERO;
		}
	}
	
	public String getCardStatusCode() {
		return cardStatusCode;
	}

	public void setCardStatusCode(String cardStatusCode) {
		this.cardStatusCode = cardStatusCode;
	}

	public String getCardNoteText() {
		return cardNoteText;
	}

	public void setCardNoteText(String cardNoteText) {
		this.cardNoteText = cardNoteText;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public void setOrderDate(String orderDate) {
		if(StringUtils.isNotBlank(orderDate)) {
			this.orderDate = (Date) (new SqlDateConverter()).convert(Date.class, orderDate);
		}
	}
	
	public String getPurchaseTime() {
		return purchaseTime;
	}

	public void setPurchaseTime(String purchaseTime) {
		this.purchaseTime = purchaseTime;
	}

	public String getShipPostal() {
		return shipPostal;
	}

	public void setShipPostal(String shipPostal) {
		this.shipPostal = shipPostal;
	}

	public String getDestinationPostal() {
		return destinationPostal;
	}

	public void setDestinationPostal(String destinationPostal) {
		this.destinationPostal = destinationPostal;
	}

	public String getDestinationCountryCode() {
		return destinationCountryCode;
	}

	public void setDestinationCountryCode(String destinationCountryCode) {
		this.destinationCountryCode = destinationCountryCode;
	}

	public KualiDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(KualiDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public void setTaxAmount(String taxAmount) {
		if(StringUtils.isNotBlank(taxAmount)) {
			this.taxAmount = new KualiDecimal(taxAmount);
		} else {
			this.taxAmount = KualiDecimal.ZERO;
		}		
	}
	
	public KualiDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(KualiDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public void setTaxRate(String taxRate) {
		if (StringUtils.isNotBlank(taxRate)) {
			this.taxRate = new KualiDecimal(taxRate);
		} else {
			this.taxRate = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(KualiDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public void setDiscountAmount(String discountAmount) {
		if(StringUtils.isNotBlank(discountAmount)) {
			this.discountAmount = new KualiDecimal(discountAmount);
		} else {
			this.discountAmount = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getFreightAmount() {
		return freightAmount;
	}

	public void setFreightAmount(KualiDecimal freightAmount) {
		this.freightAmount = freightAmount;
	}

	public void setFreightAmount(String freightAmount) {
		if(StringUtils.isNotBlank(freightAmount)) {
			this.freightAmount = new KualiDecimal(freightAmount);
		} else {
			this.freightAmount = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getDutyAmount() {
		return dutyAmount;
	}

	public void setDutyAmount(KualiDecimal dutyAmount) {
		this.dutyAmount = dutyAmount;
	}

	public void setDutyAmount(String dutyAmount) {
		if(StringUtils.isNotBlank(dutyAmount)) {
			this.dutyAmount = new KualiDecimal(dutyAmount);
		} else {
			this.dutyAmount = KualiDecimal.ZERO;
		}
	}
	
	public Date getUserEffectiveDate() {
		return userEffectiveDate;
	}

	public void setUserEffectiveDate(Date userEffectiveDate) {
		this.userEffectiveDate = userEffectiveDate;
	}

	public void setUserEffectiveDate(String userEffectiveDate) {
		if(StringUtils.isNotBlank(userEffectiveDate)) {
			this.userEffectiveDate = (Date) (new SqlDateConverter()).convert(Date.class, userEffectiveDate);
		}
	}
	
	public KualiDecimal getUserAmount() {
		return userAmount;
	}

	public void setUserAmount(KualiDecimal userAmount) {
		this.userAmount = userAmount;
	}

	public void setUserAmount(String userAmount) {
		if(StringUtils.isNotBlank(userAmount)) {
			this.userAmount = new KualiDecimal(userAmount);
		} else {
			this.userAmount = KualiDecimal.ZERO;
		}
	}
	
	public String getOilBrandName() {
		return oilBrandName;
	}

	public void setOilBrandName(String oilBrandName) {
		this.oilBrandName = oilBrandName;
	}

	public String getOdometerReading() {
		return odometerReading;
	}

	public void setOdometerReading(String odometerReading) {
		this.odometerReading = odometerReading;
	}

	public String getFleetId() {
		return fleetId;
	}

	public void setFleetId(String fleetId) {
		this.fleetId = fleetId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getFuelServiceType() {
		return fuelServiceType;
	}

	public void setFuelServiceType(String fuelServiceType) {
		this.fuelServiceType = fuelServiceType;
	}

	public String getFuelProductCd() {
		return fuelProductCd;
	}

	public void setFuelProductCd(String fuelProductCd) {
		this.fuelProductCd = fuelProductCd;
	}

	public String getProductTypeCd() {
		return productTypeCd;
	}

	public void setProductTypeCd(String productTypeCd) {
		this.productTypeCd = productTypeCd;
	}

	public BigDecimal getFuelQuantity() {
		return fuelQuantity;
	}

	public void setFuelQuantity(BigDecimal fuelQuantity) {
		this.fuelQuantity = fuelQuantity;
	}

	public void setFuelQuantity(String fuelQuantity) {
		if(StringUtils.isNotBlank(fuelQuantity)) {
			this.fuelQuantity = new BigDecimal(fuelQuantity);
		} else {
			this.fuelQuantity = BigDecimal.ZERO;
		}
	}
	
	public Integer getFuelUnitOfMeasure() {
		return fuelUnitOfMeasure;
	}

	public void setFuelUnitOfMeasure(Integer fuelUnitOfMeasure) {
		this.fuelUnitOfMeasure = fuelUnitOfMeasure;
	}

	public void setFuelUnitOfMeasure(String fuelUnitOfMeasure) {
		if(StringUtils.isNotBlank(fuelUnitOfMeasure)) {
			this.fuelUnitOfMeasure = new Integer(fuelUnitOfMeasure);
		} else {
			this.fuelUnitOfMeasure = 0;
		}
	}
	
	public KualiDecimal getFuelUnitPrice() {
		return fuelUnitPrice;
	}

	public void setFuelUnitPrice(KualiDecimal fuelUnitPrice) {
		this.fuelUnitPrice = fuelUnitPrice;
	}

	public void setFuelUnitPrice(String fuelUnitPrice) {
		if(StringUtils.isNotBlank(fuelUnitPrice)) {
			this.fuelUnitPrice = new KualiDecimal(fuelUnitPrice);
		} else {
			this.fuelUnitPrice = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getFuelSaleAmount() {
		return fuelSaleAmount;
	}

	public void setFuelSaleAmount(KualiDecimal fuelSaleAmount) {
		this.fuelSaleAmount = fuelSaleAmount;
	}

	public void setFuelSaleAmount(String fuelSaleAmount) {
		if(StringUtils.isNotBlank(fuelSaleAmount)) {
			this.fuelSaleAmount = new KualiDecimal(fuelSaleAmount);
		} else {
			this.fuelSaleAmount = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getFuelDiscountAmount() {
		return fuelDiscountAmount;
	}

	public void setFuelDiscountAmount(KualiDecimal fuelDiscountAmount) {
		this.fuelDiscountAmount = fuelDiscountAmount;
	}

	public void setFuelDiscountAmount(String fuelDiscountAmount) {
		if(StringUtils.isNotBlank(fuelDiscountAmount)) {
			this.fuelDiscountAmount = new KualiDecimal(fuelDiscountAmount);
		} else {
			this.fuelDiscountAmount = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getTaxAmount1() {
		return taxAmount1;
	}

	public void setTaxAmount1(KualiDecimal taxAmount1) {
		this.taxAmount1 = taxAmount1;
	}

	public void setTaxAmount1(String taxAmount1) {
		if(StringUtils.isNotBlank(taxAmount1)) {
			this.taxAmount1 = new KualiDecimal(taxAmount1);
		} else {
			this.taxAmount1 = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getTaxAmount2() {
		return taxAmount2;
	}

	public void setTaxAmount2(KualiDecimal taxAmount2) {
		this.taxAmount2 = taxAmount2;
	}

	public void setTaxAmount2(String taxAmount2) {
		if(StringUtils.isNotBlank(taxAmount2)) {
			this.taxAmount2 = new KualiDecimal(taxAmount2);
		} else {
			this.taxAmount2 = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(KualiDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		if(StringUtils.isNotBlank(totalAmount)) {
			this.totalAmount = new KualiDecimal(totalAmount);
		} else {
			this.totalAmount = KualiDecimal.ZERO;
		}
	}
	
	public Date getGenericEffectiveDate() {
		return genericEffectiveDate;
	}

	public void setGenericEffectiveDate(Date genericEffectiveDate) {
		this.genericEffectiveDate = genericEffectiveDate;
	}
	
	public void setGenericEffectiveDate(String genericEffectiveDate) {
		if(StringUtils.isNotBlank(genericEffectiveDate)) {
			this.genericEffectiveDate = (Date) (new SqlDateConverter()).convert(Date.class, genericEffectiveDate);
		}
	}

	public String getGenericData() {
		return genericData;
	}

	public void setGenericData(String genericData) {
		this.genericData = genericData;
	}

	public Date getArriveDate() {
		return arriveDate;
	}

	public void setArriveDate(Date arriveDate) {
		this.arriveDate = arriveDate;
	}

	public void setArriveDate(String arriveDate) {
		if(StringUtils.isNotBlank(arriveDate)) {
			this.arriveDate = (Date) (new SqlDateConverter()).convert(Date.class, arriveDate);
		}
	}
	
	public Date getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}

	public void setDepartureDate(String departureDate) {
		if(StringUtils.isNotBlank(departureDate)) {
			this.departureDate = (Date) (new SqlDateConverter()).convert(Date.class, departureDate);
		}
	}
	
	public String getFolioNum() {
		return folioNum;
	}

	public void setFolioNum(String folioNum) {
		this.folioNum = folioNum;
	}

	public String getPropertyPhoneNum() {
		return propertyPhoneNum;
	}

	public void setPropertyPhoneNum(String propertyPhoneNum) {
		this.propertyPhoneNum = propertyPhoneNum;
	}

	public String getCustomerServiceNum() {
		return customerServiceNum;
	}

	public void setCustomerServiceNum(String customerServiceNum) {
		this.customerServiceNum = customerServiceNum;
	}

	public KualiDecimal getPrePaidAmt() {
		return prePaidAmt;
	}

	public void setPrePaidAmt(KualiDecimal prePaidAmt) {
		this.prePaidAmt = prePaidAmt;
	}

	public void setPrePaidAmt(String prePaidAmt) {
		if(StringUtils.isNotBlank(prePaidAmt)) {
			this.prePaidAmt = new KualiDecimal(prePaidAmt);
		} else {
			this.prePaidAmt = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getRoomRate() {
		return roomRate;
	}

	public void setRoomRate(KualiDecimal roomRate) {
		this.roomRate = roomRate;
	}

	public void setRoomRate(String roomRate) {
		if(StringUtils.isNotBlank(roomRate)) {
			this.roomRate = new KualiDecimal(roomRate);
		} else {
			this.roomRate = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getRoomTax() {
		return roomTax;
	}

	public void setRoomTax(KualiDecimal roomTax) {
		this.roomTax = roomTax;
	}
	
	public void setRoomTax(String roomTax) {
		if(StringUtils.isNotBlank(roomTax)) {
			this.roomTax = new KualiDecimal(roomTax);
		} else {
			this.roomTax = KualiDecimal.ZERO;
		}
	}

	public String getProgramCode() {
		return programCode;
	}

	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}

	public KualiDecimal getCallCharges() {
		return callCharges;
	}

	public void setCallCharges(KualiDecimal callCharges) {
		this.callCharges = callCharges;
	}

	public void setCallCharges(String callCharges) {
		if(StringUtils.isNotBlank(callCharges)) {
			this.callCharges = new KualiDecimal(callCharges);
		} else {
			this.callCharges = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getFoodSvcCharges() {
		return foodSvcCharges;
	}

	public void setFoodSvcCharges(KualiDecimal foodSvcCharges) {
		this.foodSvcCharges = foodSvcCharges;
	}

	public void setFoodSvcCharges(String foodSvcCharges) {
		if(StringUtils.isNotBlank(foodSvcCharges)) {
			this.foodSvcCharges = new KualiDecimal(foodSvcCharges);
		} else {
			this.foodSvcCharges = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getMiniBarCharges() {
		return miniBarCharges;
	}

	public void setMiniBarCharges(KualiDecimal miniBarCharges) {
		this.miniBarCharges = miniBarCharges;
	}

	public void setMiniBarCharges(String miniBarCharges) {
		if(StringUtils.isNotBlank(miniBarCharges)) {
			this.miniBarCharges = new KualiDecimal(miniBarCharges);
		} else {
			this.miniBarCharges = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getGiftShopCharges() {
		return giftShopCharges;
	}

	public void setGiftShopCharges(KualiDecimal giftShopCharges) {
		this.giftShopCharges = giftShopCharges;
	}

	public void setGiftShopCharges(String giftShopCharges) {
		if(StringUtils.isNotBlank(giftShopCharges)) {
			this.giftShopCharges = new KualiDecimal(giftShopCharges);
		} else {
			this.giftShopCharges = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getLaundryCharges() {
		return laundryCharges;
	}

	public void setLaundryCharges(KualiDecimal laundryCharges) {
		this.laundryCharges = laundryCharges;
	}

	public void setLaundryCharges(String laundryCharges) {
		if(StringUtils.isNotBlank(laundryCharges)) {
			this.laundryCharges = new KualiDecimal(laundryCharges);
		} else {
			this.laundryCharges = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getHealthClubCharges() {
		return healthClubCharges;
	}

	public void setHealthClubCharges(KualiDecimal healthClubCharges) {
		this.healthClubCharges = healthClubCharges;
	}

	public void setHealthClubCharges(String healthClubCharges) {
		if(StringUtils.isNotBlank(healthClubCharges)) {
			this.healthClubCharges = new KualiDecimal(healthClubCharges);
		} else {
			this.healthClubCharges = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getMovieCharges() {
		return movieCharges;
	}

	public void setMovieCharges(KualiDecimal movieCharges) {
		this.movieCharges = movieCharges;
	}

	public void setMovieCharges(String movieCharges) {
		if(StringUtils.isNotBlank(movieCharges)) {
			this.movieCharges = new KualiDecimal(movieCharges);
		} else {
			this.movieCharges = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getBusCtrCharges() {
		return busCtrCharges;
	}

	public void setBusCtrCharges(KualiDecimal busCtrCharges) {
		this.busCtrCharges = busCtrCharges;
	}

	public void setBusCtrCharges(String busCtrCharges) {
		if(StringUtils.isNotBlank(busCtrCharges)) {
			this.busCtrCharges = new KualiDecimal(busCtrCharges);
		} else {
			this.busCtrCharges = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getParkingCharges() {
		return parkingCharges;
	}

	public void setParkingCharges(KualiDecimal parkingCharges) {
		this.parkingCharges = parkingCharges;
	}

	public void setParkingCharges(String parkingCharges) {
		if(StringUtils.isNotBlank(parkingCharges)) {
			this.parkingCharges = new KualiDecimal(parkingCharges);
		} else {
			this.parkingCharges = KualiDecimal.ZERO;
		}
	}
	
	public String getOtherCode() {
		return otherCode;
	}

	public void setOtherCode(String otherCode) {
		this.otherCode = otherCode;
	}

	public KualiDecimal getOtherCharges() {
		return otherCharges;
	}

	public void setOtherCharges(KualiDecimal otherCharges) {
		this.otherCharges = otherCharges;
	}
	
	public void setOtherCharges(String otherCharges) {
		if(StringUtils.isNotBlank(otherCharges)) {
			this.otherCharges = new KualiDecimal(otherCharges);
		} else {
			this.otherCharges = KualiDecimal.ZERO;
		}
	}

	public KualiDecimal getAdjustmentAmount() {
		return adjustmentAmount;
	}

	public void setAdjustmentAmount(KualiDecimal adjustmentAmount) {
		this.adjustmentAmount = adjustmentAmount;
	}

	public void setAdjustmentAmount(String adjustmentAmount) {
		if(StringUtils.isNotBlank(adjustmentAmount)) {
			this.adjustmentAmount = new KualiDecimal(adjustmentAmount);
		} else {
			this.adjustmentAmount = KualiDecimal.ZERO;
		}
	}
	
	public Date getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(Date checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	public void setCheckOutDate(String checkOutDate) {
		if(StringUtils.isNotBlank(checkOutDate)) {
			this.checkOutDate = (Date) (new SqlDateConverter()).convert(Date.class, checkOutDate);
		}
	}
	
	public String getRentalAgreementNum() {
		return rentalAgreementNum;
	}

	public void setRentalAgreementNum(String rentalAgreementNum) {
		this.rentalAgreementNum = rentalAgreementNum;
	}

	public String getRenterName() {
		return renterName;
	}

	public void setRenterName(String renterName) {
		this.renterName = renterName;
	}

	public String getReturnCity() {
		return returnCity;
	}

	public void setReturnCity(String returnCity) {
		this.returnCity = returnCity;
	}

	public String getReturnState() {
		return returnState;
	}
	
	public void setReturnState(String returnState) {
		this.returnState = returnState;
	}
	
	public String getReturnCountry() {
		return returnCountry;
	}

	public void setReturnCountry(String returnCountry) {
		this.returnCountry = returnCountry;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public void setReturnDate(String returnDate) {
		if(StringUtils.isNotBlank(returnDate)) {
			this.returnDate = (Date) (new SqlDateConverter()).convert(Date.class, returnDate);
		}
	}
	
	public String getReturnLocation() {
		return returnLocation;
	}

	public void setReturnLocation(String returnLocation) {
		this.returnLocation = returnLocation;
	}

	public String getCustomerSvcNum() {
		return customerSvcNum;
	}

	public void setCustomerSvcNum(String customerSvcNum) {
		this.customerSvcNum = customerSvcNum;
	}

	public String getRentalClass() {
		return rentalClass;
	}

	public void setRentalClass(String rentalClass) {
		this.rentalClass = rentalClass;
	}

	public KualiDecimal getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(KualiDecimal dailyRate) {
		this.dailyRate = dailyRate;
	}

	public void setDailyRate(String dailyRate) {
		if(StringUtils.isNotBlank(dailyRate)) {
			this.dailyRate = new KualiDecimal(dailyRate);
		} else {
			this.dailyRate = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getWeeklyRate() {
		return weeklyRate;
	}

	public void setWeeklyRate(KualiDecimal weeklyRate) {
		this.weeklyRate = weeklyRate;
	}

	public void setWeeklyRate(String weeklyRate) {
		if(StringUtils.isNotBlank(weeklyRate)) {
			this.weeklyRate = new KualiDecimal(weeklyRate);
		} else {
			this.weeklyRate = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getRatePerMile() {
		return ratePerMile;
	}

	public void setRatePerMile(KualiDecimal ratePerMile) {
		this.ratePerMile = ratePerMile;
	}

	public void setRatePerMile(String ratePerMile) {
		if(StringUtils.isNotBlank(ratePerMile)) {
			this.ratePerMile = new KualiDecimal(ratePerMile);
		} else {
			this.ratePerMile = KualiDecimal.ZERO;
		}
	}
	
	public Integer getMaxFreeMiles() {
		return maxFreeMiles;
	}

	public void setMaxFreeMiles(Integer maxFreeMiles) {
		this.maxFreeMiles = maxFreeMiles;
	}

	public void setMaxFreeMiles(String maxFreeMiles) {
		if(StringUtils.isNotBlank(maxFreeMiles)) {
			this.maxFreeMiles = new Integer(maxFreeMiles);
		} else {
			this.maxFreeMiles = 0;
		}
	}
	
	public Integer getTotalMiles() {
		return totalMiles;
	}

	public void setTotalMiles(Integer totalMiles) {
		this.totalMiles = totalMiles;
	}

	public void setTotalMiles(String totalMiles) {
		if(StringUtils.isNotBlank(totalMiles)) {
			this.totalMiles = new Integer(totalMiles);
		} else {
			this.totalMiles = 0;
		}
	}
	
	public KualiDecimal getOneWayCharges() {
		return oneWayCharges;
	}

	public void setOneWayCharges(KualiDecimal oneWayCharges) {
		this.oneWayCharges = oneWayCharges;
	}

	public void setOneWayCharges(String oneWayCharges) {
		if(StringUtils.isNotBlank(oneWayCharges)) {
			this.oneWayCharges = new KualiDecimal(oneWayCharges);
		} else {
			this.oneWayCharges = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getInsuranceCharges() {
		return insuranceCharges;
	}

	public void setInsuranceCharges(KualiDecimal insuranceCharges) {
		this.insuranceCharges = insuranceCharges;
	}

	public void setInsuranceCharges(String insuranceCharges) {
		if(StringUtils.isNotBlank(insuranceCharges)) {
			this.insuranceCharges = new KualiDecimal(insuranceCharges);
		} else {
			this.insuranceCharges = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getRegularCharges() {
		return regularCharges;
	}

	public void setRegularCharges(KualiDecimal regularCharges) {
		this.regularCharges = regularCharges;
	}

	public void setRegularCharges(String regularCharges) {
		if(StringUtils.isNotBlank(regularCharges)) {
			this.regularCharges = new KualiDecimal(regularCharges);
		} else {
			this.regularCharges = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getTowingCharges() {
		return towingCharges;
	}

	public void setTowingCharges(KualiDecimal towingCharges) {
		this.towingCharges = towingCharges;
	}

	public void setTowingCharges(String towingCharges) {
		if(StringUtils.isNotBlank(towingCharges)) {
			this.towingCharges = new KualiDecimal(towingCharges);
		} else {
			this.towingCharges = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getExtraCharges() {
		return extraCharges;
	}

	public void setExtraCharges(KualiDecimal extraCharges) {
		this.extraCharges = extraCharges;
	}

	public void setExtraCharges(String extraCharges) {
		if(StringUtils.isNotBlank(extraCharges)) {
			this.extraCharges = new KualiDecimal(extraCharges);
		} else {
			this.extraCharges = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getLateReturnFee() {
		return lateReturnFee;
	}

	public void setLateReturnFee(KualiDecimal lateReturnFee) {
		this.lateReturnFee = lateReturnFee;
	}

	public void setLateReturnFee(String lateReturnFee) {
		if(StringUtils.isNotBlank(lateReturnFee)) {
			this.lateReturnFee = new KualiDecimal(lateReturnFee);
		} else {
			this.lateReturnFee = KualiDecimal.ZERO;
		}
	}
	
	public String getAdjustCode() {
		return adjustCode;
	}

	public void setAdjustCode(String adjustCode) {
		this.adjustCode = adjustCode;
	}

	public KualiDecimal getAdjustAmount() {
		return adjustAmount;
	}

	public void setAdjustAmount(KualiDecimal adjustAmount) {
		this.adjustAmount = adjustAmount;
	}

	public void setAdjustAmount(String adjustAmount) {
		if(StringUtils.isNotBlank(adjustAmount)) {
			this.adjustAmount = new KualiDecimal(adjustAmount);
		} else {
			this.adjustAmount = KualiDecimal.ZERO;
		}
	}
	
	public String getProgCode() {
		return progCode;
	}

	public void setProgCode(String progCode) {
		this.progCode = progCode;
	}

	public KualiDecimal getPhoneCharges() {
		return phoneCharges;
	}

	public void setPhoneCharges(KualiDecimal phoneCharges) {
		this.phoneCharges = phoneCharges;
	}
	
	public void setPhoneCharges(String phoneCharges) {
		if(StringUtils.isNotBlank(phoneCharges)) {
			this.phoneCharges = new KualiDecimal(phoneCharges);
		} else {
			this.phoneCharges = KualiDecimal.ZERO;
		}
	}

	public KualiDecimal getOthrCharges() {
		return othrCharges;
	}

	public void setOthrCharges(KualiDecimal othrCharges) {
		this.othrCharges = othrCharges;
	}

	public void setOthrCharges(String othrCharges) {
		if(StringUtils.isNotBlank(othrCharges)) {
			this.othrCharges = new KualiDecimal(othrCharges);
		} else {
			this.othrCharges = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getTotalTaxAmount() {
		return totalTaxAmount;
	}

	public void setTotalTaxAmount(KualiDecimal totalTaxAmount) {
		this.totalTaxAmount = totalTaxAmount;
	}

	public void setTotalTaxAmount(String totalTaxAmount) {
		if(StringUtils.isNotBlank(totalTaxAmount)) {
			this.totalTaxAmount = new KualiDecimal(totalTaxAmount);
		} else {
			this.totalTaxAmount = KualiDecimal.ZERO;
		}
	}
	
	public String getPassengerName() {
		return passengerName;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	public Date getDepartDate() {
		return departDate;
	}

	public void setDepartDate(Date departDate) {
		this.departDate = departDate;
	}

	public void setDepartDate(String departDate) {
		if(StringUtils.isNotBlank(departDate)) {
			this.departDate = (Date) (new SqlDateConverter()).convert(Date.class, departDate);
		}
	}
	
	public String getDepartureCity() {
		return departureCity;
	}

	public void setDepartureCity(String departureCity) {
		this.departureCity = departureCity;
	}

	public String getAgencyCode() {
		return agencyCode;
	}

	public void setAgencyCode(String agencyCode) {
		this.agencyCode = agencyCode;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public long getTicketNumber() {
		return ticketNumber;
	}

	public void setTicketNumber(long ticketNumber) {
		this.ticketNumber = ticketNumber;
	}

	public void setTicketNumber(String ticketNumber) {
		if(StringUtils.isNotBlank(ticketNumber)) {
			this.ticketNumber = new Long(ticketNumber).longValue();
		} else {
			this.ticketNumber = 0;
		}
	}
	
	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public void setIssueDate(String issueDate) {
		if(StringUtils.isNotBlank(issueDate)) {
			this.issueDate = (Date) (new SqlDateConverter()).convert(Date.class, issueDate);
		}
	}
	
	public String getIssuingCarrier() {
		return issuingCarrier;
	}

	public void setIssuingCarrier(String issuingCarrier) {
		this.issuingCarrier = issuingCarrier;
	}

	public KualiDecimal getTotalFare() {
		return totalFare;
	}

	public void setTotalFare(KualiDecimal totalFare) {
		this.totalFare = totalFare;
	}

	public void setTotalFare(String totalFare) {
		if(StringUtils.isNotBlank(totalFare)) {
			this.totalFare = new KualiDecimal(totalFare);
		} else {
			this.totalFare = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getTotalFees() {
		return totalFees;
	}

	public void setTotalFees(KualiDecimal totalFees) {
		this.totalFees = totalFees;
	}

	public void setTotalFees(String totalFees) {
		if(StringUtils.isNotBlank(totalFees)) {
			this.totalFees = new KualiDecimal(totalFees);
		} else {
			this.totalFees = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getTotalTaxes() {
		return totalTaxes;
	}

	public void setTotalTaxes(KualiDecimal totalTaxes) {
		this.totalTaxes = totalTaxes;
	}

	public void setTotalTaxes(String totalTaxes) {
		if (StringUtils.isNotBlank(totalTaxes)) {
			this.totalTaxes = new KualiDecimal(totalTaxes);
		} else {
			this.totalTaxes = KualiDecimal.ZERO;
		}
	}
	
	public List<ProcurementCardTranAddItem> getProcurementCardTranAddItems() {
		return procurementCardTranAddItems;
	}

	public void setProcurementCardTranAddItems(List<ProcurementCardTranAddItem> procurementCardTranAddItems) {
		this.procurementCardTranAddItems = procurementCardTranAddItems;
	}
	
	/**
	 * This is a convenience method that adds a populated ProcurementCardTranAddItem object directly
	 * to the contained ArrayList.
	 * 
	 * It's primarily used by the Procurement Card Load batch process, for each of XML batch file
	 * digesting, though it can be used generally.
	 * 
	 * NOTE that it will attempt to wire the parent/child relationship by setting the
	 * procurementCardTranAddItem.transactionSequenceRowNumber to the transactionSequenceRowNumber of 'this', if the number isn't
	 * already set.
	 * 
	 * @param procurementCardTranAddItem
	 */
	public void addProcurementCardTranAddItem(ProcurementCardTranAddItem procurementCardTranAddItem) {
		// do nothing if passed-in procurementCardTranAddItem is null
		if(procurementCardTranAddItem == null) {
			return;
		}
		
		// wire the Addendum Item to ensure a valid parent/child relationship
		if(procurementCardTranAddItem.getTransactionSequenceRowNumber() ==  null) {
			if(this.transactionSequenceRowNumber != null) {
				procurementCardTranAddItem.setTransactionSequenceRowNumber(this.transactionSequenceRowNumber);
			}
		}
		
		this.procurementCardTranAddItems.add(procurementCardTranAddItem);
	}

	public List<ProcurementCardTranNonFuel> getProcurementCardTranNonFuels() {
		return procurementCardTranNonFuels;
	}

	public void setProcurementCardTranNonFuels(List<ProcurementCardTranNonFuel> procurementCardTranNonFuels) {
		this.procurementCardTranNonFuels = procurementCardTranNonFuels;
	}

	/**
	 * This is a convenience method that adds a populated ProcurementCardTranNonFuel object directly
	 * to the contained ArrayList.
	 * 
	 * It's primarily used by the Procurement Card Load batch process, for each XML batch file
	 * digesting, though it can be used generally.
	 * 
	 * NOTE that it will attempt to wire the parent/child relationship by setting the 
	 * procurementCardTranNonFuel.transactionSequenceRowNumber to the transactionSequenceRowNumber of 'this', if the number isn't
	 * already set.
	 * 
	 * @return procurementCardTranNonFuel
	 */
	public void addProcurementCardTranNonFuel(ProcurementCardTranNonFuel procurementCardTranNonFuel) {
		// do nothing if passed-in procurementCardTranNonFuel is null
		if (procurementCardTranNonFuel == null) {
			return;
		}
		
		// wire the Non Fuel Service to ensure a valid parent/child relationship
		if(procurementCardTranNonFuel.getTransactionSequenceRowNumber() == null) {
			if(this.transactionSequenceRowNumber != null) {
				procurementCardTranNonFuel.setTransactionSequenceRowNumber(this.transactionSequenceRowNumber);
			}
		}
		
		this.procurementCardTranNonFuels.add(procurementCardTranNonFuel);
	}
	
	
	public List<ProcurementCardTranShipSvc> getProcurementCardTranShipSvcs() {
		return procurementCardTranShipSvcs;
	}

	public void setProcurementCardTranShipSvcs(List<ProcurementCardTranShipSvc> procurementCardTranShipSvcs) {
		this.procurementCardTranShipSvcs = procurementCardTranShipSvcs;
	}

	/**
	 * This is a convenience method that adds a populated ProcurementCardTranShipSvc object directly
	 * to the contained ArrayList.
	 * 
	 * It's primarily used by the Procurement Card Load batch process, for each of XML batch file
	 * digesting, though it can be used generally.
	 * 
	 * NOTE that it will attempt to wire the parent/child relationship by setting the
	 * procurementCardTranShipSvc.transactionSequenceRowNumber to the transactionSequenceRowNumber of 'this', if the number isn't
	 * already set.
	 * 
	 * @param procurementCardTranShipSvc
	 */
	public void addProcurementCardTranShipSvc(ProcurementCardTranShipSvc procurementCardTranShipSvc) {
		// do nothing if passed-in procurementCardTranShipSvc is null
		if (procurementCardTranShipSvc == null) {
			return;
		}
		
		// wire the Shipping Service to ensure a valid parent/child relationship
		if (procurementCardTranShipSvc.getTransactionSequenceRowNumber() ==  null) {
			if (this.transactionSequenceRowNumber != null) {
				procurementCardTranShipSvc.setTransactionSequenceRowNumber(this.transactionSequenceRowNumber);
			}
		}
		
		this.procurementCardTranShipSvcs.add(procurementCardTranShipSvc);
	}
	
	public List<ProcurementCardTranTempSvc> getProcurementCardTranTempSvcs() {
		return procurementCardTranTempSvcs;
	}

	public void setProcurementCardTranTempSvcs(List<ProcurementCardTranTempSvc> procurementCardTranTempSvcs) {
		this.procurementCardTranTempSvcs = procurementCardTranTempSvcs;
	}

	/**
	 * This is a convenience method that adds a populated ProcurementCardTranTempSvc object directly
	 * to the contained ArrayList.
	 * 
	 * It's primarily used by the ProcurementCard Load batch process, for each of XML batch file
	 * digesting, though it can be used generally.
	 * 
	 * NOTE that it will attempt to wire the parent/child relationship by setting the
	 * procurementCardTranTempSvc.transactionSequenceRowNumber to the transactionSequenceRowNumber of 'this', if the number isn't
	 * already set.
	 * 
	 * @param procurementCardTranTempSvc
	 */
	public void addProcurementCardTranTempSvc(ProcurementCardTranTempSvc procurementCardTranTempSvc) {
		// do nothing if passed-in procurementCardTranTempSvc is null
		if (procurementCardTranTempSvc == null) {
			return;
		}
		
		// wire the Temp Service to ensure a valid parent/child relationship
		if (procurementCardTranTempSvc.getTransactionSequenceRowNumber() == null) {
			if (this.transactionSequenceRowNumber != null) {
				procurementCardTranTempSvc.setTransactionSequenceRowNumber(this.transactionSequenceRowNumber);
			}
		}
		
		this.procurementCardTranTempSvcs.add(procurementCardTranTempSvc);
	}
	
	public List<ProcurementCardTranTransportLeg> getProcurementCardTranTransportLegs() {
		return procurementCardTranTransportLegs;
	}

	public void setProcurementCardTranTransportLegs(List<ProcurementCardTranTransportLeg> procurementCardTranTransportLegs) {
		this.procurementCardTranTransportLegs = procurementCardTranTransportLegs;
	}
	
	/**
	 * This is a convenience method that adds a populated ProcurementCardTranTransportLeg object directly
	 * to the contained ArrayList.
	 * 
	 * It's primarily used by the Procurement Card Load batch process, for each of XML batch file
	 * digesting, though it can be used generally.
	 * 
	 * NOTE that it will attempt to wire the parent/child relationship by setting the 
	 * procurementCardTranTransportLeg.transactionSequenceRowNumber to the transactionSequenceRowNumber of 'this', if the number isn't
	 * already set.
	 * 
	 * @param procurementCardTranTransportLeg
	 */
	public void addProcurementCardTranTransportLeg(ProcurementCardTranTransportLeg procurementCardTranTransportLeg) {
		// do nothing if passed-in procurementCardTranTransportLeg is null
		if (procurementCardTranTransportLeg == null) {
			return;
		}
		
		// wire the Transport Leg to ensure a valid parent/child relationship
		if (procurementCardTranTransportLeg.getTransactionSequenceRowNumber() == null) {
			if (this.transactionSequenceRowNumber != null) {
				procurementCardTranTransportLeg.setTransactionSequenceRowNumber(this.transactionSequenceRowNumber);
			}
		}
		
		this.procurementCardTranTransportLegs.add(procurementCardTranTransportLeg);
	}
	
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		if (this.transactionSequenceRowNumber != null) {
			m.put("transactionSequenceRowNumber", this.transactionSequenceRowNumber.toString());
		}
		return m;
	}
}
