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
package org.kuali.kfs.module.ar.document.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.CustomerFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceDocumentTestUtil {

    public static final String CUSTOMER_MAINT_DOC_NAME = "CUS";

    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceDocumentTestUtil.class);

    /**
     * This method saves a customer BO based on the passed in customer fixture
     *
     * @param customerFixture
     */
    public static void saveNewCustomer(CustomerFixture customerFixture) throws Exception {
        SpringContext.getBean(BusinessObjectService.class).save(customerFixture.createCustomer());
    }

    /**
     * This method saves a customer invoice document BO based on passed in customer invoice document fixture/document detail fixtures
     * @param customerFixture
     * @param customerInvoiceDocumentFixture
     * @param customerInvoiceDocumentFixtures
     */
    public static String saveNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture customerInvoiceDocumentFixture, CustomerInvoiceDetailFixture[] customerInvoiceDocumentFixtures, CustomerFixture customerFixture) throws WorkflowException {

        CustomerInvoiceDocument document = null;
        if( ObjectUtils.isNotNull( customerFixture ) ){
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerFixture, customerInvoiceDocumentFixtures);
        } else {
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerInvoiceDocumentFixtures);
        }

        Document savedDocument = null;
        try {
            SpringContext.getBean(DocumentService.class).saveDocument(document, DocumentSystemSaveEvent.class);
        } catch (Exception e){
            LOG.fatal("The Customer Invoice Document was not routed, and is not available for testing.",e);
            Assert.fail("The Customer Invoice Document was not routed, and is not available for testing." + e.getClass().getName() + " : " + e.getMessage());
        }
        savedDocument = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(document.getDocumentNumber());
        return ObjectUtils.isNotNull(savedDocument)? savedDocument.getDocumentNumber() : null;
    }

    /**
     * This method submits a customer invoice document based on passed in customer fix
     * @param customerFixture
     * @param customerInvoiceDocumentFixture
     * @param customerInvoiceDocumentFixtures
     */
    public static String submitNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture customerInvoiceDocumentFixture, CustomerInvoiceDetailFixture[] customerInvoiceDetailFixtures, CustomerFixture customerFixture) throws WorkflowException {

        CustomerInvoiceDocument document = null;
        if( ObjectUtils.isNotNull( customerFixture ) ){
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerFixture, customerInvoiceDetailFixtures);
        } else {
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerInvoiceDetailFixtures);
        }
        document.getDocumentHeader().setDocumentDescription("CREATING TEST CUSTOMER INVOICE DOCUMENT");

        Document routedDocument = null;
        try {
            SpringContext.getBean(DocumentService.class).routeDocument(document, "TESTING", new ArrayList());
        } catch (Exception e){
            LOG.fatal("The Customer Invoice Document was not routed, and is not available for testing.",e);
            Assert.fail("The Customer Invoice Document was not routed, and is not available for testing." + e.getClass().getName() + " : " + e.getMessage());
        }
        routedDocument = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(document.getDocumentNumber());
        return ObjectUtils.isNotNull(routedDocument)? routedDocument.getDocumentNumber() : null;
    }

    /**
     * This method submits a customer invoice document based on passed in customer fix
     * @param customerFixture
     * @param customerInvoiceDocumentFixture
     * @param customerInvoiceDocumentFixtures
     */
    public static CustomerInvoiceDocument submitNewCustomerInvoiceDocumentAndReturnIt(CustomerInvoiceDocumentFixture customerInvoiceDocumentFixture, CustomerInvoiceDetailFixture[] customerInvoiceDetailFixtures, CustomerFixture customerFixture) throws WorkflowException {
        CustomerInvoiceDocument document = null;
        if( ObjectUtils.isNotNull( customerFixture ) ){
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerFixture, customerInvoiceDetailFixtures);
        } else {
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerInvoiceDetailFixtures);
        }
        document.getDocumentHeader().setDocumentDescription("CREATING TEST CUSTOMER INVOICE DOCUMENT");

        Document routedDocument = null;
        try {
            SpringContext.getBean(DocumentService.class).routeDocument(document, "TESTING", new ArrayList<AdHocRouteRecipient>());
        } catch (Exception e){
            LOG.fatal("The Customer Invoice Document was not routed, and is not available for testing.",e);
            Assert.fail("The Customer Invoice Document was not routed, and is not available for testing." + e.getClass().getName() + " : " + e.getMessage() + "\n" + dumpMessageMapErrors() + "\n" + document);
        }
        routedDocument = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(document.getDocumentNumber());
        return (CustomerInvoiceDocument)routedDocument;
    }

    protected static String dumpMessageMapErrors() {
        if (GlobalVariables.getMessageMap().hasNoErrors()) {
            return "";
        }

        StringBuilder message = new StringBuilder();
        for ( String key : GlobalVariables.getMessageMap().getErrorMessages().keySet() ) {
            List<ErrorMessage> errorList = GlobalVariables.getMessageMap().getErrorMessages().get(key);

            for ( ErrorMessage em : errorList ) {
                message.append(key).append(" = ").append( em.getErrorKey() );
                if (em.getMessageParameters() != null) {
                    message.append( " : " );
                    String delim = "";
                    for ( String parm : em.getMessageParameters() ) {
                        message.append(delim).append("'").append(parm).append("'");
                        if ("".equals(delim)) {
                            delim = ", ";
                        }
                    }
                }
            }
            message.append( '\n' );
        }
        return message.toString();
    }
}
