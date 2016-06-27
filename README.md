# <img align="left" src="_imgs/durian-debug.png"> DurianDebug: Utilities for quick 'n dirty debugging and profiling

<!---freshmark shields
output = [
	link(shield('Maven artifact', 'mavenCentral', '{{group}}:{{name}}', 'blue'), 'https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22{{group}}%22%20AND%20a%3A%22{{name}}%22'),
	link(shield('Latest version', 'latest', '{{stable}}', 'blue'), 'https://github.com/{{org}}/{{name}}/releases/latest'),
	link(shield('Javadoc', 'javadoc', 'OK', 'blue'), 'https://{{org}}.github.io/{{name}}/javadoc/{{stable}}/'),
	link(shield('License Apache', 'license', 'Apache', 'blue'), 'https://tldrlegal.com/license/apache-license-2.0-(apache-2.0)'),
	'',
	link(shield('Changelog', 'changelog', '{{version}}', 'brightgreen'), 'CHANGES.md'),
	link(image('Travis CI', 'https://travis-ci.org/{{org}}/{{name}}.svg?branch=master'), 'https://travis-ci.org/{{org}}/{{name}}'),
	link(shield('Live chat', 'gitter', 'live chat', 'brightgreen'), 'https://gitter.im/diffplug/durian')
	].join('\n');
-->
[![Maven artifact](https://img.shields.io/badge/mavenCentral-com.diffplug.durian%3Adurian--debug-blue.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.diffplug.durian%22%20AND%20a%3A%22durian-debug%22)
[![Latest version](https://img.shields.io/badge/latest-0.1.0-blue.svg)](https://github.com/diffplug/durian-debug/releases/latest)
[![Javadoc](https://img.shields.io/badge/javadoc-OK-blue.svg)](https://diffplug.github.io/durian-debug/javadoc/0.1.0/)
[![License Apache](https://img.shields.io/badge/license-Apache-blue.svg)](https://tldrlegal.com/license/apache-license-2.0-(apache-2.0))

[![Changelog](https://img.shields.io/badge/changelog-1.0.0-brightgreen.svg)](CHANGES.md)
[![Travis CI](https://travis-ci.org/diffplug/durian-debug.svg?branch=master)](https://travis-ci.org/diffplug/durian-debug)
[![Live chat](https://img.shields.io/badge/gitter-live_chat-brightgreen.svg)](https://gitter.im/diffplug/durian)
<!---freshmark /shields -->

<!---freshmark javadoc
output = prefixDelimiterReplace(input, 'https://{{org}}.github.io/{{name}}/javadoc/', '/', stable);
-->

A collection of utilities which are useful for quickly debugging and profiling some code.

<!---freshmark /javadoc -->

## Acknowledgements

* Formatted by [spotless](https://github.com/diffplug/spotless).
* OSGi metadata by [goomph](https://github.com/diffplug/goomph).
* Built by [gradle](http://gradle.org/).
* Tested by [junit](http://junit.org/).
* Maintained by [DiffPlug](http://www.diffplug.com/).
