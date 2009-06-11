CAS 3.x can now be deployed standalone. It is checked in to the project in the 
build/project directory and is named kuali-cas-1.0.0.war. There is a project 
in SVN for the Kuali modifications at kul-cas. To create the war run the 
following commands.

1) mvn -P cas package
2) mvn package

The first is only needed if you clean or 
want to update the CAS version the Kuali CAS is based on. It runs a maven 
overlay, which downloads the war files from the CAS maven repository and applies 
the Kuali modification. The last compiles the Kuali changes and builds a war file.

