# dev-helper
![Build Status](https://github.com/saneea/dev-helper/actions/workflows/maven.yml/badge.svg)

**dev-helper** is just simple tool for helping developer with daily routines.
It is written with pure-Java.
**dev-helper** can do for you tasks like that:

 - xml or json pretty print conversion
 - calc hash (like md5, sha-* etc.)
 - base64 encoding/decoding
 - hex encoding/decoding
 - gzip compression/extracting
 - clipboard read/write
 - uuid generation
 - and so on

Technicaly **dev-helper** is CLI framework for running CLI utils (plugins).

**dev-helper** is just **runner** for **features**.
Each **feature** can do **some work** or just **be runner** (*multifeature*) for **child feature(s)**.
As a result there are tree of features. You can print whole features tree by command `dvh.bat -c tree`
E.g. **dev-helper** contains feature **random**. But feature **random** is just a runner for features **uuid** and **bytes**
So, if you run `dvh.bat random uuid` then new uuid is printed to standard output.

Many features are designed to work with standard intput and output streams.
This fact makes possible to join commands with pipes.
E.g. `dvh clip read | dvh json format pretty | dvh clip write` - transforms JSON from your clipboard to "pretty print" JSON.

# How to build?
You need Java 11 or later, Apache Maven (I use version 3.6.2 but I guess any 3.\*.\* should works too).
Just call `mvn clean install` in root project folder (this folder contains this *README.md* file, *pom.xml* file and *src* folder).
As a result files *dev-helper-\<version\>-bin.zip* and *dev-helper-\<version\>-bin.tar.gz* are created.

# How to install?
Download binaries package from https://github.com/saneea/dev-helper/releases or build it by yourself from sources.
Extract *dev-helper-\<version\>-bin.zip* or *dev-helper-\<version\>-bin.tar.gz* to some folder.
Add *path/to/dev-helper/bin* folder to your PATH environment variable.
*Optional*. Add *path/to/dev-helper/scripts* folder to your PATH environment variable. It contains some examples.
Try to run `dvh time now` in order to test your installation (this command just prints system time).

# How to add new feature?
1. Create class for your feature
2. Implement interface *io.github.saneea.dvh.Feature*
3. Implement specific interfaces if you need working with standard input/output (as text or binary). E.g. Feature.In.Bin.Stream
4. Implement specific interfaces if you need working with command line options: Feature.CLI and/or Feature.CLI.Options
5. Register your feature class in file `src/main/config/feature-aliases.properties` or in some existent multifeature (such as `dvh bin`)

If you need to add new multifeature there are a good idea to derive your feature from io.github.saneea.dvh.feature.multi.MultiFeatureBase.