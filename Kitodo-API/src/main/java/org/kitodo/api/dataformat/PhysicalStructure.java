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

package org.kitodo.api.dataformat;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.kitodo.api.dataformat.mets.KitodoUUID;

public class PhysicalStructure extends Division<PhysicalStructure> {
    /**
     * Each media unit can be available in different variants, for each of which
     * a media file is available. This is in this map.
     */
    private Map<MediaVariant, URI> mediaFiles = new HashMap<>();

    /**
     * Saves the METS identifier for the division.
     */
    private String metsDivReferrerId;

    /**
     * List of LogicalStructures this view is assigned to.
     */
    private List<LogicalStructure> logicalStructures;

    /**
     * Creates a new PhysicalStructure.
     */
    public PhysicalStructure() {
        logicalStructures = new LinkedList<>();
    }

    /**
     * Returns the map of available media variants with the corresponding media
     * file URIs.
     *
     * @return available media variants with corresponding media file URIs
     */
    public Map<MediaVariant, URI> getMediaFiles() {
        return mediaFiles;
    }

    /**
     * Returns the ID of div, or if unknown, creates a new one.
     *
     * @return the ID of div
     */
    public String getDivId() {
        if (Objects.isNull(metsDivReferrerId)) {
            metsDivReferrerId = KitodoUUID.randomUUID();
        }
        return metsDivReferrerId;
    }

    /**
     * Set the ID of div.
     *
     * @param divId
     *            ID of div to set
     */
    public void setDivId(String divId) {
        this.metsDivReferrerId = divId;
    }

    /**
     * Get logicalStructures.
     *
     * @return value of logicalStructures
     */
    public List<LogicalStructure> getLogicalStructures() {
        return logicalStructures;
    }

    @Override
    public String toString() {
        String fileName = "No file (";
        if (!mediaFiles.isEmpty()) {
            URI uri = mediaFiles.entrySet().iterator().next().getValue();
            fileName = FilenameUtils.getBaseName(uri.getPath()).concat(" (");
        }
        if (Objects.nonNull(getType())) {
            fileName = getType() + ' ' + fileName;
        }
        return mediaFiles.keySet().stream().map(MediaVariant::getUse)
                .collect(Collectors.joining(", ", fileName, ")"));
    }

    @Override
    public boolean equals(Object compared) {
        if (this == compared) {
            return true;
        }
        if (!super.equals(compared)) {
            return false;
        }
        if (!(compared instanceof PhysicalStructure)) {
            return false;
        }
        PhysicalStructure other = (PhysicalStructure) compared;
        return Objects.equals(mediaFiles, other.mediaFiles);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((mediaFiles == null) ? 0 : mediaFiles.hashCode());
        return result;
    }
}
