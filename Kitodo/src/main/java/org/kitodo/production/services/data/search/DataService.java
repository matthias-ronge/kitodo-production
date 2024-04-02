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

import java.util.List;

import org.kitodo.api.data.*;
import org.kitodo.data.database.beans.BaseBean;
import org.kitodo.serviceloader.KitodoServiceLoader;

/**
 * Provides database-driven persistence functionality for the business objects.
 * The business objects do not need to be stored in files. This increases the
 * consistency of business objects having diverse relationships with each other.
 */
public class DataService {

    private final DataManagementInterface dataManagementModule;

    public DataService() {
        dataManagementModule = new KitodoServiceLoader<DataManagementInterface>(DataManagementInterface.class)
                .loadModule();
    }

    /**
     * Count all rows in database.
     *
     * @param <T>
     *            class type of the database object
     * @param beanClass
     *            class of the database object. Technically, this always has to
     *            be a child class of BaseBean.
     * @return amount of rows in database according to given query
     */
    public <T extends BaseBean> int count(Class<T> beanClass) throws DAOException {
        return find(new BeanQuery<>(beanClass)).size();
    }

    /**
     * Count all rows in database.
     *
     * @param <T>
     *            class type of the database object
     * @param query
     *            for counting objects
     * @return amount of rows in database according to given query
     */
    public <T extends BaseBean> int count(BeanQuery<T> query) throws DAOException {
        return find(query).size();
    }

    /**
     * Lazily retrieves BaseBean objects from database by given query.
     *
     * @param <T>
     *            class type of the database object
     * @param query
     *            as BeanQuery
     * @return list of bean object IDs in result object
     * @throws DAOException
     *             if a HibernateException is thrown
     */
    public <T extends BaseBean> LazyResult<T> find(BeanQuery<T> query) throws DAOException {
        return dataManagementModule.find(query.getBeanClass(), query.formIdQuery(), query.getQueryParameters());
    }

    /**
     * Retrieves all BaseBean objects from the database.
     *
     * @param <T>
     *            class type of the database object
     * @param beanClass
     *            class of the database object. Technically, this always has to
     *            be a child class of BaseBean.
     * @return all persisted beans
     */
    public <T extends BaseBean> List<T> getAll(Class<T> beanClass) throws DAOException {
        return find(new BeanQuery<>(beanClass)).getAll();
    }

    /**
     * Retrieves all BaseBean objects in given range.
     *
     * @param <T>
     *            class type of the database object
     * @param beanClass
     *            class of the database object. Technically, this always has to
     *            be a child class of BaseBean.
     * @param first
     *            result
     * @param max
     *            amount of results
     * @return constrained list of persisted beans
     */
    public <T extends BaseBean> List<T> getAll(Class<T> beanClass, int first, int max) throws DAOException {
        return find(new BeanQuery<>(beanClass)).get(first, max);
    }

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
    public <T extends BaseBean> T getById(Class<T> beanClass, int id) throws DAOException {
        return dataManagementModule.getById(beanClass, id);
    }

    /**
     * Retrieves BaseBean objects from database by given query.
     *
     * @param <T>
     *            class type of the database object
     * @param beanClass
     *            class of the database object. Technically, this always has to
     *            be a child class of BaseBean.
     * @param query
     *            as String. Must start with {@code FROM} keyword, followed by
     *            the class name of the base bean.
     * @return list of beans objects
     * @throws DAOException
     *             if a HibernateException is thrown
     */
    public <T extends BaseBean> List<T> getByQuery(BeanQuery<T> query) throws DAOException {
        return find(query).getAll();
    }

    /**
     * Retrieves BaseBean objects from database by given query.
     *
     * @param <T>
     *            class type of the database object
     * @param beanClass
     *            class of the database object. Technically, this always has to
     *            be a child class of BaseBean.
     * @param query
     *            as String. Must start with {@code FROM} keyword, followed by
     *            the class name of the base bean.
     * @param parameters
     *            for query
     * @param first
     *            result
     * @param max
     *            amount of results
     * @return list of beans objects
     * @throws DAOException
     *             if a HibernateException is thrown
     */
    public <T extends BaseBean> List<T> getByQuery(BeanQuery<T> query, int first, int max) throws DAOException {
        return find(query).get(first, max);
    }

    /**
     * Refresh given bean object.
     *
     * @param <T>
     *            class type of the database object
     * @param bean
     *            bean to refresh
     */
    public <T extends BaseBean> void refresh(T bean) {
        dataManagementModule.refresh(bean);
    }

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
    public <T extends BaseBean> void remove(Class<T> beanClass, int id) throws DAOException {
        dataManagementModule.remove(beanClass, id);
    }

    /**
     * Removes BaseBean object specified by the given id from the database.
     *
     * @param <T>
     *            class type of the database object
     * @param bean
     *            to delete
     * @throws DAOException
     *             if the current session can't be retrieved or an exception is
     *             thrown while performing the rollback
     */
    public <T extends BaseBean> void remove(T bean) throws DAOException {
        dataManagementModule.remove(bean.getClass(), bean.getId());
    }

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
    public <T extends BaseBean> void save(T baseBean) throws DAOException {
        dataManagementModule.save(baseBean);
    }

}
