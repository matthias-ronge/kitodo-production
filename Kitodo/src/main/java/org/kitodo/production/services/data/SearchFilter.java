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

/**
 * A single filter from filters entered by the user.
 */
public class SearchFilter {
    public static final String DEFAULT_SEARCH_FIELD = "search";
    private String where = DEFAULT_SEARCH_FIELD;
    private String what;
    private boolean database;

    /**
     * Search for a single term in the index.
     * 
     * @param what
     *            search term
     */
    public SearchFilter(String what) {
        this.what = toBaseCharacters(what);
        this.database = false;
    }

    /**
     * Search for a single value in a database column.
     * 
     * @param where
     *            database column
     * @param what
     *            search term
     */
    public SearchFilter(String where, String what) {
        this.where = toBaseCharacters(where);
        this.what = what;
        this.database = true;
    }

    /**
     * Search for a single term.
     * 
     * @param metadata
     *            if yes, the search is carried out in the metadata (in the
     *            index), otherwise in the database
     * @param where
     *            where to search
     * @param what
     *            what is being searched for
     */
    public SearchFilter(boolean metadata, String where, String what) {
        this.where = toBaseCharacters(where);
        this.what = metadata ? toBaseCharacters(what) : what;
        this.database = !metadata;
    }

    /**
     * This simple filtering mechanism filters strings to their base character
     * strings and reduces them.
     * 
     * @param input
     *            input string
     * @return filtered string
     */
    static final String toBaseCharacters(String input) {
        return input.toLowerCase().replaceAll("[^0-9a-z]", "");
    }

    /**
     * Returns the search field.
     * 
     * @return the search field
     */
    public String getWhere() {
        return where;
    }

    /**
     * Returns the search field.
     * 
     * @return the search field
     */
    public String getWhat() {
        return what;
    }

    /**
     * Returns whether the search is in the database.
     * 
     * @return whether the search is in the database
     */
    public boolean isDatabase() {
        return database;
    }

    @Override
    public String toString() {
        return (database ? "$" : "&")
                + (DEFAULT_SEARCH_FIELD.equals(where) ? "" : where + (database ? ":" : "="))
                + (what.isEmpty() || what.indexOf(' ') > -1 ? '"' + what + '"' : what);
    }
}
