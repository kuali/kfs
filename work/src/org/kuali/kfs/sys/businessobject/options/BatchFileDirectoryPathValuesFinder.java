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
