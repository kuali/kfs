/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.businessobject.lookup;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.sys.batch.BatchFile;
import org.kuali.kfs.sys.batch.BatchFileUtils;
import org.kuali.kfs.sys.batch.service.BatchFileAdminAuthorizationService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;

public class BatchFileLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    protected DateTimeService dateTimeService;
    protected BatchFileAdminAuthorizationService batchFileAdminAuthorizationService;
    
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<BatchFile> results = new ArrayList<BatchFile>();

        IOFileFilter filter = FileFilterUtils.fileFileFilter();
        
        IOFileFilter pathBasedFilter = getPathBasedFileFilter();
        if (pathBasedFilter != null) {
            filter = FileFilterUtils.andFileFilter(filter, pathBasedFilter);
        }
        
        String fileNamePattern = fieldValues.get("fileName");
        IOFileFilter fileNameBasedFilter = getFileNameBasedFilter(fileNamePattern);
        if (fileNameBasedFilter != null) {
            filter = FileFilterUtils.andFileFilter(filter, fileNameBasedFilter);
        }
        
        String lastModifiedDate = fieldValues.get("lastModifiedDate");
        IOFileFilter lastModifiedDateBasedFilter = getLastModifiedDateBasedFilter(lastModifiedDate);
        if (lastModifiedDateBasedFilter != null) {
            filter = FileFilterUtils.andFileFilter(filter, lastModifiedDateBasedFilter);
        }
        
        BatchFileFinder finder = new BatchFileFinder(results, filter);
        List<File> rootDirectories = BatchFileUtils.retrieveBatchFileLookupRootDirectories();
        finder.find(rootDirectories);
        
        return results;
    }

    protected IOFileFilter getPathBasedFileFilter() {
        List<File> selectedFiles = getSelectedDirectories(getSelectedPaths());
        if (selectedFiles.isEmpty()) {
            return null;
        }
        IOFileFilter fileFilter = null;
        for (File selectedFile : selectedFiles) {
            IOFileFilter subFilter = new SubDirectoryFileFilter(selectedFile);
            if (fileFilter == null) {
                fileFilter = subFilter;
            }
            else {
                fileFilter = FileFilterUtils.orFileFilter(fileFilter, subFilter);
            }
        }
        return fileFilter;
    }
    
    protected IOFileFilter getFileNameBasedFilter(String fileNamePattern) {
        if (StringUtils.isNotBlank(fileNamePattern)) {
            return new WildcardFileFilter(fileNamePattern, IOCase.INSENSITIVE);
        }
        return null;
    }
    
    protected IOFileFilter getLastModifiedDateBasedFilter(String lastModifiedDatePattern) {
        if (StringUtils.isBlank(lastModifiedDatePattern)) {
            return null;
        }
        try {
            if (lastModifiedDatePattern.startsWith("<=")) {
                String dateString = StringUtils.removeStart(lastModifiedDatePattern, "<=");
                Date toDate = dateTimeService.convertToDate(dateString);
                return new LastModifiedDateFileFilter(null, toDate);
            }
            if (lastModifiedDatePattern.startsWith(">=")) {
                String dateString = StringUtils.removeStart(lastModifiedDatePattern, ">=");
                Date fromDate = dateTimeService.convertToDate(dateString);
                return new LastModifiedDateFileFilter(fromDate, null);
            }
            if (lastModifiedDatePattern.contains("..")) {
                String[] dates = StringUtils.splitByWholeSeparator(lastModifiedDatePattern, "..", 2);
                Date fromDate = dateTimeService.convertToDate(dates[0]);
                Date toDate = dateTimeService.convertToDate(dates[1]);
                return new LastModifiedDateFileFilter(fromDate, toDate);
            }
        }
        catch (ParseException e) {
            throw new RuntimeException("Can't parse date", e);
        }
        throw new RuntimeException("Unable to perform search using last modified date " + lastModifiedDatePattern);
    }
    
    protected List<File> getSelectedDirectories(String[] selectedPaths) {
        List<File> directories = new ArrayList<File>();
        if (selectedPaths != null) {
            for (String selectedPath : selectedPaths) {
                directories.add(new File(selectedPath));
            }
        }
        return directories;
    }
    
    protected String[] getSelectedPaths() {
        List<Row> rows = getRows();
        if (rows == null) {
            return null;
        }
        for (Row row : rows) {
            for (Field field : row.getFields()) {
                if ("path".equals(field.getPropertyName()) && Field.MULTISELECT.equals(field.getFieldType())) {
                    return field.getPropertyValues();
                }
            }
        }
        return null;
    }
    
    protected class SubDirectoryFileFilter extends AbstractFileFilter {
        private File superDirectory;
        
        public SubDirectoryFileFilter(File superDirectory) {
            this.superDirectory = superDirectory.getAbsoluteFile();
        }
        
        @Override
        public boolean accept(File file) {
            file = file.getAbsoluteFile();
            file = file.getParentFile();
            while (file != null) {
                if (file.equals(superDirectory)) {
                    return true;
                }
                file = file.getParentFile();
            }
            return false;
        }
    }
    
    protected class LastModifiedDateFileFilter extends AbstractFileFilter {
        private Date fromDate;
        private Date toDate;
        
        public LastModifiedDateFileFilter(Date fromDate, Date toDate) {
            this.fromDate = fromDate;
            this.toDate = toDate;
        }
        
        @Override
        public boolean accept(File file) {
            Date lastModifiedDate = DateUtils.clearTimeFields(new Date(file.lastModified()));
            
            if (fromDate != null && fromDate.after(lastModifiedDate)) {
                return false;
            }
            if (toDate != null && toDate.before(lastModifiedDate)) {
                return false;
            }
            return true;
        }
    }
    
    protected class BatchFileFinder extends DirectoryWalker {
        private List<BatchFile> results;
        
        public BatchFileFinder(List<BatchFile> results, IOFileFilter fileFilter) {
            super(null, fileFilter, -1);
            this.results = results;
        }
        
        public void find(Collection<File> rootDirectories) {
            try {
                for (File rootDirectory : rootDirectories) {
                    walk(rootDirectory, null);
                }
            }
            catch (IOException e) {
                throw new RuntimeException("Error performing lookup", e);
            }
        }

        /**
         * @see org.apache.commons.io.DirectoryWalker#handleFile(java.io.File, int, java.util.Collection)
         */
        @Override
        protected void handleFile(File file, int depth, Collection results) throws IOException {
            super.handleFile(file, depth, results);
            BatchFile batchFile = new BatchFile();
            batchFile.setFile(file);
            this.results.add(batchFile);
        }
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> links = new ArrayList<HtmlData>();
        
        BatchFile batchFile = (BatchFile) businessObject;
        if (canDownloadFile(batchFile)) {
            links.add(getDownloadUrl(batchFile));
        }
        if (canDeleteFile(batchFile)) {
            links.add(getDeleteUrl(batchFile));
        }
        return links;
    }

    protected boolean canDownloadFile(BatchFile batchFile) {
        return batchFileAdminAuthorizationService.canDownload(batchFile, GlobalVariables.getUserSession().getPerson());
    }

    protected boolean canDeleteFile(BatchFile batchFile) {
        return batchFileAdminAuthorizationService.canDelete(batchFile, GlobalVariables.getUserSession().getPerson());
    }
    
    protected HtmlData getDownloadUrl(BatchFile batchFile) {
        Properties parameters = new Properties();
        parameters.put("filePath", batchFile.retrieveFile().getAbsolutePath());
        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, "download");
        String href = UrlFactory.parameterizeUrl(CamsConstants.StrutsActions.ONE_UP + "batchFileAdmin.do", parameters);
        return new AnchorHtmlData(href, "download", "Download");
    }
    
    protected HtmlData getDeleteUrl(BatchFile batchFile) {
        Properties parameters = new Properties();
        parameters.put("filePath", batchFile.retrieveFile().getAbsolutePath());
        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, "delete");
        String href = UrlFactory.parameterizeUrl(CamsConstants.StrutsActions.ONE_UP + "batchFileAdmin.do", parameters);
        return new AnchorHtmlData(href, "delete", "Delete");
    }
    
    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);
        
        String[] selectedPaths = getSelectedPaths();
        if (selectedPaths != null) {
            for (String selectedPath : selectedPaths) {
                if (!BatchFileUtils.isDirectoryAccessible(selectedPath)) {
                    throw new RuntimeException("Can't access path " + selectedPath);
                }
            }
        }
    }

    /**
     * Sets the batchFileAdminAuthorizationService attribute value.
     * @param batchFileAdminAuthorizationService The batchFileAdminAuthorizationService to set.
     */
    public void setBatchFileAdminAuthorizationService(BatchFileAdminAuthorizationService batchFileAdminAuthorizationService) {
        this.batchFileAdminAuthorizationService = batchFileAdminAuthorizationService;
    }
}    
