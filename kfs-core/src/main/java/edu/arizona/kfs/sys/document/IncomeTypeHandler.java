package edu.arizona.kfs.sys.document;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;
import edu.arizona.kfs.sys.businessobject.DocumentIncomeType;
import edu.arizona.kfs.sys.businessobject.IncomeType;
import edu.arizona.kfs.sys.service.IncomeTypeHandlerService;

/**
 * This class acts as the base handler for 1099 Income Type processing. The class is abstract - to create a
 * new instance you must pass the appropriate type names for the generic parameters <T1, T2> where
 * T1 is an implementation of DocumentIncomeType
 * T2 is the java type of the documentIdentifier (String or Integer)
 */
public class IncomeTypeHandler<T1 extends DocumentIncomeType<T2>, T2> {

    protected static final Logger LOG = Logger.getLogger(IncomeTypeHandler.class);

    private BusinessObjectService businessObjectService;
    private IncomeTypeHandlerService incomeTypeHandlerService;
    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");

    private Class<T1> documentIncomeTypeClass;
    private T1 newDocumentIncomeType;
    protected IncomeTypeContainer<T1, T2> incomeTypeDocumentContainer;
    private CurrencyFormatter currencyFormatter = new CurrencyFormatter();

    private List<String> extractCodes;
    private Map<String, IncomeType> incomeTypeMap;

    public IncomeTypeHandler(IncomeTypeContainer<T1, T2> incomeTypeDocumentContainer, Class<T1> documentIncomeTypeClass) {
        this.businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        this.incomeTypeHandlerService = SpringContext.getBean(IncomeTypeHandlerService.class);

        this.documentIncomeTypeClass = documentIncomeTypeClass;
        this.incomeTypeDocumentContainer = incomeTypeDocumentContainer;
        this.extractCodes = incomeTypeHandlerService.getExtractCodes();
        this.incomeTypeMap = incomeTypeHandlerService.getIncomeTypeMap();

        if (LOG.isDebugEnabled()) {
            LOG.debug("in IncomeTypeHandler()");
            LOG.debug("IncomeTypeDocumentContainer: " + incomeTypeDocumentContainer.getClass().getName());
        }
    }

    /**
     * Add the new DocumentIncomeType to the document.incomeTypes list
     */
    public void addNewIncomeType() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("in addNewIncomeType()");
        }

        if (newDocumentIncomeType != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("IncomeType: " + newDocumentIncomeType.getClass().getName());
            }

            // if income type already in list then don't add
            if (incomeTypeExists()) {
                newDocumentIncomeType = null;
                // check that required fields have data, if valid we will
                // add to the DocumentIncomeType list on the document
            } else if (validateIncomeType()) {
                // populate the DocumentIncomeType with the selected IncomeType object pulled from the db
                newDocumentIncomeType.setIncomeType(businessObjectService.findBySinglePrimaryKey(IncomeType.class, newDocumentIncomeType.getIncomeTypeCode()));

                // if the line number has not been set then set it
                if ((newDocumentIncomeType.getLineNumber() == null) || (newDocumentIncomeType.getLineNumber().intValue() == 0)) {
                    newDocumentIncomeType.setLineNumber(getNextIncomeTypeLineNumber());
                }

                incomeTypeDocumentContainer.getIncomeTypes().add(newDocumentIncomeType);

                // once added to list clear the holding variable
                newDocumentIncomeType = null;
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
        for (T1 documentIncomeType : incomeTypeDocumentContainer.getIncomeTypes()) {
            if (documentIncomeType.getLineNumber().intValue() > retval) {
                retval = documentIncomeType.getLineNumber().intValue();
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
        for (T1 documentIncomeType : incomeTypeDocumentContainer.getIncomeTypes()) {
            retval = retval.add(documentIncomeType.getAmount());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("totalIncomeTypeAmount: " + retval.toString());
        }
        return retval;
    }

    protected boolean incomeTypeExists() {
        boolean retval = false;

        if (StringUtils.isNotBlank(newDocumentIncomeType.getChartOfAccountsCode()) && StringUtils.isNotBlank(newDocumentIncomeType.getIncomeTypeCode())) {
            for (T1 documentIncomeType : incomeTypeDocumentContainer.getIncomeTypes()) {
                if (documentIncomeType.getChartOfAccountsCode().equals(newDocumentIncomeType.getChartOfAccountsCode()) && documentIncomeType.getIncomeTypeCode().equals(newDocumentIncomeType.getIncomeTypeCode())) {
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
        if (StringUtils.isBlank(newDocumentIncomeType.getChartOfAccountsCode())) {
            errors.putError(KFSPropertyConstants.IncomeTypeFields.NEW_INCOME_TYPE_ERROR1, KFSKeyConstants.ERROR_INCOME_TYPE_COA_REQUIRED);
            retval = false;
        }

        // make sure income type is selected
        if (StringUtils.isBlank(newDocumentIncomeType.getIncomeTypeCode())) {
            errors.putError(KFSPropertyConstants.IncomeTypeFields.NEW_INCOME_TYPE_ERROR2, KFSKeyConstants.ERROR_INCOME_TYPE_CODE_REQUIRED);
            retval = false;
        }

        // make sure we have an amount entered
        if (newDocumentIncomeType.getAmount() == null) {
            errors.putError(KFSPropertyConstants.IncomeTypeFields.NEW_INCOME_TYPE_ERROR3, KFSKeyConstants.ERROR_INCOME_TYPE_AMOUNT_REQUIRED);
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
        if (newDocumentIncomeType == null) {
            newDocumentIncomeType = getInitializedDocumentIncomeType();
        }

        return newDocumentIncomeType;
    }

    protected T1 getInitializedDocumentIncomeType() {
        T1 retval = null;

        try {
            // use the class passed in the constructor to create a new instance
            retval = documentIncomeTypeClass.newInstance();

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
        this.newDocumentIncomeType = newIncomeType;
    }

    /**
     * This method populates the 1099 IncomeTypes from the document accounting lines
     * 
     * @param accountingLines
     * @param useTaxItems
     */
    public void populateIncomeTypes(List<? extends AccountingLine> accountingLines) {
        if (isEditableRouteStatus()) {
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
    }

    protected String get1099Box(VendorHeader vendorHeader, AccountingLine acctline) {
        Map<String, String> objectCodeMap = incomeTypeHandlerService.getObjectCodeMap();
        String retval = KFSConstants.IncomeTypeConstants.DEFAULT_NON_REPORTABLE_BOX;

        // if we have a vendor and it is not a foreign vendor then
        // we will find the 1099 box if vendorHeader is null then this
        // is probably an employee
        if ((vendorHeader != null) && !vendorHeader.getVendorForeignIndicator().booleanValue()) {
            // if the object code is a 1099 extract code
            String financialObjectCode = acctline.getFinancialObjectCode();
            boolean isReportable = is1099Reportable(financialObjectCode);
            if (isReportable) {
                retval = incomeTypeHandlerService.getOverridePaymentType(vendorHeader);
                if (StringUtils.isBlank(retval)) {
                    if (StringUtils.isNotBlank(financialObjectCode) && objectCodeMap.containsKey(financialObjectCode)) {
                        retval = objectCodeMap.get(financialObjectCode);
                    } else {
                        // Set payment type to type 7 by default
                        retval = KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_7;
                    }
                }
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
    protected void zeroOutDocumentIncomeTypes() {
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
    protected T1 getDocumentIncomeType(AccountingLine acctline, String box) {
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
            IncomeType incomeType = incomeTypeMap.get(box);
            retval = getInitializedDocumentIncomeType();
            retval.setChart(acctline.getChart());
            retval.setChartOfAccountsCode(acctline.getChartOfAccountsCode());
            retval.setIncomeType(incomeType);
            retval.setIncomeTypeCode(incomeType.getIncomeTypeCode());
            retval.setLineNumber(getNextIncomeTypeLineNumber());

            // add the new DocumentIncomeType to the list
            incomeTypeDocumentContainer.getIncomeTypes().add(retval);
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
            VendorHeader vendorHeader = businessObjectService.findBySinglePrimaryKey(VendorHeader.class, getVendorIdFromVendorNumber(vendorNumber));
            return vendorHeader;
        }
        return null;
    }

    /**
     * This method parses the input vendorNumber to extract the vendor header id
     * 
     * @param vendorNumber
     * @return
     */
    protected Integer getVendorIdFromVendorNumber(String vendorNumber) {
        Integer retval = Integer.MIN_VALUE;

        if (StringUtils.isNotBlank(vendorNumber)) {
            try {
                int pos = vendorNumber.indexOf(KFSConstants.DASH);

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
            String box = get1099Box(vendorHeader, acctline);
            T1 dit = getDocumentIncomeType(acctline, box);

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

    protected boolean isNotAPCreditCardPayment() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("payment method code: " + incomeTypeDocumentContainer.getPaymentMethodCode());
        }

        return !KFSConstants.IncomeTypeConstants.PAYMENT_METHOD_AP_CREDIT_CARD.equalsIgnoreCase(incomeTypeDocumentContainer.getPaymentMethodCode());
    }

    public String getYearFromTimestamp(java.sql.Timestamp ts) {
        if (ts == null) {
            return KFSConstants.EMPTY_STRING;
        } else {
            return getYearFromDate(new java.util.Date(ts.getTime()));
        }
    }

    public String getYearFromDate(java.util.Date dt) {
        String retval = KFSConstants.EMPTY_STRING;

        if (dt != null) {
            retval = YEAR_FORMAT.format(dt);
        }

        return retval;
    }

}
