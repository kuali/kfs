/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sec.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sec.businessobject.AccessSecurityRestrictionInfo;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.document.Document;


/**
 * Exposes methods to apply access security restrictions to business objects from the various framework points (lookups, inquiries,
 * document accounting lines)
 */
public interface AccessSecurityService {

    /**
     * Retrieves any setup security permissions (with lookup template) for the given person and evaluates against List of business
     * objects. Any instances not passing validation are removed from given list.
     *
     * @param results List of business object instances with data to check
     * @param person Person to apply security for
     */
    public void applySecurityRestrictionsForLookup(List<? extends BusinessObject> results, Person person);

    /**
     * Retrieves any setup security permissions (with gl inquiry template) for the given person and evaluates against List of
     * business objects. Any instances not passing validation are removed from given list.
     *
     * @param results List of business object instances with data to check
     * @param person Person to apply security for
     */
    public void applySecurityRestrictionsForGLInquiry(List<? extends BusinessObject> results, Person person);

    /**
     * Retrieves any setup security permissions (with ld inquiry template) for the given person and evaluates against List of
     * business objects. Any instances not passing validation are removed from given list.
     *
     * @param results List of business object instances with data to check
     * @param person Person to apply security for
     */
    public void applySecurityRestrictionsForLaborInquiry(List<? extends BusinessObject> results, Person person);

    /**
     * Retrieves any setup security permissions for the given person and evaluates against List of business objects. Any instances
     * not passing validation are removed from given list.
     *
     * @param results List of business object instances with data to check
     * @param person Person to apply security for
     * @param templateId KIM template id for permissions to check
     * @param additionalPermissionDetails Any additional details that should be matched on when retrieving permissions
     */
    public void applySecurityRestrictions(List<? extends BusinessObject> results, Person person, Template permissionTemplate, Map<String,String> additionalPermissionDetails);

    /**
     * Checks any view access security permissions setup for the user and for accounting lines of the given document type
     *
     * @param document AccountingDocument that contains the line to be validated, doc type of instance is used for retrieving
     *        permissions
     * @param accountingLine AccountingLine instance with values to check
     * @param person the user who we are checking access for
     * @return boolean true if user has view access for the accounting line, false otherwise
     */
    public boolean canViewDocumentAccountingLine(AccountingDocument document, AccountingLine accountingLine, Person person);

    /**
     * Checks any edit access security permissions setup for the user and for accounting lines of the given document type
     *
     * @param document AccountingDocument instance that contains the line to be validated, doc type of instance is used for
     *        retrieving permissions
     * @param accountingLine AccountingLine instance with values to check
     * @param person the user who we are checking access for
     * @return boolean true if user has edit access for the accounting line, false otherwise
     */
    public boolean canEditDocumentAccountingLine(AccountingDocument document, AccountingLine accountingLine, Person person);

    /**
     * Checks any edit access security permissions setup for the user and for accounting lines of the given document type
     *
     * @param document AccountingDocument instance that contains the line to be validated, doc type of instance is used for
     *        retrieving permissions
     * @param accountingLine AccountingLine instance with values to check
     * @param person the user who we are checking access for
     * @param restrictionInfo Object providing information on a restriction if one is found
     * @return boolean true if user has edit access for the accounting line, false otherwise
     */
    public boolean canEditDocumentAccountingLine(AccountingDocument document, AccountingLine accountingLine, Person person, AccessSecurityRestrictionInfo restrictionInfo);

    /**
     * Checks view access on all accounting lines contained on the document for given user
     *
     * @param document AccountingDocument instance with accounting lines to check, doc type of instance is used for retrieving
     *        permissions
     * @param person the user who we are checking access for
     * @param restrictionInfo Object providing information on a restriction if one is found
     * @return boolean true if the user has view access for all accounting lines on the document, false if access is denied on one
     *         or more lines
     */
    public boolean canViewDocument(AccountingDocument document, Person person, AccessSecurityRestrictionInfo restrictionInfo);

    /**
     * Checks access is allowed to view document notes based on the document's accounting lines
     *
     * @param document AccountingDocument instance with accounting lines to check, doc type of instance is used for retrieving
     *        permissions
     * @param person the user who we are checking access for
     * @return boolean true if the user has permission to view the notes/attachments, false otherwise
     */
    public boolean canViewDocumentNotesAttachments(AccountingDocument document, Person person);

    /**
     * Gets the View Document With Field Values template ID.
     *
     * @return the View Document With Field Values template ID
     */
    public Template getViewDocumentWithFieldValueTemplate();

    /**
     * Gets the View Accounting Line With Field Value Template Id.
     *
     * @return the View Accounting Line With Field Value Template Id
     */
    public Template getViewAccountingLineWithFieldValueTemplate();

    /**
     * Gets the View Notes Attachments With Field Value Template Id.
     *
     * @return the View Notes Attachments With Field Value Template Id
     */
    public Template getViewNotesAttachmentsWithFieldValueTemplate();

    /**
     * Gets the Edit Document With Field Value Template Id.
     *
     * @return the Edit Document With Field Value Template Id
     */
    public Template getEditDocumentWithFieldValueTemplate();

    /**
     * Gets the Edit Accounting Line With Field Value Template Id.
     *
     * @return the Edit Accounting Line With Field Value Template Id
     */
    public Template getEditAccountingLineWithFieldValueTemplate();

    /**
     * Gets the Lookup With Field Value Template Id.
     *
     * @return the Lookup With Field Value Template Id
     */
    public Template getLookupWithFieldValueTemplate();

    /**
     * Gets the Inquiry With Field Value Template Id.
     *
     * @return the InquiryWithFieldValueTemplateId
     */
    public Template getInquiryWithFieldValueTemplate();

    /**
     * Calls access security service to check view access on given GLPE for current user. Access to view the GLPE on the document should be related to the view permissions for an
     * accounting line with the same account attributes. Called from generalLedgerPendingEntries.tag
     *
     * @param pendingEntry GeneralLedgerPendingEntry to check access for
     * @return boolean true if given user has view permission, false otherwise
     */
    public boolean canViewGLPE(Document document, GeneralLedgerPendingEntry pendingEntry, Person person);

    /**
     * Compares the size of the given list against the given previous size and if different adds an info message
     *
     * @param previousListSize int giving previous size of list to compare to
     * @param results List to get size for and compare
     * @param messageKey String key of message that should be added
     */
    public void compareListSizeAndAddMessageIfChanged(int previousListSize, List<?> results, String messageKey);

    /**
     * Returns all the documents for which access security controls are in place.
     *
     */
    public Collection<String> getAccessSecurityControlledDocumentTypeNames();


   public boolean isAccessSecurityControlledDocumentType( String documentTypeName );
}
