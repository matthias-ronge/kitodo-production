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

import java.util.List;
import java.util.Map;

public interface DataManagementInterface {

    /**
     * Lazily retrieves BaseBean objects from database by given query.
     *
     * @param <T>
     *            class type of the database object
     * @param beanClass
     *            class of the database object. Technically, this always has to
     *            be a child class of BaseBean.
     * @param query
     *            as String. Must start with {@code SELECT id FROM}, followed by
     *            the case-sensitive class name of the base bean.
     * @param parameters
     *            for query. May be {@code null} if the query doesn’t need
     *            parameters.
     * @return list of bean object IDs in result object
     * @throws DAOException
     *             if a HibernateException is thrown
     */
    abstract <T> LazyResult<T> find(Class<T> beanClass, String query, Map<String, Object> parameters)
            throws DAOException;

    /**
     * Retrieves a BaseBean identified by the given id from the database.
     *
     * @param <T>
     *            class type of the database object
     * @param beanClass
     *            class of the database object. Technically, this always has to
     *            be a child class of BaseBean.
     * @param id
     *            of bean to load
     * @return persisted bean
     * @throws DAOException
     *             if a HibernateException is thrown
     */
    abstract <T> T getById(Class<T> beanClass, int id) throws DAOException;

    /**
     * Refresh given bean object.
     *
     * @param <T>
     *            class type of the database object
     * @param baseBean
     *            bean to refresh
     */
    <T> void refresh(T baseBean);

    /**
     * Removes BaseBean object specified by the given id from the database.
     *
     * @param <T>
     *            class type of the database object
     * @param beanClass
     *            class of the database object. Technically, this always has to
     *            be a child class of BaseBean.
     * @param id
     *            of bean to delete
     * @throws DAOException
     *             if the current session can't be retrieved or an exception is
     *             thrown while performing the rollback
     */
    abstract <T> void remove(Class<T> beanClass, int id) throws DAOException;

    /**
     * Saves a BaseBean object to the database.
     * 
     * @param <T>
     *            class type of the database object
     * @param baseBean
     *            object to persist
     * @throws DAOException
     *             if the current session can't be retrieved or an exception is
     *             thrown while performing the rollback
     */
    <T> void save(T baseBean) throws DAOException;
}
