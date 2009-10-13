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
package org.kuali.kfs.module.purap.util;

import java.util.List;

import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.purap.businessobject.PurApItem;

/**
 * Defines an abstraction for parsing serialized <code>PurApItem</code> lines.
 */
public interface ItemParser {

    /**
     * Returns the defined format of item lines in the item import file.
     * 
     * @return the item line format as an array of item property names
     */
    public String[] getItemFormat();
    
    /**
     * Returns the expected format of the items to be imported.
     * 
     * @param itemClass the class of the items to be imported
     * @return the concatenation of the actual property names of the items to be imported
     */
    public String getExpectedItemFormatAsString( Class<? extends PurApItem> itemClass );
    
    /**
     * Parses the specified item line into an instance of the specified PurApItem subclass.
     * 
     * @param itemLine the item line string to be parsed
     * @param itemClass the subclass of the item to be generated
     * @param documentNumber the number of the docment that contains the item to be generated
     * @return the generated item
     */
    public PurApItem parseItem( String itemLine, Class<? extends PurApItem> itemClass, String documentNumber );

    /**
     * Parses the items from the specified import file line by line,
     * and generates items of the specified type from the parsed data.
     * 
     * @param itemFile the input file from which items are parsed
     * @param itemClass a subclass of PurApItem, of which new items shall be generated
     * @param documentNumber the number of the docment that contains the items to be imported
     * @return a list of items of a subclass of PurApItem.
     */
    public List<PurApItem> importItems( FormFile itemFile, Class<? extends PurApItem> itemClass, String documentNumber );

/****    
 * 
 */
  /**
   * Reads lines of <code>PurApItem</code> fields from the <code>InputStream</code> and parses them. 
   *
   * @param inputStream The <code>{@link InputStream}</code> to read data from.
   * @param itemClass The subclass of <code>PurApItem</code> which parsed items belong to.
   * @return A list of <code>{@link PurApItem}</code> instances.
   * @exception IOException
   *
  public List importItemLines(InputStream inputStream, Class<? extends PurApItem> itemClass)
      throws IOException, IllegalAccessException, InstantiationException;

  /**
   * Determines the number of fields to be parsed.
   *
   * @return int number of fields expected.
   *
  public int getExpectedFieldCount();
  
****/  
}

