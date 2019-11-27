# CSCE-629-FALL2019
Personal repository for CSCE 629 Algorithms at Texas A&amp;M University.

The GitHub repository URL is https://github.com/wumrwds/CSCE-629-FALL2019 .

<br/>

### How to Run the Project

This project is written in Java and it's a Maven project.

If you have installed Apache Maven, you can run it by entering the following commands in your shell:

```shell
$ mvn clean package
$ java -cp target/graph-algorithms-1.0-SNAPSHOT.jar edu.tamu.wumrwds.MaxBandwidthPath
```

As I used a tool for printing logs, it's better to call `mvn clean package` to package a jar file to link all the maven dependencies when using the console to run.

The library I used is listed as follows (See pom.xml):

```xml
<properties>
    <slf4j-log4j12.version>2.0.0-alpha1</slf4j-log4j12.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>${slf4j-log4j12.version}</version>
    </dependency>
</dependencies>
```

Once you run the application, both the console and the file `log.out` will output the result log.

<br/>

### Report

See the link: https://github.com/wumrwds/CSCE-629-FALL2019/blob/master/report.md

