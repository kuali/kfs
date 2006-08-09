/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service;


/**
 * Service interface for implementing methods to create procurement card documents.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public interface ProcurementCardCreateDocumentService {

    /**
     * Creates procurement card documents and routes from the records loaded into the transaction table.
     * 
     * @return boolean indicating whether the routing was successful
     */
    public boolean createProcurementCardDocuments();

    /**
     * Looks for PCDO documents in 'I' status, meaning they have been created and saved to inbox, but need routed.
     * 
     * @param documentList list of documents to be routed
     * @return boolean indicating whether the routing was successful
     */
    public boolean routeProcurementCardDocuments();

    /**
     * Finds documents that have been in route status past the number of allowed days. Then calls document service to auto approve
     * the documents.
     * 
     * @return boolean indicating whether the auto approve was successful
     */
    public boolean autoApproveProcurementCardDocuments();
}