# SimplexSS

 ![](https://img.shields.io/github/license/SimplexDevelopment/SimplexSS)
 ![](https://img.shields.io/github/languages/top/SimplexDevelopment/SimplexSS)
 ![](https://img.shields.io/github/workflow/status/SimplexDevelopment/SimplexSS/CodeQL/main) 
 ![](https://img.shields.io/github/v/release/SimplexDevelopment/SimplexSS?include_prereleases) 
 ![](https://jitpack.io/v/SimplexDevelopment/SimplexSS.svg)  
 ![](https://img.shields.io/github/issues/SimplexDevelopment/SimplexSS) 
 ![](https://img.shields.io/github/stars/SimplexDevelopment/SimplexSS?style=social) 
 ![](https://img.shields.io/github/forks/SimplexDevelopment/SimplexSS?style=social) 

 A reactive non blocking api for scheduling runnable tasks (called services)
 
# Adding SimplexSS to your project

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

# Using SimplexSS

 To use Simplex Scheduling System, the first thing you need to do is initialize a new instance of the Scheduling System.

 ```Java
 private SchedulingSystem<YourPlugin> scheduler;
 
 @Override
 public void onEnable() {
     this.scheduler = new SchedulingSystem<>(this);
 }
 ```
 
 Then, you should use the Service Manager to create some new service pools. You can use `ServicePool#emptyBukkitServicePool(String, JavaPlugin)` for a service pool which will operate on the main server thread, or you can use `ServicePool#emptyServicePool(String, boolean)` for a completely separate, non-blocking scheduler which can be either singular or multithreaded. You should also use the service manager stream to register your services, and assign a Flux<Disposable> object so we can cancel the services later on in `JavaPlugin#onDisable()`.
 
 ```Java
 private Flux<Disposable> disposables;
 
 @Override
 public void onEnable() {
     this.scheduler = new SchedulingSystem<>(this);
     
     YourFirstService firstService;
     YourSecondService secondService;
     YourThirdService thirdService;
     
     scheduler.getServiceManager().subscribe(manager -> {
         manager.emptyBukkitServicePool("pool_name", this).subscribe(pool -> {
             Set<Disposable> dispos = new HashSet<>();
 
             firstService = new YourFirstService(pool, "first_service_name");
             secondService = new YourSecondService(pool, "second_service_name", 20 * 60L);
             thirdService = new YourThirdService(pool, "third_service_name", 20 * 60L, 20 * 60 * 10L, true, false);
 
             scheduler.queue(firstService).subscribe(dispos::add);
             scheduler.queue(secondService).subscribe(dispos::add);
             scheduler.queue(thirdService).subscribe(dispos::add);
 
             disposables = Flux.fromIterable(dispos);
         });
     });
 }
 ```

 You can then stop, cancel, and/or dispose of the tasks in your `JavaPlugin#onDisable()` method by calling:
 ```Java
 @Override
 public void onDisable() {
     scheduler.getServiceManager().subscribe(manager -> {
         manager.getServicePools().doOnEach(signal -> Objects.requireNonNull(signal.get())
                    .stopServices(disposables)
                    .subscribe());
     });
 }
 ```
