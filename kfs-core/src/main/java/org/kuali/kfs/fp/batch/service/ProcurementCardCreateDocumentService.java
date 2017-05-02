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
package org.kuali.kfs.fp.batch.service;

/**
 * 
 * Service interface for implementing methods to create procurement card documents.
 * 
 */
public interface ProcurementCardCreateDocumentService {

    /**
     * Creates procurement card documents and routes from the records loaded into the transaction table.
     * 
     * @return True if the routing was successful, false otherwise.
     */
    public boolean createProcurementCardDocuments();

    /**
     * Looks for ProcurementCardDocuments with a status of 'I', meaning they have been created and saved to "inbox", 
     * but have not yet been routed.
     * 
     * @return True if the routing was successful, false otherwise.
     */
    public boolean routeProcurementCardDocuments();

    /**
     * Finds documents that have been in route status past the number of allowed days. Then calls document service 
     * to auto approve the documents.
     * 
     * @return True if the auto approve was successful, false otherwise.
     */
    public boolean autoApproveProcurementCardDocuments();
    
    /**
     * Looks for ProcurementCardDocuments that are in route status at the "AccountFullEdit" route node and routed to the error account FO. 
     * Then checks for a valid reconciler and if found, reroutes the document back to the Reconciler.
     * 
     * @return True if the re-routing was successful, false otherwise.
     */
    public boolean rerouteProcurementCardDocuments();
}
