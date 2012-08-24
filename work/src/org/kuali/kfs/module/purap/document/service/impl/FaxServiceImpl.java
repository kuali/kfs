/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.FaxService;
import org.kuali.kfs.module.purap.exception.FaxServerUnavailableError;
import org.kuali.kfs.module.purap.exception.FaxSubmissionError;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderParameters;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderPdf;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderQuotePdf;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderTransmitParameters;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryService;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class FaxServiceImpl implements FaxService {

    private static final Logger LOG = Logger.getLogger(FaxServiceImpl.class);

    protected ConfigurationService kualiConfigurationService;
    protected ParameterService parameterService;
    protected VendorService vendorService;
    protected BusinessObjectService businessObjectService;
    protected CountryService countryService;


    /**
     * Create the Purchase Order Pdf document and send it via fax to the recipient in the PO
     *
     * @param po PurchaseOrder that holds the Quote
     * @param isRetransmit if passed true then PO is being retransmitted
     * @return Collection of ServiceError objects
     */
    @Override
    public void faxPurchaseOrderPdf(PurchaseOrderDocument po, boolean isRetransmit) {
        LOG.debug("faxPurchaseOrderPdf(po,reTransmit) started");
        String pdfFileLocation = getPdfFileLocation();
        if (pdfFileLocation == null) {
            throw new RuntimeException("Application Setting PDF_DIRECTORY is missing.");
        }

        String imageTempLocation = kualiConfigurationService.getPropertyValueAsString(KFSConstants.TEMP_DIRECTORY_KEY) + "/";
        if (imageTempLocation == null) {
            throw new RuntimeException("Application Setting TEMP_DIRECTORY_KEY is missing.");
        }

        LOG.debug("faxPurchaseOrderPdf() ended");
        this.faxPurchaseOrderPdf(po, pdfFileLocation, imageTempLocation, isRetransmit);
    }



    /**
     * Create the Purchase Order Pdf document and send it via fax to the recipient in the PO
     *
     * @param po PurchaseOrder that holds the Quote
     * @param isRetransmit if passed true then PO is being retransmitted
     * @return Collection of ServiceError objects
     */
    @Override
    public void faxPurchaseOrderPdf(PurchaseOrderDocument po, String pdfFileLocation, String imageTempLocation, boolean isRetransmit) {
        LOG.debug("faxPurchaseOrderPdf() started with locations");

        // Get the vendor's country name.
        if ( StringUtils.isNotBlank( po.getVendorCountryCode() ) ) {
            Country vendorCountry = countryService.getCountry(po.getVendorCountryCode());
            if (vendorCountry != null) {
                po.setVendorCountryCode(vendorCountry.getCode());
            } else {
                po.setVendorCountryCode("NA");
            }
        } else {
            po.setVendorCountryCode("NA");
        }


        PurchaseOrderParameters purchaseOrderParameters = getPurchaseOrderParameters();
        purchaseOrderParameters.setPurchaseOrderPdfAndFaxParameters(po);

        PurchaseOrderPdf poPdf = null;
        try {
            String environment = kualiConfigurationService.getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);
            poPdf = new PurchaseOrderPdf();
            poPdf.savePdf(po, purchaseOrderParameters , isRetransmit, environment);
        }
        catch (PurError e) {
            GlobalVariables.getMessageMap().putError("errors", "error.blank");
                                                                                                  // only need to call once.
            LOG.debug("faxPurchaseOrderPdf() ended");
        }
        catch (Throwable e) {
            LOG.error("faxPurchaseOrderPdf() Faxing Failed on PDF creation - Exception was " + e.getMessage(), e);
            GlobalVariables.getMessageMap().putError("errors", "error.blank", "Faxing Error.  Unable to save pdf file. Please Contact Purchasing");
                                                                                                  // only need to call once.
            LOG.debug("faxPurchaseOrderPdf() ended");
        }

        PurchaseOrderTransmitParameters transmitParameters =  (PurchaseOrderTransmitParameters)purchaseOrderParameters;
        String[] files = new String[1];
        files[0] = transmitParameters.getPdfFileName();

        try {
            this.faxPDF(files,purchaseOrderParameters);
        }
        catch (FaxSubmissionError e) {
            GlobalVariables.getMessageMap().putError("errors", "error.blank");
        }
        catch (FaxServerUnavailableError e) {
            GlobalVariables.getMessageMap().putError("errors", "error.blank");
        }
        catch (PurError e) {
            GlobalVariables.getMessageMap().putError("errors", "error.blank");
        }
        catch (Throwable e) {
            LOG.error("faxPurchaseOrderPdf() Faxing Failed Exception was " + e.getMessage(), e);
            GlobalVariables.getMessageMap().putError("errors", "error.blank", "Faxing Error.  Please Contact Purchasing");
        }
        finally {
            try {
                if (poPdf != null) {
                    poPdf.deletePdf(pdfFileLocation, transmitParameters.getPdfFileName());
                }
                else {
                    // ignore - PDF can't be deleted if PDF doesn't exist
                }
            }
            catch (Throwable e) {
                LOG.error("faxPurchaseOrderPdf() Error deleting PDF - Exception was " + e.getMessage(), e);
                GlobalVariables.getMessageMap().putError("errors", "error.blank","Your fax was sent successfully but an error occurred that is unrelated to faxing. Please report this problem to Purchasing");
            }
        }

        LOG.debug("faxPurchaseOrderPdf() ended");
    }

    @Override
    public void faxPurchaseOrderQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq) {
        LOG.debug("faxPurchaseOrderQuotePdf() started");


        PurchaseOrderParameters purchaseOrderParameters = getPurchaseOrderParameters();
        purchaseOrderParameters.setPurchaseOrderPdfAndFaxParameters(po,povq);
        String environmentCode = kualiConfigurationService.getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);

        PurchaseOrderQuotePdf poQuotePdf = new PurchaseOrderQuotePdf();
        Collection errors = new ArrayList();


        try {

          // Get the vendor's country name.
          if ( StringUtils.isNotBlank( povq.getVendorCountryCode() ) ) {
              Country vendorCountry = countryService.getCountry(po.getVendorCountryCode());
              if (vendorCountry != null) {
                  povq.setVendorCountryCode(vendorCountry.getCode());
              }
              else {
                  povq.setVendorCountryCode("NA");
              }
          }
          else {
              povq.setVendorCountryCode("NA");
          }

          poQuotePdf.savePOQuotePDF(po, povq, purchaseOrderParameters, environmentCode);
        } catch (PurError e) {
          GlobalVariables.getMessageMap().putError("errors", "error.blank");
          LOG.debug("faxPurchaseOrderQuotePdf() ended");

        } catch (Throwable e) {
          LOG.error("faxPurchaseOrderQuotePdf() Faxing Failed on PDF creation - Exception was " + e.getMessage(), e);
          LOG.error("faxPurchaseOrderQuotePdf() Faxing Failed on PDF creation - Exception was " + e.getMessage(), e);
          GlobalVariables.getMessageMap().putError("errors", "error.blank", "Faxing Error.  Unable to save pdf file. Please Contact Purchasing");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("faxPurchaseOrderQuotePdf() Quote PDF saved successfully for PO " + po.getPurapDocumentIdentifier() + " and Quote ID " + povq.getPurchaseOrderVendorQuoteIdentifier());
        }

        PurchaseOrderTransmitParameters transmitParameters =  (PurchaseOrderTransmitParameters)purchaseOrderParameters;
        String pdfFileLocation = transmitParameters.getPdfFileLocation();
        String pdfFileName     = transmitParameters.getPdfFileName();
        String[] files = new String[1];
        files[0] = pdfFileName;

        try {
          this.faxPDF(files,transmitParameters);
        } catch (FaxSubmissionError e) {
            LOG.error("faxPurchaseOrderQuotePdf() Error faxing Quote PDF" + pdfFileName + " - Exception was " + e.getMessage(), e);
            GlobalVariables.getMessageMap().putError("errors", "error.blank");

        } catch (FaxServerUnavailableError e) {
            LOG.error("faxPurchaseOrderQuotePdf() Error faxing Quote PDF" + pdfFileName + " - Exception was " + e.getMessage(), e);
            GlobalVariables.getMessageMap().putError("errors", "error.blank","The document did not successfully transmit to the fax server. Report this to the Procurement Services Technology group, make note of the document you attempted to transmit and try again when the issue has been resolved");

        } catch (PurError e) {
            LOG.error("faxPurchaseOrderQuotePdf() Error faxing Quote PDF" + pdfFileName + " - Exception was " + e.getMessage(), e);
            GlobalVariables.getMessageMap().putError("errors", "error.blank","The document did not successfully transmit to the fax server. Report this to the Procurement Services Technology group, make note of the document you attempted to transmit and try again when the issue has been resolved");

        } catch (Throwable e) {
            LOG.error("faxPurchaseOrderQuotePdf() Error faxing Quote PDF" + pdfFileName + " - Exception was " + e.getMessage(), e);
            GlobalVariables.getMessageMap().putError("errors", "error.blank","The document did not successfully transmit to the fax server. Report this to the Procurement Services Technology group, make note of the document you attempted to transmit and try again when the issue has been resolved");

        } finally {
          try {
            poQuotePdf.deletePdf(pdfFileLocation, pdfFileName);
          } catch (Throwable e) {
            LOG.error("faxPurchaseOrderQuotePdf() Error deleting Quote PDF" + pdfFileName + " - Exception was " + e.getMessage(), e);
            GlobalVariables.getMessageMap().putError("errors", "error.blank","Your fax was sent successfully but an error occurred that is unrelated to faxing. Please report this problem to Purchasing");

          }
        }


        LOG.debug("faxPurchaseOrderQuotePdf() ended");


    }


    /**
     * Here is where the PDF is actually faxed, needs to be implemented at each institution
     */
    protected void faxPDF(String[] files, PurchaseOrderParameters transmitParameters ) {
        LOG.info("faxPDF() NEEDS TO BE IMPLEMENTED!");
        throw new RuntimeException("faxPDF() NEEDS TO BE IMPLEMENTED!");
    }



    public ConfigurationService getConfigurationService() {
        return kualiConfigurationService;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public VendorService getVendorService() {
        return vendorService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public CountryService getCountryService() {
        return countryService;
    }

    public PurchaseOrderParameters getPurchaseOrderParameters() {
        return SpringContext.getBean(PurchaseOrderParameters.class);
    }

    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    public String getPdfFileLocation() {
        return parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_DIRECTORY);
    }


}
