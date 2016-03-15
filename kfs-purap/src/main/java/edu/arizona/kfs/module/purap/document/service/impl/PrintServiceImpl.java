package edu.arizona.kfs.module.purap.document.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.exception.PurapConfigurationException;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderParameters;
import edu.arizona.kfs.module.purap.pdf.PurchaseOrderPdf;
//import org.kuali.kfs.module.purap.pdf.PurchaseOrderQuoteRequestsPdf;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderTransmitParameters;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PrintServiceImpl extends org.kuali.kfs.module.purap.document.service.impl.PrintServiceImpl {
    private static Log LOG = LogFactory.getLog(PrintServiceImpl.class);

    /**
     * Creates purchase order pdf document given the input parameters.
     * 
     * @param po                     The PurchaseOrderDocument.
     * @param byteArrayOutputStream  ByteArrayOutputStream that the action is using, where the pdf will be printed to.
     * @param isRetransmit           boolean true if this is a retransmit purchase order document.
     * @param environment            The current environment used (e.g. DEV if it is a development environment).
     * @param retransmitItems        The items selected by the user to be retransmitted.
     * @return                       Collection of error strings.
     */
    @Override
     protected Collection generatePurchaseOrderPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream, boolean isRetransmit, String environment, List<PurchaseOrderItem> retransmitItems) {
        LOG.debug("generatePurchaseOrderPdf() started");

        PurchaseOrderPdf poPdf = new PurchaseOrderPdf();
        Collection errors = new ArrayList();
        try {
            PurchaseOrderParameters purchaseOrderParameters = getPurchaseOrderParameters();
            purchaseOrderParameters.setPurchaseOrderPdfParameters(po);
            PurchaseOrderTransmitParameters pdfParameters = (PurchaseOrderTransmitParameters)purchaseOrderParameters;
            poPdf.generatePdf(po, pdfParameters, byteArrayOutputStream, isRetransmit, environment, retransmitItems);
        }
        catch (PurError e) {
            LOG.error("Caught exception ", e);
            errors.add(e.getMessage());
        }
        catch (PurapConfigurationException pce) {
            LOG.error("Caught exception ", pce);
            errors.add(pce.getMessage());
        }

        LOG.debug("generatePurchaseOrderPdf() ended");
        return errors;
    }

    /**
     * Saves the purchase order pdf document.
     * 
     * @param po            The PurchaseOrderDocument.
     * @param isRetransmit  boolean true if this is a retransmit purchase order document.
     * @param environment   The current environment used (e.g. DEV if it is a development environment).
     * @return              Collection of error strings.
     */
    @Override
    protected Collection savePurchaseOrderPdf(PurchaseOrderDocument po, boolean isRetransmit, String environment) {
        LOG.debug("savePurchaseOrderPdf() started");

        PurchaseOrderPdf poPdf = new PurchaseOrderPdf();
        Collection errors = new ArrayList();

        PurchaseOrderTransmitParameters pdfParameters = null;

        try {
            PurchaseOrderParameters purchaseOrderParameters = getPurchaseOrderParameters();
            purchaseOrderParameters.setPurchaseOrderPdfParameters(po);
            pdfParameters = (PurchaseOrderTransmitParameters)purchaseOrderParameters;
            poPdf.savePdf(po, pdfParameters, isRetransmit, environment);
        }
        catch (PurError e) {
            LOG.error("Caught exception ", e);
            errors.add(e.getMessage());
        }
        catch (PurapConfigurationException pce) {
            LOG.error("Caught exception ", pce);
            errors.add(pce.getMessage());
        }
        finally {
            try {
                poPdf.deletePdf(pdfParameters.getPdfFileLocation(), pdfParameters.getPdfFileName());
            }
            catch (Throwable e) {
                LOG.error("savePurchaseOrderPdf() Error deleting PDF" + pdfParameters.getPdfFileName() + " - Exception was " + e.getMessage(), e);
                errors.add("Error while deleting the pdf after savePurchaseOrderPdf" + e.getMessage());
            }
        }

        LOG.debug("savePurchaseOrderPdf() ended");
        return errors;
    }
}