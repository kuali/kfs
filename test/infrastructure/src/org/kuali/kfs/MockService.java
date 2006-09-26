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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import junit.framework.AssertionFailedError;

/**
 * This class implements a mock object for any service, using a dynamic proxy. It returns results for specific methods and
 * arguments, and can also relay unspecified methods or arguments to a fallback service. Note that this proxy does not do Spring
 * things like AOP transactions, altho the fallback service may.
 * 
 * @author Kuali Financial Transactions Team ()
 */
public class MockService implements InvocationHandler {

    private final HashMap nameToMockMethodMap = new HashMap();
    private final Object noMethodFallback;

    private MockService(MockMethod[] mockMethods, Object noMethodFallback) {
        this.noMethodFallback = noMethodFallback;
        for (int i = 0; i < mockMethods.length; i++) {
            MockMethod mockMethod = mockMethods[i];
            nameToMockMethodMap.put(mockMethod.getName(), mockMethod);
        }
    }

    /**
     * Returns the result associated with the given method and list of arguments. If there is no mock method, invokes the given
     * method on the noMethodFallback Object. If the noMethodFallback Object is null, throws an AssertionFailedError.
     * 
     * @see java.lang.reflect.InvocationHandler#invoke
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if (nameToMockMethodMap.containsKey(name)) {
            MockMethod m = (MockMethod) nameToMockMethodMap.get(name);
            return m.invoke(proxy, method, args);
        }
        if (noMethodFallback == null) {
            throw new AssertionFailedError("no mock method " + name);
        }
        return method.invoke(noMethodFallback, args);
    }

    /**
     * Creates a dynamic proxy with the given mock methods.
     * 
     * @param iface the interface to proxy
     * @param mockMethods the methods to mock
     * @param noMethodFallback the Object to use when there is no mock method for the invoked method, or null if an
     *        AssertionFailedError should be thrown in this case instead.
     * @return a dynamic proxy
     */
    public static Object createProxy(Class iface, MockMethod[] mockMethods, Object noMethodFallback) {
        return Proxy.newProxyInstance(iface.getClassLoader(), new Class[] { iface }, new MockService(mockMethods, noMethodFallback));
    }

    /**
     * Creates a dynamic proxy with a single mock method that has a single result. Invocations of other methods or arguments will
     * throw an AssertionFailedError.
     * 
     * @param iface the interface to proxy
     * @param methodName the name of the method to mock
     * @param args the arguments to expect for the named method
     * @param result the result to return from the named method
     * @return a dynamic proxy
     */
    public static Object createProxy(Class iface, String methodName, Object[] args, Object result) {
        MockMethod mockMethod = new MockMethod(methodName, null);
        mockMethod.setResult(result, args);
        return createProxy(iface, new MockMethod[] { mockMethod }, null);
    }
}
