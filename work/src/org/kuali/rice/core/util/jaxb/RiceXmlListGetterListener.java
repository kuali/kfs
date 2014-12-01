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
