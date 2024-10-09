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

public class IndexQueryPart implements UserSpecifiedFilter {

    private static final char VALUE_SEPARATOR = 'q';
    private static final char DOMAIN_SEPARATOR = 'j';
    private List<String> lookfor = new ArrayList<>();
    private final FilterField filterField;

    public IndexQueryPart(FilterField filterField, String values) {
        this.filterField = filterField;
        for (String value : splitValues(values)) {
            this.lookfor.add(addOptionalDomain(filterField.getDomain(), normalize(value)));
        }
    }

    private final String addOptionalDomain(String domain, String normalize) {
        return Objects.nonNull(domain) ? domain + VALUE_SEPARATOR + normalize : normalize;
    }

    public IndexQueryPart(String key, FilterField filterField, String values) {
        this.filterField = filterField;
        for (String value : splitValues(values)) {
            lookfor.add(normalize(key) + DOMAIN_SEPARATOR + filterField.getDomain() + VALUE_SEPARATOR + normalize(
                value));
        }
    }

    private List<String> splitValues(String value) {
        String i = value != null ? value : "";
        return Arrays.asList(i.split("[ ,\\-._]+"));
    }

    private String normalize(String string) {
        return string.toLowerCase().replaceAll("[\0-/:-`{-¿]", "");
    }

    @Override
    public FilterField getFilterField() {
        return filterField;
    }

    @Override
    public String toString() {
        return "~(" + filterField.getSearchField() + ')' + String.join(" ", lookfor) + "~";
    }
}
