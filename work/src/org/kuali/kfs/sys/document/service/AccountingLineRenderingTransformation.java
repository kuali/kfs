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
package org.kuali.kfs.sys.document.service;

import java.util.List;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.web.TableJoining;

/**
 * An interface that describes the methods used by a service that transforms AccountingLineViewRenderableElements 
 */
public interface AccountingLineRenderingTransformation {

    /**
     * Transforms the given tree of elements into the transformed tree of elements
     * @param elements a List of renderable elements to transform
     * @param accountingLine the accounting line that will be rendered
     */
    public abstract void transformElements(List<TableJoining> elements, AccountingLine accountingLine);
}
