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

