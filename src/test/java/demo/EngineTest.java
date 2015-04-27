package demo;

import static org.junit.Assert.assertNotNull;

import fixture.ActivitiEngineFixture;
import org.activiti.engine.ProcessEngine;
import org.junit.Test;

public class EngineTest {
  @Test
  public void initializeEngine() {
    ActivitiEngineFixture activitiEngineFixture = new ActivitiEngineFixture();
    ProcessEngine processEngine = activitiEngineFixture.getTestProcessEngine();
    assertNotNull(processEngine);
  }
}
