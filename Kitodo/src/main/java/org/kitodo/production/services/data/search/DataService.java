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

import org.kitodo.api.data.*;
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
}
