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

package org.kitodo.production.services.data;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.kitodo.data.database.beans.Process;
import org.kitodo.production.services.ServiceManager;

public class ProcessServiceTest {

    @Test
    public void shouldGetSortedCorrectionSolutionMessages() {
        final Process process = new Process();

        Property firstProperty = new Property();
        firstProperty.setId(1);
        firstProperty.setTitle("Korrektur notwendig");
        firstProperty.setValue("Fix it");
        firstProperty.setCreationDate(null);

        Property secondProperty = new Property();
        secondProperty.setId(2);
        secondProperty.setTitle("Korrektur notwendig");
        secondProperty.setValue("Fix it also");
        secondProperty.setCreationDate(null);

        Property thirdProperty = new Property();
        thirdProperty.setId(3);
        thirdProperty.setTitle("Other title");
        thirdProperty.setValue("Other value");
        thirdProperty.setCreationDate("2017-12-01");

        Property fourthProperty = new Property();
        fourthProperty.setId(4);
        fourthProperty.setTitle("Korrektur durchgef\u00FChrt");
        fourthProperty.setValue("Fixed second");
        fourthProperty.setCreationDate("2017-12-05");

        Property fifthProperty = new Property();
        fifthProperty.setId(5);
        fifthProperty.setTitle("Korrektur durchgef\u00FChrt");
        fifthProperty.setValue("Fixed first");
        fifthProperty.setCreationDate("2017-12-03");

        process.getProperties().add(firstProperty);
        process.getProperties().add(secondProperty);
        process.getProperties().add(thirdProperty);
        process.getProperties().add(fourthProperty);
        process.getProperties().add(fifthProperty);

    }

    @Test
    public void testGetMetadataFileUri() {
        Process process = new Process();
        process.setProcessBaseUri(URI.create("relative/path/no/ending/slash"));
        URI uri = ServiceManager.getProcessService().getMetadataFileUri(process);
        Assert.assertEquals(URI.create("relative/path/no/ending/slash/meta.xml"), uri);

        process.setProcessBaseUri(URI.create("relative/path/with/ending/slash/"));
        uri = ServiceManager.getProcessService().getMetadataFileUri(process);
        Assert.assertEquals(URI.create("relative/path/with/ending/slash/meta.xml"), uri);
    }

    @Test
    public void testGetProcessURI() {
        Process process = new Process();
        process.setId(42);
        URI uri = ServiceManager.getProcessService().getProcessURI(process);
        Assert.assertEquals(URI.create("database://?process.id=42"), uri);
    }

    @Test
    public void testProcessIdFromUri() {
        URI uri = URI.create("database://?process.id=42");
        int processId = ServiceManager.getProcessService().processIdFromUri(uri);
        Assert.assertEquals(42, processId);
    }
}
