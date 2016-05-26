package edu.arizona.kfs.sys.batch;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.kuali.kfs.sys.batch.BatchJobStatus;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.rice.core.api.datetime.DateTimeService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * This class allows for remote invocation of batch jobs via the file system and watches for files with a
 * specific name format to invoke the appropriate job. It also reports the status of the invoked batch jobs
 * in a status file.
 *
 * @author jpbriede 2012, shaloo 2016
 */
public class RemoteBatchJobInvoker extends TimerTask {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RemoteBatchJobInvoker.class);
    private static String NEWLINE = System.getProperty("line.separator").toString();

    private static final int DIRECTORY_DEPTH_LIMIT = 1;
    private static final String FILENAME_REGEX = "^(.+)_{1}(\\d{14}).*$";
    private static final String HOSTNAME_REGEX = ".*";
    private static final int WARNING_LOG_FREQUENCY = 12;
    public static final String REFUSED_JOB_STATUS_CODE = "Refused";
    public static final String NULL_JOB_STATUS_CODE = "Null Status";
    public static final String STARTING_STATUS_CODE = "Starting";
    public static final String UNRUNNABLE_JOB_MESSAGE = "Unable to run job!";

    protected String directoryToWatch;
    protected String historyDirectory;
    protected String runFileSuffix;
    protected String statusFileSuffix;
    protected String hostFileSuffix;
    protected DateTimeService dateTimeService;
    protected SchedulerService schedulerService;

    protected IOFileFilter runFileFilter;
    protected IOFileFilter statusFileFilter;
    protected IOFileFilter hostFileFilter;
    protected Pattern filenamePattern;
    protected Pattern hostnamePattern;
    protected Map<String, String> runningJobs;

    protected Long startUpTime;
    protected int warningLogTicks;

    public RemoteBatchJobInvoker() {
        filenamePattern = Pattern.compile(FILENAME_REGEX);
        hostnamePattern = Pattern.compile(HOSTNAME_REGEX);
        runningJobs = new HashMap<String, String>();
        startUpTime = null;
        warningLogTicks = 0;
    }

    @Override
    public void run() {
        // Check if scheduler service has loaded its list of jobs and is ready and
        // this host is signaled to run the invoker.
        try {
            if(!schedulerService.getJobs().isEmpty() && isRunnableOnHost()) {
                // Once the scheduler service has started up, we'll check if the startUpTime has been
                // recorded yet, and if not, we'll record the startUpTime and remove any stale
                // run files that were created before the service was ready.
                if(startUpTime == null) {
                    startUpTime = new Date().getTime();
                    clearStaleRunFiles(startUpTime);
                }
                refreshRunningJobs();
                startNewJobs();
                checkRunningJobs();
            }
        } catch (RuntimeException e) {
            LOG.error("Could not invoke batch jobs", e);
        }
    }

    protected void refreshRunningJobs() {
        runningJobs.clear();

        List<File> statusFiles = lookForFiles(statusFileFilter);
        for(File statusFile : statusFiles) {
            String baseName = FilenameUtils.getBaseName(statusFile.getName());
            String jobName = parseJobnameFromFilename(baseName);
            runningJobs.put(jobName, baseName);
        }
    }

    protected boolean isRunnableOnHost() {
        String hostname = getHostName();

        List<File> runnables = lookForFiles(hostFileFilter);
        if(runnables.size() != 1) {
            if((warningLogTicks % WARNING_LOG_FREQUENCY) == 0) {
                LOG.warn("Exactly one host must be signaled to invoke batch jobs with a .runnable file. " +
                        "No batch jobs will be invoked until one and only one host is signaled to invoke them.");
            }
            warningLogTicks++;
            return false;
        } else {
            warningLogTicks = 0;
        }

        File runnable = runnables.get(0);
        String runnableHost = FilenameUtils.getBaseName(runnable.getName());
        return hostname.equals(runnableHost);
    }

    protected String getHostName() {
        String hostname = "unknown";
        java.net.InetAddress localhost = null;
        try {
            localhost = java.net.InetAddress.getLocalHost();
        }
        catch (UnknownHostException ex) {
            LOG.error("Unknown host", ex);
        }

        if (localhost != null) {
            hostname = localhost.getHostName();
        }
        return hostname;
    }

    protected void startNewJobs() {
        List<File> runFiles = lookForFiles(runFileFilter);

        for(File runFile : runFiles) {
            String baseName = FilenameUtils.getBaseName(runFile.getName());
            String jobName = parseJobnameFromFilename(baseName);

            String statusFileName = baseName + statusFileSuffix;
            createStatusFile( runFile, statusFileName );
            writeJobStatus(baseName, STARTING_STATUS_CODE);
            boolean jobRan = runBatchJob(jobName, baseName);
            if(jobRan) {
                runningJobs.put(jobName, baseName);
            } else {
                writeJobStatus(baseName, REFUSED_JOB_STATUS_CODE);
                moveStatusFileToHistory(baseName);
            }
        }
    }

    protected boolean runBatchJob(String jobName, String baseName) {
        // Job is still in the running state. Do not run job again.
        if(runningJobs.containsKey(jobName) || SchedulerService.RUNNING_JOB_STATUS_CODE.equals(getJobStatus(jobName))) {
            return false;
        }

        try {
            schedulerService.runJob(jobName, null);
        }
        catch (RuntimeException e) {
            writeJobStatus(baseName, UNRUNNABLE_JOB_MESSAGE);
            return false;
        }

        return true;
    }

    protected String parseJobnameFromFilename(String baseName) {
        Matcher match = filenamePattern.matcher(baseName);
        if(match.find()) {
            return match.group(1);
        } else {
            return null;
        }
    }

    protected void createStatusFile(File runFile, String statusFileName) {
        try {
            String path = directoryToWatch + File.separator;
            File statusFile = new File(path + statusFileName);
            FileUtils.moveFile(runFile, statusFile);
        }
        catch (IOException ex) {
            LOG.error("Failed to create status file: " + statusFileName, ex);
        }
    }

    protected void checkRunningJobs() {
        String jobBaseName;
        String jobStatus;

        for(String jobName : runningJobs.keySet()) {
            jobBaseName = runningJobs.get(jobName);
            jobStatus = getJobStatus(jobName);
            writeJobStatus(jobBaseName, jobStatus);

            if(RemoteBatchJobInvoker.NULL_JOB_STATUS_CODE.equals(jobStatus)
                    || SchedulerService.SUCCEEDED_JOB_STATUS_CODE.equals(jobStatus)
                    || SchedulerService.FAILED_JOB_STATUS_CODE.equals(jobStatus)
                    || SchedulerService.CANCELLED_JOB_STATUS_CODE.equals(jobStatus)) {
                moveStatusFileToHistory(jobBaseName);
            }
        }
    }

    protected void moveStatusFileToHistory(String jobBaseName) {
        File srcFile = new File(directoryToWatch + File.separator + jobBaseName + statusFileSuffix);
        moveFileToHistory(srcFile);
    }

    protected void moveFileToHistory(File srcFile) {
        File destDir = new File(historyDirectory);
        try {
            // Move the file to the history. Will throw exception if file already
            // exists in destination folder.
            FileUtils.moveFileToDirectory(srcFile, destDir, true);
        }
        catch (IOException ex) {
            LOG.error("Could not move the file: " + srcFile.getName() + " to history directory: " + historyDirectory, ex);
        }
    }

    protected String getJobStatus(String jobName) {
        String status = null;
        List<BatchJobStatus> allJobs = schedulerService.getJobs();

        for (BatchJobStatus jobStatus : allJobs) {
            if (jobStatus.getName().equalsIgnoreCase(jobName)) {
                status = jobStatus.getStatus();
                break;
            }
        }

        if(status == null) {
            status = NULL_JOB_STATUS_CODE;
        }
        return status;
    }

    protected void writeJobStatus(String jobBaseName, String jobStatus) {
        final String filename = directoryToWatch + File.separator + jobBaseName + statusFileSuffix;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(filename, true));

            Date now = dateTimeService.getCurrentDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]");

            bufferedWriter.write(dateFormat.format(now) + " " + jobStatus + NEWLINE);
        } catch (IOException ex) {
            LOG.error("Could not write to batch job status file: " + filename);
        } finally {
            try {
                if(bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                LOG.error("Could not close batch job status file: " + filename);
            }
        }
    }

    protected IOFileFilter setupFileFilter(Pattern filePattern, String fileExtension) {
        RegexFileFilter regexFileFilter = new RegexFileFilter(filePattern);

        IOFileFilter filter = FileFilterUtils.fileFileFilter();
        filter = FileFilterUtils.andFileFilter(filter, FileFilterUtils.suffixFileFilter(fileExtension));
        filter = FileFilterUtils.andFileFilter(filter, regexFileFilter);

        return filter;
    }

    protected List<File> lookForFiles(IOFileFilter filter) {
        List<File> foundFiles = new ArrayList<File>();
        FileFinder fileFinder = new FileFinder(foundFiles, filter);
        fileFinder.find(new File(directoryToWatch));

        return foundFiles;
    }

    protected class FileFinder extends DirectoryWalker {
        List<File> foundFiles;

        public FileFinder(List<File> foundFiles, IOFileFilter fileFilter) {
            super(fileFilter, DIRECTORY_DEPTH_LIMIT);

            this.foundFiles = foundFiles;
        }

        public void find(File directory) {
            try {
                walk(directory, null);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected void handleFile(File file, int depth, Collection results) throws IOException {
            foundFiles.add(file);
        }
    }

    protected void clearStaleRunFiles(long expirationTime) {
        if(runFileFilter == null) {
            return;
        }

        List<File> runFiles = lookForFiles(runFileFilter);
        for(File runFile : runFiles) {
            if(FileUtils.isFileOlder(runFile, expirationTime)) {
                moveFileToHistory(runFile);
            }
        }
    }

    public void setDirectoryToWatch(String directoryToWatch) {
        this.directoryToWatch = directoryToWatch;
    }

    public void setHistoryDirectory(String historyDirectory) {
        this.historyDirectory = historyDirectory;
    }

    public void setRunFileSuffix(String runFileSuffix) {
        this.runFileSuffix = runFileSuffix;
        this.runFileFilter = setupFileFilter(filenamePattern, this.runFileSuffix);
    }

    public void setStatusFileSuffix(String statusFileSuffix) {
        this.statusFileSuffix = statusFileSuffix;
        this.statusFileFilter = setupFileFilter(filenamePattern, this.statusFileSuffix);
    }

    public void setHostFileSuffix(String hostFileSuffix) {
        this.hostFileSuffix = hostFileSuffix;
        this.hostFileFilter = setupFileFilter(hostnamePattern, this.hostFileSuffix);
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}
