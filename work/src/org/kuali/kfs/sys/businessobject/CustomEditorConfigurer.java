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
package org.kuali.kfs.sys.businessobject;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

public class CustomEditorConfigurer implements InitializingBean {
    protected static final Logger LOG = Logger.getLogger(CustomEditorConfigurer.class);

    private Map customEditors;

    public CustomEditorConfigurer() {
        super();
        customEditors = new HashMap();
    }

    public void setCustomEditors(Map customEditors) {
        this.customEditors = customEditors;
    }

    public Map getCustomEditors() {
        return customEditors;
    }

    protected void registerEditors() {
        for (String key : (Set<String>) customEditors.keySet()) {
            PropertyEditor value = (PropertyEditor) customEditors.get(key);
            try {
                PropertyEditorManager.registerEditor(Class.forName(key), value.getClass());
            }
            catch (ClassNotFoundException e) {
                LOG.debug("Cannot register property editor " + value + " for class " + key, e);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        registerEditors();
    }

}
