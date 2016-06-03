# messendgerOOP

<p>Программа представляет собой клиент-серверное приложение, которое позволяет клиентам при подключении к серверу обмениваться друг с
другом сообщениями.</p> 

<p>После подключения клиент должен отправить тестовое сообщение, после которого ему будет доступен следующий набор команд:</p> 
<ul>
 <li>\login <user_login> <password> - for authorization</li>
 <li>\login new <user_login> <password> - for register new user</li>
 <li>\history - for getting history of messages</li>
 <li>\find <key_word> - for searching through the history</li>
 <li>\create_chat <chat_name> - create new chat with chat_name name</li>
 <li>\connect_chat <chat_name> - connect you to chat with chat_name name</li>
 <li>q|exit - for leaving chat</li>
</ul>

<p>Для корректной работы приложения необходимы следующие библиотеки:</p> 
<ul>
 <li>jackson-core-asl-1.9.13.jar</li>
 <li>ojdbc6.jar</li>
 <li>org_mockito_mockito_core_1_9_0.xml</li>
</ul>

<p>Проект собирается с помощью Maven. pom-файл смотрите внутри самого поекта. Здесь приведен его код.</p> 

<p>
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>KzkCo</groupId>
    <artifactId>Messendger</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <maven.compiler.useIncrementalCompilation>false</maven.compiler.useIncrementalCompilation>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>1.4.0</version>
                <configuration>
                    <mainClass>working.Main</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
    <dependencies>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.12</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.7.12</version>
        </dependency>
        <dependency>
            <groupId>com.oracle</groupId>
            <artifactId>ojdbc</artifactId>
            <version>14</version>
            <scope>system</scope>
            <systemPath>${basedir}/.idea/libraries/ojdbc6.jar</systemPath>
            <!-- must match file name -->
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>9.4-1204-jdbc42</version>
        </dependency>
        <dependency>
            <groupId>org.codehaus.jackson</groupId>
            <artifactId>jackson-mapper-asl</artifactId>
            <version>1.9.13</version>
        </dependency>
        <dependency>
            <groupId>com.mchange</groupId>
            <artifactId>c3p0</artifactId>
            <version>0.9.5.1</version>
        </dependency>

        <dependency>
            <groupId>org.hamcrest</groupId>
            <artifactId>hamcrest-core</artifactId>
            <version>1.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit-dep</artifactId>
            <version>4.10</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.hamcrest</groupId>
            <artifactId>hamcrest-library</artifactId>
            <version>1.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
                <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>1.9.0</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>
    </dependencies>



</project>
</p>
