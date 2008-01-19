/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.purap.util;

import static org.kuali.module.purap.PurapKeyConstants.ERROR_ITEMPARSER_INVALID_FILE_FORMAT;
import static org.kuali.module.purap.PurapKeyConstants.ERROR_ITEMPARSER_ITEMLINE;
import static org.kuali.module.purap.PurapKeyConstants.ERROR_ITEMPARSER_WRONG_PROPERTY_NUMBER;
import static org.kuali.module.purap.PurapKeyConstants.ERROR_ITEMPARSER_EMPTY_PROPERTY_VALUE;
import static org.kuali.module.purap.PurapKeyConstants.ERROR_ITEMPARSER_INVALID_PROPERTY_VALUE;
import static org.kuali.module.purap.PurapPropertyConstants.ITEM_CATALOG_NUMBER;
import static org.kuali.module.purap.PurapPropertyConstants.ITEM_DESCRIPTION;
import static org.kuali.module.purap.PurapPropertyConstants.ITEM_QUANTITY;
import static org.kuali.module.purap.PurapPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE;
import static org.kuali.module.purap.PurapPropertyConstants.ITEM_UNIT_PRICE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.FormatException;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.exceptions.ItemParserException;

public class ItemParserBase implements ItemParser {

    protected static final String[] DEFAULT_FORMAT = {ITEM_QUANTITY, ITEM_UNIT_OF_MEASURE_CODE, ITEM_CATALOG_NUMBER, ITEM_DESCRIPTION, ITEM_UNIT_PRICE};
    private Integer lineNo = 0;

    /**
     * @see org.kuali.module.purap.util.ItemParser#getItemFormat()
     */
    public String[] getItemFormat() {
        return DEFAULT_FORMAT;
    }

    /**
     * @see org.kuali.module.purap.util.ItemParser#getExpectedItemFormatAsString(java.lang.Class)
     */
    public String getExpectedItemFormatAsString( Class<? extends PurApItem> itemClass ) {
        checkItemClass( itemClass );
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (String attributeName : getItemFormat()) {
            if (!first) {
                sb.append(",");
            }
            else {
                first = false;
            }
            sb.append( getAttributeLabel( itemClass, attributeName ) );
        }
        return sb.toString();
    }

    /**
     * Retrieves the attribute label for the specified attribute.
     * 
     * @param clazz the class in which the specified attribute is defined
     * @param attributeName the name of the specified attribute
     * @return the attribute label for the specified attribute
     */
    protected String getAttributeLabel( Class clazz, String attributeName ) {
        String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(clazz, attributeName);
        if (StringUtils.isBlank(label)) {
            label = attributeName;
        }
        return label;
    }

    /**
     * Checks whether the specified item class is a subclass of PurApItem;
     * throws exceptions if not.
     * 
     * @param itemClass the specified item class
     */
    private void checkItemClass(Class<? extends PurApItem> itemClass) {
        if (!PurApItem.class.isAssignableFrom(itemClass)) {
            throw new IllegalArgumentException("unknown item class: " + itemClass);
        }
    }
    
    /**
     * Checks whether the specified item import file is not null and of a valid format;
     * throws exceptions if conditions not satisfied.
     * 
     * @param itemClass the specified item import file
     */
    private void checkItemFile(FormFile itemFile) {
        if (itemFile == null) {
            throw new ItemParserException("invalid (null) item import file", KFSKeyConstants.ERROR_UPLOADFILE_NULL);
        }
        String fileName = itemFile.getFileName();
        if (StringUtils.isNotBlank(fileName) && !StringUtils.lowerCase(fileName).endsWith(".csv") && !StringUtils.lowerCase(fileName).endsWith(".xls")) {
            throw new ItemParserException("unsupported item import file format: " + fileName, ERROR_ITEMPARSER_INVALID_FILE_FORMAT, fileName);
        }
    }

    /**
     * Parses a line of item data from a csv file and retrieves the attributes as key-value string pairs into a map.
     * 
     * @param itemLine a string read from a line in the item import file
     * @return a map containing item attribute name-value string pairs
     */
    private Map<String, String> retrieveItemAttributes( String itemLine ) {
        String[] attributeNames = getItemFormat();
        String[] attributeValues = StringUtils.splitPreserveAllTokens(itemLine, ',');
        if ( attributeNames.length != attributeValues.length ) {
            String[] errorParams = { "" + attributeNames.length, "" + attributeValues.length, "" + lineNo };
            GlobalVariables.getErrorMap().putError( PurapConstants.ITEM_TAB_ERRORS, ERROR_ITEMPARSER_WRONG_PROPERTY_NUMBER, errorParams );
            throw new ItemParserException("wrong number of item properties: " + attributeValues.length + " exist, " + attributeNames.length + " expected (line " + lineNo + ")", ERROR_ITEMPARSER_WRONG_PROPERTY_NUMBER, errorParams); 
        }

        Map<String, String> itemMap = new HashMap<String, String>();
        for (int i=0; i < attributeNames.length; i++) {
            itemMap.put( attributeNames[i], attributeValues[i] );
        }
        return itemMap;
    }
    
    /**
     * Generates an item instance and populates it with the specified attribute map.
     * 
     * @param itemMap the specified attribute map from which attributes are populated
     * @param itemClass the class of which the new item instance shall be created
     * @return the populated item
     */
    private PurApItem genItemWithRetrievedAttributes( Map<String, String> itemMap, Class<? extends PurApItem> itemClass ) {
        PurApItem item;
        try {
            item = itemClass.newInstance();
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("unable to complete item line population.", e);
        }
        catch (InstantiationException e) {
            throw new InfrastructureException("unable to complete item line population.", e);
        }
        
        boolean failed = false;
        for (Entry<String, String> entry : itemMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();          
            try {
                if ( (key.equals(ITEM_DESCRIPTION) || key.equals(ITEM_UNIT_PRICE)) && value.equals("") ) {
                    String[] errorParams = { key, "" + lineNo };
                    throw new ItemParserException("empty property value for " + key + " (line " + lineNo + ")", ERROR_ITEMPARSER_EMPTY_PROPERTY_VALUE, errorParams);                    
                }
                Class type = ObjectUtils.easyGetPropertyType( item, key );
                if (String.class.isAssignableFrom(type)) { // force uppercase
                    entry.setValue(value.toUpperCase());
                }
                try {
                    ObjectUtils.setObjectProperty(item, key, value);
                }
                catch (FormatException e) {
                    String[] errorParams = { value, key, "" + lineNo };
                    throw new ItemParserException("invalid property value: " + key + " = " + value + " (line " + lineNo + ")", ERROR_ITEMPARSER_INVALID_PROPERTY_VALUE, errorParams);
                }
            }
            catch (ItemParserException e) {
                // continue to parse the rest of the item properties after the current property fails
                GlobalVariables.getErrorMap().putError( PurapConstants.ITEM_TAB_ERRORS, e.getErrorKey(), e.getErrorParameters() );
                failed = true;
            }
            catch (IllegalAccessException e) {
                throw new InfrastructureException("unable to complete item line population.", e);
            }
            catch (NoSuchMethodException e) {
                throw new InfrastructureException("unable to complete item line population.", e);
            }
            catch (InvocationTargetException e) {
                throw new InfrastructureException("unable to complete item line population.", e);
            }
        }

        if (failed) {
            throw new ItemParserException("empty or invalid item properties in line " + lineNo + ")");             
        }
        return item;
    }

    /**
     * Populates extra item attributes not contained in the imported item data to default values.
     * 
     * @param item the item to be populated
     * @param documentNumber the number of the docment that contains the item
     */
    private void populateExtraAttributes( PurApItem item, String documentNumber ) {     
        if (item.getItemQuantity() == null) {
            //TODO: fetch the default item type code from DB param
            item.setItemTypeCode(PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE);
        }
        if (item instanceof RequisitionItem)
            ((RequisitionItem)item).setItemRestrictedIndicator(false);
        if (item instanceof PurchaseOrderItem)
            ((PurchaseOrderItem)item).setDocumentNumber(documentNumber);
    }
    
    /**
     * @see org.kuali.module.purap.util.ItemParser#parseItem(java.lang.String,java.lang.Class,java.lang.String)
     */
    public PurApItem parseItem( String itemLine, Class<? extends PurApItem> itemClass, String documentNumber ) {
        Map<String, String> itemMap = retrieveItemAttributes( itemLine );
        PurApItem item = genItemWithRetrievedAttributes( itemMap, itemClass );
        populateExtraAttributes( item, documentNumber );
        item.refresh();
        return item;
    }
    
    /**
     * @see org.kuali.module.purap.util.ItemParser#parseItem(org.apache.struts.upload.FormFile,java.lang.Class,java.lang.String)
     */
    public List<PurApItem> importItems( FormFile itemFile, Class<? extends PurApItem> itemClass, String documentNumber ) {
        // check input parameters
        try {
            checkItemClass( itemClass );
            checkItemFile( itemFile );
        }
        catch (IllegalArgumentException e) {
            throw new InfrastructureException("unable to import items.", e);
        }

        // open input stream
        List<PurApItem> importedItems = new ArrayList<PurApItem>();
        InputStream is;
        BufferedReader br;
        try {
            is = itemFile.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
        }
        catch (IOException e) {
            throw new InfrastructureException("unable to open import file in ItemParserBase.", e);
        }
        
        // parse items line by line
        lineNo = 0;
        boolean failed = false;
        String itemLine = null;
        try {
            while ( (itemLine = br.readLine()) != null ) {
                lineNo++;
                try {
                    PurApItem item = parseItem( itemLine, itemClass, documentNumber );
                    importedItems.add(item);
                }
                catch (ItemParserException e) {
                    // continue to parse the rest of the items after the current item fails
                    // error messages are already dealt with inside parseItem, so no need to do anything here
                    failed = true;
                }                
            }
            
            if (failed) {
                throw new ItemParserException("errors in parsing item lines in file " + itemFile.getFileName(), ERROR_ITEMPARSER_ITEMLINE, itemFile.getFileName());             
            }
        }
        catch (IOException e) {
            throw new InfrastructureException("unable to read line from BufferReader in ItemParserBase", e);
        }
        finally {
            try {
                br.close();
            }
            catch (IOException e) {
                throw new InfrastructureException("unable to close BufferReader in ItemParserBase", e);
            }
        }

        return importedItems;
    }

/****

    private static final int EXPECTED_FIELDS = 5;

    /**
     * Determines the number of fields to be parsed.
     * 
     * @return int number of fields expected.
     *
    public int getExpectedFieldCount() {
        return EXPECTED_FIELDS;
    }

    /**
     * Converts a whole line read from BufferredReader into an instance of PurApItem's sublclass,
     * which contains the fields that are needed in RequisitionItem and PurchaseOrderItem.
     * 
     * @param currentLine The current line to be parsed.
     * @return the generated item from the parsed line.
     * @throws IOException
     *
    private PurApItem parseItem(String currentLine, Class<? extends PurApItem> itemClass) throws IOException, IllegalAccessException, InstantiationException {
        PurApItem item = itemClass.newInstance();
        String[] itemDetailLineData = new String[EXPECTED_FIELDS];
        int arrayIndex = 0;

        // call tokenizeLine to populate the itemDetailLineData by
        // tokenizing the entire currentLine read in from BufferredReader
        StringBuffer currentLineBuffer = new StringBuffer(currentLine);
        tokenizeLine(currentLineBuffer, itemDetailLineData, arrayIndex);

        // after we finish parsing 1 line, populate the tokens into item
        KualiDecimal quantity = null;
        if (!StringUtils.isEmpty(itemDetailLineData[0])) {
            // check whether there's a comma in the field, if so, remove it to avoid NumberFormatException
            int commaPos = itemDetailLineData[0].indexOf(",");
            if (commaPos > 0) {
                String quantityString = new String();
                StringTokenizer tokenizer = new StringTokenizer(itemDetailLineData[0], ",");
                while (tokenizer.hasMoreTokens()) {
                    String partial = tokenizer.nextToken();
                    quantityString = quantityString + partial;
                }
                quantity = new KualiDecimal(quantityString);
            }
            else {
                quantity = new KualiDecimal(itemDetailLineData[0]);
            }
        }
        
        // if the unit of measure code is not null, we'll convert it to upper case
        String unitOfMeasureCode = (itemDetailLineData[1] != null ? itemDetailLineData[1].toUpperCase() : null);
        String catalogNumber = itemDetailLineData[2];
        String description = itemDetailLineData[3];
        BigDecimal unitPrice = null;

        // check whether there's a comma in the field, if so, remove it to avoid
        // NumberFormatException
        int commaPos = itemDetailLineData[4].indexOf(",");
        if (commaPos > 0) {
            String unitPriceString = new String();
            StringTokenizer tokenizer = new StringTokenizer(itemDetailLineData[4], ",");
            while (tokenizer.hasMoreTokens()) {
                String partial = tokenizer.nextToken();
                unitPriceString = unitPriceString + partial;
            }
            unitPrice = new BigDecimal(unitPriceString);
        }
        else {
            unitPrice = new BigDecimal(itemDetailLineData[4]);
        }
        
        // populate item with parsed fields
        item.setItemQuantity(quantity);
        item.setItemUnitOfMeasureCode(unitOfMeasureCode);
        item.setItemCatalogNumber(catalogNumber);
        item.setItemDescription(description);
        item.setItemUnitPrice(unitPrice);
        return item;
    }

    /**
     * Breaks a line of string to be processed into tokens recursively; 
     * extracts the first token, then parses the remainder of the line recursively.
     * 
     * @param currentLine The current line to be parsed.
     *
    private void tokenizeLine(StringBuffer currentLineBuf, String[] itemDetailLineData, int arrayIndex) throws IOException {
        StringBuffer firstTokenBuf = new StringBuffer();
        StringBuffer endLineBuf = new StringBuffer();
        CharSequence firstTokenSeq = new StringBuffer();
        CharSequence endLineSeq = new StringBuffer();

        int commaPos = findTheFirstDelimiter(currentLineBuf);
        if (commaPos > -1) {
            if (currentLineBuf.length() > commaPos) {
                // if we find the position of the first delimiting comma, then we'll
                // create a substring to extract out the first token for further
                // processing.
                firstTokenSeq = currentLineBuf.subSequence(0, commaPos);
                endLineSeq = currentLineBuf.subSequence(commaPos + 1, currentLineBuf.length());
                firstTokenBuf.append(firstTokenSeq);
                endLineBuf.append(endLineSeq);
            }
        }
        else {
            // couldn't find any commas, so the token is the entire line
            firstTokenBuf = currentLineBuf;
        }

        // only need to remove excess quotes if firstTokenBuf is longer than 0
        if (firstTokenBuf.length() > 0) {
            // remove the double quotes that are surrounding the first token but
            // not part of the token
            firstTokenBuf = removeExcessQuotes(firstTokenBuf);
        }
        
        // push the firstToken into the array
        itemDetailLineData[arrayIndex] = firstTokenBuf.toString();

        // calls tokenizeLine again to process the remaining of the line,
        // increasing the arrayIndex by 1.
        if (endLineBuf.length() > 0) {
            arrayIndex = arrayIndex + 1;
            tokenizeLine(endLineBuf, itemDetailLineData, arrayIndex);
        }
    }

    /**
     * Locates the position of the comma, considering that commas that appear inside a pair of quotes are not delimiters.
     * 
     * @param currentLine The current line to be parsed.
     * @return The index fist delimiter.
     *
    private int findTheFirstDelimiter(StringBuffer currentLineBuf) {
        if (currentLineBuf.charAt(0) == '"') {
            // if it starts with a " then we're currently within the quotes
            boolean isWithinQuotes = true;
            int currentIndex = 1;
            // loop through the currentLine string until either we
            // find the first comma that is not within quote or we have
            // reached the end of the line
            while (currentIndex < currentLineBuf.length()) {
                char currentChar = currentLineBuf.charAt(currentIndex);
                boolean currentIsAQuote = (currentChar == '"' ? true : false);
                boolean currentIsAComma = (currentChar == ',' ? true : false);
                if (currentIsAComma && (isWithinQuotes == false)) {
                    // This is the position of the first delimiting comma after the
                    // double quoted token ends, e.g. "abc,def",
                    // In the example above, the currentIndex will be 9
                    return currentIndex;
                }
                else if (currentIsAQuote) {
                    // We have found another " so it is either the matching "
                    // if we are currently within quotes, which means that we are
                    // now no longer within the quote or
                    // this could be the start of new quoted token if we were no longer
                    // within quotes. That's why we negate the isWithinQuotes
                    isWithinQuotes = !isWithinQuotes;
                }
                currentIndex++;
            }
            // couldn't find any commas in the currentLine
            return -1;
        }
        else {
            // This is the position of the delimiting comma when the token is
            // not surrounded by double quotes, e.g. abc,
            // In the example above, the currentIndex will be 3
            return currentLineBuf.indexOf(",");
        }
    }

    /**
     * Removes quotes around a token and pairs of quotes within a token that aren't meant to be displayed, 
     * but exist because of the CSV standard format.
     * 
     * @param token The token to be processed.
     * @return The StringBuffer after quotes are removed.
     *
    private StringBuffer removeExcessQuotes(StringBuffer token) {
        int currentIndex = 0;
        StringBuffer result = new StringBuffer();
        boolean containsAQuote = false;
        boolean containsTwoQuotes = false;
        int tokenLength = token.length();

        // if the token is already surrounded by double quotes,
        // we'll be iterating the while loop below after excluding
        // these outermost double quotes.
        if (token.charAt(0) == '"') {
            if (token.charAt(tokenLength - 1) == '"') {
                currentIndex = currentIndex + 1;
                tokenLength = tokenLength - 1;
            }
        }

        // Loop from the start until the end of the token; if
        // there were double quotes surrounding this token, then
        // this loop is from the start to the end excluding the first
        // surrounding double quotes.
        for (int i = currentIndex; i < tokenLength; i++) {
            if (token.charAt(i) == '"') {
                if (containsAQuote) {
                    // if the current char is a double quote and we already have a double
                    // quote previously, then this token must be containing 2 double quotes
                    containsTwoQuotes = true;
                }
                else {
                    // if the current char is a double quote, but it's not containsAQuote,
                    // then it contains only one double quote, so containsTwoQuotes is false
                    containsTwoQuotes = false;
                }
                // if the current char is a double quote, then at least containsAQuote
                // must be true (the containsTwoQuotes could be either true or false,
                // depending on whether the existing containsAQuote is true or false.
                containsAQuote = true;
            }
            else {
                // if the current char is not a double quote, then we don't have to
                // keep track of whether the token contains a double quote or two,
                // until the next time we find a double quote in the current character,
                // therefore we're setting both of these variables to false now.
                containsAQuote = false;
                containsTwoQuotes = false;
            }

            // if we only have one double quote or no double quote, then
            // we should continue appending this current character to the
            // buffer because we want to see this character appearing in
            // the field on the webpage
            if (!containsTwoQuotes) {
                result.append(token.charAt(i));
            }
            else {
                // if it contains two double quotes, we don't want to
                // append both of them to the buffer, only one of them,
                // because the other double quote is used to "wrap" the
                // appearance of double quote in the token (i.e. the one
                // that we actually want to see in the field on the webpage)
                containsTwoQuotes = false;
                containsAQuote = false;
            }
        }
        
        return result;
    }

    /**
     * @see org.kuali.module.purap.service.ItemPaser#importItems(InputStream inputStream, int startLineNumber)
     *
    public List importItems(InputStream inputStream, Class<? extends PurApItem> itemClass) throws IOException, IllegalAccessException, InstantiationException {
        List item = new ArrayList();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String currentLine = br.readLine();
            while (currentLine != null) {
                PurApItem line = null;
                line = parseItem(currentLine, itemClass);
                item.add(line);
                currentLine = br.readLine();
            }
        }
        finally {
            br.close();
        }
        return item;
    }
    
****/
}
