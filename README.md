# Authorization

# Principle

Create your own AuthorizationManager. in this class, in the method
````
public boolean isAuthorized(Permission permission, Resource resource, String resourceId) {
    final Authentication currentAuthentication = getCurrentAuthentication();
    logger.info("isAuthorized: Permission{} Resource:{} resourceid{} userId{}", permission.getName(), resource.resourceName(),
        resourceId, currentAuthentication.getUserId());

````

Decide, for the resource and the user, your strategy. Throw an exception if the operation is not allowed for a user.

Visit src/main/java/org/camunda/authorization/BlueAuthorization.java
# Register your AuthorizationService

To register your service, use the plugIn feature. Create a plug in, extend the ProcessEnginePlugin, and 
in the pre-init, register your service.


````
public class AuthorizationPlugIn implements ProcessEnginePlugin {
````

To override the default Authorization, use the ShadowManagerFactory

```java

  /**
   * This class is used to shadow an existing service. it overrides a class, and return the new one. 
   */
  public class ShadowManagerFactory extends GenericManagerFactory {

    Class<?> overrideClass;
    
    ShadowManagerFactory(Class<?> overrideClass, Class<? extends Session> replaceClass) {
      super(replaceClass);
      this.overrideClass = overrideClass;
    }
    
    @Override
    public Class<?> getSessionType() {
      return overrideClass;
    }
  }
```
and register the BlueAuthorization

```
listSessionFactory.add( new ShadowManagerFactory(AuthorizationManager.class, BlueAuthorization.class));
```

Then, and access to the AuthorizationManager return your class.




Visit src/main/java/org/camunda/authorization/AuthorizationPlugIn.java

# Other resources
https://camunda.slack.com/archives/C6M6X3EKZ/p1717451825508819
org.camunda.bpm.engine. interface AuthorizationService
