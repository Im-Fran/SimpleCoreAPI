## v0.3.2 - Snapshot
* Fixed class loader issue causing ClassNotFoundException for some servers.
* Now the class loader will only load the main class of the modules.

## v0.3.1 - Snapshot
* Added class `SoftwareType` to list all available software types.
* Added Method `SimpleCoreAPI.softwareType` to get the current software running the server
* Added Method `SimpleCoreAPI#isRunningSoftwareType` to check if the server is running a specific software

## v0.3.0 - Snapshot
* Now the API will be initialized at the load stage of the server
* Added Module#onLoad function to add the ability of loading a module at the load stage of the server
* Added Module#isEnabled function to check if the module is enabled
* Added Module#isLoaded function to check if the module is loaded
* Improved the Jar update process (where the files from plugins/SimpleCoreAPI/update/ are moved to the modules folder)
* Fixed Settings.yml not being properly updated

## v0.2.5 - Snapshot
* Improved dependency sorting (It puts first the modules with no dependencies, then the modules with dependencies)
* Added GitHub Update Checker and GitHub Auto Updater for the modules
* Added `github-repository` to the module properties
* Added configuration

## v0.2.4 - Snapshot
* Fixed Class Not Found error
* Improvements to the Module Loader

## v0.2.3 - Snapshot
* Fixes to update checker to print the message
* Now GitHub actions will automatically upload the jar file

## v0.2.2 - Snapshot
* Fixed module loader not loading all the classes and stopping when the module is initialized.

## v0.2.1 - Snapshot
* Fixed loop on load while downloading certain dependencies

## v0.2.0 - Snapshot
* Added Static Instance for Spigot and Bungee Loaders
* Set initializer private to avoid issues

## v0.1.12 - Snapshot 
* Updated Dependencies
* Updated Deprecated Code

## v0.1.11 - Snapshot
* Added scanner to load all required modules

## v0.1.10 - Snapshot
* Added GitHub update checker
* Added method to download modules from SimpleCoreAPI.kt 

## v0.1.9 - Snapshot
* Fixed when trying to get simplecoreapi.properties resource returning null

## v0.1.8 - Snapshot
* Updated gradle to v7.3.2

## v0.1.7 - Snapshot
* Fixes to the module download
* Migrated to new module download system (repository based)
* Added required repository-id field to module properties
* Removed CloudModule.kt due to migration to new module download system

## v0.1.6 - Snapshot
* Added update system like update folder in bukkit servers
* Fixed modules not being loaded (Class Not Found Exception)
* Fixed Null Pointer Exception when abruptly disabling the api
* Improvements to the module load order
* Fixes to issue templates

## v0.1.4 - Snapshot / v0.1.5 - Snapshot
* Fixed build script not working
* Implemented custom actions

## v0.1.3 - Snapshot
* Added ability to re-deploy a version by specifying it in the environment variable `VERSION`
* Add a way to let know the user the current commit hash of the running release.
* Added Dokka
* Added Gradle Wrapper Validator
* Improve deploy script to update edited releases.
* Fixed shadowJar not saving the file without version
* Cleanup some code

## v0.1.2 - Snapshot
* Fixed fat jar not recognized by kotlin plugin [KTIJ-20430](http://youtrack.jetbrains.com/issue/KTIJ-20430)
* Added dokka
* Fixed `minimize()` removing dependencies
* Updated maven publish config
* Removed code-ql analysis because kotlin is not supported :/

## v0.1.1 - Snapshot
* Fixed empty dependency being loaded (Currently being fixed)
* Cleanup of the code
* Migration to Kotlin

## v0.1.0 - Snapshot
Hello, World!
