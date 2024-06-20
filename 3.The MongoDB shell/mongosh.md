Connect to mongodb:

    ~ mongosh "mongodb+srv://<clustername>.mongodb.net/<dbname>" --username <username> //Connect to Atlas cluster with connection string
    ~ mongosh "mongodb+srv://<username>:<password>@<clustername>.mongodb.net/<dbname>" //Connect to Atlas cluster with connection string

    ~ mongosh  //Connect to local instance not having access control setup, running on default port 27017
    ~ mongosh --port 27018 //will connect to test database if db is not specified
    ~ mongosh students //connect to students db on localhost
    ~ mongosh –host localhost students //connect to student db on specific host

Not specifying database will connect us to the test database.

mongosh commands:

    db -> will return the database we are connected to
	db.hello() //provides info about the mongod instance we are connected to
	use <other_db> -> switch to other db
	show collections -> list collections in the current db
	show dbs -> list all dbs on the server
	db.zips.find() -> returns all documents from a collection
	db.zips.insertOne({account_id: 99,...})
	db.zips.deleteOne({account_id: 99})
	db.zips.findOne({ id: ObjectId("3h34g58dsfd")}) -> find a single document
	exit -> exit mongosh

### <span style="color:darkgoldenrod"> How to configure mongosh?
Configure mongosh via:
- config API

Променя настройките в shell-a. Настройките се запазват между отделните сесии, като се съхраняват за конкретния user, във файловата система.
Config API-то се достъпва през config обекта

    config ->returns list of config options
    config.get('enableTelemetry') > returns specific option
    config.set('enableTelemetry', false)
    config.reset('enableTelemetry') //set property to default value

The available configuration options are as follows:
  - `displayBatchSize` - The number of documents to display when using the `it` iterator.
  - `maxTimeMS` - The maximum amount of time to allow a query to run.
  - `enableTelemetry` - Whether to enable telemetry.
  - `editor` - The editor to use when editing code.
  - `snippetIndexSourceURLs` - The URLs to use when fetching snippet index files.
  - `snippetRegistryURL` - The URL to use when fetching snippet registry files.
  - `snippetAutoload` - Whether to automatically load snippets.
  - `inspectCompact` - Whether to use compact mode when inspecting objects.
  - `inspectDepth` - The maximum depth to use when inspecting objects.
  - `historyLength` - The number of history entries to keep.
  - `showStackTraces` - Whether to show stack traces when errors occur.
  - `redactHistory` - Whether to redact sensitive information from history.

- mongosh.conf file

Plaintext файл, който ни позволява да променяме всички настройки, които можем да променяме през config api-то,
с разликата,че настройките в conf файла се предефинират от тези зададени през api-то. Също през conf файла могат 
да се задават настройки за всички user-и.Conf файлът се създава от user-a и локацията му зависи от OS-a.

    ~ sudo touch /etc/mongosh.conf //on Ubuntu, on Win the file should be mongosh.cfg and located in the same dir as mongosh.exe
    ~ vim /etc/mongosh.conf
        mongosh:
            displayBatchSize: 5 //find командата ще връща по 5 документа на batch, 20 e default
            editor: "vim"

- passing commands or JS code to mongosh via the --eval flag (to change settings or to return result of a query without entering the shell)

Променяне на mongosh конфигурацията чрез --eval флага:
![title](./resources/changeMongoshSettingsViaEvalFlag.png)

Изпълняване на заявка без да влизаме в shell-a, чрез --eval флага:
![title](./resources/executeQueryWithoutEnteringMongoShell.png)

    mongosh  "mongodb+srv://myAtlasDBUser:myatlas-001@myatlasclusteredu.fyvbwbb.mongodb.net/sample_analytics?appName=config-mongosh" --eval "db.customers.find({username:'samantha27'})"

Подаване променливи на shell-a чрез --eval флага:
![title](./resources/UseEvalFlagOnMongoshCommand.png)

mongosh is build on top of NodeJS, so we can execute JS code within the shell

### <span style="color:darkgoldenrod"> How to write JS functions in mongosh?
Датите в монго обикновено се записват с ISODate формат.
Следната функция конвертира string дата в ISODate:
![title](./resources/defineJsFunctionInMongosh.png)

Можем да запишем функция в js файл и да я извикаме по следния начин:
![title](./resources/loadJsFunctionInMongosh.png)

The load() method allows you to load and use an external JavaScript file within an active mongosh session. 
For example, if you have a file named myScript.js in the current working directory, you can load it by running load('myScript.js')

### <span style="color:darkgoldenrod"> How to switch between and reference databases within a JS script?
db.getSiblingDB("sample_training");

### <span style="color:darkgoldenrod"> How to add function in the global scope of mongosh in an editor?
Set editor in mongosh:

![title](./resources/setEditorInMongosh.png)

Edit function:

    edit giveMeADate

![title](./resources/editJsFunctionInMongosh.png)


### <span style="color:darkgoldenrod"> За какво служи mongoshrc.js файла?
При стартиране на mongosh, home директорията се проверява за наличието на mongoshrc.js файла и се прочита 
неговото съдържание преди изобразяването на prompt-а.

Добавяне на ф-я в mongoshrc.js:

    touch .mongoshrc.js //по подразбиране файлът не е създаден
    nvim ~./.mongoshrc.js
        const fcv = () => fb.adminCommand({getParameter: 1,featureCompatibilityVersion: 1})
    mongosh
        test> fcv()
        { featureCompatibilityVersion: { version: '7.0' }, ok: 1 }

![title](./resources/editThePromptFunctionCalledOnMongoshEnter.png)

Рестартирайте шела след всяка промяна по mongoshrc.js файла.

### <span style="color:darkgoldenrod"> За какво служи db.adminCommand()-ата?
Изпълнява команди срещу admin базата

    db.adminCommand({<command_name> : 1}) //името на командата най-често се поадава като key със стойност 1

### <span style="color:darkgoldenrod"> How to exit mongosh?
Run exit, quit, or press Ctrl + D on your keyboard.
  