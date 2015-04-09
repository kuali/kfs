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
package org.kuali.kfs.sys.businessobject.options;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.batch.BatchFileUtils;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class BatchFileDirectoryPathValuesFinder extends KeyValuesBase {
    public List<KeyValue> getKeyValues() {
        List<File> rootDirectories = BatchFileUtils.retrieveBatchFileLookupRootDirectories();
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        
        for (File rootDirectory: rootDirectories) {
            SubDirectoryWalker walker = new SubDirectoryWalker(keyValues);
            try {
                walker.addKeyValues(rootDirectory);
            }
            catch (IOException e) {
                throw new RuntimeException("IOException caught.", e);
            }
        }
        
        return keyValues;
    }

    protected class SubDirectoryWalker extends DirectoryWalker {
        private List<KeyValue> keyValues;
        private int recursiveDepth;
        private File rootDirectory;
        
        public SubDirectoryWalker(List<KeyValue> keyValues) {
            super(DirectoryFileFilter.DIRECTORY, -1);
            this.keyValues = keyValues;
            this.recursiveDepth = 0;
        }

        public void addKeyValues(File startDirectory) throws IOException {
            rootDirectory = startDirectory;
            walk(startDirectory, null);
        }
        
        /**
         * @see org.apache.commons.io.DirectoryWalker#handleDirectoryStart(java.io.File, int, java.util.Collection)
         */
        @Override
        protected void handleDirectoryStart(File directory, int depth, Collection results) throws IOException {
            super.handleDirectoryStart(directory, depth, results);
            ConcreteKeyValue entry = new ConcreteKeyValue();
            entry.setKey(BatchFileUtils.pathRelativeToRootDirectory(directory.getAbsolutePath()));
            // use the unicode literal for space....KFSMI-7392 fix
            entry.setValue( StringUtils.repeat("\u00A0", 4 * this.recursiveDepth) + directory.getName());
            keyValues.add(entry);
            this.recursiveDepth++;
        }

        /**
         * @see org.apache.commons.io.DirectoryWalker#handleDirectoryEnd(java.io.File, int, java.util.Collection)
         */
        @Override
        protected void handleDirectoryEnd(File directory, int depth, Collection results) throws IOException {
            super.handleDirectoryEnd(directory, depth, results);
            this.recursiveDepth--;
        }
    }
}
