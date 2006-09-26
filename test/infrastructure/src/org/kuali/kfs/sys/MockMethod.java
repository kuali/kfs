/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import junit.framework.AssertionFailedError;

/**
 * This class implements a method for a {@link MockService}.
 * 
 * @author Kuali Financial Transactions Team ()
 */
public class MockMethod {

    private final String name;
    private final Object noResultFallback;
    private final HashMap argListToResultMap = new HashMap();

    /**
     * Constructs a new instance.
     * 
     * @param name the name of the method to mock
     * @param noResultFallback the Object on which to actually invoke this method if no mock results are associated, or null if an
     *        AssertionFailedError should be thrown in this case instead.
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
     * results, invokes the given method on the noResultFallback Object. If the noResultFallback Object is null, throws an
     * AssertionFailedError.
     * 
     * @see java.lang.reflect.InvocationHandler#invoke
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List key = Arrays.asList(args);
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
