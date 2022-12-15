# SimplexSS
 ![](https://img.shields.io/github/workflow/status/SimplexDevelopment/SimplexSS/CodeQL/main) ![](https://img.shields.io/github/v/release/SimplexDevelopment/SimplexSS?include_prereleases) ![](https://img.shields.io/github/license/SimplexDevelopment/SimplexSS) !(https://img.shields.io/github/issues/SimplexDevelopment/SimplexSS) ![](https://img.shields.io/github/stars/SimplexDevelopment/SimplexSS?style=social) ![](https://img.shields.io/github/forks/SimplexDevelopment/SimplexSS?style=social) ![](https://img.shields.io/github/languages/top/SimplexDevelopment/SimplexSS)

 A reactive non blocking api for scheduling runnable tasks (called services)
 
# Using SimplexSS in your project
 In order to use SimplexSS in your project, you need to add the jitpack repository to your build.gradle or pom.xml file.
 Here's an example, in Gradle:
 
 ```gradle
 repositories {
     maven {
         id 'jitpack'
         url 'https://jitpack.io'
     }
 }
 ```
 
 Then, you can add the dependency.
 The `groupId` is `com.github.SimplexDevelopment`
 The `artifactId` is `SimplexSS`
 The `version` is `1.0.1-SNAPSHOT`
 
 It is recommended you use either the Maven Shade Plugin, 
 
 ```maven
 <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-jar-plugin</artifactId>
        ...
        <configuration>
          <archive>
            <manifest>
              <addClasspath>true</addClasspath>
              <mainClass>path.to.MainClass</mainClass>
            </manifest>
          </archive>
        </configuration>
        ...
      </plugin>
    </plugins>
  </build>
  ```
  
 or the Gradle Shadow Plugin (com.github.johnrengelman.shadow).
 
 ```gradle
 plugins {
     id 'com.github.johnrengelman.shadow' version '7.1.2'
 }
 ```
 
 Here is an example of the dependency, in Gradle:
 ```gradle
 dependencies {
    shadow 'com.github.SimplexDevelopment:SimplexSS:1.0.1-SNAPSHOT'
 }
 ```
