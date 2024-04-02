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

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Delivers a referenced object set from the database without loading the
 * objects immediately. The list can be viewed piece by piece.
 * 
 * @param <T>
 *            class type of the database object
 */
public class LazyResult<T> implements Iterable<T> {

    private DataManagementInterface accessor;
    private Class<T> beanClass;
    private List<Integer> data;

    public LazyResult(DataManagementInterface accessor, Class<T> beanClass, List<Integer> data) {
        this.accessor = accessor;
        this.beanClass = beanClass;
        this.data = data;
    }

    public List<T> get(int offset, int limit) throws DAOException {
        ArrayList<T> beans = new ArrayList<T>(limit);
        for (int listIndex = offset; listIndex < Math.min(offset + limit, data.size()); listIndex++) {
            beans.add(accessor.getById(beanClass, data.get(listIndex)));
        }
        return beans;
    }

    public List<T> getAll() throws DAOException {
        ArrayList<T> beans = new ArrayList<T>();
        for (int listIndex = 0; listIndex < data.size(); listIndex++) {
            beans.add(accessor.getById(beanClass, data.get(listIndex)));
        }
        return beans;
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> lazyResultIterator = new Iterator<T>() {
            private int iteratorIndex = 0;

            @Override
            public boolean hasNext() {
                return iteratorIndex < data.size();
            }

            @Override
            public T next() {
                try {
                    return accessor.getById(beanClass, data.get(iteratorIndex++));
                } catch (DAOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
        return lazyResultIterator;
    }

    public int size() {
        return data.size();
    }
}
