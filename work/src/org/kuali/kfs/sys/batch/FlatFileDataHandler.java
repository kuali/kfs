package org.kuali.kfs.sys.batch;

/**
 * The interface for a FlatFileDataValidator.  Implementations will have code which validates the parsed contents of the flat file.
 */
public interface FlatFileDataHandler {
	
	/**
     * Performs specific validation on the parsed file contents. If errors were found, method will return false and
     * GlobalVariables.errorMap will contain the error message. If no errors were encountered the method will return true.
     * 
     * @param parsedFileContents - object populated with the uploaded file contents
     */
	public abstract boolean validate(Object parsedFileContents);
	
	 /**
     * Invokes optional processing of file after validation
     * 
     * @param fileName name of the file
     * @param parsedFileContents objects populated with file contents
     */
    public abstract void process(String fileName, Object parsedFileContents);
    
    /**
     * Returns the name of an uploaded file. 
     */
    public  String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifier) ;
        
   
}
