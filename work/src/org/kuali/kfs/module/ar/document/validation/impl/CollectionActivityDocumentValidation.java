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
package org.kuali.kfs.module.ar.document.validation.impl;


import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

/**
 * Document level validation for Collection Activity Document.
 */
public class CollectionActivityDocumentValidation extends GenericValidation {

    private CollectionActivityDocument collectionActivityDocument;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        return true;
    }

    /**
     * Gets the collectionActivityDocument attribute.
     * 
     * @return Returns the collectionActivityDocument.
     */
    public CollectionActivityDocument getCollectionActivityDocument() {
        return collectionActivityDocument;
    }

    /**
     * Sets the collectionActivityDocument attribute value.
     * 
     * @param collectionActivityDocument The collectionActivityDocument to set.
     */
    public void setCollectionActivityDocument(CollectionActivityDocument collectionActivityDocument) {
        this.collectionActivityDocument = collectionActivityDocument;
    }


}
