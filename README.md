Combine Archive
===============
This provide an experimental implementation of a Java API to the combine archive (<http://co.mbine.org/documents/archive>).

The archive maintains the manifest of the archive and provides access to the metadata file to enable custom annotation
of the archive. It provides basic annotation of the archive including the creation and modification dates of the contents.
This could be extended to include, for example, the date elements were added to the archive etc.

The API is relatively simple to use. There are two examples files in the that show how to create and populate a new archive
and also how to update an existing archive. These are called:

* CreateNewArchiveTest.java
* UpdateArchiveTest.java

Build Instructions
------------------

The project is built using Apache Ant and uses Apache Ivy for dependency resolution.

<h3> Building from command line

    # fetch dependencies
    ant resolve
    # create the JARS containing binaries, sources and documentation
    ant jarAll
For a list of all available targets please use `ant -p`.

<h3> IDE Support

Eclipse and IntelliJ IDEA project files are available in the project's root folder.
Ensure you perform dependency resolution (by running `ant resolve`) so that your classpath
is configured correctly.

Contact
--------
Feedback and suggestions are welcome.

Developed by Stuart Moodie. Maintained by Mihai Glonț. Please raise issues or feature
requests [on GitHub](https://github.com/mglont/CombineArchive/issues).

License
-------
Copyright EMBL-EBI 2017. This code is licensed under Apache V2.0. See LICENSE for more details.
