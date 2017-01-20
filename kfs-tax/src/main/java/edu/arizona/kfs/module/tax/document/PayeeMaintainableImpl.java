package edu.arizona.kfs.module.tax.document;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.module.tax.TaxPropertyConstants;
import edu.arizona.kfs.module.tax.businessobject.Payee;
import edu.arizona.kfs.vnd.VendorConstants;

@SuppressWarnings({ "unused", "deprecation" })
public class PayeeMaintainableImpl extends FinancialSystemMaintainable {
    private static final long serialVersionUID = -6848410590752361292L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PayeeMaintainableImpl.class);

    public static final String VENDOR_NUMBER_SEARCH_FIELD_NAME = "document.newMaintainableObject.vendorNumber";

    private transient VendorService vendorService = SpringContext.getBean(VendorService.class);

    public void refresh(String refreshCaller, Map<String, String> fieldValues, MaintenanceDocument document) {
        if (StringUtils.equals(refreshCaller, VendorConstants.VENDOR_LOOKUPABLE_IMPL) && fieldValues.containsKey(VENDOR_NUMBER_SEARCH_FIELD_NAME)) {
            Payee payee = (Payee) this.getBusinessObject();
        }
    }

    protected void populateVendorFields(Payee payee) {
        VendorDetail vendor = vendorService.getByVendorNumber(payee.getVendorNumber());

        if (ObjectUtils.isNull(vendor)) {
            return;
        }

        VendorHeader vendorHeader = vendor.getVendorHeader();
        if (ObjectUtils.isNull(vendorHeader)) {
            vendor.refreshReferenceObject(TaxPropertyConstants.PayeeFields.VENDOR_HEADER);
            vendorHeader = vendor.getVendorHeader();
        }

        if (ObjectUtils.isNull(vendorHeader)) {
            return;
        }

        payee.setVendorName(vendor.getVendorName());
        payee.setHeaderTaxNumber(vendorHeader.getVendorTaxNumber());
        payee.setHeaderTypeCode(vendorHeader.getVendorTypeCode());
        payee.setHeaderOwnershipCategoryCode(vendorHeader.getVendorOwnershipCategoryCode());
        payee.setHeaderOwnershipCode(vendorHeader.getVendorOwnershipCode());
        VendorAddress vendorAddress = vendorService.getVendorDefaultAddress(vendor.getVendorAddresses(), vendorHeader.getVendorType().getAddressType().getVendorAddressTypeCode(), StringUtils.EMPTY);
        payee.setAddressTypeCode(vendorAddress.getVendorAddressTypeCode());
        payee.setAddressLine1Address(vendorAddress.getVendorLine1Address());
        payee.setAddressLine2Address(vendorAddress.getVendorLine2Address());
        payee.setAddressCityName(vendorAddress.getVendorCityName());
        payee.setAddressStateCode(vendorAddress.getVendorStateCode());
        payee.setAddressCountryCode(vendorAddress.getVendorCountryCode());
        payee.setAddressZipCode(vendorAddress.getVendorZipCode());

        boolean isForeignVendor = vendorService.isVendorForeign(vendor.getVendorHeaderGeneratedIdentifier());
        payee.setVendorForeignIndicator(isForeignVendor);
    }

}
