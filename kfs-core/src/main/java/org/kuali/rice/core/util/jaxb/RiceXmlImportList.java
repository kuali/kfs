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
import java.util.AbstractList;

/**
 * Custom subclass of AbstractList that, when adding new items, will pass them on to a listener instead of
 * storing them internally.
 * 
 * <p>This is based off of the JAXB "streaming" unmarshalling strategy, which is briefly mentioned here:
 * 
 * <p>http://jaxb.java.net/guide/Dealing_with_large_documents.html
 * 
 * <p>and is presented in the example code available here:
 * 
 * <p>http://jaxb.java.net/2.2.4/
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class RiceXmlImportList<E> extends AbstractList<E> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** The listener that this list will pass new items to. */
    private final RiceXmlListAdditionListener<E> listAdditionListener;
    
    /**
     * Constructs a new streaming list that will pass new items to the given listener instead of storing them.
     * 
     * @param listAdditionListener The listener to use.
     * @throws IllegalArgumentException if listAdditionListener is null.
     */
    public RiceXmlImportList(RiceXmlListAdditionListener<E> listAdditionListener) {
        super();
        if (listAdditionListener == null) {
            throw new IllegalArgumentException("listAdditionListener cannot be null");
        }
        this.listAdditionListener = listAdditionListener;
    }
    
    /**
     * Instead of adding the item to the list, simply invoke the appropriate listener.
     * 
     * <p>This is based off of the "streaming" unmarshalling strategy used in one of the JAXB sample apps.
     * 
     * @return false, since the list never gets altered as a result of invoking this method.
     */
    @Override
    public boolean add(E e) {
        listAdditionListener.newItemAdded(e);
        return false;
    }
    
    /**
     * This method always throws an exception, since the list never contains any items.
     * 
     * @throws IndexOutOfBoundsException
     */
    @Override
    public E get(int index) {
        throw new IndexOutOfBoundsException();
    }

    /**
     * This method always returns zero, since items are never actually added to the list.
     * 
     * @return zero.
     */
    @Override
    public int size() {
        return 0;
    }
}
