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

package org.kitodo.production.services.data.search;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.kitodo.data.database.beans.BaseBean;

public class BeanQuery<T extends BaseBean> {

    private static final String KEYWORD_FROM = "FROM";
    private static final String KEYWORD_SELECT = "SELECT";
    private static final String KEYWORD_SORT = "ORDER BY";
    private static final String KEYWORD_SORT_ASCENDING = "ASC";

    private static final String BASEBEAN_FIELD_ID = "id";

    private static final List<Pair<String, String>> DEFAULT_SORTING = Collections
            .singletonList(Pair.of(BASEBEAN_FIELD_ID, KEYWORD_SORT_ASCENDING));

    private final Class<T> beanClass;
    private List<Pair<String, String>> sorting;

    /**
     * Creates a new object for creating search queries.
     * 
     * @param beanClass
     */
    public BeanQuery(Class<T> beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * Returns the class of bean for which this object forms queries.
     * 
     * @return the class of bean
     */
    public Class<T> getBeanClass() {
        return beanClass;
    }

    /**
     * Returns the query parameters used for the last query formed. Can be
     * {@code null} if no parameters were used.
     * 
     * @return the query parameters used, or {@code null}
     */
    public Map<String, Object> getQueryParameters() {
        return null;
    }

    /**
     * Forms the compiled query as a string.
     * 
     * @return the query
     */
    public String formIdQuery() {
        List<String> query = Arrays.asList(KEYWORD_SELECT, BASEBEAN_FIELD_ID, KEYWORD_FROM, beanClass.getName(),
            formSortString());
        return String.join(" ", query);
    }

    private String formSortString() {
        List<Pair<String, String>> useSorting = Objects.nonNull(sorting) ? sorting : DEFAULT_SORTING;
        List<String> sortings = useSorting.stream().map(λ -> λ.getLeft() + " " + λ.getRight())
                .collect(Collectors.toList());
        return KEYWORD_SORT + " " + String.join(", ", sortings);
    }
}
