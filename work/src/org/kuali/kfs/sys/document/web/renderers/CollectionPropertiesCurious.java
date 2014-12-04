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
