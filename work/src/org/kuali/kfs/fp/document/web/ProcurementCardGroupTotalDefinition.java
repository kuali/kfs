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
