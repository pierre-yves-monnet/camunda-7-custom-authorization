package org.camunda.authorization;

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

  public void checkAuthorization(CompositePermissionCheck compositePermissionCheck) {
    final Authentication currentAuthentication = getCurrentAuthentication();
    logger.info("checkAuthorization:{} userId{}",compositePermissionCheck, currentAuthentication.getUserId());
  }

  public void checkAuthorization(Permission permission, Resource resource, String resourceId) {
    final Authentication currentAuthentication = getCurrentAuthentication();
    logger.info("checkAuthorization: Permission{} Resource:{} resourceid{} userId{}",
        permission.getName(), resource.resourceName(), resourceId,
        currentAuthentication.getUserId());
  }
  public boolean isAuthorized(Permission permission, Resource resource, String resourceId) {
    final Authentication currentAuthentication = getCurrentAuthentication();

    logger.info("isAuthorized: Permission{} Resource:{} resourceid{} userId{}", permission.getName(), resource.resourceName(),
        resourceId, currentAuthentication.getUserId());
    return true;
  }
}
