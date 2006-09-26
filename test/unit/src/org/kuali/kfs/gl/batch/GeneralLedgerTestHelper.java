/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.util;

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
 */
public class GeneralLedgerTestHelper {

    static public List loadOutputOriginEntriesFromClasspath(String nameOfOutputOriginEntryFileFromFis) throws IOException {
        return loadOutputOriginEntriesFromClasspath(nameOfOutputOriginEntryFileFromFis, null);
    }

    /**
     * This method differs from OriginEntryServiceImpl.loadFlatFile in that it loads a file using a classloader instead of loading a
     * file from an absolute file path. This allows and in fact requires that the file from which the entries will be loaded be
     * checked into the source repository along with this test.
     * 
     * @param nameOfOutputOriginEntryFileFromFis
     * @return
     * @throws IOException
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
            lineChars[91] = ' ';
            int idx = 92;
            do {
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
            expectedOutputOriginEntries.add(lin2);
        }

        return expectedOutputOriginEntries;
    }

}
