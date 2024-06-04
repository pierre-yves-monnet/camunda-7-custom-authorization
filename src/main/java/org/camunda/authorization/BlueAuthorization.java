package org.camunda.authorization;

import org.camunda.bpm.engine.AuthorizationException;
import org.camunda.bpm.engine.authorization.Permission;
import org.camunda.bpm.engine.authorization.Resource;
import org.camunda.bpm.engine.impl.db.CompositePermissionCheck;
import org.camunda.bpm.engine.impl.identity.Authentication;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlueAuthorization extends AuthorizationManager {

  Logger logger = LoggerFactory.getLogger(BlueAuthorization.class.getName());

  @Value("${camunda.bpm.authorization.readonly:false}")
  public boolean readOnlyAccess;

  @Override
  public void checkAuthorization(CompositePermissionCheck compositePermissionCheck) {
    final Authentication currentAuthentication = getCurrentAuthentication();
    logger.info("checkAuthorization:{} userId{}",compositePermissionCheck, currentAuthentication.getUserId());
  }

  @Override
  public void checkAuthorization(Permission permission, Resource resource, String resourceId) {
    logger.info("checkAuthorization: Permission:[{}] Resource:[{}] resourceId:[{}] userId:[{}]",
        permission.getName(), resource.resourceName(), resourceId,
        getCurrentUserId());
    if (resource.resourceName().equals("Deployment"))
      throw new AuthorizationException("Can't access deployment");
  }

  @Override
  public boolean isAuthorized(Permission permission, Resource resource, String resourceId) {
    logger.info("isAuthorized: Permission[{}] Resource:[{}] resourceId:[{}] userId:[{}]", permission.getName(), resource.resourceName(),
        resourceId, getCurrentUserId());
    return true;
  }

  /**
   * Return the current UserId, if there is one for this request.
   * @return the userId, null if there is not
   */
  private String getCurrentUserId() {
    final Authentication currentAuthentication = getCurrentAuthentication();
    if (currentAuthentication!=null && currentAuthentication.getUserId()!=null)
      return currentAuthentication.getUserId();
    return null;
  }
}
