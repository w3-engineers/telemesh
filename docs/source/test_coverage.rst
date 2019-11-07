.. test_coverage:

Test Coverage
-------------

-  This repo is configured with [Travis][travis] and
   [coverall][coverall]. Every merge with *master* produced a test
   coverage report. Latest coverage report is available `here`_. `This`_
   badge here shows coverage status.

-  To generate report locally you should go to project’s root directory,
   then execute below command: > gradlew coveralls

**NOTE:** You must have a connected device or emulator as it runs
instrumentation tests. You will find the coverage report at
*telemesh/app/build/reports/coverage*

.. _here: https://coveralls.io/github/w3-engineers/telemesh?branch=master
.. _This: #Telemesh