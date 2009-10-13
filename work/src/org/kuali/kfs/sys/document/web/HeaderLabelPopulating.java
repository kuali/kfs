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
 * Contract for classes than plan to be have labels rendered for them
 */
public interface HeaderLabelPopulating {
    /**
     * Asks that the HeaderLabelPopulating implementor populate the header label
     * @param label the header label to populate
     * @param renderingContext the rendering context the label will be populated in
     */
    public abstract void populateHeaderLabel(HeaderLabel label, AccountingLineRenderingContext renderingContext);
}
