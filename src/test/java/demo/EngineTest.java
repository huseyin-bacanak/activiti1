package demo;

import static org.junit.Assert.assertNotNull;

import demo.fixture.ActivitiEngineFixture;
import org.activiti.engine.ProcessEngine;
import org.junit.Test;

public class EngineTest {

  @Test
  public void startUpTest() {
    ProcessEngine processEngine = ActivitiEngineFixture.processEngine();
    assertNotNull(processEngine);
  }
}
