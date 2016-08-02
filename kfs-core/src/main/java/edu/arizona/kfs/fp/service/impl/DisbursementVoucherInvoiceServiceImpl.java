package edu.arizona.kfs.fp.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;

import edu.arizona.kfs.fp.service.DisbursementVoucherInvoiceService;
import edu.arizona.kfs.sys.KFSPropertyConstants;

public class DisbursementVoucherInvoiceServiceImpl implements DisbursementVoucherInvoiceService {

    private static transient volatile DataDictionaryService dataDictionaryService;
    private static transient volatile BusinessObjectService businessObjectService;

    private static DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        }
        return dataDictionaryService;
    }

    private static BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }

    @Override
    public List<String> findDisbursementVouchersWithInvoiceNumber(String payeeId, String payeeType, String invoiceNumber) {
        if (StringUtils.isBlank(invoiceNumber)){
            return new ArrayList<String>();
        }
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.DV_PAYEE_DETAIL_PAYEE_ID_NUMBER, payeeId);
        fieldValues.put(KFSPropertyConstants.DV_PAYEE_DETAIL_PAYEE_TYPE_CODE, payeeType);
        fieldValues.put(KFSPropertyConstants.UPPER_SOURCE_ACCOUNTING_LINES_EXTENSION_INVOICE_NUMBER, invoiceNumber.toUpperCase());

        Class<? extends Document> dvClass = getDataDictionaryService().getDocumentClassByTypeName(DisbursementVoucherConstants.DOCUMENT_TYPE_CODE);
        Collection<? extends Document> dvs = getBusinessObjectService().findMatchingOrderBy(dvClass, fieldValues, KFSPropertyConstants.DOCUMENT_NUMBER, true);
        ArrayList<String> results = new ArrayList<String>();

        for (Document dvd : dvs) {
            if (!results.contains(dvd.getDocumentNumber())) {
                results.add(dvd.getDocumentNumber());
            }
        }
        return results;
    }

}
