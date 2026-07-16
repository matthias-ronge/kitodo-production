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

package org.kitodo.dataformat.iiif;

public enum ViewingDirection {
    LEFT_TO_RIGHT("left-to-right"),
    RIGHT_TO_LEFT("right-to-left"),
    TOP_TO_BOTTOM("top-to-bottom"),
    BOTTOM_TO_TOP("bottom-to-top");

    private String code;

    ViewingDirection(String code) {
        this.code = code;
    }
}
