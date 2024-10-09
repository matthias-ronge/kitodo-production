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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitodo.api.dataeditor.rulesetmanagement.FunctionalDivision;
import org.kitodo.api.dataeditor.rulesetmanagement.RulesetManagementInterface;
import org.kitodo.config.ConfigCore;
import org.kitodo.config.enums.ParameterCore;
import org.kitodo.data.database.beans.Ruleset;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.data.database.persistence.RulesetDAO;
import org.kitodo.data.exceptions.DataException;
import org.kitodo.exceptions.RulesetNotFoundException;
import org.kitodo.production.helper.Helper;
import org.kitodo.production.helper.metadata.legacytypeimplementations.LegacyPrefsHelper;
import org.kitodo.production.services.ServiceManager;
import org.kitodo.production.services.data.lookup.BeanQuery;
import org.primefaces.model.SortOrder;

public class RulesetService extends BaseBeanService<Ruleset, RulesetDAO> {

    private static final Map<String, String> SORT_FIELD_MAPPING;

    static {
        SORT_FIELD_MAPPING = new HashMap<>();
        SORT_FIELD_MAPPING.put("title.keyword", "title");
        SORT_FIELD_MAPPING.put("file.keyword", "file");
        SORT_FIELD_MAPPING.put("orderMetadataByRuleset", "orderMetadataByRuleset");
    }

    private static final Logger logger = LogManager.getLogger(RulesetService.class);
    private static volatile RulesetService instance = null;

    /**
     * Constructor with Searcher and Indexer assigning.
     */
    private RulesetService() {
        super(new RulesetDAO());
    }

    /**
     * Return singleton variable of type RulesetService.
     *
     * @return unique instance of RulesetService
     */
    public static RulesetService getInstance() {
        RulesetService localReference = instance;
        if (Objects.isNull(localReference)) {
            synchronized (RulesetService.class) {
                localReference = instance;
                if (Objects.isNull(localReference)) {
                    localReference = new RulesetService();
                    instance = localReference;
                }
            }
        }
        return localReference;
    }

    @Override
    public Long count() throws DAOException {
        return count("SELECT COUNT(*) FROM Ruleset");
    }

    @Override
    public Long countResults(Map<?, String> filtersNotImplemented) throws DAOException {
        BeanQuery beanQuery = new BeanQuery(Ruleset.class);
        beanQuery.restrictToClient(ServiceManager.getUserService().getSessionClientId());
        return count(beanQuery.formCountQuery(), beanQuery.getQueryParameters());
    }

    @Override
    public List<Ruleset> loadData(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<?, String> filtersNotImplemented) throws DAOException {
        BeanQuery beanQuery = new BeanQuery(Ruleset.class);
        beanQuery.restrictToClient(ServiceManager.getUserService().getSessionClientId());
        beanQuery.defineSorting(SORT_FIELD_MAPPING.getOrDefault(sortField, sortField), sortOrder);
        return getByQuery(beanQuery.formQueryForAll(), beanQuery.getQueryParameters(), first, pageSize);
    }

    /**
     * Returns all business domain models of the client, for which the logged in
     * user is currently working.
     * 
     * <p>
     * <b>Implementation Requirements:</b><br>
     * The function requires that the thread is assigned to a logged-in user.
     * 
     * @return all dockets for the selected client
     */
    public List<Ruleset> getAllForSelectedClient() {
        return dao.getByQuery("SELECT r FROM Ruleset AS r INNER JOIN r.client AS c WITH c.id = :clientId",
            Collections.singletonMap("clientId", ServiceManager.getUserService().getSessionClientId()));
    }

    /**
     * Returns all business domain models with the specified label. This can be
     * used to check whether a label is still available.
     * 
     * <p>
     * <b>Implementation Note:</b><br>
     * There is currently no filtering by client, so a label used by one client
     * cannot be used by another client.
     * 
     * @param title
     *            name to search for
     * @return list of dockets
     */
    public List<Ruleset> getByTitle(String title) {
        return dao.getByQuery("FROM Ruleset WHERE title = :title", Collections.singletonMap("title", title));
    }

    /**
     * Find ruleset with exact file.
     *
     * @param file
     *            of the searched ruleset
     * @return search result
     */
    public Map<String, Object> findByFile(String file) throws DataException {
        // TODO delete method stub
        throw new UnsupportedOperationException("no longer used function");
    }

    /**
     * Find rulesets for client id.
     *
     * @param clientId
     *            of the searched rulesets
     * @return search result
     */
    List<Map<String, Object>> findByClientId(Integer clientId) throws DataException {
        // TODO delete method stub
        throw new UnsupportedOperationException("no longer used function");
    }

    /**
     * Find ruleset with exact title and file name.
     *
     * @param title
     *            of the searched ruleset
     * @param file
     *            of the searched ruleset
     * @return search result
     */
    public Map<String, Object> findByTitleAndFile(String title, String file) throws DataException {
        // TODO delete method stub
        throw new UnsupportedOperationException("no longer used function");
    }

    /**
     * Find ruleset with exact title or file name.
     *
     * @param title
     *            of the searched ruleset
     * @param file
     *            of the searched ruleset
     * @return search result
     */
    public List<Map<String, Object>> findByTitleOrFile(String title, String file) throws DataException {
        // TODO delete method stub
        throw new UnsupportedOperationException("no longer used function");
    }

    /**
     * Get preferences.
     *
     * @param ruleset
     *            object
     * @return preferences
     */
    public LegacyPrefsHelper getPreferences(Ruleset ruleset) {
        LegacyPrefsHelper myPreferences = new LegacyPrefsHelper();
        try {
            myPreferences.loadPrefs(ConfigCore.getParameter(ParameterCore.DIR_RULESETS) + ruleset.getFile());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return myPreferences;
    }

    /**
     * Acquires a ruleset Management and loads a ruleset into it.
     *
     * @param ruleset
     *            database object that references the ruleset
     * @return a Ruleset Management in which the ruleset has been loaded
     */
    public RulesetManagementInterface openRuleset(Ruleset ruleset) throws IOException {
        final long begin = System.nanoTime();
        RulesetManagementInterface rulesetManagement = ServiceManager.getRulesetManagementService()
                .getRulesetManagement();
        String fileName = ruleset.getFile();
        try {
            rulesetManagement.load(Paths.get(ConfigCore.getParameter(ParameterCore.DIR_RULESETS), fileName).toFile());
        } catch (FileNotFoundException | IllegalArgumentException e) {
            throw new RulesetNotFoundException(fileName);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Reading ruleset took {} ms", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin));
        }
        return rulesetManagement;
    }

    /**
     * Returns the names of those divisions that fulfill a given function.
     * 
     * @param rulesetId
     *            ruleset database number
     * @param function
     *            function that the divisions are supposed to fulfill
     * @return collection of identifiers for divisions that fulfill this
     *         function
     */
    public Collection<String> getFunctionalDivisions(Integer rulesetId, FunctionalDivision function) {
        try {
            Ruleset ruleset = ServiceManager.getRulesetService().getById(rulesetId);
            RulesetManagementInterface rulesetManagement;
            rulesetManagement = ServiceManager.getRulesetService().openRuleset(ruleset);
            return rulesetManagement.getFunctionalDivisions(function);
        } catch (DAOException | IOException e) {
            Helper.setErrorMessage(e.getLocalizedMessage(), logger, e);
            return Collections.emptySet();
        }
    }
}
