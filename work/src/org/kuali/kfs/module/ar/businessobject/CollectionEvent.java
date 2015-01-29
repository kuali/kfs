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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

/**
 * Collection Event class.
 */
public class CollectionEvent extends PersistableBusinessObjectBase {

    private Long id;
    private String collectionEventCode;
    private String invoiceNumber;
    private String activityCode;
    private Date activityDate;
    private String activityText;
    private Date followupDate;
    private Date completedDate;
    private Timestamp postedDate;
    private String userPrincipalId;
    private String documentNumber;

    private transient Person user;
    private ContractsGrantsInvoiceDocument invoiceDocument;
    private CollectionActivityType collectionActivityType;

    /**
     * Gets the id attribute.
     *
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id attribute value.
     *
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the collectionEventCode attribute.
     *
     * @return Returns the collectionEventCode.
     */
    public String getCollectionEventCode() {
        return collectionEventCode;
    }

    /**
     * Sets the collectionEventCode attribute.
     *
     * @param collectionEventCode The collectionEventCode to set.
     */
    public void setCollectionEventCode(String collectionEventCode) {
        this.collectionEventCode = collectionEventCode;
    }

    /**
     * Gets the invoiceNumber attribute.
     *
     * @return Returns the invoiceNumber.
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoiceNumber attribute,
     *
     * @param invoiceNumber The invoiceNumber to set.
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the activityCode attribute.
     *
     * @return Returns the activityCode.
     */
    public String getActivityCode() {
        return activityCode;
    }

    /**
     * Sets the activityCode attribute.
     *
     * @param activityCode The activityCode to set.
     */
    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    /**
     * Gets the activityDate attribute.
     *
     * @return Returns the activityDate.
     */
    public Date getActivityDate() {
        return activityDate;
    }

    /**
     * Sets the activityDate attribute.
     *
     * @param activityDate The activityDate to set.
     */
    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    /**
     * Gets the activityText attribute.
     *
     * @return Returns the activityText.
     */
    public String getActivityText() {
        return activityText;
    }

    /**
     * Sets the activityText attribute.
     *
     * @param activityText The activityText to set.
     */
    public void setActivityText(String activityText) {
        this.activityText = activityText;
    }

    /**
     * Gets the followupDate attribute.
     *
     * @return Returns the followupDate.
     */
    public Date getFollowupDate() {
        return followupDate;
    }

    /**
     * Sets the followupDate attribute.
     *
     * @param followupDate The followupDate to set.
     */
    public void setFollowupDate(Date followupDate) {
        this.followupDate = followupDate;
    }

    /**
     * Gets the completedDate attribute.
     *
     * @return Returns the completedDate.
     */
    public Date getCompletedDate() {
        return completedDate;
    }

    /**
     * Sets the completedDate attribute.
     *
     * @param completedDate The completedDate to set.
     */
    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    /**
     * Gets the postedDate attribute.
     *
     * @return Returns the postedDate.
     */
    public java.util.Date getPostedDate() {
        return postedDate;
    }

    /**
     * Sets the postedDate attribute.
     *
     * @param postedDate The postedDate to set.
     */
    public void setPostedDate(Timestamp postedDate) {
        this.postedDate = postedDate;
    }

    /**
     * Gets the userPrincipalId attribute.
     *
     * @return Returns the userPrincipalId.
     */
    public String getUserPrincipalId() {
        return userPrincipalId;
    }

    /**
     * Sets the userPrincipalId attribute.
     *
     * @param userPrincipalId The userPrincipalId to set.
     */
    public void setUserPrincipalId(String userPrincipalId) {
        this.userPrincipalId = userPrincipalId;
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the user attribute.
     *
     * @return Returns the user from userPrincipalId.
     */
    public Person getUser() {
        if (ObjectUtils.isNotNull(userPrincipalId)) {
            this.user = SpringContext.getBean(PersonService.class).updatePersonIfNecessary(userPrincipalId, user);
        }
        return user;
    }

    /**
     * Sets the user attribute.
     *
     * @param user The user to set.
     */
    public void setUser(Person user) {
        this.user = user;
    }

    /**
     * Gets the invoiceDocument from given invoiceNumber.
     *
     * @return Return the invoiceDocument.
     */
    public ContractsGrantsInvoiceDocument getInvoiceDocument() {
        if (ObjectUtils.isNull(invoiceDocument)) {
            DocumentService documentService = SpringContext.getBean(DocumentService.class);
            try {
                invoiceDocument = (ContractsGrantsInvoiceDocument) documentService.getByDocumentHeaderId(this.invoiceNumber);
            }
            catch (WorkflowException e) {
                throw new RuntimeException("A WorkflowException was thrown when trying to open the details parent document.", e);
            }
        }
        return invoiceDocument;
    }

    /**
     * Sets the invoiceDocument attribute.
     *
     * @param invoiceDocument The invoiceDocument attribute to set.
     */
    public void setInvoiceDocument(ContractsGrantsInvoiceDocument invoiceDocument) {
        this.invoiceDocument = invoiceDocument;
    }

    /**
     * Gets the collectionActivityType attribute.
     *
     * @return Returns the collectionActivityType object.
     */
    public CollectionActivityType getCollectionActivityType() {
        return collectionActivityType;
    }

    /**
     * Sets the collectionActivityType attribute.
     *
     * @param collectionActivityType The collectionActivityType object to set.
     */
    public void setCollectionActivityType(CollectionActivityType collectionActivityType) {
        this.collectionActivityType = collectionActivityType;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("id", id);
        m.put("collectionEventCode", this.collectionEventCode);
        m.put("invoiceNumber", this.invoiceNumber);
        m.put("activityCode", this.activityCode);
        if (ObjectUtils.isNotNull(this.activityText)) {
            m.put("activityText", this.activityText);
        }
        return m;
    }
}
