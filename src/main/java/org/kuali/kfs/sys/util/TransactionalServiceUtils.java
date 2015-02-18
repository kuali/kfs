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
package org.kuali.kfs.sys.util;

import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;

/**
 * This class provides utility methods to support the operation of transactional services
 */
public final class TransactionalServiceUtils {
	
	private TransactionalServiceUtils() {
		throw new UnsupportedOperationException("do not call");
	}
    /**
     * Copys iterators so that they may be used outside of this class.  Often, the DAO may
     * return iterators that may not be used outside of this class because the transaction/
     * connection may be automatically closed by Spring.
     * 
     * This method copies all of the elements in the OJB backed iterators into list-based iterators
     * by placing the returned BOs into a list
     * 
     * @param iter an OJB backed iterator to copy
     * @return an Iterator that may be used outside of this class
     */
    public static <E> Iterator<E> copyToExternallyUsuableIterator(Iterator<E> iter) {
        return IteratorUtils.toList(iter).iterator();
    }
    
    /**
     * Returns the first element and exhausts an iterator
     * 
     * @param <E> the type of elements in the iterator
     * @param iterator the iterator to exhaust
     * @return the first element of the iterator; null if the iterator's empty
     */
    public static <E> E retrieveFirstAndExhaustIterator(Iterator<E> iterator) {
        E returnVal = null;
        if (iterator.hasNext()) {
            returnVal = iterator.next();
        }
        exhaustIterator(iterator);
        return returnVal;
    }
    
    /**
     * Exhausts (i.e. complete iterates through) an iterator
     * 
     * @param iterator
     */
    public static void exhaustIterator(Iterator<?> iterator) {
        while (iterator.hasNext()) {
            iterator.next();
        }
    }
}
