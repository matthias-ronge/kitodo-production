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

package org.kitodo.data.database.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.Hibernate;
import org.kitodo.data.database.persistence.BaseDAO;
import org.kitodo.data.database.beans.BaseBean;

/**
 * Base bean class.
 */
@MappedSuperclass
public abstract class BaseBean implements Serializable {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    /**
     * Returns the record number of the object in the database. Can be
     * {@code null} if the object has not yet been persisted.
     *
     * @return the record number
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the data record number of the object. This should only happen when
     * data from a third-party source is integrated during operation, or in
     * tests. Normally the data record number is assigned by the database when
     * the object is saved.
     *
     * @param id
     *            data record number to use
     */
    public void setId(Integer id) {
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    void initialize(BaseDAO baseDAO, List<? extends BaseBean> list) {
        if (Objects.nonNull(this.id) && !Hibernate.isInitialized(list)) {
            baseDAO.initialize(this, list);
        }
    }
}
