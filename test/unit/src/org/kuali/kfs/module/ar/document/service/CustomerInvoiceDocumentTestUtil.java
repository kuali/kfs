/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
