package demo;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableUpdate;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryServiceTest {
  @Rule
  public ActivitiRule activitiRule =
          new ActivitiRule("activiti.cfg-mem-fullhistory.xml");

  private HistoryService historyService;

  @Before
  public void setup() {
    historyService = activitiRule.getHistoryService();
  }


  @Test
  @Deployment(resources = {"processes/bookorder.bpmn20.xml"})
  public void historicProcessInstanceInfo() {
    // create and complete test process instance
    final Date beforeProcessCreatedTime = new Date();
    final String processInstanceId = startAndComplete();
    final Date afterProcessCompletedTime = new Date();

    // get process instance info
    HistoricProcessInstance historicProcessInstance = historyService
            .createHistoricProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .singleResult();

    assertEquals("startevent1", historicProcessInstance.getStartActivityId());
    assertEquals("endevent1", historicProcessInstance.getEndActivityId());

    assertNotNull(historicProcessInstance);
    assertEquals(processInstanceId, historicProcessInstance.getId());

    // check timeline
    Date processStartTime = historicProcessInstance.getStartTime();
    assertTrue(processStartTime.after(beforeProcessCreatedTime));
    assertTrue(processStartTime.before(afterProcessCompletedTime));

    Date processEndTime = historicProcessInstance.getStartTime();
    assertTrue(processEndTime.after(beforeProcessCreatedTime));
    assertTrue(processEndTime.before(afterProcessCompletedTime));
    assertTrue(processEndTime.before(afterProcessCompletedTime));

    assertTrue(historicProcessInstance.getDurationInMillis() > 0);
  }

  @Test
  @Deployment(resources = {"processes/bookorder.bpmn20.xml"})
  public void historicVariableUpdateInfo() {
    // create and complete test process instance
    startAndComplete();
    HistoryService historyService = activitiRule.getHistoryService();
    List<HistoricDetail> historicVariableUpdateList = historyService
            .createHistoricDetailQuery()
            .variableUpdates()
            .list();

    assertNotNull(historicVariableUpdateList);
    assertEquals(3, historicVariableUpdateList.size());

    HistoricVariableUpdate firstIsbn = (HistoricVariableUpdate)historicVariableUpdateList.get(0);

    assertEquals("string", firstIsbn.getVariableTypeName());
    assertEquals("isbn", firstIsbn.getVariableName());
    assertEquals("123456", firstIsbn.getValue());
    assertEquals(0, firstIsbn.getRevision());

    HistoricVariableUpdate secondIsbn = (HistoricVariableUpdate)historicVariableUpdateList.get(1);
    assertEquals("string", secondIsbn.getVariableTypeName());
    assertEquals("isbn", secondIsbn.getVariableName());
    assertEquals("654321", secondIsbn.getValue());
    assertEquals(1, secondIsbn.getRevision());

    HistoricVariableUpdate extraInformation =
            (HistoricVariableUpdate) historicVariableUpdateList.get(2);
    assertEquals("string", extraInformation.getVariableTypeName());
    assertEquals("extraInfo", extraInformation.getVariableName());
    assertEquals("Extra Information", extraInformation.getValue());
    assertEquals(0, extraInformation.getRevision());
  }

  /**
   * Create and complete a test process instance.
   */
  private String startAndComplete() {
    RuntimeService runtimeService = activitiRule.getRuntimeService();

    Map<String, Object> variableMap = new HashMap<>();
    variableMap.put("isbn", "123456");
    final String processInstanceId = runtimeService
            .startProcessInstanceByKey("bookorder", variableMap)
            .getId();

    TaskService taskService = activitiRule.getTaskService();
    final Task task = taskService.createTaskQuery()
            .taskCandidateGroup("sales")
            .singleResult();

    variableMap = new HashMap<>();
    variableMap.put("extraInfo", "Extra Information");
    variableMap.put("isbn", "654321");
    taskService.complete(task.getId(), variableMap);
    return processInstanceId;
  }
}
