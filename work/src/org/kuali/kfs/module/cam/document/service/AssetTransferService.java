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
package org.kuali.kfs.module.cam.document.service;

import org.kuali.kfs.module.cam.document.AssetTransferDocument;

public interface AssetTransferService {
    /**
     * This method is called when the work flow document is reached its final approval
     * <ol>
     * <li>Gets the latest asset details from DB</li>
     * <li>Save asset owner data</li>
     * <li>Save location changes </li>
     * <li>Save organization changes</li>
     * <li>Create offset payments</li>
     * <li>Create new payments</li>
     * <li>Update original payments</li>
     * </ol>
     */
    void saveApprovedChanges(AssetTransferDocument document);

    /**
     * Creates GL Postables using the source plant account number and target plant account number
     */
    void createGLPostables(AssetTransferDocument document);


}
