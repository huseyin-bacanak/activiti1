package demo.fixture;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;

public class ActivitiEngineFixture {
  /**
   * Get a pre configured process engine for test purposes.
   * @return processEngine ProcessEngine
   */
  public static ProcessEngine processEngine() {
    ProcessEngine processEngine = ProcessEngineConfiguration
            .createStandaloneInMemProcessEngineConfiguration()
            .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
            .setJdbcUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000")
            .setJobExecutorActivate(true)
            .buildProcessEngine();

    return processEngine;
  }
}
