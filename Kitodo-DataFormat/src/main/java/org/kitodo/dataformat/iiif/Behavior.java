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

public enum Behavior {
    AUTO_ADVANCE("auto-advance"),
    NO_AUTO_ADVANCE("no-auto-advance"),
    REPEAT("repeat"),
    NO_REPEAT("no-repeat"),

    UNORDERED("unordered"),
    INDIVIDUALS("individuals"),
    CONTINUOUS("continuous"),
    PAGED("paged"),
    FACING_PAGES("facing-pages"),
    NON_PAGED("non-paged"),

    MULTI_PART("multi-part"),
    TOGETHER("together"),

    SEQUENCE("sequence"),
    THUMBNAIL_NAV("thumbnail-nav"),
    NO_NAV("no-nav"),

    HIDDEN("hidden");

    private String code;

    Behavior(String code) {
        this.code=code;
    }
}
