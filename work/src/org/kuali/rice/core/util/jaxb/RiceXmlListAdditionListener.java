/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.core.util.jaxb;

import java.io.Serializable;

/**
 * Helper interface for use with the RiceXmlStreamingList class.
 * 
 * <p>If "streaming" of child elements is desired during JAXB unmarshalling, then the parent element
 * assigns an instance of RiceXmlStreamingList to the appropriate list field/property, and gives the
 * list an implementation of this interface for the list to invoke whenever it receives a 
 * newly-unmarshalled child element. This allows the implementation to process the new element and then
 * discard it.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface RiceXmlListAdditionListener<T> extends Serializable {
    /**
     * This method is invoked whenever the associated RiceXmlStreamingList instance receives
     * a newly-unmarshalled child element.
     * 
     * @param item The unmarshalled element (or adapter-generated object) to be processed.
     */
    public void newItemAdded(T item);
}
