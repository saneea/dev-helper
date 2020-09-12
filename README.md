# dev-helper
[![Build Status](https://travis-ci.org/saneea/dev-helper.svg?branch=master)](https://travis-ci.org/saneea/dev-helper)

It is just simple tool for helping developer with every daily routines.
dev-helper can do for you tasks like that:

 - xml or json pretty print conversion
 - calc hash (like md5, sha-* etc.)
 - base64 encoding/decoding
 - hex encoding/decoding
 - clipboard read/write
 - uuid generation
 - and so on

So, as you can see it is not rocket science. It is not something new: many another tools IDE, text editors or even bare POSIX-compatible environment can provide these feature. So, why I had to create **dev-helper**? In the beginning I just need tool which transform one-line XML file to "pretty print" XML. Of course any modern IDE can format XML (JSON and many another formats) for you. But what if you want do some automation as shell scripts? For that purpose CLI tools is the best. Yes, I tried to find already created CLI tools but I didn't like them for some reasons.
That's why dev-helper was born. So, it
 - ...contains features which I need
 - ...is cross-platform (because of pure java is used)
 - ...is extendable
 - ...is simple

**dev-helper** is based on few main concepts:
 - **dev-helper** is just runner for separated features (e.g. feature "*uuid*" can be used as shell call "`dvh.bat uuid`")
 - if it possible features is designed to works with standard IO streams: std_in, std_out, std_err. This fact allows to chain executions with OS pipes (e.g. "`dvh fromCB | dvh jsonPrettyPrint | dvh toCB`" - transforms JSON from your clipboard to "pretty print" JSON)
