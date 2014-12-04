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
package org.kuali.kfs.sys.document.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.service.AccountingLineRenderingTransformation;
import org.kuali.kfs.sys.document.web.ReadOnlyable;
import org.kuali.kfs.sys.document.web.RenderableElement;
import org.kuali.kfs.sys.document.web.TableJoining;

/**
 * If all the fields of a line are read only, then any actions blocks should be completely removed.
 */
public class AllReadOnlyNoActionsAccountingLineRenderingTransformationImpl implements AccountingLineRenderingTransformation {

    /**
     * Traverses through the elements to see if they're all read only; if so, traverses through again and removes any action blocks
     * @see org.kuali.kfs.sys.document.service.AccountingLineRenderingTransformation#transformElements(java.util.List, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public void transformElements(List<TableJoining> elements, AccountingLine accountingLine) {
        if (allReadOnly(elements)) {
            removeActionBlocks(elements);
        }
    }
    
    /**
     * Traverses all elements, determining if all of the elements are read only
     * @param elements the elements to render
     * @return true if all elements are read only, false otherwise
     */
    protected boolean allReadOnly(List<TableJoining> elements) {
        for (TableJoining element : elements) {
            if (element instanceof ReadOnlyable && !((ReadOnlyable)element).isReadOnly()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Takes any action blocks out of the line
     * @param elements the elements which contain action blocks to remove
     */
    protected void removeActionBlocks(List<? extends TableJoining> elements) {
        Set<TableJoining> elementsToRemove = new HashSet<TableJoining>();
        for (TableJoining element : elements) {
            element.removeAllActionBlocks();
            if (element instanceof RenderableElement && ((RenderableElement)element).isActionBlock()) {
                elementsToRemove.add(element);
            }
        }
        elements.removeAll(elementsToRemove);
    }

}
