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
package org.kuali.kfs.sys.document.web;

/**
 * an interface that indicates the totaling is for the nested field
 */
public interface NestedFieldTotaling {

    /**
     * set the property name of the containing object if the field for totaling is a nested property 
     * 
     * @param containingPropertyName the given property name of the containing object
     */
    public void setContainingPropertyName(String containingPropertyName);

    /**
     * determine whether the field for totaling is a nested property
     * 
     * @return true if the field for totaling is a nested property; otherwise, false
     */
    public boolean isNestedProperty();
}
