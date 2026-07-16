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

package org.kitodo.dataformat.iiif;

import java.net.URI;
import java.util.List;

/**
 * An organization or person that contributed to the resource. This allows to
 * have richer information about the people and organizations. Clients can
 * display this information to the user to acknowledge the provider’s
 * contributions.
 *
 * <P>
 * This class implements {@code InternalLink} because it can internally be
 * referenced.
 */
public class Agent implements InternalLink {

    private URI id;
    private String type = "Agent";
    private MultiText label;
    private List<ContentResource> homepage;
    private List<ContentResource> logo;
    private List<ContentResource> seeAlso;

    /**
     * &#x1F511; Returns the identifier. A URI that identifies the agent.
     * 
     * @return the identifier
     */
    public URI getId() {
        return id;
    }

    /**
     * &#x1F511; Sets the identifier. Agents must have an id.
     * 
     * @param id identifier to set. Must not be {@code null}.
     */
    public void setId(URI id) {
        assert id != null : "id must not be null";

        this.id = id;
    }

    /**
     * &#x1F511; Returns the type property.
     * 
     * @return the type property
     */
    public String getType() {
        return type;
    }

    /**
     * &#x1F511; Returns the name.
     * 
     * @return the name
     */
    public MultiText getLabel() {
        return label;
    }

    /**
     * &#x1F511; Sets the name. Agents must have a label.
     * 
     * @param label name to set. Must not be {@code null}.
     */
    public void setLabel(MultiText label) {
        assert label != null : "label must not be null";

        this.label = label;
    }

    /**
     * &#x1F511; Returns the homepage references.
     * 
     * @return the homepage references
     */
    public List<ContentResource> getHomepage() {
        return homepage;
    }

    /**
     * &#x1F511; Sets the homepage references. Agents is recommended to have a
     * homepage.
     * 
     * @param homepage homepage references to set. Must not be {@code null}.
     */
    public void setHomepage(List<ContentResource> homepage) {
        assert homepage != null : "homepage must not be null";

        this.homepage = homepage;
    }

    /**
     * &#x1F511; Returns the agent logos.
     * 
     * @return the agent logos
     */
    public List<ContentResource> getLogo() {
        return logo;
    }

    /**
     * &#x1F511; Sets the agent logos. Agents is recommended to have a logo.
     * 
     * @param logo agent logos to set. Must not be {@code null}.
     */
    public void setLogo(List<ContentResource> logo) {
        assert logo != null : "logo must not be null";

        this.logo = logo;
    }

    /**
     * Returns the further information.
     * 
     * @return the further information
     */
    public List<ContentResource> getSeeAlso() {
        return seeAlso;
    }

    /**
     * Sets the further information.
     * 
     * @param seeAlso further information to set. Must not be {@code null}.
     */
    public void setSeeAlso(List<ContentResource> seeAlso) {
        assert seeAlso != null : "seeAlso must not be null";

        this.seeAlso = seeAlso;
    }
}
