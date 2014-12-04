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
package org.kuali.kfs.fp.document.web;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.document.web.renderers.CollectionPropertiesCurious;
import org.kuali.kfs.sys.document.web.renderers.GroupTotalRenderer;

/**
 * 
 */
public class ProcurementCardGroupTotalRenderer extends GroupTotalRenderer implements CollectionPropertiesCurious {
    private String collectionProperty = null;
    private String simpleTotalProperty = null;
    
    /**
     * @see org.kuali.kfs.sys.document.web.renderers.CollectionPropertiesCurious#setCollectionItemProperty(java.lang.String)
     */
    public void setCollectionItemProperty(String collectionItemProperty) {
        // whoops, dropped it...
    }

    /**
     * @see org.kuali.kfs.sys.document.web.renderers.CollectionPropertiesCurious#setCollectionProperty(java.lang.String)
     */
    public void setCollectionProperty(String collectionProperty) {
        this.collectionProperty = collectionProperty;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.renderers.GroupTotalRenderer#clear()
     */
    @Override
    public void clear() {
        super.clear();
        this.collectionProperty = null;
        this.simpleTotalProperty = null;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.renderers.GroupTotalRenderer#getTotalProperty()
     */
    @Override
    public String getTotalProperty() {
        if (!StringUtils.isBlank(collectionProperty)) {
            return collectionProperty.replaceFirst("\\.[a-z]+AccountingLines", "")+"."+simpleTotalProperty;
        }
        return simpleTotalProperty;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.renderers.GroupTotalRenderer#setTotalProperty(java.lang.String)
     */
    @Override
    public void setTotalProperty(String totalProperty) {
        this.simpleTotalProperty = totalProperty;
    }

}
