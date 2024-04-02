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

package org.kitodo.api.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface DataManagementInterface {

    /**
     * Retrieves a BaseBean identified by the given id from the database.
     * @param <T>
     *
     * @param id
     *            of bean to load
     * @return persisted bean
     * @throws DAOException
     *             if a HibernateException is thrown
     */
    public abstract <T> T getById(Integer id) throws DAOException;

    /**
     * Retrieves all BaseBean objects from the database.
     *
     * @return all persisted beans
     */
    public abstract List<T> getAll() throws DAOException;

    /**
     * Retrieves all BaseBean objects in given range.
     *
     * @param offset
     *            result
     * @param size
     *            amount of results
     * @return constrained list of persisted beans
     */
    public abstract List<T> getAll(int offset, int size) throws DAOException;

    /**
     * Retrieves all not indexed BaseBean objects in given range.
     *
     * @param offset
     *            result
     * @param size
     *            amount of results
     * @return constrained list of persisted beans
     */
    public abstract List<T> getAllNotIndexed(int offset, int size) throws DAOException;

    /**
     * Saves a BaseBean object to the database.
     *
     * @param baseBean
     *            object to persist
     * @throws DAOException
     *             if the current session can't be retrieved or an exception is
     *             thrown while performing the rollback
     */
    public void save(T baseBean) throws DAOException {
        storeObject(baseBean);
    }

    /**
     * Saves base bean objects as indexed.
     *
     * @param baseBeans
     *            list of base beans
     * @throws DAOException
     *             if the current session can't be retrieved or an exception is
     *             thrown while performing the rollback
     */
    public void saveAsIndexed(List<T> baseBeans) throws DAOException {
        storeAsIndexed(baseBeans);
    }

    /**
     * Removes BaseBean object specified by the given id from the database.
     *
     * @param id
     *            of bean to delete
     * @throws DAOException
     *             if the current session can't be retrieved or an exception is
     *             thrown while performing the rollback
     */
    public abstract void remove(Integer id) throws DAOException;

    /**
     * Removes given BaseBean object from the database.
     *
     * @param baseBean
     *            bean to delete
     * @throws DAOException
     *             if the current session can't be retrieved or an exception is
     *             thrown while performing the rollback
     */
    public void remove(T baseBean) throws DAOException {
        if (baseBean.getId() != null) {
            try (Session session = HibernateUtil.getSession()) {
                Transaction transaction = session.beginTransaction();
                synchronized (lockObject) {
                    Object merged = session.merge(baseBean);
                    session.delete(merged);
                    session.flush();
                    transaction.commit();
                }
            } catch (PersistenceException e) {
                throw new DAOException(e);
            }
        }
    }

    /**
     * Refresh given bean object.
     *
     * @param baseBean
     *            bean to refresh
     */
    public void refresh(T baseBean) {
        refreshObject(baseBean);
    }

    /**
     * Evict given bean object.
     *
     * @param baseBean
     *            bean to evict
     */
    public void evict(T baseBean) {
        evictObject(baseBean);
    }

    /**
     * Retrieves BaseBean objects from database by given query.
     *
     * @param query
     *            as String
     * @param parameters
     *            for query
     * @param first
     *            result
     * @param max
     *            amount of results
     * @return list of beans objects
     */
    @SuppressWarnings("unchecked")
    public List<T> getByQuery(String query, Map<String, Object> parameters, int first, int max) {
        try (Session session = HibernateUtil.getSession()) {
            Query<T> q = session.createQuery(query);
            q.setFirstResult(first);
            q.setMaxResults(max);
            addParameters(q, parameters);
            return q.list();
        } catch (SQLGrammarException e) {
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves BaseBean objects from database by given query.
     *
     * @param query
     *            as String
     * @param parameters
     *            for query
     * @return list of beans objects
     */
    @SuppressWarnings("unchecked")
    public List<T> getByQuery(String query, Map<String, Object> parameters) {
        try (Session session = HibernateUtil.getSession()) {
            Query<T> q = session.createQuery(query);
            addParameters(q, parameters);
            return q.list();
        }
    }

    /**
     * Retrieves BaseBean objects from database by given query.
     *
     * @param query
     *            as String
     * @return list of beans objects
     */
    @SuppressWarnings("unchecked")
    public List<T> getByQuery(String query) {
        try (Session session = HibernateUtil.getSession()) {
            List<T> baseBeanObjects = session.createQuery(query).list();
            if (Objects.isNull(baseBeanObjects)) {
                baseBeanObjects = new ArrayList<>();
            }
            return baseBeanObjects;
        }
    }

    /**
     * Count all rows in database.
     *
     * @param query
     *            for counting objects
     * @param parameters
     *            for query
     * @return amount of rows in database according to given query
     */
    public Long count(String query, Map<String, Object> parameters) throws DAOException {
        try (Session session = HibernateUtil.getSession()) {
            Query<?> q = session.createQuery(query);
            addParameters(q, parameters);
            return (Long) q.uniqueResult();
        } catch (PersistenceException e) {
            throw new DAOException(e);
        }
    }

    /**
     * Count all rows in database.
     *
     * @param query
     *            for counting objects
     * @return amount of rows in database according to given query
     */
    public Long count(String query) throws DAOException {
        try (Session session = HibernateUtil.getSession()) {
            return (Long) session.createQuery(query).uniqueResult();
        } catch (PersistenceException e) {
            throw new DAOException(e);
        }
    }

    
}
