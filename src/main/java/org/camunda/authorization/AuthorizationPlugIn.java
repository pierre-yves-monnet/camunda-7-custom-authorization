package org.camunda.authorization;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;
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
}
