
package org.kuali.module.purap.dao;

/**
 * Image DAO Interface.
 */
public interface ImageDao {
    
    /**
     * Get the purchasing director signature image.  This will get the image
     * from webdav and copy it to a temp directory.
     * 
     * @param key - Key for use in filename to make it unique
     * @param campusCode - Campus code for image
     * @param location - location of where image resides
     * @return - Full path on the local box for image file name
     */
    public String getPurchasingDirectorImage(String key,String campusCode,String location);

    /**
     * Get the contract manager signature image.  This will get the image
     * from webdav and copy it to a temp directory.
     * 
     * @param key - Key for use in filename to make it unique
     * @param campusCode - Contract manager ID for image
     * @param location - location of where image resides
     * @return - Full path on the local box for image file name
     */
    public String getContractManagerImage(String key,Integer contractManagerId,String location);

    /**
     * Get the campus logo image.  This will get the image
     * from webdav and copy it to a temp directory.
     * 
     * @param key - Key for use in filename to make it unique
     * @param campusCode - Campus code for image
     * @param location - location of where image resides
     * @return - Full path on the local box for image file name
     */
    public String getLogo(String key,String campusCode,String location);

    /**
     * Remove temporary images
     * 
     * @param key - Key for use in the filenames
     * @param location - location of where image resides
     */
    public void removeImages(String key,String location);
    
}
