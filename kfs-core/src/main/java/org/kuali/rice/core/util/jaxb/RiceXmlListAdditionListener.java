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
