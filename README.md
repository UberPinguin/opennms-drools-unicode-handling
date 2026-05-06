# Testing Unicode Handling in OpenNMS's Correlator Drools Engines

Sets up a small KieSession with a handful of rules, then inserts some Events containing a couple of Unicode characters.

Each test looks at a different way that an Event might be constructed and handled, and checks that the Unicode
characters haven't been replaced by escape codes.

Requires a repository of Maven artifacts built from OpenNMS Meridian 2024.2.3.