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
 * Helper interface for use with the RiceXmlExportList class.
 * 
 * <p>If semi-"streaming" of child elements is desired during JAXB marshalling, then the parent element
 * assigns an instance of RiceXmlExportList to the appropriate list field/property, and gives the
 * list an implementation of this interface for the list to invoke whenever it needs to create a new
 * instance of the next child element. This allows the implementation to create and then discard
 * child elements during marshalling.
 * 
 * @param E The type that the list is expected to return.
 * @param T The type that the list stores internally and passes to the listener for conversion as needed.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface RiceXmlListGetterListener<E,T> extends Serializable {
        /**
         * A listener method that converts the given item into the one expected by the list. It is invoked
         * whenever the associated list's "get" method is called.
         * 
         * @param nextItem The item to convert.
         * @param index The index being accessed on the RiceXmlExportList instance.
         * @return The converted element that the list is expected to return.
         */
        public E gettingNextItem(T nextItem, int index);
}
