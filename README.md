Air Community Platform
============================

Air Community Platform.

**MUST** follow the instructions of **NOTE** section.

#### NOTE
* Please install `JDK 1.8` before build the project.
* **MUST NOT** add any domain logics to this project.
* **MUST NOT** push any jar files, use maven dependency instead.
* **MUST NOT** push any unnecessary binary files.
* **MUST** push source code with meaningful message `git commit -m "meaningful message"`.
* **MUST** import `codequality/codestyle-formatter.xml`, and **format source code** (CTRL+SHIFT+F) and **organize imports** (CTRL+SHIFT+O) before commit.
* **MUST** use standard `JavaDoc Tags` on all java source code.
* **SHOULD** use `English` in JavaDoc, comments and any source code related resources **as possible**.
* **SHOULD** follow [Java Coding Conventions](http://www.oracle.com/technetwork/java/codeconventions-150003.pdf) and [Java Style Guide](https://google.github.io/styleguide/javaguide.html) if you haven't to improve code quality.


#### Run in Eclipse

* Project Properties > Maven > Active Maven Profiles: dev
* Run `net.aircommunity.platform.Application` with VM Arguments: `-Dspring.profiles.active=dev`



#### Build for Development

`mvn clean package -P dev`

#### Build for Production

`mvn clean package -P prd`

 
#### Deployment

* Require `MySQL 5.5.3+` to support `utf8mb4` (most bytes 4)
* Update `/etc/mysql/my.cnf`, and `service mysql restart`

	[mysqld]
	skip-character-set-client-handshake
	collation-server=utf8_unicode_ci
	character-set-server=utf8
* Check charset: 

    SHOW VARIABLES WHERE Variable_name LIKE 'character_set_%' OR Variable_name LIKE 'collation%';

* Create database

	CREATE USER 'airadmin' IDENTIFIED BY 'air@p0o9i8u7';
	GRANT ALL PRIVILEGES ON *.* TO 'airadmin'@'%' IDENTIFIED BY 'air@p0o9i8u7' WITH GRANT OPTION;
	GRANT ALL PRIVILEGES ON *.* TO 'airadmin'@'localhost' IDENTIFIED BY 'air@p0o9i8u7' WITH GRANT OPTION;
	FLUSH PRIVILEGES;
	CREATE DATABASE air_platform CHARACTER SET utf8 COLLATE utf8_general_ci;

* Update Table 
   
    Update table after all tables are auto generated:
    update air_platform_product_comment column: content set encoding to utf8mb4 and collation to utf8mb4_general_ci to support emoji

* Config Redis Cache
    
    maxmemory in bytes, e.g. 3G=3221225472
    maxmemory 3221225472
 
 * Run Platform
   
    AIR_HOME/start.sh - start platform
    AIR_HOME/stop.sh  - stop platform
    Where AIR_HOME is the home of AIR Platform deployment
 