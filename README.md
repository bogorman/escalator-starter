# eSCALAtor

Opinionated Full stack Scala starter project. Scala with some guide rails.

- Scala 2.13
- Akka + AkkaHttp
- Laminar
- Postgres
- Quill
- Monix
- Generate scaffolding from DB. 
- Flyway migrations. 
- Simple starter app with Login/User Registration. 
- UI / Backend share models. 
- Monitoring with Kamon

--Coming soon.
- CQRS/ES. Akka persistence with Posgtres. 
- OAuth support


# escalator-starter

1. clone this repo and the escalator repo. 
2. this project will also compil the escalator project so we will symlink the folder to our modules directory. 
cd modules
ln -s /path-to-the-dir/escalator

3. we are going to rename the project to what you want. open the rename_project.rb file and change the <CHANGE THIS> variable to be the new name of your project in CamelCase. the project assumes you are using a .com domain. Complete the initial setup with the .com and then rename as needed after. 

4. create a new postgres database and update the source_local.sh to relect these details

5. we are now going to run the database setup. We have some flyway wrapper commands. 

update the modules/db/migration with your migrations. the structure of the database has great importance and the database generation will try and infer relationships and generate scala code to match it. 
```
	sbt dbMigrate - migrate the database

	sbt dbSeed - run the seed files located at modules/db/seed

	sbt dbGenerate - generate the scala code for the schema

	sbt dbReset - drop database and reset

	sbt rmGenerated - remove the generated files
```

6. Before running the "sbt dbGenerate" check the file Codegen.scala file located at
/modules/db/generators/src/main/scala/Codegen.scala

update the packageName and appName.
```
    val packageName = "com.escalatorstarter"
    val appName = "EscalatorStarter"
```
7.  run
```
source source_local.sh
```
and then run 
```
sbt dbGenerate
```
 and inspect the output. 

Check here for the database models. This is compatible with scala and scalajs so these models can be used in JS.
```
/modules/shared/src/main/scala/com/escalatorstarter/models
```

For the DB access check here.
```
/modules/persistence/src/main/scala/postgres/tables
```

You can search the project for the string "THIS FILE IS AUTO-GENERATED" to see which of the files have been generated. You need to be able to run "sbt dbGenerate" after the database changes so do not edit these files. 

If you need custom DB access methods then create a custom file derived from the table here.
```
/modules/core/src/main/scala/com/escalatorstarter/core/repositories/
```
Update the EscalatorStarterRepository to reflect these changes. 

If the plural/singular naming convention conversion goes not generate propler table/column names in the models then you can add custom mappings here.
```
/modules/common/src/main/scala/com/escalatorstarter/persistence/CustomNameMapper.scala 
```

8. setup the UI. Use a bootstrap V5 starter template. for e.g

https://github.com/themesberg/volt-bootstrap-5-dashboard

follow the instructions for the "gulp build:dev"

copy the contents of the "html&css" folder to "frontend/src/main/static/public"

in the public folder rename index.html to volt.html

9.  TIME TO START. Open 3 terminals

Terminal1
```
sbt backend/run
```

Terminal2
```
sbt 
project frontend
~fastLinkJS
```

Terminal3
```
export NODE_OPTIONS=--openssl-legacy-provider
npm start
```

10. open
localhost:30190/register

You should be alble to register and login.


https://simerplaha.github.io/html-to-scala-converter/

You will need this to help you build your screens fast. 



