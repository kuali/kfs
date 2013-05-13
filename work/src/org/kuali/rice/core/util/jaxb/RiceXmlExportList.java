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
import java.util.AbstractList;
import java.util.List;

/**
 * Custom subclass of AbstractList that, whenever the "get" method is called, will pass an
 * internally-stored list's object to the given listener for conversion into another object matching
 * the list's type. This allows for the marshalling process to discard generated items after they
 * have been marshalled.
 * 
 * <p>These lists are constructed by passing in another list containing the unconverted items,
 * as well as a listener that will create items of this list's type upon each invocation of
 * the "get" method.
 * 
 * <p>This is similar to the "streaming" unmarshalling strategy used in the RiceXmlImportList
 * class, except that this list has been adapted for marshalling instead.
 * 
 * @param E The type that the list is expected to return.
 * @param T The type that the list stores internally and passes to the listener for conversion as needed. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class RiceXmlExportList<E,T> extends AbstractList<E> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final List<? extends T> sourceList;
    private final RiceXmlListGetterListener<E,T> listGetterListener;
    
    /**
     * Constructs a new export list that will rely on the given listener for converting the provided
     * list's items into the appropriate type.
     * 
     * @param sourceList The list of objects to convert.
     * @param listGetterListener The listener to use.
     * @throws IllegalArgumentException if sourceList or listGetterListener are null.
     */
    public RiceXmlExportList(List<? extends T> sourceList, RiceXmlListGetterListener<E,T> listGetterListener) {
        super();
        if (sourceList == null) {
            throw new IllegalArgumentException("sourceList cannot be null");
        } else if (listGetterListener == null) {
            throw new IllegalArgumentException("listGetterListener cannot be null");
        }
        this.sourceList = sourceList;
        this.listGetterListener = listGetterListener;
    }
    
    /**
     * Passes the item at the given index of the internal list to the listener, and then returns
     * the listener's result.
     * 
     * @param index The unconverted item's index in the internal list.
     * @return The item converted by the listener at the given list index.
     */
    @Override
    public E get(int index) {
        return listGetterListener.gettingNextItem(sourceList.get(index), index);
    }

    /**
     * Returns the size of the internal list.
     * 
     * @return The size of the internal list.
     */
    @Override
    public int size() {
        return sourceList.size();
    }

}
