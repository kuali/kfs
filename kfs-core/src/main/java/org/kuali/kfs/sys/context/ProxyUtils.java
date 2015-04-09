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
package org.kuali.kfs.sys.context;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

public class ProxyUtils {

    public static Object getTargetIfProxied(Object object) {
        if (AopUtils.isAopProxy(object) && object instanceof Advised) {
            Advised advised = (Advised) object;
            try {
                Object target = advised.getTargetSource().getTarget();

                return target;
            }
            catch (Exception ex) {
                throw new RuntimeException("Unable to get class for proxy: " + ex.getMessage(), ex);
            }
        }

        return object;
    }

}
