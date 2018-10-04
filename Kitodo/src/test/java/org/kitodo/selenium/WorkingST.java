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

package org.kitodo.selenium;

import java.io.File;

import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kitodo.config.KitodoConfig;
import org.kitodo.config.enums.ParameterCore;
import org.kitodo.data.database.beans.Task;
import org.kitodo.data.database.helper.enums.TaskStatus;
import org.kitodo.selenium.testframework.BaseTestSelenium;
import org.kitodo.selenium.testframework.Browser;
import org.kitodo.selenium.testframework.Pages;
import org.kitodo.services.ServiceManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class WorkingST extends BaseTestSelenium {

    private ServiceManager serviceManager = new ServiceManager();

    @Before
    public void login() throws Exception {
        Pages.getLoginPage().goTo().performLoginAsAdmin();
    }

    @After
    public void logout() throws Exception {
        Pages.getTopNavigation().logout();
        if (Browser.isAlertPresent()) {
            Browser.getDriver().switchTo().alert().accept();
        }
    }

    @Test
    public void takeOpenTaskAndGiveItBackTest() throws Exception {
        Task task = serviceManager.getTaskService().getById(2);
        assertEquals("Task can not be taken by user!", task.getProcessingStatusEnum(), TaskStatus.OPEN);

        Pages.getTasksPage().goTo().takeOpenTask();
        assertTrue("Redirection after click take task was not successful", Pages.getCurrentTasksEditPage().isAt());

        task = serviceManager.getTaskService().getById(2);
        assertEquals("Task was not taken by user!", task.getProcessingStatusEnum(), TaskStatus.INWORK);

        Pages.getCurrentTasksEditPage().releaseTask();
        assertTrue("Redirection after click release task was not successful", Pages.getTasksPage().isAt());

        task = serviceManager.getTaskService().getById(2);
        assertEquals("Task was not released by user!", task.getProcessingStatusEnum(), TaskStatus.OPEN);
    }

    @Test
    public void editOwnedTaskTest() throws Exception {
        Pages.getTasksPage().goTo().editOwnedTask();
        assertTrue("Redirection after click edit own task was not successful", Pages.getCurrentTasksEditPage().isAt());

        Pages.getCurrentTasksEditPage().closeTask();
        assertTrue("Redirection after click close task was not successful", Pages.getTasksPage().isAt());

        Task task = serviceManager.getTaskService().getById(8);
        assertEquals("Task was not closed!", task.getProcessingStatusEnum(), TaskStatus.DONE);
    }

    @Test
    public void downloadDocketTest() throws Exception {
        Pages.getProcessesPage().goTo().downloadDocket();
        assertTrue("Docket file was not downloaded", new File(Browser.DOWNLOAD_DIR + "Second process.pdf").exists());
    }

    @Test
    public void downloadLogTest() throws Exception {
        assumeTrue(!SystemUtils.IS_OS_WINDOWS && !SystemUtils.IS_OS_MAC);

        Pages.getProcessesPage().goTo().downloadLog();
        assertTrue("Log file was not downloaded",
            new File(KitodoConfig.getParameter(ParameterCore.DIR_USERS) + "kowal/Second process_log.xml").exists());
    }

    @Test
    public void editMetadataTest() throws Exception {
        Pages.getProcessesPage().goTo().editMetadata();
        assertTrue("Redirection after click edit metadata was not successful", Pages.getMetadataEditorPage().isAt());
    }

    @Test
    public void downloadSearchResultAsExcelTest() throws Exception {
        Pages.getProcessesPage().goTo().downloadSearchResultAsExcel();
        assertTrue("Search result excel file was not downloaded",
            new File(Browser.DOWNLOAD_DIR + "search.xls").exists());
    }

    @Test
    public void downloadSearchResultAsPdfTest() throws Exception {
        Pages.getProcessesPage().goTo().downloadSearchResultAsPdf();
        assertTrue("Search result pdf file was not downloaded", new File(Browser.DOWNLOAD_DIR + "search.pdf").exists());
    }
}
