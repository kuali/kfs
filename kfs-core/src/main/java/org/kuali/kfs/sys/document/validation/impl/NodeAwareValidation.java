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
package org.kuali.kfs.sys.document.validation.impl;

import org.kuali.kfs.sys.document.validation.Validation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.document.Document;

/**
 * Parent of validations which either only run at or skip certain routing nodes at validation
 */
public abstract class NodeAwareValidation implements Validation {
    protected static final String PRE_ROUTE = "PreRoute";

    protected boolean quitOnFail;
    protected Validation validation;

    /**
     * Gets the shouldQuitOnFail attribute.
     * @return Returns the shouldQuitOnFail.
     */
    @Override
    public boolean shouldQuitOnFail() {
        return quitOnFail;
    }

    /**
     * Sets the shouldQuitOnFail attribute value.
     * @param shouldQuitOnFail The shouldQuitOnFail to set.
     */
    public void setQuitOnFail(boolean shouldQuitOnFail) {
        this.quitOnFail = shouldQuitOnFail;
    }

    /**
     * @return the validation being wrapped by this node specific logic
     */
    public Validation getValidation() {
        return validation;
    }

    /**
     * Sets a validation to be run at the given nodes
     * @param validation the validation to be run at the given nodes
     */
    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    /**
     * Currently, there does not seem a way in which this method would be called - stageValidation should be called, not this.  So we'll
     * throw an UnsupportedOperationException until someone calls me up and tells me I'm crazy
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        throw new UnsupportedOperationException("NodeSpecificValidation does not run validate - all validations are pushed to the composed Validation via stageValidation.  However, if you believe you called this method for a valid reason, please e-mail the KFS tech list.");
    }

    /**
     * Runs the injected validation, but only if the shouldRunValidation method has indicated at the validation should be run for the given document
     * @see org.kuali.kfs.sys.document.validation.Validation#stageValidation(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean stageValidation(AttributedDocumentEvent event) {
        if (getValidation() == null) {
            throw new IllegalStateException("Attempting to run "+getClass().getName()+" but no child validation was specified.");
        }
        if (!isNodesPropertyValid()) {
            throw new IllegalStateException("Attempting to run "+getClass().getName()+" but the nodes property is not set");
        }
        if (shouldRunValidation(event.getDocument())) {
            return getValidation().stageValidation(event);
        }
        return true; // don't run the validation here - just run true
    }

    /**
     *
     *
     * @return
     */
    protected abstract boolean isNodesPropertyValid();

    /**
     *
     *
     * @param document
     * @return
     */
    protected abstract boolean shouldRunValidation(Document document);
}
