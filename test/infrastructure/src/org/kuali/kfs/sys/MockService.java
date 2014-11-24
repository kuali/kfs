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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import junit.framework.AssertionFailedError;

/**
 * This class implements a mock object for any service, using a dynamic proxy. It returns results for specific methods and
 * arguments, and can also relay unspecified methods or arguments to a fallback service. Note that this proxy does not do Spring
 * things like AOP transactions, altho the fallback service may.
 */
public class MockService implements InvocationHandler {

    private final HashMap<String, MockMethod> nameToMockMethodMap = new HashMap<String, MockMethod>();
    private final Object noMethodFallback;

    private MockService(MockMethod[] mockMethods, Object noMethodFallback) {
        this.noMethodFallback = noMethodFallback;
        for (MockMethod m : mockMethods) {
            nameToMockMethodMap.put(m.getName(), m);
        }
    }

    /**
     * Returns the result associated with the given method and list of arguments. If there is no mock method, invokes the given
     * method on the noMethodFallback Object. If the noMethodFallback Object is null, throws a
     * {@link junit.framework.AssertionFailedError}.
     * 
     * @see java.lang.reflect.InvocationHandler#invoke
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if (nameToMockMethodMap.containsKey(name)) {
            MockMethod m = nameToMockMethodMap.get(name);
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
     * @param noMethodFallback the Object to use when there is no mock method for the invoked method, or null if a
     *        {@link junit.framework.AssertionFailedError} should be thrown in this case instead.
     * @return a dynamic proxy implementing the given interface
     */
    public static <I> I createProxy(Class<I> iface, MockMethod[] mockMethods, Object noMethodFallback) {
        // noinspection unchecked
        return (I) Proxy.newProxyInstance(iface.getClassLoader(), new Class[] { iface }, new MockService(mockMethods, noMethodFallback));
    }

    /**
     * Creates a dynamic proxy with a single mock method that has a single result. Invocations of other methods or arguments will
     * throw a {@link junit.framework.AssertionFailedError}.
     * 
     * @param iface the interface to proxy
     * @param methodName the name of the method to mock
     * @param args the arguments to expect for the named method
     * @param result the result to return from the named method
     * @return a dynamic proxy implementing the given interface
     */
    public static <I> I createProxy(Class<I> iface, String methodName, Object[] args, Object result) {
        MockMethod mockMethod = new MockMethod(methodName, null);
        mockMethod.setResult(result, args);
        return createProxy(iface, new MockMethod[] { mockMethod }, null);
    }
}
