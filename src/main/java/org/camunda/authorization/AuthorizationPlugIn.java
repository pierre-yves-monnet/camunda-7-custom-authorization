package org.camunda.authorization;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.interceptor.Session;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;
import org.camunda.bpm.engine.impl.persistence.GenericManagerFactory;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@Named("custom-authorization-configurations")

public class AuthorizationPlugIn implements ProcessEnginePlugin {
  Logger logger = LoggerFactory.getLogger(AuthorizationPlugIn.class.getName());



  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    logger.info("AuthorizationInit.preInit");
    List<SessionFactory> listSessionFactory= processEngineConfiguration.getCustomSessionFactories();
    listSessionFactory.add( new ShadowManagerFactory(AuthorizationManager.class, com.bnymellon.wm.camunda.bpm.engine.authorization.CustomCeepAuthorization.class));
    processEngineConfiguration.setCustomSessionFactories(listSessionFactory);
  }

  @Override
  public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    logger.info("AuthorizationInit.postInit");

  }

  @Override
  public void postProcessEngineBuild(ProcessEngine processEngine) {
    logger.info("AuthorizationInit.preInit");

  }

  /**
   * This class is used to shadow an existing service. it overrides a class, and return the new one.
   */
  public static class ShadowManagerFactory extends GenericManagerFactory {

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
}
