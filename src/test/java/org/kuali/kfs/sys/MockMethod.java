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
package org.kuali.kfs.sys;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import junit.framework.AssertionFailedError;

/**
 * This class implements a method for a {@link MockService}.
 */
public class MockMethod {

    private final String name;
    private final Object noResultFallback;
    private final HashMap<List<Object>, Object> argListToResultMap = new HashMap<List<Object>, Object>();

    /**
     * Constructs a new instance.
     * 
     * @param name the name of the method to mock
     * @param noResultFallback the Object on which to actually invoke this method if no mock results are associated, or null if a
     *        {@link junit.framework.AssertionFailedError} should be thrown in this case instead.
     */
    public MockMethod(String name, Object noResultFallback) {
        this.name = name;
        this.noResultFallback = noResultFallback;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets a result from this method for the given list of arguments.
     * 
     * @param result the result to return for invoking with the given args
     * @param args the arguments to associate with the given result. The given array is copied, but if the hashCode or equality of
     *        Objects in this array change, then the invoke method may fail to find the associated result.
     */
    public void setResult(Object result, Object[] args) {
        argListToResultMap.put(Arrays.asList((Object[]) args.clone()), result);
    }

    /**
     * Returns the result associated with the given list of arguments (regardless of the given method). If there are no associated
     * results, invokes the given method on the noResultFallback Object. If the noResultFallback Object is null, throws a
     * {@link junit.framework.AssertionFailedError}.
     * 
     * @see java.lang.reflect.InvocationHandler#invoke
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> key = Arrays.asList(args);
        if (argListToResultMap.containsKey(key)) {
            return argListToResultMap.get(key);
        }
        else {
            if (noResultFallback == null) {
                throw new AssertionFailedError("method " + name + " has no result for " + key);
            }
            return method.invoke(noResultFallback, args);
        }
    }
}
