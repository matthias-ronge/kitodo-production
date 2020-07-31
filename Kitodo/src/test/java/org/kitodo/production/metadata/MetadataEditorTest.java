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

package org.kitodo.production.metadata;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.kitodo.api.dataformat.LogicalStructure;
import org.kitodo.api.dataformat.mets.LinkedMetsResource;

public class MetadataEditorTest {

    @Test
    public void testDetermineLogicalStructurePathToChildRecursive() throws Exception {
        LogicalStructure logicalStructure = new LogicalStructure();
        logicalStructure.setType("newspaperYear");

        LogicalStructure monthLogicalStructure = new LogicalStructure();
        monthLogicalStructure.setType("newspaperMonth");

        LogicalStructure wrongDayLogicalStructure = new LogicalStructure();
        wrongDayLogicalStructure.setType("newspaperDay");
        wrongDayLogicalStructure.setLabel("wrong");
        LinkedMetsResource wrongLink = new LinkedMetsResource();
        wrongLink.setUri(URI.create("database://?process.id=13"));
        wrongDayLogicalStructure.setLink(wrongLink);
        monthLogicalStructure.getChildren().add(wrongDayLogicalStructure);

        LogicalStructure correctDayLogicalStructure = new LogicalStructure();
        correctDayLogicalStructure.setType("newspaperDay");
        correctDayLogicalStructure.setLabel("correct");
        LinkedMetsResource correctLink = new LinkedMetsResource();
        correctLink.setUri(URI.create("database://?process.id=42"));
        correctDayLogicalStructure.setLink(correctLink);
        monthLogicalStructure.getChildren().add(correctDayLogicalStructure);

        logicalStructure.getChildren().add(monthLogicalStructure);
        int number = 42;

        Method determineLogicalStructurePathToChild = MetadataEditor.class
                .getDeclaredMethod("determineLogicalStructurePathToChild", LogicalStructure.class, int.class);
        determineLogicalStructurePathToChild.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<LogicalStructure> result = (List<LogicalStructure>) determineLogicalStructurePathToChild
                .invoke(null, logicalStructure, number);

        Assert.assertEquals(
            new LinkedList<>(Arrays.asList(logicalStructure, monthLogicalStructure, correctDayLogicalStructure)),
            result);
    }
}
