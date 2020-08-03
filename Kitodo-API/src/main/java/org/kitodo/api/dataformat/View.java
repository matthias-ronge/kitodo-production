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

import java.util.Objects;

/**
 * A view on a media unit. The individual levels of the
 * {@link LogicalStructure} refer to {@code View}s on
 * {@link PhysicalStructure}s. At the moment, each {@code View} refers to exactly one
 * {@code PhysicalStructure} as a whole.
 */
public class View {
    /**
     * Media unit in view.
     */
    private PhysicalStructure physicalStructure;

    /**
     * Returns the media unit in the view.
     *
     * @return the media unit
     */
    public PhysicalStructure getPhysicalStructure() {
        return physicalStructure;
    }

    /**
     * Inserts a media unit into the view.
     *
     * @param physicalStructure
     *            media unit to insert
     */
    public void setPhysicalStructure(PhysicalStructure physicalStructure) {
        this.physicalStructure = physicalStructure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        View view = (View) o;
        return Objects.equals(physicalStructure, view.physicalStructure);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;
        hashCode = prime * hashCode + ((physicalStructure == null) ? 0 : physicalStructure.hashCode());
        return hashCode;
    }
}
