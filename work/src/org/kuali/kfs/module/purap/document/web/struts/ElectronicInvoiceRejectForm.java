/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.web.struts;

import java.util.List;
import java.util.Properties;

import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PurchasingPaymentDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Struts Action Form for Electronic Invoice Reject document.
 */
public class ElectronicInvoiceRejectForm extends FinancialSystemTransactionalDocumentFormBase {

    /**
     * Constructs a PaymentRequestForm instance and sets up the appropriately casted document.
     */
    public ElectronicInvoiceRejectForm() {
        super();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "EIRT";
    }
    
    public ElectronicInvoiceRejectDocument getElectronicInvoiceRejectDocument() {
        return (ElectronicInvoiceRejectDocument) getDocument();
    }

    public void setElectronicInvoiceRejectDocument(ElectronicInvoiceRejectDocument eirDocument) {
        setDocument(eirDocument);
    }

    /**
     * Build additional electronic invoice specific buttons and set extraButtons list.
     * 
     * @return - list of extra buttons to be displayed to the user
     * 
     * KRAD Conversion: Performs customization of an extra button.
     * 
     * No data dictionary is involved.
     */
    
    @Override
    public List<ExtraButton> getExtraButtons() {
        // clear out the extra buttons array
        extraButtons.clear();

        ElectronicInvoiceRejectDocument eirDoc = this.getElectronicInvoiceRejectDocument();

        String externalImageURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY);
        String appExternalImageURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);

        if (eirDoc.getDocumentHeader().getWorkflowDocument().isEnroute()){
          if (eirDoc.isInvoiceResearchIndicator()) {
              addExtraButton("methodToCall.completeResearch", appExternalImageURL + "buttonsmall_complresearch.gif", "Complete Research");
          } else {
              addExtraButton("methodToCall.startResearch", appExternalImageURL + "buttonsmall_research.gif", "Research");
          }
        }

        return extraButtons;
    }

    /**
     * Adds a new button to the extra buttons collection.
     * 
     * @param property - property for button
     * @param source - location of image
     * @param altText - alternate text for button if images don't appear
     * 
     * KRAD Conversion: Performs customization of an extra button.
     * 
     */
    protected void addExtraButton(String property, String source, String altText) {

        ExtraButton newButton = new ExtraButton();

        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);

        extraButtons.add(newButton);
    }
    
    /**
     * This method builds the url for the disbursement info on the purap documents.
     * @return the disbursement info url
     */
    public String getDisbursementInfoUrl() {
        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        String orgCode = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_ORG_CODE);
        String subUnitCode = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_SUB_UNIT_CODE);

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + "/" + KFSConstants.MAPPING_PORTAL + ".do");
        parameters.put(KRADConstants.DOC_FORM_KEY, "88888888");
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PurchasingPaymentDetail.class.getName());
        parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        parameters.put(KFSConstants.SUPPRESS_ACTIONS, "false");
        parameters.put(PdpPropertyConstants.PaymentDetail.PAYMENT_UNIT_CODE, orgCode);
        parameters.put(PdpPropertyConstants.PaymentDetail.PAYMENT_SUBUNIT_CODE, subUnitCode);

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + KFSConstants.LOOKUP_ACTION, parameters);

        return lookupUrl;
    }

}
