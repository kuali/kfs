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

import org.kuali.kfs.sys.document.service.AccountingLineTableTransformation;
import org.kuali.kfs.sys.document.web.AccountingLineTableCell;
import org.kuali.kfs.sys.document.web.AccountingLineTableRow;
import org.kuali.kfs.sys.document.web.RenderableElement;

/**
 * A transformation which takes all of the non-visible hidden fields of the accounting line and moves them
 * to the first possible position
 */
public class HiddenFieldRearrangementAccountingLineRenderingTransformationImpl implements AccountingLineTableTransformation {

    public void transformRows(List<AccountingLineTableRow> rows) {
        // 1. find the first top-level container element with non-hidden variables
        AccountingLineTableCell cell = findFirstNonHiddenCell(rows);
        // 2. move all the hidden fields
        for (AccountingLineTableRow row : rows) {
            moveHiddenFields(row, cell);
        }
    }
    
    /**
     * Finds the first top-level non-hidden container from the given list of elements
     * @param elements the elements to find the first non-hidden container from
     * @return the first top-level non-hidden container
     */
    protected AccountingLineTableCell findFirstNonHiddenCell(List<AccountingLineTableRow> rows) {
        for (AccountingLineTableRow row : rows) {
            for (AccountingLineTableCell cell : row.getCells()) {
                if (!cell.isHidden()) {
                    return cell;
                }
            }
        }
        throw new IllegalArgumentException("The renderable element tree specified does not seem to have any elements that will display as non-hidden specified");
    }
    
    /**
     * Moves any hidden fields in the source container to the target container 
     * @param sourceContainer the container which may have hidden fields
     * @param targetContainer the container which should be carrying all hidden fields
     */
    protected void moveHiddenFields(AccountingLineTableRow sourceRow, AccountingLineTableCell targetCell) {
        for (AccountingLineTableCell cell : sourceRow.getCells()) {
            moveHiddenFields(cell, targetCell);
        }
    }
    
    /**
     * Moves any hidden renderable fields in the source cell to the target cell
     * @param sourceCell the source cell to move hidden elements from
     * @param targetCell the target cell to move hidden elements to
     */
    protected void moveHiddenFields(AccountingLineTableCell sourceCell, AccountingLineTableCell targetCell) {
        Set<RenderableElement> renderableElementsToRemove = new HashSet<RenderableElement>();
        for (RenderableElement element : sourceCell.getRenderableElement()) {
            if (element.isHidden()) {
                renderableElementsToRemove.add(element);
            }
        }
        sourceCell.getRenderableElement().removeAll(renderableElementsToRemove);
        targetCell.getRenderableElement().addAll(renderableElementsToRemove);
    }
}
