Combine Archive
===============
This provide an experimental implementation of a Java API to the combine archive (<http://co.mbine.org/documents/archive>).

The archive maintains the manifest of the archive and provides access to the metadata file to enable custom annotation
of the archive. It provides basic annotation of the archive including the creation and modification dates of the contents.
This could be extended to include, for example, the date elements were added to the archive etc.

The API is relatively simple to use. There are three examples files in the test directory that show how to create, 
populate and iterate a new archive
and also how to update an existing archive. These are called:

* CreateNewArchiveTest.java
* UpdateArchiveTest.java
* ExtractArchiveTest.java

Build Instructions
------------------

The project is built by using Apache Ant and uses Apache Ivy for dependency resolution. Recently, we have created 
```pom.xml``` file to allow developers able to build and deploy the library with Maven.

<h3> Building from command line </h3>

    # Ant
    # fetch dependencies
    ant resolve
    # create the JARS containing binaries, sources and documentation
    ant jarAll

For a list of all available targets please use `ant -p`.
 
    # Maven
    # mvn clean compile test verify package install

<h3> IDE Support </h3>

Eclipse and IntelliJ IDEA project files are available in the project's root folder.
Ensure you perform dependency resolution (by running `ant resolve`) so that your classpath
is configured correctly.

Contact
--------
All feedback and suggestions are welcome via either raising an issue on this repository or sending us a message to  
[biomodels-developers@sf.net](biomodels-developers@sf.net).

Developed by Stuart Moodie. Maintained by [Mihai Glonț](https://github.com/mglont) and [Tung Nguyen](https://github.com/ntung). Please raise 
issues or feature
requests [on GitHub](https://github.com/mglont/CombineArchive/issues).

License
-------
Copyright EMBL-EBI 2017 - 2021. This code is licensed under Apache V2.0. See LICENSE for more details.
