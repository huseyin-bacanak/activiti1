package demo.fixture;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;

public class ActivitiEngineFixture {
  private final ProcessEngine processEngine;

  /**
   * Create activiti process engine with in memory database
   * for test purposes.
   */
  public ActivitiEngineFixture() {
    processEngine = ProcessEngineConfiguration
            .createStandaloneInMemProcessEngineConfiguration()
            .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
            .setJdbcUrl("jdbc:h2:mem:activiti")
            .setJobExecutorActivate(true)
            .buildProcessEngine();
  }

  public ProcessEngine getTestProcessEngine() {
    return processEngine;
  }
}
