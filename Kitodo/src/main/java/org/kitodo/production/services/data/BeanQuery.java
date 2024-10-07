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

import static java.lang.Character.charCount;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.kitodo.data.database.beans.BaseBean;
import org.kitodo.data.database.beans.Role;
import org.kitodo.data.database.enums.TaskStatus;
import org.kitodo.production.enums.ProcessState;
import org.primefaces.model.SortOrder;

/**
 * Provides programmatic composition of Hibernate queries.
 */
public class BeanQuery {
    private static final Pattern EXPLICIT_ID_SEARCH = Pattern.compile("id:(\\d+)");
    private static final Map<String, TaskStatus> TASK_STATUS_FOR_FILTER = new HashMap<>() {
        {
            put("steplocked", TaskStatus.LOCKED);
            put("stepopen", TaskStatus.OPEN);
            put("stepinwork", TaskStatus.INWORK);
            put("stepdone", TaskStatus.DONE);
        }
    };
    private final Class<? extends BaseBean> beanClass;
    private final String className;
    private final String varName;
    private final Collection<String> extensions = new ArrayList<>();
    private final Collection<String> restrictions = new ArrayList<>();
    private Pair<String, String> sorting = Pair.of("id", "ASC");
    private final Map<String, SearchFilter> indexQueries = new HashMap<>();
    private final Map<String, Object> parameters = new HashMap<>();

    /**
     * Constructor. Creates a new query builder instance.
     * 
     * @param beanClass
     *            class of beans to search for
     */
    public BeanQuery(Class<? extends BaseBean> beanClass) {
        this.beanClass = beanClass;
        this.className = beanClass.getSimpleName();
        this.varName = className.toLowerCase();
    }

    /**
     * Requires that the hits in a specific field must have a specific value.
     * 
     * @param fieldName
     *            class field that must have the specified value
     * @param value
     *            value that the field must have
     */
    public void addBooleanRestriction(String fieldName, Boolean value) {
        String joker = varName(fieldName);
        restrictions.add(varName + '.' + fieldName + " = :" + joker);
        parameters.put(joker, value);
    }

    /**
     * Requires that the hits must correspond to any of the specified values in
     * the specified class field.
     * 
     * @param fieldName
     *            class field in which the value must be
     * @param values
     *            value that the class field must accept one of
     */
    public void addInCollectionRestriction(String fieldName, Collection<?> values) {
        String parameterName = varName(fieldName);
        restrictions.add(varName + '.' + fieldName + " IN (:" + parameterName + ')');
        parameters.put(parameterName, values);
    }

    /**
     * Requires that the hits in a specific field must have a specific value.
     * 
     * @param field
     *            field that must have the specified value
     * @param value
     *            value that the field must have
     */
    public void addIntegerRestriction(String field, int value) {
        String parameterName = varName(field);
        restrictions.add(varName + '.' + field + " = :" + parameterName);
        parameters.put(parameterName, value);
    }

    /**
     * Requires that the hits do not correspond to any of the specified values
     * in the specified database field ​​(exclusion).
     * 
     * @param field
     *            field in which the value must not be
     * @param values
     *            value that the field must not accept
     */
    public void addNotInCollectionRestriction(String field, Collection<Integer> values) {
        String parameterName = varName(field);
        restrictions.add(varName + '.' + field + " NOT IN (:" + parameterName + ')');
        parameters.put(parameterName, values);
    }

    /**
     * Requires that the value in the given field is {@code null}.
     * 
     * @param field
     *            field that should be {@code null}
     */
    public void addNullRestriction(String field) {
        restrictions.add(varName + '.' + field + " IS NULL");
    }

    /**
     * Requires that a member with the given ID is in the {@code @ManyToMany}
     * relationship.
     * 
     * @param xField
     *            name of {@code @ManyToMany}-mapped field
     * @param id
     *            ID of required member
     */
    public void addXIdRestriction(String xField, Integer id) {
        String otherName = varName(xField).replaceFirst("s$", "");
        String joker = otherName.concat("Id");
        extensions.add(varName + "." + xField + " AS " + otherName + " WITH " + otherName + ".id = :" + joker);
        parameters.put(joker, id);
    }

    /**
     * Requires that the search only finds objects where the user input either
     * matches the record number, or is part of the <i>title</i>. Title here
     * means the label. If the input is not a number, the first option is
     * omitted.
     * 
     * @param searchInput
     *            single line input by the user
     */
    public void forIdOrInTitle(String searchInput) {
        if (searchInput.startsWith("\"") && searchInput.endsWith("\"")) {
            searchInput = searchInput.substring(1, searchInput.length() - 1);
        }
        String searchInputAnywhere = '%' + searchInput + '%';
        try {
            Matcher idSearchInput = EXPLICIT_ID_SEARCH.matcher(searchInput);
            if (idSearchInput.matches()) {
                Integer expectedId = Integer.valueOf(idSearchInput.group(1));
                if (className.equals("Task")) {
                    restrictions.add(varName + ".process.id = :id");
                } else {
                    restrictions.add(varName + ".id = :id");
                }
                parameters.put("id", expectedId);
            } else {
                Integer possibleId = Integer.valueOf(searchInput);
                restrictions.add('(' + varName + ".id = :possibleId OR " + varName + ".title LIKE :searchInput)");
                parameters.put("possibleId", possibleId);
                parameters.put("searchInput", searchInputAnywhere);
            }
        } catch (NumberFormatException e) {
            restrictions.add(varName + ".title LIKE :searchInput");
            parameters.put("searchInput", searchInputAnywhere);
        }
    }

    /**
     * Performs index searches.
     * 
     * @param session
     *            session of hibernate
     */
    public void performIndexSearches(Session session) {
        SearchSession searchSession = Search.session(session);
        for (var iterator = indexQueries.entrySet().iterator(); iterator.hasNext();) {
            Entry<String, SearchFilter> entry = iterator.next();
            SearchFilter searchFilter = entry.getValue();
            List<Integer> ids = searchSession.search(beanClass).select(searchSession.scope(beanClass).projection()
                    .field("id", Integer.class).toProjection()).where(function -> function.match().field(searchFilter
                            .getWhere()).matching(searchFilter.getWhat())).fetchAll().hits();
            parameters.put(entry.getKey(), ids);
            iterator.remove();
        }
    }

    /**
     * Requires that the query only find objects owned by the specified client.
     * 
     * @param sessionClientId
     *            client record number
     */
    public void restrictToClient(int sessionClientId) {
        switch (className) {
            case "Docket":
            case "Project":
            case "Ruleset":
            case "Template":
            case "Workflow":
                restrictions.add(varName + ".client.id = :sessionClientId");
                break;
            case "Process":
                restrictions.add(varName + ".project.client.id = :sessionClientId");
                break;
            case "Task":
                restrictions.add(varName + ".process.project.client.id = :sessionClientId");
                break;
            default:
                throw new IllegalStateException("BeanQuery.restrictToClient() not yet implemented for "
                        .concat(className));
        }
        parameters.put("sessionClientId", sessionClientId);
    }

    /**
     * Requires that the search only finds processes that are not yet completed.
     */
    public void restrictToNotCompletedProcesses() {
        restrictions.add('(' + varName + ".sortHelperStatus IS NULL OR " + varName
                + ".sortHelperStatus != :completedState)");
        parameters.put("completedState", ProcessState.COMPLETED.getValue());
    }

    /**
     * Requires that the search only find items that belong to one of the
     * specified projects.
     * 
     * @param projectIDs
     *            record numbers of the projects to which the hits may belong
     */
    public void restrictToProjects(Collection<Integer> projectIDs) {
        switch (className) {
            case "Process":
                restrictions.add(varName + ".project.id IN (:projectIDs)");
                break;
            case "Task":
                restrictions.add(varName + ".process.project.id IN (:projectIDs)");
                break;
            default:
                throw new IllegalStateException("BeanQuery.restrictToProjects() not yet implemented for "
                        .concat(className));
        }
        parameters.put("projectIDs", projectIDs);
    }

    /**
     * Requires that the search only finds tasks that are allowed to be
     * processed by one of the specified roles.
     * 
     * <!-- As far as I can tell, there is no way to formulate an is-one-in
     * relationship in HQL. So this has to be expanded into an OR query. That's
     * possible, but if there's a nicer query solution, that would be nice too.
     * Cf. https://stackoverflow.com/a/14020432/1503237 -->
     * 
     * @param roles
     *            roles of the task
     */
    public void restrictToRoles(List<Role> roles) {
        int rolesSize = roles.size();
        boolean multipleRoles = rolesSize > 1;
        StringBuilder restriction = new StringBuilder();
        for (int i = 0; i < rolesSize; i++) {
            String roleVarName = "role" + (i + 1);
            if (multipleRoles) {
                boolean firstIteration = (i == 0);
                restriction.append(firstIteration ? "(" : " OR ");
            }
            restriction.append(':');
            restriction.append(roleVarName);
            restriction.append(" IN elements(");
            restriction.append(varName);
            restriction.append(".roles)");
            boolean lastIteration = (i == rolesSize - 1);
            if (multipleRoles && lastIteration) {
                restriction.append(')');
            }
            parameters.put(roleVarName, roles.get(i));
        }
        restrictions.add(restriction.toString());
    }

    /**
     * This sets a restriction to which the user defined filters.
     * 
     * @param userDefinedFilters
     *            which user-defined filters
     */
    public void restrictWithUserFilterString(String userDefinedFilters) {
        List<SearchFilter> searchFilters = parseFilterString(userDefinedFilters);
        for (int i = 0; i < searchFilters.size(); i++) {
            String parameterName = "userFilter".concat(Integer.toString(i + 1));
            SearchFilter searchFilter = searchFilters.get(i);
            if (searchFilter.isDatabase()) {
                addDatabaseFilter(searchFilter, parameterName);
            } else {
                restrictions.add(varName + ".id IN (:" + parameterName + ')');
                indexQueries.put(parameterName, searchFilter);
            }
        }
    }

    /**
     * Adds a database specific user filter.
     * 
     * @param searchFilter
     *            What to search for. Where you can search:<br>
     *            <ul>
     *            <li>"id": the process ID. Also for tasks, "id" searches for
     *            the process ID, not the task ID.</li>
     *            <li>"project": the label of the process</li>
     *            <li>"projekt": the label of the process. The German is
     *            supported for a historical reason.</li>
     *            <li>"step": the label of the step. For the process, this finds
     *            every process with this step, for the step all of the
     *            name.</li>
     *            <li>"stepdone": the label of the step, and the step must have
     *            been done.</li>
     *            <li>"stepinwork": the label of the step, and the step must
     *            currently be performed.</li>
     *            <li>"steplocked": the label of the step, and the step must be
     *            locked.</li>
     *            <li>"stepopen": the label of the step, and the step must be
     *            open.</li>
     *            <li>"vorlage": the label of the production template. The
     *            German is supported for a historical reason.</li>
     *            </ul>
     * @param parameterName
     *            name of the generic search field
     */
    private void addDatabaseFilter(SearchFilter searchFilter, String parameterName) {
        final String databaseFilter = searchFilter.getWhere();
        switch (databaseFilter) {
            case "id":
                switch (className) {
                    case "Process":
                        restrictions.add(varName + ".id = :" + parameterName);
                        break;
                    case "Task":
                        restrictions.add(varName + ".process.id = :" + parameterName);
                        break;
                    default:
                        throw new IllegalStateException("BeanQuery.addDatabaseFilter(id) not yet implemented for "
                                .concat(className));
                }
                break;

            case "project":
            case "projekt":
                switch (className) {
                    case "Process":
                        restrictions.add(varName + ".project.title = :" + parameterName);
                        break;
                    case "Task":
                        restrictions.add(varName + ".process.project.title = :" + parameterName);
                        break;
                    default:
                        throw new IllegalStateException("BeanQuery.addDatabaseFilter(\"" + databaseFilter
                                + "\") not yet implemented for ".concat(className));
                }
                break;

            case "step":
                extensions.add(varName + ".tasks AS task WITH task.title = :" + parameterName);
                break;

            case "steplocked":
            case "stepopen":
            case "stepinwork":
            case "stepdone":
                extensions.add(varName + ".tasks AS task WITH task.title = :" + parameterName
                        + " AND task.processingStatus = :processingStatus");
                parameters.put("processingStatus", TASK_STATUS_FOR_FILTER.get(databaseFilter));
                break;

            case "vorlage":
                switch (className) {
                    case "Process":
                        restrictions.add(varName + ".template.title = :" + parameterName);
                        break;
                    case "Task":
                        restrictions.add(varName + ".process.template.title = :" + parameterName);
                        break;
                    default:
                        throw new IllegalStateException("BeanQuery.addDatabaseFilter(\"" + databaseFilter
                                + "\") not yet implemented for ".concat(className));
                }
                break;
            default:
                restrictions.add(varName + '.' + databaseFilter + " = :" + parameterName);
                break;
        }
        parameters.put(parameterName, Objects.equals(databaseFilter, "id") ? Integer.valueOf(searchFilter.getWhat())
                : searchFilter.getWhat());
    }

    /**
     * The filter string entered by the user is decomposed.
     * 
     * @param filterString
     *            filter string
     * @return filter that the user wants
     */
    private static List<SearchFilter> parseFilterString(String filterString) {
        List<SearchFilter> searchFilters = new ArrayList<>();
        StringBuilder tokenCollector = new StringBuilder();
        boolean inQuote = false;
        for (int offset = 0; offset < filterString.length(); offset += charCount(filterString.codePointAt(offset))) {
            int codePoint = filterString.codePointAt(offset);
            if (codePoint == '"') {
                inQuote = !inQuote;
            } else if (!inQuote && codePoint <= ' ') {
                if (tokenCollector.length() > 0) {
                    searchFilters.addAll(convertToUserSpecifiedFilter(tokenCollector));
                    tokenCollector = new StringBuilder();
                }
            } else {
                tokenCollector.appendCodePoint(codePoint);
            }
        }
        if (tokenCollector.length() > 0) {
            searchFilters.addAll(convertToUserSpecifiedFilter(tokenCollector));
        }
        return searchFilters;
    }

    /**
     * Returns the search filters from the terminated substring buffer.
     * 
     * @param tokenSequence
     * @return
     */
    private static Collection<SearchFilter> convertToUserSpecifiedFilter(StringBuilder tokenSequence) {
        int colon = tokenSequence.indexOf(":");
        if (colon < 0) {
            return splitSearchFilters(SearchFilter.DEFAULT_SEARCH_FIELD, tokenSequence.toString());
        } else {
            String column = tokenSequence.substring(0, colon);
            String value = tokenSequence.substring(colon + 1);
            int anotherColon = value.indexOf(":");
            if (anotherColon < 0) {
                return Collections.singleton(new SearchFilter(column, value));
            } else {
                String metadataKey = value.substring(0, anotherColon);
                String metadataValue = value.substring(anotherColon + 1);
                return splitSearchFilters(metadataKey, metadataValue);
            }
        }
    }

    /**
     * Here the filters are split. If there is more than one word in the string,
     * individual filters are found for the words, since the index searches for
     * words.
     * 
     * @param metadataKey
     *            search field that is searched in the metadata
     * @param tokens
     *            input string leads to multiple index searches
     * @return the user-specified filters
     */
    private static Collection<SearchFilter> splitSearchFilters(String metadataKey, String tokens) {
        return Arrays.stream(tokens.split(" +")).map(metadataValue -> new SearchFilter(true, metadataKey,
                metadataValue))
                .collect(Collectors.toList());
    }

    /**
     * Sets the sort order for the search results.
     * 
     * @param sortField
     *            the database field to sort on
     * @param sortOrder
     *            the sorting direction
     */
    public void defineSorting(String sortField, SortOrder sortOrder) {
        sorting = Pair.of(varName + '.' + sortField, SortOrder.DESCENDING.equals(sortOrder) ? "DESC" : "ASC");
    }

    /**
     * Forms and returns a query to count all objects.
     * 
     * @return a query to count all objects
     */
    public String formCountQuery() {
        StringBuilder query = new StringBuilder(512);
        query.append("SELECT COUNT(*) ");
        innerFormQuery(query);
        return query.toString();
    }

    /**
     * Forms and returns a query for all objects.
     * 
     * @return a query for all objects
     */
    public String formQueryForAll() {
        StringBuilder query = new StringBuilder(512);
        if (!extensions.isEmpty()) {
            query.append("SELECT ").append(varName).append(' ');
        }
        innerFormQuery(query);
        query.append(" ORDER BY ").append(sorting.getKey()).append(' ').append(sorting.getValue());
        return query.toString();
    }

    /**
     * Forms and returns a query for a unique collection of strings.
     * 
     * @param field
     *            field of the bean being queried
     * @param sorted
     *            whether the list should be sorted
     */
    public String formQueryForDistinct(String field, boolean sorted) {
        StringBuilder query = new StringBuilder(512);
        query.append("SELECT DISTINCT ").append(varName).append('.').append(field).append(' ');
        innerFormQuery(query);
        query.append(" ORDER BY ").append(varName).append('.').append(field).append(" ASC");
        return query.toString();
    }

    private void innerFormQuery(StringBuilder query) {
        query.append("FROM ").append(className).append(" AS ").append(varName);
        for (String extension : extensions) {
            query.append(" INNER JOIN ").append(extension);
        }
        if (!restrictions.isEmpty()) {
            boolean first = true;
            for (String restriction : restrictions) {
                query.append(first ? " WHERE " : " AND ").append(restriction);
                first = false;
            }
        }
    }

    public Map<String, Object> getQueryParameters() {
        if (!indexQueries.isEmpty()) {
            throw new IllegalStateException("index searches not yet performed");
        }
        return parameters;
    }

    private String varName(String input) {
        StringBuilder result = new StringBuilder();
        CharacterIterator inputIterator = new StringCharacterIterator(input);
        boolean upperCase = false;
        while (inputIterator.current() != CharacterIterator.DONE) {
            char currentChar = inputIterator.current();
            if (currentChar < '0' || (currentChar > '9' && currentChar < 'A')
                    || (currentChar > 'Z' && currentChar < 'a') || currentChar > 'z') {
                upperCase = true;
            } else {
                result.append(upperCase ? Character.toUpperCase(currentChar) : Character.toLowerCase(currentChar));
                upperCase = false;
            }
            inputIterator.next();
        }
        return result.toString();
    }
}
