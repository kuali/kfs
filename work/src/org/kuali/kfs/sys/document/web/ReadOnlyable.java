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
 * A contract needed by any element that can be set to be read only
 */
public interface ReadOnlyable {
    /**
     * Sets any renderable element within this table joining block to be editable
     */
    public abstract void setEditable();
    
    /**
     * Sets any renderable element within this table joining block to be read only
     */
    public abstract void readOnlyize();
    
    /**
     * Determines whether is element is entirely read only or not
     * @return true if the entire element is read only; false otherwise
     */
    public abstract boolean isReadOnly();
}
