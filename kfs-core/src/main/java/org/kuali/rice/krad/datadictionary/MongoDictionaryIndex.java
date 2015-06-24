package org.kuali.rice.krad.datadictionary;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kuali.kfs.sys.businessobject.datadictionary.FinancialSystemBusinessObjectEntry;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class MongoDictionaryIndex implements Runnable, DictionaryIndex {
    private static final Log LOG = LogFactory.getLog(MongoDictionaryIndex.class);

    // keyed by BusinessObject class
    private Map<String, BusinessObjectEntry> businessObjectEntries;

    // keyed by documentTypeName
    private Map<String, DocumentEntry> documentEntries;
    // keyed by other things
    private Map<Class, DocumentEntry> documentEntriesByBusinessObjectClass;
    private Map<Class, DocumentEntry> documentEntriesByMaintainableClass;
    private Map<String, DataDictionaryEntry> entriesByJstlKey;

    // keyed by a class object, and the value is a set of classes that may block the class represented by the key from inactivation
    private Map<Class, Set<InactivationBlockingMetadata>> inactivationBlockersForClass;

    @Override
    public Map<String, DocumentEntry> getDocumentEntries() {
        return this.documentEntries;
    }

    @Override
    public Map<Class, DocumentEntry> getDocumentEntriesByBusinessObjectClass() {
        return this.documentEntriesByBusinessObjectClass;
    }

    @Override
    public Map<Class, DocumentEntry> getDocumentEntriesByMaintainableClass() {
        return this.documentEntriesByMaintainableClass;
    }

    @Override
    public Map<String, DataDictionaryEntry> getEntriesByJstlKey() {
        return this.entriesByJstlKey;
    }

    @Override
    public Map<Class, Set<InactivationBlockingMetadata>> getInactivationBlockersForClass() {
        return this.inactivationBlockersForClass;
    }

    @Override
    public Map<String, BusinessObjectEntry> getBusinessObjectEntries() {
        return this.businessObjectEntries;
    }

    @Override
    public void index() {
        // primary indices
        businessObjectEntries = new ConcurrentHashMap<>();
        documentEntries = new ConcurrentHashMap<>();

        // alternate indices
        documentEntriesByBusinessObjectClass = new ConcurrentHashMap<>();
        documentEntriesByMaintainableClass = new ConcurrentHashMap<>();
        entriesByJstlKey = new ConcurrentHashMap<>();

        final ObjectMapper mapper = new ObjectMapper();
        populateFromMongo("localhost", "kfs_dd", "business_objects", document -> {
            try {
                final FinancialSystemBusinessObjectEntry entry = mapper.readValue(document.toJson(), FinancialSystemBusinessObjectEntry.class);
                businessObjectEntries.put(entry.getBusinessObjectClass().getName(), entry);
                businessObjectEntries.put(entry.getBusinessObjectClass().getSimpleName(), entry);
                businessObjectEntries.put(entry.getJstlKey(), entry);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void run() {
        index();
    }

    protected void populateFromMongo(String hostUrl, String databaseName, String collectionName, Consumer<Document> consumer) {
        MongoDatabase database;
        try (MongoClient client = new MongoClient(new MongoClientURI("mongodb://" + hostUrl + ":27017"))) {
            database = client.getDatabase(databaseName);
            MongoCollection dds = database.getCollection(collectionName);
            FindIterable iterable = dds.find();
            MongoCursor cursor = null;
            try {
                cursor = iterable.iterator();
                while (cursor.hasNext()) {
                    Document doc = (Document) cursor.next();
                    consumer.accept(doc);
                }
            } finally {
                cursor.close();
            }
        }

    }


}
