/*
 * Copyright 2007 The Kuali Foundation
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
