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

import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupTotalDefinition;
import org.kuali.kfs.sys.document.web.renderers.Renderer;

/**
 * A class which "cheats" on where to find the total for the group of accounting lines
 */
public class ProcurementCardGroupTotalDefinition extends AccountingLineGroupTotalDefinition {

    /**
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupTotalDefinition#getTotalRenderer()
     */
    @Override
    public Renderer getTotalRenderer() {
        ProcurementCardGroupTotalRenderer renderer = new ProcurementCardGroupTotalRenderer();
        
        renderer.setTotalLabelProperty(getTotalLabelProperty());
        renderer.setRepresentedCellPropertyName(getRepresentedProperty());
        
        renderer.setTotalProperty(getTotalProperty());
        
        return renderer;
    }
}
