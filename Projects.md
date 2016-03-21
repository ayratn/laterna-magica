# The libraries #
### TreeProperties ###
A configuration framework and file format that supports a hierarchic tree of properties, therefore enabling structured configuration files. In addition, type safety and conversion is available to help validating configurations, as well as `${property/references}` inside values. Another main feature is the ability to split configurations over several files.
### [Config](Config.md) ###
A new configuration file format using ANTLR under the surface. It is more tightly coupled to object oriented modelling: Types may be declared, but can also be inferred from bean property types. "Annotations" make it possible to modify how the file is interpreted, e.g. by adding a subtree from another config file.
### BeanUtils ###
A library for easier creation of bean properties. The main feature is the Properties support interface, which enables the configuration of a bean's capabilities, like bound or vetoable, and creating properties with that capabilities, including collections.
### [Utils](Utils.md) ###
This poorly named library is for softcoding application startup, configured using a [Config](Config.md) file. Capabilities include reading configurations, configuring log4j logging, and can be extended by application developers.
### [Utilities](Utilities.md) ###
A loose collection of classes that may be practical for any project and need no external dependencies.
### LaternaDownloader ###
A graphical, multithreaded downloader utility that shows download progress overall and for individual files being currently downloaded.
# The laterna projects #
### LaternaParent ###
Maven parent project for the projects below. The POM contains dependency version numbers, repository declarations and plugin configurations.
### LaternaTools ###
"Application Initializer" for Laterna Magica; it loads and stores the application, and supports the initialization of laterna's modules, if they need any.
### AbilityScripts ###
API for developing new "ability scripting languages" for LaternaMagica.
### StaticModel ###
Exposes LaternaMagica's model for static, i.e. not game-state specific aspects of Magic. Examples are Cards, sets, formats etc. The goal is to provide tools and plugins with an API that is independent from the actual game, therefore making it safer and easier to extend the environment of LaternaMagica.
### GameModel ###
Non-static (in-game) logic of LaternaMagica.
### CardFormats ###
API for adding new card file formats to LaternaMagica.
### TextCardFormat ###
A TreeProperties based format to represent cards for LaternaMagica.
### ConfigCardFormat ###
A [Config](Config.md) based format to represent cards for LaternaMagica.
### OracleParser ###
An ANTLR grammar for parsing oracle text.
### OracleAbilityScript ###
LaternaMagica "oracle" ability scripting language for parsing rules text almost directly.
# LaternaMagica #
The main project, containing mainly the GUI for playing the game.