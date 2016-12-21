package edu.arizona.kfs.tax.document;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

import edu.arizona.kfs.tax.TaxConstants;
import edu.arizona.kfs.tax.TaxKeyConstants;
import edu.arizona.kfs.tax.businessobject.DocumentIncomeType;
import edu.arizona.kfs.tax.businessobject.IncomeType;
import edu.arizona.kfs.tax.service.TaxParameterHelperService;

/**
 * This class acts as the base handler for 1099 Income Type processing. The class is abstract - to create a
 * new instance you must pass the appropriate type names for the generic parameters <T1, T2> where
 * T1 is an implementation of DocumentIncomeType
 * T2 is the java type of the documentIdentifier (String or Integer)
 */
public abstract class IncomeTypeHandler<T1 extends DocumentIncomeType<T2>, T2> {
	private static final Logger LOG = Logger.getLogger(IncomeTypeHandler.class);

	private BusinessObjectService businessObjectService;
	private TaxParameterHelperService taxParameterHelperService;

	private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");

	private Class<T1> incomeTypeClass;
	private T1 newIncomeType;
	private IncomeTypeContainer<T1, T2> incomeTypeDocumentContainer;
	private CurrencyFormatter currencyFormatter = new CurrencyFormatter();

	private Map<String, String> overridePaymentTypeCodeMap;
	private Map<String, String> objectCodeMap;
	private Set<String> overridingObjCodes;
	private String extractType;
	private Set<String> extractCodes;
	private Map<String, IncomeType> incomeTypeMap;

	public IncomeTypeHandler(IncomeTypeContainer<T1, T2> incomeTypeDocumentContainer, Class<T1> incomeTypeClass) {
		this.incomeTypeClass = incomeTypeClass;
		this.incomeTypeDocumentContainer = incomeTypeDocumentContainer;
		overridePaymentTypeCodeMap = getTaxParameterHelperService().getOverridePaymentTypeCodeMap();
		objectCodeMap = getTaxParameterHelperService().getObjectCodeMap();
		overridingObjCodes = getTaxParameterHelperService().getOverridingObjectCodes();
		extractType = getTaxParameterHelperService().getExtractType();
		extractCodes = getTaxParameterHelperService().getExtractCodes(extractType, overridingObjCodes);
		incomeTypeMap = getIncomeTypeMap();

		if (LOG.isDebugEnabled()) {
			LOG.debug("in IncomeTypeHandler()");
			LOG.debug("IncomeTypeDocumentContainer: " + incomeTypeDocumentContainer.getClass().getName());
		}
	}

	private BusinessObjectService getBusinessObjectService() {
		if (businessObjectService == null) {
			businessObjectService = SpringContext.getBean(BusinessObjectService.class);
		}
		return businessObjectService;
	}

	private TaxParameterHelperService getTaxParameterHelperService() {
		if (taxParameterHelperService == null) {
			taxParameterHelperService = SpringContext.getBean(TaxParameterHelperService.class);
		}
		return taxParameterHelperService;
	}

	/**
	 * Add the new DocumentIncomeType to the document.incomeTypes list
	 * This method...
	 */
	public void addNewIncomeType() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("in addNewIncomeType()");
		}

		if (newIncomeType != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("IncomeType: " + newIncomeType.getClass().getName());
			}

			// if income type already in list then don't add
			if (incomeTypeExists()) {
				newIncomeType = null;
				// check that required fields have data, if valid we will
				// add to the DocumentIncomeType list on the document
			} else if (validateIncomeType()) {
				// populate the DocumentIncomeType with the selected IncomeType object pulled from the db
				newIncomeType.setIncomeType(getBusinessObjectService().findBySinglePrimaryKey(IncomeType.class, newIncomeType.getIncomeTypeCode()));

				// if the line number has not been set then set it
				if ((newIncomeType.getLineNumber() == null) || (newIncomeType.getLineNumber().intValue() == 0)) {
					newIncomeType.setLineNumber(getNextIncomeTypeLineNumber());
				}

				incomeTypeDocumentContainer.getIncomeTypes().add(newIncomeType);

				// once added to list clear the holding variable
				newIncomeType = null;
			}
		}
	}

	public String getCurrencyFormattedTotalIncomeTypeAmount() {
		return (String) currencyFormatter.format(getTotalIncomeTypeAmount());
	}

	/**
	 * This method remove DocumentIncome type form list that is at the input index
	 * 
	 * @param index
	 */
	public void removeIncomeType(int index) {
		incomeTypeDocumentContainer.getIncomeTypes().remove(index);
	}

	/**
	 * This method loops over the current DocumentIncomeType list, finds the largest line number and returns largest-line-number + 1
	 * 
	 * @return
	 */
	public Integer getNextIncomeTypeLineNumber() {
		int retval = 0;
		for (T1 ic : incomeTypeDocumentContainer.getIncomeTypes()) {
			if (ic.getLineNumber().intValue() > retval) {
				retval = ic.getLineNumber().intValue();
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("lineNumber: " + (retval + 1));
		}

		return Integer.valueOf(retval + 1);
	}

	/**
	 * This method loops over the document.incomeTypes list and summarizes the amounts.
	 * 
	 * @return
	 */
	public KualiDecimal getTotalIncomeTypeAmount() {
		KualiDecimal retval = new KualiDecimal(0);

		// loop over the DocumentIncomeTypes and add up the 1099 amounts
		for (T1 ic : incomeTypeDocumentContainer.getIncomeTypes()) {
			retval = retval.add(ic.getAmount());
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("totalIncomeTypeAmount: " + retval.toString());
		}

		return retval;
	}

	private boolean incomeTypeExists() {
		boolean retval = false;

		if (StringUtils.isNotBlank(newIncomeType.getChartOfAccountsCode()) && StringUtils.isNotBlank(newIncomeType.getIncomeTypeCode())) {
			for (T1 ic : incomeTypeDocumentContainer.getIncomeTypes()) {
				if (ic.getChartOfAccountsCode().equals(newIncomeType.getChartOfAccountsCode()) && ic.getIncomeTypeCode().equals(newIncomeType.getIncomeTypeCode())) {
					retval = true;
					break;
				}
			}
		}
		return retval;
	}

	/**
	 * This method validates the DocumentIncomeType entry when the user hits the "Add" button on the screen
	 * 
	 * @return
	 */
	public boolean validateIncomeType() {
		boolean retval = true;

		if (LOG.isDebugEnabled()) {
			LOG.debug("validateIncomeType()");
		}

		MessageMap errors = GlobalVariables.getMessageMap();

		// make sure chart is selected
		if (StringUtils.isBlank(newIncomeType.getChartOfAccountsCode())) {
			errors.putError("newIncomeTypeError1", TaxKeyConstants.ERROR_INCOME_TYPE_COA_REQUIRED);
			retval = false;
		}

		// make sure income type is selected
		if (StringUtils.isBlank(newIncomeType.getIncomeTypeCode())) {
			errors.putError("newIncomeTypeError2", TaxKeyConstants.ERROR_INCOME_TYPE_CODE_REQUIRED);
			retval = false;
		}

		// make sure we have an amount entered
		if (newIncomeType.getAmount() == null) {
			errors.putError("newIncomeTypeError3", TaxKeyConstants.ERROR_INCOME_TYPE_AMOUNT_REQUIRED);
			retval = false;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("validateIncomeType() retval: " + retval);
		}

		return retval;
	}

	public CurrencyFormatter getCurrencyFormatter() {
		return currencyFormatter;
	}

	public T1 getNewIncomeType() {
		if (newIncomeType == null) {
			newIncomeType = getInitializedDocumentIncomeType();
		}

		return newIncomeType;
	}

	private T1 getInitializedDocumentIncomeType() {
		T1 retval = null;

		try {
			// use the class passed in the constructor to create a new instance
			retval = incomeTypeClass.newInstance();

			// initialize amount to 0
			retval.setAmount(new KualiDecimal(0));

			// set the document identifier from the document
			retval.setDocumentIdentifier(incomeTypeDocumentContainer.getDocumentIdentifier());
		}

		catch (Exception ex) {
			LOG.error(ex);
		}

		return retval;
	}

	public void setNewIncomeType(T1 newIncomeType) {
		this.newIncomeType = newIncomeType;
	}

	/**
	 * This method loads IncomeTypes into a map keyed by box number
	 * 
	 * @return
	 */
	private Map<String, IncomeType> getIncomeTypeMap() {
		Map<String, IncomeType> retval = new HashMap<String, IncomeType>();

		// Load the IncomeTypes from the database
		List<IncomeType> incomeTypes = (List<IncomeType>) getBusinessObjectService().findAll(IncomeType.class);

		if (incomeTypes != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("found " + incomeTypes.size() + " income types");
			}

			// create IncomeType map by 1099 box
			for (IncomeType ic : incomeTypes) {
				if (StringUtils.isNotBlank(ic.getIncomeTypeBox())) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("IncomeTypeCode: " + ic.getIncomeTypeCode() + ", box: " + ic.getIncomeTypeBox());
					}

					retval.put(ic.getIncomeTypeBox(), ic);
				}
			}
		}

		return retval;
	}

	/**
	 * This method populates the 1099 IncomeTypes from the document accounting lines
	 * 
	 * @param accountingLines
	 * @param useTaxItems
	 */
	public void populateIncomeTypes(List<? extends AccountingLine> accountingLines) {
		VendorHeader vendorHeader = incomeTypeDocumentContainer.getVendorHeader();
		zeroOutDocumentIncomeTypes();

		// if we found the vendor
		if (vendorHeader != null) {
			// loop over the accounting lines associated with the documents and summarize
			// valid 1099 accounting lines by income type (box on the 1099 form)
			for (AccountingLine acctline : accountingLines) {
				// get the 1099 box for this account
				String box = get1099Box(vendorHeader, acctline);

				if (StringUtils.isNotBlank(box)) {
					// See if a DocumentIncomeType associated with this 1099 box
					// exists in out map
					T1 dit = getDocumentIncomeType(acctline, box);

					if (dit != null) {
						// add the amount from the current accounting line to the DocumentIncomeType
						dit.setAmount(dit.getAmount().add(acctline.getAmount()));
					}
				}
			}
		}
	}

	// This method is used by the public populateIncomeTypes method.
	private String get1099Box(VendorHeader vendorHeader, AccountingLine acctline) {
		String retval = getTaxParameterHelperService().getOverridePaymentType(vendorHeader, overridePaymentTypeCodeMap);
		if (StringUtils.isBlank(retval)) {
			String finObjectCode = acctline.getFinancialObjectCode();

			if (StringUtils.isNotBlank(finObjectCode) && objectCodeMap.containsKey(finObjectCode)) {
				retval = objectCodeMap.get(finObjectCode);
			} else {
				// Set payment type to type 7 by default
				retval = TaxConstants.AMOUNT_CODE_7;
			}
		}

		return retval;
	}

	/**
	 * This method checks to see if the current document is cancelled or approved - in those cases we will
	 * not allow the income types to pre-populate
	 * 
	 * @return
	 */
	public boolean isEditableRouteStatus() {
		String routeStatus = incomeTypeDocumentContainer.getRouteStatus();

		return (KewApiConstants.ROUTE_HEADER_INITIATED_CD.equals(routeStatus) || KewApiConstants.ROUTE_HEADER_SAVED_CD.equals(routeStatus) || KewApiConstants.ROUTE_HEADER_ENROUTE_CD.equals(routeStatus));
	}

	/**
	 * This method loops over the current DocumentIncomeType list on this document and sets the amount to 0
	 */
	private void zeroOutDocumentIncomeTypes() {
		for (T1 dit : incomeTypeDocumentContainer.getIncomeTypes()) {
			dit.setAmount(new KualiDecimal(0));
		}
	}

	/**
	 * This looks for a DocumentIncomeType in the current document incomeTypes Collection that
	 * matches our AccountingLine. If it does not find a match and this accounting line is a valid 1099
	 * type then we create a new DocumentIncomeType and return it
	 * 
	 * @param acctline
	 * @param box
	 * @return
	 */
	private T1 getDocumentIncomeType(AccountingLine acctline, String box) {
		T1 retval = null;

		if (LOG.isDebugEnabled()) {
			LOG.debug("financialObjectCode:" + acctline.getFinancialObjectCode());
		}

		// loop over current DocumentIncomeTypes and see if we can find
		// a match for AccountingLine
		for (T1 dit : incomeTypeDocumentContainer.getIncomeTypes()) {
			if (box.equals(dit.getIncomeType().getIncomeTypeBox())) {
				retval = dit;
				break;
			}
		}

		if (LOG.isDebugEnabled()) {
			if (retval == null) {
				LOG.debug("did not find income type in map");
			} else {
				LOG.debug("found income type " + retval.getIncomeTypeCode() + " in map");
			}
		}

		// if DocumentIncomeType does not exist - create one
		if (retval == null) {
			IncomeType ic = incomeTypeMap.get(box);
			retval = getInitializedDocumentIncomeType();
			retval.setChart(acctline.getChart());
			retval.setChartOfAccountsCode(acctline.getChartOfAccountsCode());
			retval.setIncomeType(ic);
			retval.setIncomeTypeCode(ic.getIncomeTypeCode());
			retval.setLineNumber(getNextIncomeTypeLineNumber());

			// add the new DocumentIncomeType to the list
			incomeTypeDocumentContainer.getIncomeTypes().add(retval);
		}

		return retval;
	}

	// This method is used by the public onAccountingLineAdded method.
	private String get1099Box(VendorHeader vendorHeader, String financialObjectCode) {
		String retval = TaxConstants.NON_REPORTABLE_INCOME_CODE;

		// if we have a vendor and it is not a foreign vendor then
		// we will find the 1099 box if vendorHeader is null then this
		// is probably an employee
		if ((vendorHeader != null) && !vendorHeader.getVendorForeignIndicator().booleanValue()) {
			// if the object code is a 1099 extract code
			if (is1099Reportable(financialObjectCode)) {
				retval = getTaxParameterHelperService().getOverridePaymentType(vendorHeader, overridePaymentTypeCodeMap);
				if (StringUtils.isBlank(retval)) {
					if (StringUtils.isNotBlank(financialObjectCode) && objectCodeMap.containsKey(financialObjectCode)) {
						retval = objectCodeMap.get(financialObjectCode);
					} else {
						// Set payment type to type 7 by default
						retval = TaxConstants.AMOUNT_CODE_7;
					}
				}
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("box: " + retval);
		}

		return retval;
	}

	/**
	 * This method strips the vendor header generated identifier from the input vendorNumber, loads VendorHeader
	 * from the database and returns the VendorHeader
	 * 
	 * @param vendorNumber
	 * @return
	 */
	public VendorHeader getVendorHeaderFromVendorNumber(String vendorNumber) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("vendorNumber: " + vendorNumber);
		}

		if (StringUtils.isNotBlank(vendorNumber)) {
			return getBusinessObjectService().findBySinglePrimaryKey(VendorHeader.class, getVendorIdFromVendorNumber(vendorNumber));
		} else {
			return null;
		}
	}

	/**
	 * This method parses the input vendorNumber to extract the vendor header id
	 * 
	 * @param vendorNumber
	 * @return
	 */
	private Integer getVendorIdFromVendorNumber(String vendorNumber) {
		Integer retval = Integer.MIN_VALUE;

		if (StringUtils.isNotBlank(vendorNumber)) {
			try {
				int pos = vendorNumber.indexOf("-");

				// if we find a dash then the value that precedes the dash is the vendor id
				if (pos > 0) {
					retval = Integer.valueOf(vendorNumber.substring(0, pos));
				} else {
					retval = Integer.valueOf(vendorNumber);
				}
			}

			catch (Exception ex) {
				LOG.error(ex);
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("vendorNumber: " + vendorNumber + ", retval: " + retval);
		}

		return retval;
	}

	/**
	 * This method used when account line is added to document to update the document.incomeTypes list
	 * 
	 * @param acctline
	 */
	public void onAccountingLineAdded(AccountingLine acctline) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("in onAccountingLineAdded()");
		}

		if ((acctline.getAmount() != null) && !acctline.getAmount().isZero()) {
			VendorHeader vendorHeader = incomeTypeDocumentContainer.getVendorHeader();
			// See if a DocumentIncomeType associated with this 1099 box
			// exists in out map
			T1 dit = getDocumentIncomeType(acctline, get1099Box(vendorHeader, acctline.getFinancialObjectCode()));

			// if we have a DocumentIncomeType then this is a valid 1099 accounting line
			if (dit != null) {
				// add the amount from the current accounting line to the DocumentIncomeType
				dit.setAmount(dit.getAmount().add(acctline.getAmount()));
			}
		}
	}

	/**
	 * This method removes any DocumentIncomeType that has a zero amount
	 */
	public void removeZeroValuedIncomeTypes() {
		Iterator<T1> it = incomeTypeDocumentContainer.getIncomeTypes().iterator();
		while (it.hasNext()) {
			if (it.next().getAmount().isZero()) {
				it.remove();
			}
		}
	}

	/**
	 * This method return true if this account line is 1099 reportable
	 * 
	 * @param acctline
	 * @return
	 */
	public boolean is1099Reportable(String financialObjectCode) {
		boolean retval = false;

		if (isNotAPCreditCardPayment()) {
			if (StringUtils.isNotBlank(financialObjectCode) && extractCodes.contains(financialObjectCode)) {

				VendorHeader vendorHeader = incomeTypeDocumentContainer.getVendorHeader();

				// if no vendorHeader (employee) or vendor is foreign then not reportable
				retval = ((vendorHeader != null) && !vendorHeader.getVendorForeignIndicator());
			}
		}

		return retval;
	}

	private boolean isNotAPCreditCardPayment() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("payment method code: " + incomeTypeDocumentContainer.getPaymentMethodCode());
		}

		return !TaxConstants.PAYMENT_METHOD_AP_CREDIT_CARD.equalsIgnoreCase(incomeTypeDocumentContainer.getPaymentMethodCode());
	}

	public String getYearFromTimestamp(java.sql.Timestamp ts) {
		if (ts == null) {
			return "";
		} else {
			return getYearFromDate(new java.util.Date(ts.getTime()));
		}
	}

	public String getYearFromDate(java.util.Date dt) {
		String retval = "";

		if (dt != null) {
			retval = YEAR_FORMAT.format(dt);
		}

		return retval;
	}
}
