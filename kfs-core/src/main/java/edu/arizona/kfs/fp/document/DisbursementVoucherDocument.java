package edu.arizona.kfs.fp.document;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;

import edu.arizona.kfs.fp.businessobject.DisbursementVoucherSourceAccountingLineExtension;

public class DisbursementVoucherDocument extends org.kuali.kfs.fp.document.DisbursementVoucherDocument {

    private static final long serialVersionUID = 8820340507728738505L;

    private static Logger LOG = Logger.getLogger(DisbursementVoucherDocument.class);

    @Override
    public void prepareForSave() {
        LOG.debug("DisbursementVoucherDocument.prepareForSave()");
        super.prepareForSave();

        for (Object o : getSourceAccountingLines()) {
            SourceAccountingLine accountingLine = (SourceAccountingLine) o;
            DisbursementVoucherSourceAccountingLineExtension accountingLineExtension = (DisbursementVoucherSourceAccountingLineExtension) accountingLine.getExtension();
            accountingLineExtension.setDocumentNumber(accountingLine.getDocumentNumber());
            accountingLineExtension.setSequenceNumber(accountingLine.getSequenceNumber());
        }
    }

}
