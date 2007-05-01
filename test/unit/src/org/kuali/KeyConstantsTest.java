/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.test.KualiTestBase;

import com.opensymphony.oscache.util.StringUtil;

import junit.framework.TestCase;

public class KeyConstantsTest extends KualiTestBase {

    /**
     * checks to see if the properties defined in the KFSKeyConstants class are in the AppilcationResources.properties
     */
    public final void testKeyConstants() throws Exception {
        final ResourceBundle applicationResources = ResourceBundle.getBundle("ApplicationResources");
        List<String> keys = extractConstants(KFSKeyConstants.class);
        assertFalse("no properties define in KFSKeyConstants", keys.isEmpty());
        List<String> notFound = new ArrayList<String>();
        for (String key : keys) {
            try {
                applicationResources.getString(key);
            }
            catch (MissingResourceException e) {
                notFound.add(key);
            }
        }
        assertTrue("The following keys found in KFSKeyConstants.java do not map to keys in ApplicationResources.properties: "+notFound, notFound.isEmpty());

    }

    public static final List<String> extractConstants(Class clazz) throws Exception {
        // get all the properties defined by class parameter
        List<String> keys = new ArrayList<String>();
        Field[] fields = clazz.getDeclaredFields();
        Object classInstance = clazz.newInstance();
        for (Field field : fields) {
            keys.add(field.get(classInstance).toString());
        }

        // get all the properties for the classes declared by this class
        Class[] classes = clazz.getDeclaredClasses();
        for (Class declaredClass : classes) {
            keys.addAll(extractConstants(declaredClass));
        }
        return keys;
    }
}
