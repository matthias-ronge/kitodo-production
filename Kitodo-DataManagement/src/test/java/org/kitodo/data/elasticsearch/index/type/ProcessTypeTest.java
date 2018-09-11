/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.data.elasticsearch.index.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.kitodo.data.database.beans.Batch;
import org.kitodo.data.database.beans.Docket;
import org.kitodo.data.database.beans.Process;
import org.kitodo.data.database.beans.Project;
import org.kitodo.data.database.beans.Property;
import org.kitodo.data.database.beans.Ruleset;
import org.kitodo.data.database.beans.Task;
import org.kitodo.data.elasticsearch.index.type.enums.BatchTypeField;
import org.kitodo.data.elasticsearch.index.type.enums.ProcessTypeField;
import org.kitodo.data.elasticsearch.index.type.enums.PropertyTypeField;
import org.kitodo.data.elasticsearch.index.type.enums.TaskTypeField;

/**
 * Test class for ProcessType.
 */
public class ProcessTypeTest {

    private static List<Process> prepareData() {

        List<Process> processes = new ArrayList<>();
        List<Property> properties = new ArrayList<>();
        List<Batch> batches = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        Batch batch = new Batch();
        batch.setId(1);
        batch.setTitle("First");
        batches.add(batch);

        Project project = new Project();
        project.setTitle("Project");
        project.setId(1);

        Ruleset ruleset = new Ruleset();
        ruleset.setId(1);

        Docket docket = new Docket();
        docket.setId(1);

        Property firstProperty = new Property();
        firstProperty.setId(1);
        properties.add(firstProperty);

        Property secondProperty = new Property();
        secondProperty.setId(2);
        properties.add(secondProperty);

        Task firstTask = new Task();
        firstTask.setId(1);
        firstTask.setTitle("Task one");
        tasks.add(firstTask);

        Task secondTask = new Task();
        secondTask.setId(2);
        secondTask.setTitle("Task two");
        tasks.add(secondTask);

        Process firstProcess = new Process();
        firstProcess.setId(1);
        firstProcess.setTitle("Testing");
        LocalDate localDate = new LocalDate(2017, 1, 1);
        firstProcess.setCreationDate(localDate.toDate());
        firstProcess.setSortHelperImages(20);
        firstProcess.setBatches(batches);
        firstProcess.setTasks(tasks);
        firstProcess.setWikiField("Wiki");
        firstProcess.setProject(project);
        firstProcess.setRuleset(ruleset);
        processes.add(firstProcess);

        Process secondProcess = new Process();
        secondProcess.setId(2);
        secondProcess.setTitle("Rendering");
        secondProcess.setWikiField("Field");
        secondProcess.setSortHelperImages(30);
        secondProcess.setProject(project);
        secondProcess.setDocket(docket);
        secondProcess.setProperties(properties);
        processes.add(secondProcess);

        Process thirdProcess = new Process();
        thirdProcess.setId(3);
        thirdProcess.setTitle("Incomplete");
        processes.add(thirdProcess);

        return processes;
    }

    @Test
    public void shouldCreateFirstDocument() throws Exception {
        ProcessType processType = new ProcessType();

        Process process = prepareData().get(0);
        HttpEntity document = processType.createDocument(process);

        JsonObject actual = Json.createReader(new StringReader(EntityUtils.toString(document))).readObject();

        assertEquals("Key title doesn't match to given value!", "Testing",
            ProcessTypeField.TITLE.getStringValue(actual));
        assertEquals("Key wikiField doesn't match to given value!", "Wiki",
            ProcessTypeField.WIKI_FIELD.getStringValue(actual));
        assertEquals("Key processBaseUri doesn't match to given value!", "",
            ProcessTypeField.PROCESS_BASE_URI.getStringValue(actual));
        assertEquals("Key template.id doesn't match to given value!", 0,
            ProcessTypeField.TEMPLATE_ID.getIntValue(actual));
        assertEquals("Key template.title doesn't match to given value!", "",
            ProcessTypeField.TEMPLATE_TITLE.getStringValue(actual));
        assertEquals("Key creationDate doesn't match to given value!", "2017-01-01 00:00:00",
            ProcessTypeField.CREATION_DATE.getStringValue(actual));
        assertEquals("Key sortHelperStatus doesn't match to given value!", "",
            ProcessTypeField.SORT_HELPER_STATUS.getStringValue(actual));
        assertEquals("Key sortHelperImages doesn't match to given value!", 20,
            ProcessTypeField.SORT_HELPER_IMAGES.getIntValue(actual));
        assertEquals("Key sortHelperArticles doesn't match to given value!", 0,
            ProcessTypeField.SORT_HELPER_ARTICLES.getIntValue(actual));
        assertEquals("Key sortHelperDocstructs doesn't match to given value!", 0,
            ProcessTypeField.SORT_HELPER_DOCSTRUCTS.getIntValue(actual));
        assertEquals("Key sortHelperMetadata doesn't match to given value!", 0,
            ProcessTypeField.SORT_HELPER_METADATA.getIntValue(actual));
        assertEquals("Key project.id doesn't match to given value!", 1,
            ProcessTypeField.PROJECT_ID.getIntValue(actual));
        assertEquals("Key project.title doesn't match to given value!", "Project",
            ProcessTypeField.PROJECT_TITLE.getStringValue(actual));
        assertTrue("Key project.active doesn't match to given value!",
            ProcessTypeField.PROJECT_ACTIVE.getBooleanValue(actual));
        assertEquals("Key docket doesn't match to given value!", 0, ProcessTypeField.DOCKET.getIntValue(actual));
        assertEquals("Key ruleset doesn't match to given value!", 1, ProcessTypeField.RULESET.getIntValue(actual));

        JsonArray templates = ProcessTypeField.TEMPLATES.getJsonArray(actual);
        assertEquals("Size templates doesn't match to given value!", 0, templates.size());

        JsonArray properties = ProcessTypeField.PROPERTIES.getJsonArray(actual);
        assertEquals("Size properties doesn't match to given value!", 0, properties.size());

        JsonArray workpieces = ProcessTypeField.WORKPIECES.getJsonArray(actual);
        assertEquals("Size workpieces doesn't match to given value!", 0, workpieces.size());

        JsonArray batches = ProcessTypeField.BATCHES.getJsonArray(actual);
        assertEquals("Size batches doesn't match to given value!", 1, batches.size());

        JsonObject batch = batches.getJsonObject(0);
        assertEquals("Key batches.id doesn't match to given value!", 1, BatchTypeField.ID.getIntValue(batch));
        assertEquals("Key batches.title doesn't match to given value!", "First",
            BatchTypeField.TITLE.getStringValue(batch));

        JsonArray tasks = ProcessTypeField.TASKS.getJsonArray(actual);
        assertEquals("Size batches doesn't match to given value!", 2, tasks.size());

        JsonObject task = tasks.getJsonObject(0);
        assertEquals("Key tasks.id doesn't match to given value!", 1, TaskTypeField.ID.getIntValue(task));
        assertEquals("Key tasks.title doesn't match to given value!", "Task one",
            TaskTypeField.TITLE.getStringValue(task));

        task = tasks.getJsonObject(1);
        assertEquals("Key tasks.id doesn't match to given value!", 2, TaskTypeField.ID.getIntValue(task));
        assertEquals("Key tasks.title doesn't match to given value!", "Task two",
            TaskTypeField.TITLE.getStringValue(task));
    }

    @Test
    public void shouldCreateSecondDocument() throws Exception {
        ProcessType processType = new ProcessType();

        Process process = prepareData().get(1);
        HttpEntity document = processType.createDocument(process);

        JsonObject actual = Json.createReader(new StringReader(EntityUtils.toString(document))).readObject();

        assertEquals("Key title doesn't match to given value!", "Rendering",
            ProcessTypeField.TITLE.getStringValue(actual));
        assertEquals("Key wikiField doesn't match to given value!", "Field",
            ProcessTypeField.WIKI_FIELD.getStringValue(actual));
        assertEquals("Key processBaseUri doesn't match to given value!", "",
            ProcessTypeField.PROCESS_BASE_URI.getStringValue(actual));
        assertEquals("Key template.id doesn't match to given value!", 0,
            ProcessTypeField.TEMPLATE_ID.getIntValue(actual));
        assertEquals("Key template.title doesn't match to given value!", "",
            ProcessTypeField.TEMPLATE_TITLE.getStringValue(actual));
        assertEquals("Key creationDate doesn't match to given value!", formatDate(process.getCreationDate()),
            ProcessTypeField.CREATION_DATE.getStringValue(actual));
        assertEquals("Key sortHelperStatus doesn't match to given value!", "",
            ProcessTypeField.SORT_HELPER_STATUS.getStringValue(actual));
        assertEquals("Key sortHelperImages doesn't match to given value!", 30,
            ProcessTypeField.SORT_HELPER_IMAGES.getIntValue(actual));
        assertEquals("Key sortHelperArticles doesn't match to given value!", 0,
            ProcessTypeField.SORT_HELPER_ARTICLES.getIntValue(actual));
        assertEquals("Key sortHelperDocstructs doesn't match to given value!", 0,
            ProcessTypeField.SORT_HELPER_DOCSTRUCTS.getIntValue(actual));
        assertEquals("Key sortHelperMetadata doesn't match to given value!", 0,
            ProcessTypeField.SORT_HELPER_METADATA.getIntValue(actual));
        assertEquals("Key project.id doesn't match to given value!", 1,
            ProcessTypeField.PROJECT_ID.getIntValue(actual));
        assertEquals("Key project.title doesn't match to given value!", "Project",
            ProcessTypeField.PROJECT_TITLE.getStringValue(actual));
        assertTrue("Key project.active doesn't match to given value!",
            ProcessTypeField.PROJECT_ACTIVE.getBooleanValue(actual));
        assertEquals("Key docket doesn't match to given value!", 1, ProcessTypeField.DOCKET.getIntValue(actual));
        assertEquals("Key ruleset doesn't match to given value!", 0, ProcessTypeField.RULESET.getIntValue(actual));

        JsonArray templates = ProcessTypeField.TEMPLATES.getJsonArray(actual);
        assertEquals("Size templates doesn't match to given value!", 0, templates.size());

        JsonArray tasks = ProcessTypeField.TASKS.getJsonArray(actual);
        assertEquals("Size batches doesn't match to given value!", 0, tasks.size());

        JsonArray workpieces = ProcessTypeField.WORKPIECES.getJsonArray(actual);
        assertEquals("Size workpieces doesn't match to given value!", 0, workpieces.size());

        JsonArray batches = ProcessTypeField.BATCHES.getJsonArray(actual);
        assertEquals("Size batches doesn't match to given value!", 0, batches.size());

        JsonArray properties = ProcessTypeField.PROPERTIES.getJsonArray(actual);
        assertEquals("Size properties doesn't match to given value!", 2, properties.size());

        JsonObject property = properties.getJsonObject(0);
        assertEquals("Key properties.id doesn't match to given value!", 1,
            PropertyTypeField.ID.getIntValue(property));

        property = properties.getJsonObject(1);
        assertEquals("Key properties.id doesn't match to given value!", 2,
            PropertyTypeField.ID.getIntValue(property));
    }

    @Test
    public void shouldCreateThirdDocument() throws Exception {
        ProcessType processType = new ProcessType();

        Process process = prepareData().get(2);
        HttpEntity document = processType.createDocument(process);

        JsonObject actual = Json.createReader(new StringReader(EntityUtils.toString(document))).readObject();

        assertEquals("Key title doesn't match to given value!", "Incomplete",
            ProcessTypeField.TITLE.getStringValue(actual));
        assertEquals("Key wikiField doesn't match to given value!", "",
            ProcessTypeField.WIKI_FIELD.getStringValue(actual));
        assertEquals("Key processBaseUri doesn't match to given value!", "",
            ProcessTypeField.PROCESS_BASE_URI.getStringValue(actual));
        assertEquals("Key template.id doesn't match to given value!", 0,
            ProcessTypeField.TEMPLATE_ID.getIntValue(actual));
        assertEquals("Key template.title doesn't match to given value!", "",
            ProcessTypeField.TEMPLATE_TITLE.getStringValue(actual));
        assertEquals("Key creationDate doesn't match to given value!", formatDate(process.getCreationDate()),
            ProcessTypeField.CREATION_DATE.getStringValue(actual));
        assertEquals("Key sortHelperStatus doesn't match to given value!", "",
            ProcessTypeField.SORT_HELPER_STATUS.getStringValue(actual));
        assertEquals("Key sortHelperImages doesn't match to given value!", 0,
            ProcessTypeField.SORT_HELPER_IMAGES.getIntValue(actual));
        assertEquals("Key sortHelperArticles doesn't match to given value!", 0,
            ProcessTypeField.SORT_HELPER_ARTICLES.getIntValue(actual));
        assertEquals("Key sortHelperDocstructs doesn't match to given value!", 0,
            ProcessTypeField.SORT_HELPER_DOCSTRUCTS.getIntValue(actual));
        assertEquals("Key sortHelperMetadata doesn't match to given value!", 0,
            ProcessTypeField.SORT_HELPER_METADATA.getIntValue(actual));
        assertEquals("Key project.id doesn't match to given value!", 0,
            ProcessTypeField.PROJECT_ID.getIntValue(actual));
        assertEquals("Key project.title doesn't match to given value!", "",
            ProcessTypeField.PROJECT_TITLE.getStringValue(actual));
        assertFalse("Key project.active doesn't match to given value!",
            ProcessTypeField.PROJECT_ACTIVE.getBooleanValue(actual));
        assertEquals("Key docket doesn't match to given value!", 0, ProcessTypeField.DOCKET.getIntValue(actual));
        assertEquals("Key ruleset doesn't match to given value!", 0, ProcessTypeField.RULESET.getIntValue(actual));

        JsonArray templates = ProcessTypeField.TEMPLATES.getJsonArray(actual);
        assertEquals("Size templates doesn't match to given value!", 0, templates.size());

        JsonArray tasks = ProcessTypeField.TASKS.getJsonArray(actual);
        assertEquals("Size batches doesn't match to given value!", 0, tasks.size());

        JsonArray workpieces = ProcessTypeField.WORKPIECES.getJsonArray(actual);
        assertEquals("Size workpieces doesn't match to given value!", 0, workpieces.size());

        JsonArray batches = ProcessTypeField.BATCHES.getJsonArray(actual);
        assertEquals("Size batches doesn't match to given value!", 0, batches.size());

        JsonArray properties = ProcessTypeField.PROPERTIES.getJsonArray(actual);
        assertEquals("Size properties doesn't match to given value!", 0, properties.size());
    }

    @Test
    public void shouldCreateDocumentWithCorrectAmountOfKeys() throws Exception {
        ProcessType processType = new ProcessType();

        Process process = prepareData().get(0);
        HttpEntity document = processType.createDocument(process);

        JsonObject actual = Json.createReader(new StringReader(EntityUtils.toString(document))).readObject();
        assertEquals("Amount of keys is incorrect!", 21, actual.keySet().size());

        JsonArray batches = ProcessTypeField.BATCHES.getJsonArray(actual);
        JsonObject batch = batches.getJsonObject(0);
        assertEquals("Amount of keys in batches is incorrect!", 3, batch.keySet().size());

        JsonArray tasks = ProcessTypeField.TASKS.getJsonArray(actual);
        JsonObject task = tasks.getJsonObject(0);
        assertEquals("Amount of keys in tasks is incorrect!", 2, task.keySet().size());
    }

    @Test
    public void shouldCreateDocuments() {
        ProcessType processType = new ProcessType();

        List<Process> processes = prepareData();
        Map<Integer, HttpEntity> documents = processType.createDocuments(processes);
        assertEquals("HashMap of documents doesn't contain given amount of elements!", 3, documents.size());
    }

    private String formatDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
