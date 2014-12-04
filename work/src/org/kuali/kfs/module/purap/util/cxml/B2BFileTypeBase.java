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
package org.kuali.kfs.module.purap.util.cxml;

import java.io.File;

import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;

/**
 * This is the base class for all the b2b file type classes. All the methods in this class
 * returns a default value since there is no need to do anything in these methods in b2b.
 */

public abstract class B2BFileTypeBase  extends XmlBatchInputFileTypeBase{

    public String getFileName(String principalId, Object parsedFileContents, String fileUserIdentifer) {
        return null;
    }

    public boolean validate(Object parsedFileContents) {
        return false;
    }
    
    public String getTitleKey() {
        return null;
    }

    public String getAuthorPrincipalName(File file) {
        return null;
    }
}
