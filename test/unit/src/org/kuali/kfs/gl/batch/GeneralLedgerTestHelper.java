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
package org.kuali.kfs.gl.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Utilities for helping set up and run GL tests, mostly giving GL unit test writers the ability
 * to load flat files of String-formatted origin entries from the classpath
 */
public class GeneralLedgerTestHelper {

    /**
     * Loads a file of String-formatted Origin Entries from the class path
     * 
     * @param nameOfOutputOriginEntryFileFromFis the name of the file to load
     * @return a List of origin entries
     * @throws IOException thrown if the file cannot be read
     */
    static public List loadOutputOriginEntriesFromClasspath(String nameOfOutputOriginEntryFileFromFis) throws IOException {
        return loadOutputOriginEntriesFromClasspath(nameOfOutputOriginEntryFileFromFis, null);
    }

    /**
     * This method differs from OriginEntryServiceImpl.loadFlatFile in that it loads a file using a classloader instead of loading a
     * file from an absolute file path. This allows and in fact requires that the file from which the entries will be loaded be
     * checked into the source repository along with this test.
     * 
     * @param nameOfOutputOriginEntryFileFromFis the name of the file to load
     * @return a List of origin entries
     * @throws IOException  thrown if the file cannot be read
     */
    static public List loadOutputOriginEntriesFromClasspath(String nameOfOutputOriginEntryFileFromFis, Date date) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(nameOfOutputOriginEntryFileFromFis);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        List expectedOutputOriginEntries = new ArrayList();
        String line = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        char[] dateChars = null == date ? null : dateFormat.format(date).toCharArray();

        while (null != (line = reader.readLine())) {
            // correct for differences in line format
            char[] lineChars = new char[173];
            Arrays.fill(lineChars, ' ');
            char[] lineA = line.toCharArray();
            System.arraycopy(lineA, 0, lineChars, 0, lineA.length);
            // converts the + at pos 91 to a space
            lineChars[91] = ' ';
            int idx = 92;
            do {
                // converts leading 0 characters for amount to spaces
                if ('0' != lineChars[idx]) {
                    break;
                }
                else {
                    lineChars[idx] = ' ';
                }
            } while (103 > idx++);

            if (null != date) { // splice in the date

                for (int i = 0; i < dateChars.length; i++) {
                    lineChars[109 + i] = dateChars[i];
                }

            }

            String lin2 = new String(lineChars);

            // KULRNE-34: in FIS, doc numbers were 9 characters long, and in Kuali, they are 14
            // it means we need to add 5 spaces after the doc number
            // first char after doc number is at array pos 46

            String prefix = lin2.substring(0, 46);
            String suffix = lin2.substring(46);

            lin2 = prefix + "     " + suffix;

            // now we add 5 characters to reference doc number
            // reference doc number ends 11 spaces from end (see OriginEntryFull.java)
            prefix = lin2.substring(0, lin2.length() - 11);
            suffix = lin2.substring(lin2.length() - 11);

            lin2 = prefix + "     " + suffix;

            // the string is too long, so we truncate 10 characters off from it
            lin2 = lin2.substring(0, lin2.length() - 10);

            expectedOutputOriginEntries.add(lin2);
        }

        return expectedOutputOriginEntries;
    }

}
