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
package org.kuali.kfs.sys.document.validation.impl;

import java.util.Collection;
import java.util.Iterator;

import org.kuali.kfs.sys.document.validation.Validation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * A validation that runs a list of child validations over each member of a collection.
 */
public class CollectionValidation extends CompositeValidation {
    protected String collectionProperty;
    protected boolean removeCollectionPropretyPluralization = true;

    /**
     * Iterates over each member of the collection, which is assumed to be the property of the validation event named by the given collectionProperty
     * @see org.kuali.kfs.sys.document.validation.impl.CompositeValidation#validate(java.lang.Object[])
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;

        if (collectionProperty == null) {
            throw new IllegalStateException("collectionProperty must not be null");
        }

        Iterator iter = getCollection(event).iterator();

        // hold on to any existing iteration subject until after we're done doing our thing
        Object parentIterationSubject = event.getIterationSubject();

        int count = 0;
        while (iter.hasNext()) {
            result &= validateEachObject(event, iter.next(), count);
            count += 1;
        }

        event.setIterationSubject(parentIterationSubject); // or back to null if the event didn't have an iteration subject in the first place

        return result;
    }

    /**
     * Validates each object in the collection
     * @param event the event to validate against
     * @param objectToValidate the object from the collection which is being validated
     * @return true if object passed all child sub-validations, false otherwise
     */
    protected boolean validateEachObject(AttributedDocumentEvent event, Object objectToValidate, int count) {
        boolean result = true;
        String errorPath = buildPropertyName(count);
        GlobalVariables.getMessageMap().addToErrorPath(errorPath);
        boolean currentResult = true;
        event.setIterationSubject(objectToValidate);
        for (Validation validation: getValidations()) {
            currentResult = validation.stageValidation(event);
            result &= currentResult;
            if (!currentResult && validation.shouldQuitOnFail()) {
                break;
            }
        }
        GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
        return result;
    }

    /**
     * Here's a hack for all my homies: this method builds the property name of a specific item in a collection by chopping the presumed "s" at the end of collectionProperties,
     * and then attaches array/list syntax to it to hold the count.  It'll work for most stuff, won't it?  If it doesn't, set removeCollectionPropretyPluralization to false
     * @params count the count of the current item in the collection
     * @return
     */
    protected String buildPropertyName(int count) {
        String collectionProp = collectionProperty;
        if (isRemoveCollectionPropretyPluralization()) {
            collectionProp = collectionProperty.substring(0, collectionProperty.length()-1);
        }
        return new StringBuilder().append(collectionProp).append('[').append(count).append(']').toString();
    }

    /**
     * Based on the collection property, finds the events
     * @param event
     * @return
     */
    protected Collection getCollection(KualiDocumentEvent event) {
        return (Collection)ObjectUtils.getPropertyValue(event, collectionProperty);
    }

    /**
     * Gets the collectionProperty attribute.
     * @return Returns the collectionProperty.
     */
    public String getCollectionProperty() {
        return collectionProperty;
    }

    /**
     * Sets the collectionProperty attribute value.
     * @param collectionProperty The collectionProperty to set.
     */
    public void setCollectionProperty(String collectionProperty) {
        this.collectionProperty = collectionProperty;
    }

    public boolean isRemoveCollectionPropretyPluralization() {
        return removeCollectionPropretyPluralization;
    }

    public void setRemoveCollectionPropretyPluralization(boolean removeCollectionPropretyPluralization) {
        this.removeCollectionPropretyPluralization = removeCollectionPropretyPluralization;
    }

}
