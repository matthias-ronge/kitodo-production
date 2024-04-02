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

package org.kitodo.data.database;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.kitodo.api.data.DAOException;
import org.kitodo.api.data.DataManagementInterface;
import org.kitodo.api.data.LazyResult;
import org.kitodo.data.database.persistence.HibernateUtil;

public class DataManagement implements DataManagementInterface {

    @Override
    public <T> LazyResult<T> find(Class<T> beanClass, String query, Map<String, Object> parameters) throws DAOException {
        try (Session session = HibernateUtil.getSession()) {
            Query<?> questioner = session.createQuery(query);
            addParameters(questioner, parameters);
            List<?> answer = questioner.list();
            return new LazyResult<T>(this, beanClass, answer.stream().map(Integer.class::cast).collect(Collectors.toList()));
        } catch (PersistenceException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public <T> T getById(Class<T> beanClass, Integer id) throws DAOException {
        try (Session session = HibernateUtil.getSession()) {
            T bean = session.get(beanClass, id);
            if (bean == null) {
                throw new DAOException("Object cannot be found in database");
            }
            return bean;
        } catch (PersistenceException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public <T> void refresh(T bean) {
        try (Session session = HibernateUtil.getSession()) {
            session.refresh(bean);
        }
    }

    @Override
    public <T> void remove(Class<T> beanClass, int id) throws DAOException {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            synchronized (DataManagement.class) {
                Object object = session.load(beanClass, id);
                session.delete(object);
                session.flush();
                transaction.commit();
            }
        } catch (PersistenceException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public <T> void save(T baseBean) throws DAOException {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            session.saveOrUpdate(baseBean);
            session.flush();
            transaction.commit();
        } catch (PersistenceException e) {
            throw new DAOException(e);
        }
    }

    private void addParameters(Query<?> query, Map<String, Object> parameters) {
        if (Objects.nonNull(parameters)) {
            for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
                if (parameter.getValue() instanceof List) {
                    query.setParameterList(parameter.getKey(), (List<?>) parameter.getValue());
                } else {
                    query.setParameter(parameter.getKey(), parameter.getValue());
                }
            }
        }
    }
}
