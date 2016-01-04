package edu.arizona.kfs.fp.businessobject;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.sys.KFSPropertyConstants;

public class DisbursementVoucherSourceAccountingLine extends SourceAccountingLine {

    private static final long serialVersionUID = -9073526498671725886L;

    public DisbursementVoucherSourceAccountingLine() {
        super();
        if (ObjectUtils.isNull(extension)) {
            this.getExtension();
        }
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map getValuesMap() {
        Map ret = (HashMap<String, String>) super.getValuesMap();
        ret.put(KFSPropertyConstants.EXTENSION_INVOICE_NUMBER, ((DisbursementVoucherSourceAccountingLineExtension) getExtension()).getInvoiceNumber());
        return ret;
    }

    @Override
    public DisbursementVoucherSourceAccountingLineExtension getExtension() {
        DisbursementVoucherSourceAccountingLineExtension disbursementVoucherSourceAccountingLineExtension = (DisbursementVoucherSourceAccountingLineExtension) super.getExtension();
        if (ObjectUtils.isNotNull(disbursementVoucherSourceAccountingLineExtension)) {
            disbursementVoucherSourceAccountingLineExtension.setDocumentNumber(this.getDocumentNumber());
            disbursementVoucherSourceAccountingLineExtension.setFinancialDocumentLineTypeCode(this.getFinancialDocumentLineTypeCode());
            disbursementVoucherSourceAccountingLineExtension.setSequenceNumber(this.getSequenceNumber());
        }
        return disbursementVoucherSourceAccountingLineExtension;
    }

}
