package edu.arizona.kfs.module.tax.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.module.tax.TaxConstants;
import edu.arizona.kfs.module.tax.TaxPropertyConstants;
import edu.arizona.kfs.module.tax.businessobject.Payee;
import edu.arizona.kfs.module.tax.businessobject.Payment;
import edu.arizona.kfs.module.tax.dataaccess.TaxReportingDao;
import edu.arizona.kfs.module.tax.service.TaxParameterHelperService;
import edu.arizona.kfs.module.tax.service.TaxPayeeService;

public class TaxPayeeServiceImpl implements TaxPayeeService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TaxPayeeService.class);

    private TaxParameterHelperService taxParameterHelperService;
    private BusinessObjectService businessObjectService;
    private TaxReportingDao taxReportingDao;
    private Map<String, String> form1099BoxInformation;

    // Spring Injectors

    public void setTaxParameterHelperService(TaxParameterHelperService taxParameterHelperService) {
        this.taxParameterHelperService = taxParameterHelperService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setTaxReportingDao(TaxReportingDao taxReportingDao) {
        this.taxReportingDao = taxReportingDao;
    }

    public void setForm1099BoxInformation(Map<String, String> form1099BoxInformation) {
        this.form1099BoxInformation = form1099BoxInformation;
    }

    // Public Service Methods

    @Override
    public Payee createNewPayee(Integer taxYear, VendorDetail vendor) {
        Payee retval = new Payee();

        VendorAddress defaultAddress = getVendorDefaultAddress(vendor);

        if (defaultAddress != null) {
            setPayeeAddress(defaultAddress, retval);
        } else {
            retval.setAddressTypeCode(TaxConstants.TAX_ADDRESS_TYPE_CD);
            LOG.warn("No address found for " + vendor.getVendorName());
        }

        setPayeeAttributes(taxYear, vendor, retval);

        if (LOG.isInfoEnabled()) {
            LOG.info("Payee Created : " + retval.getVendorName());
        }

        return retval;
    }

    @Override
    public List<Payee> searchPayees(String vendorName, String headerTaxNumber, String vendorNumber, Integer taxYear) {
        List<Payee> payees = taxReportingDao.getPayees(vendorName, headerTaxNumber, vendorNumber, taxYear);
        // remove payees that do not meet tax threshhold
        List<Payee> retval = removePayeesBelowThreshold(payees, taxYear);

        // sort by vendor number
        Collections.sort(retval, new Comparator<Payee>() {
            @Override
            public int compare(Payee p1, Payee p2) {
                return p1.getVendorNumber().compareTo(p2.getVendorNumber());
            }
        });

        return retval;
    }

    @Override
    public KualiDecimal getPayeeTaxAmount(Payee payee, String typeCode, Integer taxYear) {
        KualiDecimal retval = KualiDecimal.ZERO;

        KualiDecimal amount = getPayeeTaxAmountByTypeCode(payee, typeCode, taxYear);
        double taxThreshold = taxParameterHelperService.getIncomeThreshold();
        Map<String, KualiDecimal> taxAmountByPaymentType = taxParameterHelperService.getTaxAmountByPaymentType();
        // if this type threshold is setup in parameters
        if (taxAmountByPaymentType.containsKey(typeCode)) {
            if (amount.isGreaterEqual(taxAmountByPaymentType.get(typeCode))) {
                retval = amount;
            }
        } else if (amount.doubleValue() >= taxThreshold) {
            retval = amount;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("payee: " + payee.getVendorNumber() + ", typeCode: " + typeCode + ", taxAmount: " + retval.toString() + ", amountFound: " + amount);
        }

        return retval;
    }

    @Override
    public List<Payee> loadPayees(Integer year) {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(TaxPropertyConstants.PayeeFields.TAX_YEAR, year);
        criteria.put(TaxPropertyConstants.PayeeFields.EXCLUDE_INDICATOR, Boolean.FALSE);
        List<Payee> payeeList = (List<Payee>) businessObjectService.findMatching(Payee.class, criteria);
        List<Payee> retval = loadPayee1099Information(year, payeeList);
        return retval;
    }

    @Override
    public Map<String, String> getForm1099Boxes() {
        return form1099BoxInformation;
    }

    // Internal Service Methods

    private VendorAddress getVendorDefaultAddress(VendorDetail vendor) {
        VendorAddress retval = null;
        // make sure we have some addresses
        if ((vendor.getVendorAddresses() != null) && !vendor.getVendorAddresses().isEmpty()) {

            // look for tax first
            retval = findAddressByType(vendor.getVendorAddresses(), TaxConstants.AddressTypes.TAX);
            LOG.debug("found TX address for vendor " + vendor.getVendorNumber() + ".");
            // then look for remit
            if (retval == null) {
                retval = findAddressByType(vendor.getVendorAddresses(), TaxConstants.AddressTypes.REMIT);
                LOG.debug("found RM address for vendor " + vendor.getVendorNumber() + ".");
            }
            // then po
            if (retval == null) {
                retval = findAddressByType(vendor.getVendorAddresses(), TaxConstants.AddressTypes.PURCHASE_ORDER);
                LOG.debug("found PO address for vendor " + vendor.getVendorNumber() + ".");
            }
            // if we cannot find one of the desired addresses take the first
            if (retval == null) {
                retval = vendor.getVendorAddresses().get(0);
                LOG.debug("first address for vendor " + vendor.getVendorNumber() + "was used.");
            }
        }
        return retval;
    }

    private VendorAddress findAddressByType(List<VendorAddress> addresses, String type) {
        List<VendorAddress> validAddresses = new ArrayList<VendorAddress>();
        // load all the addresses for this type into a list
        for (VendorAddress address : addresses) {
            if (type.equals(address.getVendorAddressTypeCode())) {
                validAddresses.add(address);
            }
        }
        if (!validAddresses.isEmpty()) {
            // if an address is marked as default, use it.
            for (VendorAddress address : validAddresses) {
                if (address.isVendorDefaultAddressIndicator()) {
                    return address;
                }
            }
            // if not, then return the first address.
            return validAddresses.get(0);
        }
        // there were no addresses available.
        return null;
    }

    private void setPayeeAddress(VendorAddress defaultAddress, Payee payee) {
        payee.setAddressCityName(defaultAddress.getVendorCityName());
        payee.setAddressCountryCode(defaultAddress.getVendorCountryCode());
        if (StringUtils.isNotBlank(defaultAddress.getVendorLine1Address()) && (defaultAddress.getVendorLine1Address().length() > TaxConstants.VENDOR_MAX_ADDRESS_LENGTH)) {
            payee.setAddressLine1Address(defaultAddress.getVendorLine1Address().substring(0, TaxConstants.VENDOR_MAX_ADDRESS_LENGTH));
            LOG.info("Truncated address line 1 of payee : " + payee.getVendorName() + ".");
        } else {
            payee.setAddressLine1Address(defaultAddress.getVendorLine1Address());
        }
        if (StringUtils.isNotBlank(defaultAddress.getVendorLine2Address()) && (defaultAddress.getVendorLine2Address().length() > TaxConstants.VENDOR_MAX_ADDRESS_LENGTH)) {
            payee.setAddressLine2Address(defaultAddress.getVendorLine2Address().substring(0, TaxConstants.VENDOR_MAX_ADDRESS_LENGTH));
            LOG.info("Truncated address line 2 of payee : " + payee.getVendorName() + ".");
        } else {
            payee.setAddressLine2Address(defaultAddress.getVendorLine2Address());
        }
        payee.setAddressStateCode(defaultAddress.getVendorStateCode());
        payee.setAddressZipCode(StringUtils.remove(defaultAddress.getVendorZipCode(), "-"));
        payee.setAddressTypeCode(defaultAddress.getVendorAddressTypeCode());
    }

    private void setPayeeAttributes(Integer taxYear, VendorDetail vendor, Payee p) {
        p.setVendorHeaderGeneratedIdentifier(vendor.getVendorHeader().getVendorHeaderGeneratedIdentifier());
        p.setVendorDetailAssignedIdentifier(vendor.getVendorDetailAssignedIdentifier());
        p.setVendorName(vendor.getVendorName());
        p.setHeaderOwnershipCategoryCode(vendor.getVendorHeader().getVendorOwnershipCategoryCode());
        p.setHeaderOwnershipCode(vendor.getVendorHeader().getVendorOwnershipCode());
        p.setHeaderTaxNumber(vendor.getVendorHeader().getVendorTaxNumber());
        p.setHeaderTypeCode(vendor.getVendorHeader().getVendorTypeCode());
        p.setTaxYear(taxYear);
        if (vendor.getVendorHeader().getVendorForeignIndicator() == null) {
            p.setVendorForeignIndicator(Boolean.FALSE);
        } else {
            p.setVendorForeignIndicator(vendor.getVendorHeader().getVendorForeignIndicator());
        }
        p.setCorrectionIndicator(Boolean.FALSE);
        p.setExcludeIndicator(Boolean.FALSE);
        p.setVendorFederalWithholdingTaxBeginningDate(vendor.getVendorHeader().getVendorFederalWithholdingTaxBeginningDate());
        p.setVendorFederalWithholdingTaxEndDate(vendor.getVendorHeader().getVendorFederalWithholdingTaxEndDate());
        p.setVendorNumber(vendor.getVendorNumber());
    }

    private List<Payee> removePayeesBelowThreshold(List<Payee> payees, Integer year) {
        List<Payee> retval = new ArrayList<Payee>();
        Map<Integer, Map<String, Double>> totals = new HashMap<Integer, Map<String, Double>>();
        Map<String, KualiDecimal> taxAmountByPaymentType = taxParameterHelperService.getTaxAmountByPaymentType();
        Map<String, Double> payeeTotalsMap;

        // loop over payees and rollup amount to parent
        for (Payee p : payees) {
            payeeTotalsMap = totals.get(p.getVendorHeaderGeneratedIdentifier());

            if (payeeTotalsMap == null) {
                totals.put(p.getVendorHeaderGeneratedIdentifier(), payeeTotalsMap = new HashMap<String, Double>());
            }

            // sum up amounts by different types
            for (Payment payment : (List<Payment>) p.getPayments()) {
                if (!payment.getExcludeIndicator() && year.equals(p.getTaxYear())) {
                    if (payment.getAcctNetAmount().isNonZero()) {
                        Double taxAmount = payeeTotalsMap.get(payment.getPaymentTypeCode());
                        if (taxAmount == null) {
                            payeeTotalsMap.put(payment.getPaymentTypeCode(), taxAmount = Double.valueOf(0));
                        }

                        taxAmount = Double.valueOf(taxAmount.doubleValue() + payment.getAcctNetAmount().doubleValue());
                        payeeTotalsMap.put(payment.getPaymentTypeCode(), taxAmount);
                        totals.put(p.getVendorHeaderGeneratedIdentifier(), payeeTotalsMap);
                    }
                }
            }
        }

        double taxThreshhold = taxParameterHelperService.getIncomeThreshold();

        // loop over payees and add any that meet minimum required amount based on type
        for (Payee p : payees) {
            payeeTotalsMap = totals.get(p.getVendorHeaderGeneratedIdentifier());

            if (payeeTotalsMap != null) {
                for (String paymentType : payeeTotalsMap.keySet()) {
                    Double amount = payeeTotalsMap.get(paymentType);

                    KualiDecimal d = taxAmountByPaymentType.get(paymentType);
                    // if this limit is setup in parameters
                    if ((d != null) && (amount.doubleValue() >= d.doubleValue())) {
                        retval.add(p);
                        break;
                    } else if (amount.doubleValue() >= taxThreshhold) { // default limit
                        retval.add(p);
                        break;
                    }
                }
            }
        }
        return retval;
    }

    @Override
    public Payee getPayee(Integer id) {
        Payee retval = null;
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(TaxPropertyConstants.PayeeFields.PAYEE_ID, id);
        List<Payee> payees = (List<Payee>) businessObjectService.findMatching(Payee.class, fieldValues);
        if ((payees != null) && !payees.isEmpty()) {
            retval = payees.get(0);
        }
        return retval;
    }

    @Override
    public Payee loadPayee(Integer year, Payee payee) {
        Payee retval = null;

        // lets get all the child payees for this parent payee
        Map<String, Object> criteria = new HashMap<String, Object>();

        criteria.put(TaxPropertyConstants.PayeeFields.TAX_YEAR, year);
        criteria.put(TaxPropertyConstants.PayeeFields.EXCLUDE_INDICATOR, Boolean.FALSE);
        criteria.put(TaxPropertyConstants.PayeeFields.VENDOR_HEADER_GENERATED_IDENTIFIER, payee.getVendorHeaderGeneratedIdentifier());
        List<Payee> payees = (List<Payee>) businessObjectService.findMatching(Payee.class, criteria);

        List<Payee> payeeInfo = loadPayee1099Information(year, payees);

        if (!payeeInfo.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                for (Payee p : payeeInfo) {
                    LOG.debug("payee: " + p.getVendorNumber());
                }
            }
            retval = payeeInfo.get(0);
        }

        if (LOG.isDebugEnabled()) {
            if (retval != null) {
                LOG.debug("retval payee: " + retval.getVendorNumber());
            } else {
                LOG.debug("retval payee: is null");
            }
        }

        return retval;
    }

    private List<Payee> loadPayee1099Information(Integer year, List<Payee> payees) {
        List<Payee> retval = new ArrayList<Payee>();

        if ((payees == null) || payees.isEmpty()) {
            LOG.warn("No payee candidates found");
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("found " + payees.size() + " payee candidates");
            }

            Map<Integer, List<Payee>> payeeMap = new HashMap<Integer, List<Payee>>();

            // Create a map of payee lists keyed by vendorHeaderGeneratedIdentifier. Each list will be related child/parent payees
            for (Payee p : payees) {
                List<Payee> plist = payeeMap.get(p.getVendorHeaderGeneratedIdentifier());

                if (plist == null) {
                    payeeMap.put(p.getVendorHeaderGeneratedIdentifier(), plist = new ArrayList<Payee>());
                }

                plist.add(p);
            }

            // loop over the parent/child payee lists, find the parent payee and copy all child payee payments to the parent
            for (List<Payee> plist : payeeMap.values()) {
                if (plist.isEmpty()) {
                    LOG.warn("empty payee list found");
                } else {
                    // find the Payee record for the vendor parent. If parent has no Payee record create one
                    Payee parentPayee = getParentPayee(year, plist);

                    if (parentPayee != null) {
                        // copy all child payee payments to the parent
                        KualiDecimal saveOriginalParentTax = null;

                        if (LOG.isDebugEnabled()) {
                            // save this for debug information
                            saveOriginalParentTax = parentPayee.getTaxAmount(year);
                            LOG.debug("original parent tax: " + saveOriginalParentTax);
                        }

                        summarizePayeeTax(parentPayee, plist);

                        // check to see if this payee meets tax threshold for a 1099
                        if (isAboveTaxThreshold(parentPayee, year)) {
                            retval.add(parentPayee);

                            if (LOG.isDebugEnabled()) {
                                LOG.debug("payee " + parentPayee.getVendorNumber() + " meets tax threshhold");
                                if (!plist.isEmpty()) {
                                    LOG.debug("*** tax totals ***");
                                    for (Payee p : plist) {
                                        LOG.debug("child payee: " + p.getVendorNumber() + ", tax=" + p.getTaxAmount(year));
                                    }
                                    LOG.debug("parent payee: " + parentPayee.getVendorNumber() + ", original tax=" + saveOriginalParentTax + ", summarized tax=" + parentPayee.getTaxAmount(year));
                                }
                            }
                        }
                    }
                }
            }
        }

        return retval;
    }

    private Payee getParentPayee(Integer year, List<Payee> plist) {
        Payee retval = null;

        if ((plist != null) && !plist.isEmpty()) {
            // try to find the parent Payee(vendor) in the list
            for (Payee p : plist) {
                if (p.getVendorDetail().isVendorParentIndicator()) {
                    retval = p;
                    break;
                }
            }

            // if we did not find the parent we will attempt to load from the database
            if (retval == null) {
                // get the first payee and use this vendor identifier
                Payee p = plist.get(0);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("did not find parent payee (" + p.getVendorHeaderGeneratedIdentifier() + ") in list so will attempt to load from db");
                }

                Map<String, Object> criteria = new HashMap<String, Object>();
                criteria.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, p.getVendorDetail().getVendorHeaderGeneratedIdentifier());
                criteria.put(VendorPropertyConstants.VENDOR_DETAIL + "." + VendorPropertyConstants.VENDOR_PARENT_INDICATOR, true);
                criteria.put(TaxPropertyConstants.PayeeFields.TAX_YEAR, year);

                Collection<Payee> payees = businessObjectService.findMatching(Payee.class, criteria);
                // if we found some payees take the first one (should only be one)
                if ((payees != null) && !payees.isEmpty()) {
                    retval = payees.iterator().next();
                }

                // if we did not find the parent payee create one
                if (retval == null) {
                    retval = createParentPayeeFromChild(year, p);
                }

                if (retval == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("failed to load parent payee for (" + p.getVendorNumber() + ") 1099 will not be created");
                    }
                }
            }
        }

        return retval;
    }

    private void summarizePayeeTax(Payee parentPayee, List<Payee> plist) {
        // remove parentPayee from list if required so we only have a list of child payees
        Iterator<Payee> it = plist.iterator();
        while (it.hasNext()) {
            Payee p = it.next();
            if ((parentPayee == p) || parentPayee.getVendorNumber().equals(p.getVendorNumber())) {
                it.remove();
                break;
            }
        }

        if (LOG.isDebugEnabled()) {
            if (plist.size() > 0) {
                LOG.debug("found " + plist.size() + " child payees for parent payee(" + parentPayee.getVendorHeaderGeneratedIdentifier() + ")");
            }
        }

        // check to make sure we have a valid list
        if (parentPayee.getPayments() == null) {
            parentPayee.setPayments(new ArrayList<Payment>());
        }

        // add all the payments from the child records to the parentPayee record
        for (Payee p : plist) {
            if ((p.getPayments() != null) && !p.getPayments().isEmpty()) {
                parentPayee.getPayments().addAll(p.getPayments());
            }
        }
    }

    private boolean isAboveTaxThreshold(Payee payee, Integer year) {
        for (String box : form1099BoxInformation.keySet()) {
            KualiDecimal amount = getPayeeTaxAmount(payee, box, year);
            if (amount.isPositive()) {
                return true;
            }
        }
        return false;
    }

    private Payee createParentPayeeFromChild(Integer year, Payee childPayee) {
        Payee retval = new Payee();

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, childPayee.getVendorHeaderGeneratedIdentifier());
        criteria.put(VendorPropertyConstants.VENDOR_PARENT_INDICATOR, true);

        VendorDetail vendor = (VendorDetail) businessObjectService.findByPrimaryKey(VendorDetail.class, criteria);

        if (vendor != null) {
            VendorAddress defaultAddress = getVendorDefaultAddress(vendor);

            if (defaultAddress != null) {
                setPayeeAddress(defaultAddress, retval);
            } else {
                retval.setAddressTypeCode(TaxConstants.TAX_ADDRESS_TYPE_CD);
                LOG.info("No address found for " + vendor.getVendorName());
            }

            setPayeeAttributes(year, vendor, retval);

            // Save parent payee
            businessObjectService.save(retval);

            if (LOG.isDebugEnabled()) {
                LOG.debug("created parent payee " + retval.getVendorNumber());
            }
        } else {
            LOG.error("failed to load parent vendor for child payee " + childPayee.getVendorNumber());
        }

        return retval;

    }

    private KualiDecimal getPayeeTaxAmountByTypeCode(Payee payee, String typeCode, Integer taxYear) {
        KualiDecimal retval = KualiDecimal.ZERO;

        if ((payee.getPayments() != null) && !payee.getPayments().isEmpty()) {
            if (StringUtils.isNotBlank(typeCode)) {
                for (Payment payment : (List<Payment>) payee.getPayments()) {
                    if (!payment.getExcludeIndicator() && typeCode.equals(payment.getPaymentTypeCode()) && taxYear.equals(payment.getPayee().getTaxYear())) {
                        retval = retval.add(payment.getAcctNetAmount());
                    }
                }
            }
        }

        return retval;
    }

}
