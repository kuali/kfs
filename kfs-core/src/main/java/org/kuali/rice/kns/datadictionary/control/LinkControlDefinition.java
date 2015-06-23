/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary.control;

/**
 The link element is used to render an HTML link control.
 */
@Deprecated
public class LinkControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = -7568912421829207545L;

    protected String styleClass;
    protected String target;
    protected String hrefText;

    public LinkControlDefinition() {
        this.type = ControlDefinitionType.LINK;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isLink()
     */
    public boolean isLink() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.getClass().getName();
    }

    /**
     * @return the styleClass
     */
    public String getStyleClass() {
        return this.styleClass;
    }

    /**
     * @param styleClass the styleClass to set
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the hrefText
     */
    public String getHrefText() {
        return this.hrefText;
    }

    /**
     * @param hrefText the hrefText to set
     */
    public void setHrefText(String hrefText) {
        this.hrefText = hrefText;
    }

}
