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

package org.kitodo.production.services.data.lookup;
import java.util.*;

public class DatabaseQueryPart implements UserSpecifiedFilter {

    private FilterField filterField;
    private Integer firstId;
    private Integer upToId;

    public DatabaseQueryPart(FilterField filterField, String group, String group2) {
        this.filterField = filterField;
        this.firstId = Integer.valueOf(group);
        this.upToId = Objects.nonNull(group2) ? Integer.valueOf(group2) : null;
    }

    @Override
    public FilterField getFilterField() {
        return filterField;
    }

    @Override
    public String toString() {
        return "process." + filterField.getProcessQuery() + (upToId == null ? " = " + firstId
                : " BETWEEN " + firstId + " AND " + upToId);
    }
}
