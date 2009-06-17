READ ME for the jMeter test : arPaymentApplicationDocument


The Aging Report for the Accounts Receivable (AR) module generates a report of the outstanding balance for each customer and how long it has been since the balance was billed.  The report is displayed in rows by customer, but amounts listed for each of five to six time frames (ie. 0-30 days, 31-60 days, etc).

JMETER VARIABLES

Per-user/thread Parameters:

There is a parameter that must be manually set before this jMeter test is run.  This parameter is date specific and must be set to successfully perform the test with meaningful results.  The variable "reportRunDate" should be set to the current date.  This will help ensure that all the invoices created by the unit test (see below) appear in the resulting report.

NOTE: reportRunDate is defined in the Per-user/thread parameters section of the thread group for the test arAgingReport.


RUN UNIT TEST

In order to effectively test out this report and its functionality, a collection of invoices needs to be generated to run this report against.  Given the nature of the report and the difficulty of implementing an algorithm in jMeter to effectively generate a collection of invoices for testing the Aging Report, it was decided that instead, a jUnit test should be used to populate the database with the necessary invoices.  (This jUnit test had already been written and is being used here as a shortcut to populating the needed data.)

Some minor changes need to be made to the jUnit test to get it to run, as it was written for the sole purpose of generating invoices to test out the aging report and print features of the AR module.  Because of this, it was not desirable to have this test running with the normal unit tests on a repeating basis.  To prevent this automatic running of this test, some of the names of the methods were modified to prevent them from appearing as a runnable unit test.

See kfs/test/integration/org/kuali/kfs/module/ar/core/ReportingLoadTest.java

Within this file, there are detailed instructions above each method that describe the functionality of that method if it is renamed to a runnable method, in addition to a description of the purpose of the method.  

At the top of the ReportingLoadTest.java class, there is a variable defined which is called INVOICES_TO_CREATE.  This variable should be increased to at least 7 to generate enough invoices to fill all the date ranges within the report.  (For testing purposes, I typically used 20.)

For the purposes of this jMeter test, the method 'createManyInvoicesForPrintTesting' should be renamed to 'testCreateManyInvoicesForPrinting'.  Then this class should be run in jUnit to perform the appropriate data populating of invoices.

Once this unit test is run, the arAgingReport jMeter test can be run against this newly created data to performance test the report.





