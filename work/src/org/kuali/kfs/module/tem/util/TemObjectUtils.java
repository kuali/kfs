/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.format.FormatException;

public class TemObjectUtils {
    private static Logger LOG = Logger.getLogger(TemObjectUtils.class);
    private static final String GET_PREFIX = "get";
    private static final String IS_PREFIX = "is";
    private static final String SET_PREFIX = "set";
    
    public static List<Field> getStaticFields(Class<?> c) {
        Field[] fields = c.getDeclaredFields();
        List<Field> staticFields = new ArrayList<Field>();
        for (Field f : fields) {
            if (Modifier.isStatic(f.getModifiers())) {
                staticFields.add(f);
            }
        }
        
        return staticFields;
    }

    public static List<Field> getNonStaticFields(Class<?> c){
        Field[] fields = c.getDeclaredFields();
        List<Field> nonStaticFields = new ArrayList<Field>();
        for (Field f : fields) {
            if (!Modifier.isStatic(f.getModifiers())) {
                nonStaticFields.add(f);
            }
        }
           
        return nonStaticFields;        
    }
    
    public static List<String> getSettableFieldNames(Class<?> c){
        Method[] methods = c.getMethods();
        List<String> settableFields = new ArrayList<String>();
        for (Method m : methods) {
            if(Modifier.isPublic(m.getModifiers())){
                String methodName = m.getName();
                if (methodName.startsWith(SET_PREFIX)) {
                    String tmpFirstLetter = methodName.charAt(SET_PREFIX.length()) +"";
                    settableFields.add(tmpFirstLetter.toLowerCase()+methodName.substring(SET_PREFIX.length()+1));
                }
            }
        }
           
        return settableFields;        
    }    
    
    /**
     * 
     * This method copies all properties based on the destination object's settable fields
     * @param dest
     * @param orig
     */
    public static void copyProperties(Object dest, Object orig){
        if(dest != null && orig != null){
            List<String> destFieldNames =  getSettableFieldNames(dest.getClass());
            
            for(String fieldName : destFieldNames){
                try {
                    ObjectUtils.setObjectProperty(dest, fieldName, ObjectUtils.getPropertyValue(orig,fieldName));
                }
                catch (FormatException ex) {
                    LOG.warn("Class: "+ dest.getClass().getSimpleName() + " - fieldName: " + fieldName);
                    ex.printStackTrace();
                }
                catch (IllegalAccessException ex) {
                    LOG.warn("Class: "+ dest.getClass().getSimpleName() + " - fieldName: " + fieldName);
                    ex.printStackTrace();
                }
                catch (InvocationTargetException ex) {
                    LOG.warn("Class: "+ dest.getClass().getSimpleName() + " - fieldName: " + fieldName);
                    ex.printStackTrace();
                }
                catch (NoSuchMethodException ex) {
                    LOG.warn("Class: "+ dest.getClass().getSimpleName() + " - fieldName: " + fieldName);
                    ex.printStackTrace();
                }
            }
        }
    }    
}
