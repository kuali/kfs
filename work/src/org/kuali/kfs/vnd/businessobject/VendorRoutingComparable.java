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
package org.kuali.kfs.vnd.businessobject;

/**
 * Defines methods that must be implemented by classes providing a VendorRoutingComparable.
 */
public interface VendorRoutingComparable {

    /**
     * A predicate to test equality of all the persisted attributes of an instance of this class, not including
     * member collections. This is used to help determine whether to route.
     * 
     * @param toCompare An Object, which should be of this class if the comparison is to be meaningful.
     * @return True if all non-derived attributes of the given object other than collections are equal to this one's. False if the
     *         given object is null or of a different class.
     */
    public boolean isEqualForRouting(Object toCompare);

}
