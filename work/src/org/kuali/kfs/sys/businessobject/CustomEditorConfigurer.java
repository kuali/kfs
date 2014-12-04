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
