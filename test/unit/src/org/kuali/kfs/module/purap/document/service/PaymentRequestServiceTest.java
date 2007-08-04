/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.purap.service;

import java.sql.Date;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.test.ConfigureContext;
import org.kuali.test.fixtures.UserNameFixture;

import edu.iu.uis.eden.exception.WorkflowException;

@ConfigureContext(session=UserNameFixture.APPLETON)
public class PaymentRequestServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestServiceTest.class);
    
    private DocumentService documentService;
    // private PaymentRequestDocument document;
    private KualiDecimal defaultMinimumLimit;
    private KualiConfigurationService kualiConfigurationService;
    private NegativePaymentRequestApprovalLimitService npras;
    private PaymentRequestService paymentRequestService;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        if(null == documentService) {
            documentService = SpringServiceLocator.getDocumentService();
        }
        if(null == kualiConfigurationService) {
            kualiConfigurationService = SpringServiceLocator.getKualiConfigurationService();
            String samt = kualiConfigurationService.getApplicationParameterValue(
                    PurapParameterConstants.PURAP_ADMIN_GROUP, 
                    PurapParameterConstants.PURAP_DEFAULT_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT);
            defaultMinimumLimit = new KualiDecimal(samt);
        }
        if(null == npras) {
            npras = SpringServiceLocator.getNegativePaymentRequestApprovalLimitService();
        }
        if(null == paymentRequestService) {
            paymentRequestService = SpringServiceLocator.getPaymentRequestService();
        }
    }
    
    private void cancelDocument(Document document) throws WorkflowException {
        documentService.cancelDocument(document, "testing complete");
    }
    
    private PaymentRequestDocument createBasicDocument() throws WorkflowException {
        PaymentRequestDocument _document = 
            (PaymentRequestDocument) documentService.getNewDocument(
                    PaymentRequestDocument.class);
        _document.setStatusCode(PurapConstants.PaymentRequestStatuses.IN_PROCESS);
        _document.setPurchaseOrderIdentifier(1);
        Date today = SpringServiceLocator.getDateTimeService().getCurrentSqlDate();
        _document.setInvoiceDate(today);
        _document.setPaymentRequestCostSourceCode(PurapConstants.POCostSources.ESTIMATE);
        UniversalUser currentUser = (UniversalUser)GlobalVariables.getUserSession().getUniversalUser();
        _document.setAccountsPayableProcessorIdentifier(currentUser.getPersonUniversalIdentifier());
        
        documentService.saveDocument(_document);
        return _document;
    }
    
    /**
     * Payment requests with a negative payment request approval limit higher
     * than the default limit should be auto-approved.
     * 
     * @throws Exception
     */
    public void testAutoApprovePaymentRequests_defaultLimit() throws Exception {
        
        // (laran) This is just sample code that doesn't really do anything.
        // I was trying to come up with a good testing strategy. This code
        // probably shouldn't be used for real tests. But it may be helpful to
        // look at.
        /*
        Collection<NegativePaymentRequestApprovalLimit> notOverriddenByChart = new HashSet<NegativePaymentRequestApprovalLimit>();
        Collection<NegativePaymentRequestApprovalLimit> limits = npras.findAboveLimit(defaultMinimumLimit);
        for(NegativePaymentRequestApprovalLimit limit : limits) {
            LOG.info("Creating PayReq for limit.");
            PaymentRequestDocument document = createBasicDocument();
            PurchasingApItem item = new PaymentRequestItem();
            item.setItemQuantity(new KualiDecimal(1));
            // Set the total for this document below the default minimum.
            // 
            item.setItemUnitPrice(new BigDecimal(defaultMinimumLimit.intValue() - 1));
            document.addItem(item);
            String preSaveStatusCode = document.getStatusCode();
            paymentRequestService.autoApprovePaymentRequests();
            // Status should have changed if document was approved.
            LOG.info("Pre-save status code is " + preSaveStatusCode + ". Post-save status code is " + document.getStatusCode());
            assertNotSame(preSaveStatusCode, document.getStatusCode());
            cancelDocument(document);
        }
        */
    }

    /**
     * When the chart negative payment request approval limit is lower than the 
     * default limit, and lower than the limit according to (chart + account)
     * and the limit according to (chart and org) the payreq should be auto-approved.
     * 
     * @throws Exception
     */
    public void testAutoApprovePaymentRequests_chartLimit() throws Exception {}
    
    /**
     * When the (chart+account) negative payment request approval limit is lower than the 
     * default limit, and lower than the limit according to chart and the limit 
     * according to (chart and org) the payreq should be auto-approved.
     * 
     * @throws Exception
     */
    public void testAutoApprovePaymentRequests_chartAndAccountLimit() throws Exception {}
    
    public void testAutoApprovePaymentRequests_chartAndOrganizationLimit() throws Exception {}
    
    public void testAutoApprovePaymentRequests_held() throws Exception {}
    
    public void testAutoApprovePaymentRequests_cancelled() throws Exception {}
    
    public void testAutoApprovePaymentRequests_futurePayDate() throws Exception {}

}
