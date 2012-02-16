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
package org.kuali.kfs.module.tem.batch;

import java.lang.reflect.Method;

import java.io.File;
import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import com.sun.jdi.Bootstrap;
//import com.sun.jdi.ArrayType;
//import com.sun.jdi.ClassType;
//import com.sun.jdi.ReferenceType;
//import com.sun.jdi.InterfaceType;
//import com.sun.jdi.VirtualMachine;
//import com.sun.jdi.connect.spi.Connection;
//import com.sun.jdi.connect.spi.TransportService;

import org.kuali.kfs.sys.batch.AbstractStep;

import static org.kuali.kfs.module.tem.util.BufferedLogger.*;
import static org.apache.commons.lang.StringUtils.replace;

/**
 * Batch step that traverses the webapp for changed classes. It then reloads the classes into the current {@link ClassLoader}
 * this gives to appearance of hot redeploys.
 *
 * @author leo [at] rsmart.com
 */
public class WebappWatchStep { // extends AbstractStep {
    // Commented out, requires sun tools.jar
//    private Date lastUpdated;
//    private String webappPath;
//    private Connection conn;
//    private VirtualMachine vm;
//
//    public WebappWatchStep() throws Throwable {
//        super();
//        lastUpdated = new Date();
//    }
//
//    /**
//     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
//     */
//    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
//        info("WebappWatch triggered. I'm going in");
//        final Collection<File> classFiles = new ArrayList<File>();
//        traverseForClasses(new File(getWebappPath()), classFiles);
//        final Map<ReferenceType,byte[]> toRedefineMap = new HashMap<ReferenceType,byte[]>();
//
//        try {
//            conn = openJdiConnection();
//            vm = Bootstrap.virtualMachineManager().createVirtualMachine(conn);
//        }
//        catch (Exception e) {
//            return false;
//        }
//
//        try {
//            for (final File classFile : classFiles) {
//                final byte[] classData = new byte[new Long(classFile.length()).intValue()];
//                FileInputStream classStream = null;
//                try {
//                    classStream = new FileInputStream(classFile);
//                    
//                    classStream.read(classData, 0, classData.length);
//                }
//                catch (Exception e) {
//                }
//                finally {
//                    if (classStream != null) {
//                        try {
//                            classStream.close();
//                        }
//                        catch (Exception e) { } // failure to close. Do nothing.
//                    }
//                }
//                
//                final String className = classFromFileName(classFile.getAbsolutePath()); 
//                boolean classExists = false;
//                try {
//                    Class.forName(className);
//                    classExists = true;
//                }
//                catch (ClassNotFoundException cnfe) {
//                }
//                
//                if (classExists) {
//                    addRedefineClass(toRedefineMap, className, classData);
//                }
//                else {
//                    defineClass(className, classData, 0, classData.length);
//                }
//            }
//            
//            debug("Delivering the redefinition map");
//            vm.redefineClasses(toRedefineMap);
//        }
//        catch (Exception e) {
//            warn(e.getMessage());
//            e.printStackTrace();
//            Throwable cause = e.getCause();
//            while (cause != null) {
//                cause.printStackTrace();
//                warn("Caused by: ");
//                cause = cause.getCause();
//            }
//        }
//        finally {
//            try {
//                vm.resume();
//                if (conn != null && conn.isOpen()) {
//                    debug("Closing the JPDA connection...");
//                    conn.close();
//                    debug("Connection closed");
//                }
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        
//        setLastUpdated(new Date());
//        return false;
//    }
//
//    /**
//     * Defines a class. Only used for new classes that don't already exist in the {@link ClassLoader}
//     * @param className name of the {@link Class} to define
//     * @param b byte array of data
//     * @param offset where in the byte array to start
//     * @param len size of the byte array to read
//     * @throws Exception <code>defineClass()</code> on the {@link ClassLoader} is protected. It cannot normally be called.
//     * an exception can be thrown if something goes wrong.
//     */
//    protected void defineClass(final String className, final byte[] b, final int offset, final int len) throws Exception {
//        info("Defining class ", className);
//        final ClassLoader classLoader = getClass().getClassLoader();
//
//        final Method method = ClassLoader.class.getDeclaredMethod("defineClass", 
//                                                                  new Class[] { String.class, byte[].class, int.class, int.class });
//        method.setAccessible(true);
//        final Class definedClass = (Class) method.invoke(classLoader, className, b, offset, len);
//        info("Defined class ", definedClass);
//        // info("Loaded class ", getClass().getClassLoader().loadClass(className));
//    }
//
//    /**
//     * Populates a map of {@link ReferenceType} and {@link byte[]} data. This {@link Map} is used by JDI for 
//     * redefining classes.
//     *
//     * @param toRedefineMap {@link Map} of {@link byte[]} mapped by {@link ReferenceType}. This map is populated by the method, so it shouldn't be null.
//     * @see com.sun.jdi.VirtualMachine#redefineClasses(Map, byte[])
//     */
//    protected void addRedefineClass(final Map<ReferenceType,byte[]> toRedefineMap, final String className, final byte[] b) throws Exception { 
//        debug("Found change in ", className);
//        for (ReferenceType ref : vm.classesByName(className)) {
//            if (ref.name().equals(className) && className.indexOf("WebappWatch") < 0) {
//                debug("redefining ", className);
//                toRedefineMap.put(ref, b);
//                return;
//            }
//        }
//    }
//
//    /**
//     * Creates a {@link SocketTransportService} instance and uses that to attach to localhost:8080 and return a {@link Connection} to the 
//     * current running JVM instance. This is used by bootstrap to create a {@link VirtualMachine} instance.
//     * 
//     * @throws Exception when there's a problem attaching to localhost:8080 or if it cannot create an instance of {@link SocketTransportService}
//     * @return an open {@link Connection} to the JPDA instance of the current running JVM
//     * @see com.sun.jdi.VirtualMachineManager#createVirtualMachine(Connection)
//     * @see com.sun.tools.jdi.SocketTransportService
//     * @see com.sun.jdi.Bootstrap#virtualMachineManager()
//     */
//    protected Connection openJdiConnection() throws Exception {
//        TransportService ts = null;
//        try {
//            Class c = Class.forName("com.sun.tools.jdi.SocketTransportService");
//            ts = (TransportService)c.newInstance();
//        } catch (Exception x) {
//            throw new Error(x);
//        }
//        
//        return ts.attach("localhost:8000", 5000, 5000);
//    }
//
//    /**
//     * Tries to create a canonical class name from a .class file.
//     *
//     * @param fileName is the fully qualified path of a .class file
//     * @return a canonical class name (java.lang.String)
//     */
//    protected String classFromFileName(final String fileName) {
//        String retval = fileName.substring(0, fileName.indexOf(".class"));
//        retval = retval.substring(getWebappPath().length() + 1);
//        retval = replace(retval, File.separator, ".");
//        retval = replace(retval, "/", ".");
//        return retval;
//    }
//
//    
//    
//    /**
//     * Recursively searches for classes. If <code>file</code> is a directory, it traverses recursively. If it is a
//     * file, then it compares the <code>lastModified</code> date with the cached {@link #getLastModified()} value.
//     * 
//     * @param file {@link File} instance for traversing
//     * @param classFiles is a {@link Collection} of {@link File} instances where the {@link File#lastUpdated()} value is
//     * before {@link #getLastUpdated()}
//     */
//    protected void traverseForClasses(final File file, final Collection<File> classFiles) {
//        if (file.isDirectory()) {
//            final String[] fileNames = file.list();
//            
//            for (final String fileName : fileNames) {
//                traverseForClasses(new File(file, fileName), classFiles);
//            }
//        }
//        else if (new Date(file.lastModified()).after(lastUpdated) && file.getName().endsWith(".class")) {
//            classFiles.add(file);
//        }
//    }
//    
//    /**
//     * Gets the value of WebappPath
//     *
//     * @return the value of WebappPath
//     */
//    public String getWebappPath() {
//        return this.webappPath;
//    }
//    
//    /**
//     * Sets the value of WebappPath
//     *
//     * @param argWebappPath Value to assign to this.WebappPath
//     */
//    public void setWebappPath(final String argWebappPath) {
//        this.webappPath = argWebappPath;
//    }
//
//    /**
//     * Gets the value of LastUpdated
//     *
//     * @return the value of LastUpdated
//     */
//    public Date getLastUpdated() {
//        return this.lastUpdated;
//    }
//    
//    /**
//     * Sets the value of LastUpdated
//     *
//     * @param argLastUpdated Value to assign to this.LastUpdated
//     */
//    public void setLastUpdated(final Date argLastUpdated) {
//        this.lastUpdated = argLastUpdated;
//    }
}