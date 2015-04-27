package fixture;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;

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

  /**
   * Clear databases between tests.
   */
  public void clearDatabase() {
    CommandExecutor commandExecutor = ((ProcessEngineImpl)getTestProcessEngine())
            .getProcessEngineConfiguration()
            .getCommandExecutor();

    CommandConfig config = new CommandConfig().transactionNotSupported();
    commandExecutor.execute(config, new Command<Object>() {
      public Object execute(CommandContext commandContext) {
        DbSqlSession session = commandContext.getSession(DbSqlSession.class);
        session.dbSchemaDrop();
        return null;
      }
    });
  }
}
