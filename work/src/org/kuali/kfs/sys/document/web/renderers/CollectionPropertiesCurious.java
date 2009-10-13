/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.document.web.renderers;

/**
 * An interface for totals renders which are dying to know what the collection properties 
 * for the group they are displaying totals for is
 */
public interface CollectionPropertiesCurious {
    /**
     * Sets the name of the collection property for the currently being rendered group
     * @param collectionProperty the collection property
     */
    public abstract void setCollectionProperty(String collectionProperty);
    
    /**
     * Sets the name of the collection item property of the currently being rendered group
     * @param collectionItemProperty the collection item property
     */
    public abstract void setCollectionItemProperty(String collectionItemProperty);
}
