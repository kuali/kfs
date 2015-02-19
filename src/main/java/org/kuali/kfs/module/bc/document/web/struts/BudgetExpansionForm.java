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
package org.kuali.kfs.module.bc.document.web.struts;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.rice.kns.util.MessageList;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Holds properties necessary for expansion screen handling.
 */
public class BudgetExpansionForm extends KualiForm {
    private String backLocation;
    private String returnAnchor;
    private String returnFormKey;

    // current active budget fiscal year
    private Integer universityFiscalYear;

    // form messages
    private MessageList messages;

    private MessageList callBackMessages = new MessageList();
    private MessageMap callBackErrors = new MessageMap();

    private Map<String, String> editingMode;
    private Map<String, String> documentActions;

    private boolean mainWindow = true;

    /**
     * Default Constructor
     */
    public BudgetExpansionForm() {
        super();
        messages = new MessageList();
        editingMode = new HashMap<String, String>();
        documentActions = new HashMap<String, String>();
    }

    /**
     * Gets the backLocation attribute.
     *
     * @return Returns the backLocation.
     */
    @Override
    public String getBackLocation() {
        return backLocation;
    }

    /**
     * Sets the backLocation attribute value.
     *
     * @param backLocation The backLocation to set.
     */
    @Override
    public void setBackLocation(String backLocation) {
        this.backLocation = backLocation;
    }

    /**
     * Gets the returnAnchor attribute.
     *
     * @return Returns the returnAnchor.
     */
    public String getReturnAnchor() {
        return returnAnchor;
    }

    /**
     * Sets the returnAnchor attribute value.
     *
     * @param returnAnchor The returnAnchor to set.
     */
    public void setReturnAnchor(String returnAnchor) {
        this.returnAnchor = returnAnchor;
    }

    /**
     * Gets the returnFormKey attribute.
     *
     * @return Returns the returnFormKey.
     */
    public String getReturnFormKey() {
        return returnFormKey;
    }

    /**
     * Sets the returnFormKey attribute value.
     *
     * @param returnFormKey The returnFormKey to set.
     */
    public void setReturnFormKey(String returnFormKey) {
        this.returnFormKey = returnFormKey;
    }

    /**
     * Gets the universityFiscalYear attribute.
     *
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     *
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the messages attribute.
     *
     * @return Returns the messages.
     */
    public MessageList getMessages() {
        return messages;
    }

    /**
     * Sets the messages attribute value.
     *
     * @param messages The messages to set.
     */
    public void setMessages(MessageList messages) {
        this.messages = messages;
    }

    /**
     * Adds a message to the form message list.
     *
     * @param message message to add
     */
    public void addMessage(String message) {
        if (this.messages == null) {
            messages = new MessageList();
        }

        this.messages.add(message);
    }

    /**
     * Gets the callBackMessages attribute.
     *
     * @return Returns the callBackMessages.
     */
    public MessageList getCallBackMessages() {
        return callBackMessages;
    }

    /**
     * Gets the callBackErrors attribute.
     *
     * @return Returns the callBackErrors.
     */
    public MessageMap getCallBackErrors() {
        return callBackErrors;
    }

    /**
     * Gets the editingMode attribute.
     *
     * @return Returns the editingMode.
     */
    public Map<String, String> getEditingMode() {
        return editingMode;
    }

    /**
     * Sets the editingMode attribute value.
     *
     * @param editingMode The editingMode to set.
     */
    public void setEditingMode(Map<String, String> editingMode) {
        this.editingMode = editingMode;
    }

    /**
     * Gets the documentActions attribute.
     *
     * @return Returns the documentActions.
     */
    public Map<String, String> getDocumentActions() {
        return documentActions;
    }

    /**
     * Sets the documentActions attribute value.
     *
     * @param documentActions The documentActions to set.
     */
    public void setDocumentActions(Map<String, String> documentActions) {
        this.documentActions = documentActions;
    }

    /**
     * Helper method to check edit mode Map for system view only entry
     */
    public boolean isSystemViewOnly() {
        return getEditingMode().containsKey(BCConstants.EditModes.SYSTEM_VIEW_ONLY);
    }

    /**
     * Helper method to check document actions Map for can edit entry
     */
    public boolean isEditAllowed() {
        return getDocumentActions().keySet().contains(KRADConstants.KUALI_ACTION_CAN_EDIT);
    }

    /**
     * Gets the mainWindow attribute.
     *
     * @return Returns the mainWindow.
     */
    public boolean isMainWindow() {
        return mainWindow;
    }

    /**
     * Sets the mainWindow attribute value.
     *
     * @param mainWindow The mainWindow to set.
     */
    public void setMainWindow(boolean mainWindow) {
        this.mainWindow = mainWindow;
    }

}
