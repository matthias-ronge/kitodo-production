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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A portion of the filter entered by the user that is resolved through the
 * search index.
 */
class IndexQueryPart implements UserSpecifiedFilter {

    private static final String UNIQUE_PARAMETER_EXTENSION = "query";

    private static final char VALUE_SEPARATOR = ':';
    private final List<String> lookfor = new ArrayList<>();
    private final FilterField filterField;
    private final boolean operand;

    /**
     * Constructor. Creates a new IndexQueryPart.
     * 
     * @param filterField
     *            search field selected by the user
     * @param values
     *            search terms
     * @param operand
     *            whether the search must match
     */
    IndexQueryPart(FilterField filterField, String values, boolean operand) {
        this.filterField = filterField;
        for (String value : splitValues(values)) {
            if (value.length() >= filterField.getMinTokenLength()) {
                this.lookfor.add(normalize(value));
            }
        }
        this.operand = operand;
    }

    /**
     * Constructor. Creates a new IndexQueryPart.
     * 
     * @param key
     *            in the ruleset
     * @param filterField
     *            search field selected by the user
     * @param values
     *            search terms
     * @param operand
     *            whether the search must match
     */
    IndexQueryPart(String key, FilterField filterField, String values, boolean operand) {
        this.filterField = filterField;
        for (String value : splitValues(values)) {
            lookfor.add(normalize(key) + VALUE_SEPARATOR + normalize(value));
        }
        this.operand = operand;
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

    /**
     * Inserts the search parameters into the database query logic.
     * 
     * @param varName
     *            variable name of the HQL search
     * @param parameterName
     *            name of the search parameter for the results
     * @param restrictions
     *            puts the HQL restrictions here
     * @param parameters
     *            puts query parameters here
     * @param innerJoins 
     *            puts join requirements here
     */
    void putQueryParameters(String varName, String parameterName,
            Collection<String> restrictions, Map<String, Object> parameters,
            Set<String> innerJoins) {
        if (lookfor.size() == 1) {
            restrictions.add(
                    "WHERE " + filterField.getSearchField() + " " + (operand ? "=" : "!=") + " :" + parameterName);
            parameters.put(parameterName, lookfor.get(0));
        } else if (lookfor.size() >= 1) {
            int queryCount = 0;
            for (String lookingFor : lookfor) {
                queryCount++;
                String uniqueParameterName = parameterName + UNIQUE_PARAMETER_EXTENSION + queryCount;
                restrictions.add("WHERE " + filterField.getSearchField() + " " + (operand ? "=" : "!=") + " :"
                          + uniqueParameterName);
                parameters.put(uniqueParameterName, lookingFor);
            }
        }
        innerJoins.add(varName + "." + filterField.getSearchField() + " " + filterField.getSearchField());
    }

    @Override
    public String toString() {
        return "~(" + filterField.getSearchField() + ')' + String.join(" ", lookfor) + "~";
    }
}
