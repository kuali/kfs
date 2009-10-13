/*
 * Copyright 2009 The Kuali Foundation
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
