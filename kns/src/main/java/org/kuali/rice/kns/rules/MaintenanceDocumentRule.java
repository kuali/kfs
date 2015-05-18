/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.rules;

import org.kuali.rice.krad.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.rules.rule.event.ApproveDocumentEvent;

/**
 * Rule event interface for implementing business rules against a <code>MaintenanceDocument</code>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface MaintenanceDocumentRule {

    /**
     * Runs all business rules needed prior to saving. This includes both common rules for all maintenance documents,
     * plus class-specific business rules.
     *
     * Will only return false if it fails the isValidForSave() test. Otherwise, it will always return positive
     * regardless of the outcome of the business rules. However, any error messages resulting from the business rules
     * will still be populated, for display to the consumer of this service.
     *
     * @see org.kuali.rice.krad.rules.rule.SaveDocumentRule#processSaveDocument(Document)
     */
    public abstract boolean processSaveDocument(Document document);

    /**
     * Runs all business rules needed prior to routing. This includes both common rules for all maintenance documents,
     * plus class-specific business rules.
     *
     * Will return false if any business rule fails, or if the document is in an invalid state, and not routable (see
     * isDocumentValidForRouting()).
     *
     * @see org.kuali.rice.krad.rules.rule.RouteDocumentRule#processRouteDocument(Document)
     */
    public abstract boolean processRouteDocument(Document document);

    /**
     * Runs all business rules needed prior to approving. This includes both common rules for all maintenance documents,
     * plus class-specific business rules.
     *
     * Will return false if any business rule fails, or if the document is in an invalid state, and not approvable (see
     * isDocumentValidForApproving()).
     *
     * @see org.kuali.rice.krad.rules.rule.ApproveDocumentRule#processApproveDocument(ApproveDocumentEvent)
     */
    public abstract boolean processApproveDocument(ApproveDocumentEvent approveEvent);

    /**
     * Sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document.
     *
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects from the DB by their
     * primary keys, if available.
     *
     * @param document - the maintenanceDocument being evaluated
     */
    public void setupBaseConvenienceObjects(MaintenanceDocument document);

    /**
     * Should always be overriden if a subclass is created.
     *
     * The goal for this is to cast the oldBo and newBo into the correct types of the subclass.
     */
    public void setupConvenienceObjects();
}
