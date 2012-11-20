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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
}
