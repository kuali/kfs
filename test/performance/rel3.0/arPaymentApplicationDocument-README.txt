READ ME for the jMeter test : arPaymentApplicationDocument


JMETER VARIABLES

Per-user/thread Parameters:

There is a parameter that must be manually set before this jMeter test is run.  This parameter is date specific and must be set to validate 
a business rule.  

The rule is defined as such: 

- the invoice due date must not be before or equal to the billing date.  In the business rules class, the billing date is defined 
as the current date, so when the jMeter test is run, the parameter "invoiceDueDate" must be defined as today's date plus one.  
(ie. today is 6/10/09, then the parameter must be set to 6/11/09 or later).  

NOTE: invoiceDueDate is defined in the Per-user/thread parameters section of the thread group for the test arPaymentApplicationDocument.






