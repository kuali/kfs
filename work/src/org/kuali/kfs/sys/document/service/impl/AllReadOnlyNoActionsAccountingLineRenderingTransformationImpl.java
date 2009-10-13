/*
 * Copyright 2008 The Kuali Foundation
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
