/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.core.impl.encryption;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.encryption.EncryptionService;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 * KFS note - see reference implementation change in RICE EncryptionService.java 
 * development-tools/src/main/java/org/kuali/rice/krad/maintainablexml/EncryptionService.java
 * 
 * **** Ccomment from EncryptionService.java ****
 *      
 *      This class has been copied from the base rice code but has added an old secret key that allows for data encrypted
 *      with the commons-codec 1.3 api to be decrypted with newer versions of the api. The Base64.decodeBase64 method did not
 *      decode the last two bytes correctly in 1.3 when the encoded key did not end with '=', it always defaulted those
 *      bytes to '1'
 * 
 * This original implementation of DemonstrationGradeEncryptionServiceImpl class is replaced by RICE's EncryptionService class
 * 
 * RICE implementation stores a second SecretKey for existing encrypted data (using commons-codec 1.3) which could be 
 * decrypted correctly with post 1.4 codec. 
 * 
 * Implementation of encryption service for demonstration. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemonstrationGradeEncryptionServiceImpl implements EncryptionService {
    
    public final static String ALGORITHM = "DES/ECB/PKCS5Padding";
    public final static String HASH_ALGORITHM = "SHA";
    private final static String CHARSET = "UTF-8";
    private transient SecretKey desKey;
    private transient SecretKey desKeyOld;
    private boolean isEnabled = false;

    public DemonstrationGradeEncryptionServiceImpl() throws Exception {
        if (desKey != null) {
            throw new RuntimeException("The secret key must be kept secret. Storing it in the Java source code is a really bad idea.");
        }

        String key = ConfigContext.getCurrentContextConfig().getProperty("encryption.key");
        if (!StringUtils.isEmpty(key)) {
            setSecretKey(key);
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String encrypt(Object valueToHide) throws GeneralSecurityException {
        checkEnabled();

        if (valueToHide == null) {
            return "";
        }

        // Initialize the cipher for encryption
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getDesKey());

        try {
            // Our cleartext
            byte[] cleartext = valueToHide.toString().getBytes(CHARSET);

            // Encrypt the cleartext
            byte[] ciphertext = cipher.doFinal(cleartext);

            return new String(Base64.encodeBase64(ciphertext), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String decrypt(String ciphertext) throws GeneralSecurityException {
        checkEnabled();

        if (StringUtils.isBlank(ciphertext)) {
            return "";
        }

        // Initialize the same cipher for decryption
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getDesKey());

        try {
            // un-Base64 encode the encrypted data
            byte[] encryptedData = Base64.decodeBase64(ciphertext.getBytes(CHARSET));

            // Decrypt the ciphertext
            byte[] cleartext1 = cipher.doFinal(encryptedData);
            return new String(cleartext1, CHARSET);
        } catch (Exception e) {
            Cipher cipher2 = Cipher.getInstance(ALGORITHM);
            cipher2.init(Cipher.DECRYPT_MODE, getDesKeyOld());

            try {
                // un-Base64 encode the encrypted data
                byte[] encryptedData = Base64.decodeBase64(ciphertext.getBytes(CHARSET));

                // Decrypt the ciphertext
                byte[] cleartext1 = cipher2.doFinal(encryptedData);
                return new String(cleartext1, CHARSET);
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(e);
            }            
        }
    }

    public byte[] encryptBytes(byte[] valueToHide) throws GeneralSecurityException {
        checkEnabled();

        if (valueToHide == null) {
            return new byte[0];
        }

        // Initialize the cipher for encryption
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getDesKey());

        // Our cleartext
        byte[] cleartext = valueToHide;

        // Encrypt the cleartext
        byte[] ciphertext = cipher.doFinal(cleartext);

        return ciphertext;
    }

    public byte[] decryptBytes(byte[] ciphertext) throws GeneralSecurityException {
        checkEnabled();

        if (ciphertext == null) {
            return new byte[0];
        }

        // Initialize the same cipher for decryption
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getDesKey());

        // un-Base64 encode the encrypted data
        byte[] encryptedData = ciphertext;

        // Decrypt the ciphertext
        byte[] cleartext1 = cipher.doFinal(encryptedData);
        return cleartext1;
    }

    /**
     * 
     * This method generates keys. This method is implementation specific and should not be present in any general purpose interface
     * extracted from this class.
     * 
     * @return
     * @throws Exception
     */
    public static String generateEncodedKey() throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance("DES");
        SecretKey desKey = keygen.generateKey();

        // Create the cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init((Cipher.WRAP_MODE), desKey);

        SecretKeyFactory desFactory = SecretKeyFactory.getInstance("DES");
        DESKeySpec desSpec = (DESKeySpec) desFactory.getKeySpec(desKey, javax.crypto.spec.DESKeySpec.class);
        byte[] rawDesKey = desSpec.getKey();

        return new String(Base64.encodeBase64(rawDesKey));
    }

    private SecretKey unwrapEncodedKey(String key) throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance("DES");
        SecretKey desKey = keygen.generateKey();

        // Create the cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init((Cipher.UNWRAP_MODE), desKey);

        byte[] bytes = Base64.decodeBase64(key.getBytes());

        SecretKeyFactory desFactory = SecretKeyFactory.getInstance("DES");

        DESKeySpec keyspec = new DESKeySpec(bytes);
        SecretKey k = desFactory.generateSecret(keyspec);

        return k;

    }

    private SecretKey unwrapEncodedKeyOld(String key) throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance("DES");
        SecretKey desKey = keygen.generateKey();

        // Create the cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init((Cipher.UNWRAP_MODE), desKey);

        byte[] bytes = Base64.decodeBase64(key.getBytes());


        // If decoding was done with commons-codec 1.3 and the key not ended with '='
        bytes[6] = 1;
        bytes[7] = 1;

        SecretKeyFactory desFactory = SecretKeyFactory.getInstance("DES");

        DESKeySpec keyspec = new DESKeySpec(bytes);
        SecretKey k = desFactory.generateSecret(keyspec);

        return k;

    }

    /**
     * Sets the secretKey attribute value.
     * 
     * @param secretKey The secretKey to set.
     * @throws Exception
     */
    public void setSecretKey(String secretKey) throws Exception {
        if (!StringUtils.isEmpty(secretKey)) {
            desKey = this.unwrapEncodedKey(secretKey);
            setDesKeyOld(this.unwrapEncodedKeyOld(secretKey));
            isEnabled = true;
            // Create the cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init((Cipher.WRAP_MODE), getDesKey());
        }
    }

    /** Hash the value by converting to a string, running the hash algorithm, and then base64'ng the results.
     * Returns a blank string if any problems occur or the input value is null or empty.
     *
     */
    public String hash(Object valueToHide) throws GeneralSecurityException {
        if (valueToHide == null || StringUtils.isEmpty(valueToHide.toString())) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            return new String(Base64.encodeBase64(md.digest(valueToHide.toString().getBytes(CHARSET))), CHARSET);
        } catch (UnsupportedEncodingException ex) {
            // should never happen
        }
        return "";
    }

    /**
     * Performs a check to see if the encryption service is enabled.  If it is not then an
     * IllegalStateException will be thrown.
     */
    protected void checkEnabled() {
        if (!isEnabled()) {
            throw new IllegalStateException("Illegal use of encryption service.  Ecryption service is disabled, to enable please configure 'encryption.key'.");
        }
    }

    /**
     * @return the desKey
     */
    public SecretKey getDesKey() {
        return desKey;
    }

    /**
     * @return the desKeyOld
     */
    public SecretKey getDesKeyOld() {
        return desKeyOld;
    }

    /**
     * @param desKeyOld the desKeyOld to set
     */
    public void setDesKeyOld(SecretKey desKeyOld) {
        this.desKeyOld = desKeyOld;
    }

}
