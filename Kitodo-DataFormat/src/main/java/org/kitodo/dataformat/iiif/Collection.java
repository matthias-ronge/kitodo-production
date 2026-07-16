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
import java.time.Instant;
import java.util.List;

/**
 * A list of manifests available for viewing. Collections allow clients to
 * visualize or provide navigation through lists or hierarchies of related
 * manifests.
 *
 * <P>
 * Manifests may be referenced from more than one Collection. Collections with
 * an empty items property are discouraged.
 *
 * <P>
 * This class implements {@code CollectionOrManifest} because it must be mixable
 * with manifests in the {@code items} list of {@code Collection}s. This class
 * implements {@code InternalLink} because it can internally be referenced.
 */
public class Collection implements CollectionOrManifest, InternalLink {

    private MultiText label;
    private List<MultiField> metadata;
    private MultiText summary;
    private MultiField requiredStatement;
    private URI rights;
    private Instant navDate;
    private List<Agent> provider;
    private List<ContentResource> thumbnail;
    private Canvas placeholderCanvas;
    private Canvas accompanyingCanvas;
    private URI id;
    private String type = "Collection";
    private ViewingDirection viewingDirection;
    private Behavior behavior;
    private List<ContentResource> seeAlso;
    private List<ContentResource> service;
    private List<ContentResource> homepage;
    private List<ContentResource> rendering;
    private InternalLink partOf;
    private List<ContentResource> services;
    private List<CollectionOrManifest> items;
    private List<AnnotationPage> annotations;

    /**
     * &#x1F511; Returns the name. A human readable label, name or title.
     * 
     * @return the name
     */
    public MultiText getLabel() {
        return label;
    }

    /**
     * &#x1F511; Sets the name. A collection must have a label.
     * 
     * @param label name to set. Must not be {@code null}.
     */
    public void setLabel(MultiText label) {
        assert label != null : "label must not be null";

        this.label = label;
    }

    /**
     * &#x1F511; Returns the metadata. An ordered list of descriptions.
     * 
     * @return the metadata
     */
    public List<MultiField> getMetadata() {
        return metadata;
    }

    /**
     * &#x1F511; Sets the metadata. A collections is recommended to have metadata.
     * 
     * @param metadata metadata to set. Must not be {@code null}.
     */
    public void setMetadata(List<MultiField> metadata) {
        assert metadata != null : "metadata must not be null";

        this.metadata = metadata;
    }

    /**
     * &#x1F511; Returns the summary. A short textual summary to be conveyed to the
     * user when the metadata is not displayed.
     * 
     * @return the summary
     */
    public MultiText getSummary() {
        return summary;
    }

    /**
     * &#x1F511; Sets the summary. A collection is recommended to have a summary.
     * 
     * @param summary summary to set. Must not be {@code null}.
     */
    public void setSummary(MultiText summary) {
        assert summary != null : "summary must not be null";

        this.summary = summary;
    }

    /**
     * Returns the required statement. Text that must be displayed when the resource
     * is displayed.
     * 
     * @return the required statement
     */
    public MultiField getRequiredStatement() {
        return requiredStatement;
    }

    /**
     * Sets the required statement.
     * 
     * @param requiredStatement required statement to set. Must not be {@code
     * null}                 .
     */
    public void setRequiredStatement(MultiField requiredStatement) {
        assert requiredStatement != null : "requiredStatement must not be null";

        this.requiredStatement = requiredStatement;
    }

    /**
     * Returns the rights. A license or rights statement URI from Creative Commons
     * or RightsStatements.org.
     * 
     * @return the rights
     */
    public URI getRights() {
        return rights;
    }

    /**
     * Sets the rights. Must be a URI defined by a Creative Commons or
     * RightsStatements.org specification.
     * 
     * @param rights rights to set. Must not be {@code null}.
     */
    public void setRights(URI rights) {
        assert rights != null : "rights must not be null";

        this.rights = rights;
    }

    /**
     * Returns the nav date. A date that clients may use for navigation purposes.
     * 
     * @return the nav date
     */
    public Instant getNavDate() {
        return navDate;
    }

    /**
     * Sets the nav date.
     * 
     * @param navDate nav date to set. Must not be {@code null}.
     */
    public void setNavDate(Instant navDate) {
        assert navDate != null : "navDate must not be null";

        this.navDate = navDate;
    }

    /**
     * &#x1F511; Returns the provider. An organization or person that contributed to
     * providing the content of the resource.
     * 
     * @return the provider
     */
    public List<Agent> getProvider() {
        return provider;
    }

    /**
     * &#x1F511; Sets the provider. A collection is recommended to have a provider.
     * 
     * @param provider provider to set. Must not be {@code null}.
     */
    public void setProvider(List<Agent> provider) {
        assert provider != null : "provider must not be null";

        this.provider = provider;
    }

    /**
     * &#x1F511; Returns the thumbnail. A small image or short audio clip that
     * represents the resource.
     * 
     * @return the thumbnail
     */
    public List<ContentResource> getThumbnail() {
        return thumbnail;
    }

    /**
     * &#x1F511; Sets the thumbnail. A collection is recommended to have a
     * thumbnail.
     * 
     * @param thumbnail thumbnail to set. Must not be {@code null}.
     */
    public void setThumbnail(List<ContentResource> thumbnail) {
        assert thumbnail != null : "thumbnail must not be null";

        this.thumbnail = thumbnail;
    }

    /**
     * Returns the placeholder canvas. A single canvas that provides additional
     * content for use before the main content of the resource that has the
     * placeholderCanvas property is rendered, or as an advertisement or stand-in
     * for that content.
     * 
     * @return the placeholder canvas
     */
    public Canvas getPlaceholderCanvas() {
        return placeholderCanvas;
    }

    /**
     * Sets the placeholder canvas.
     * 
     * @param placeholderCanvas placeholder canvas to set. Must not be {@code
     * null}                 .
     */
    public void setPlaceholderCanvas(Canvas placeholderCanvas) {
        assert placeholderCanvas != null : "placeholderCanvas must not be null";

        this.placeholderCanvas = placeholderCanvas;
    }

    /**
     * Returns the accompanying canvas. A single canvas that provides additional
     * content for use while rendering the resource, for example an image to show
     * while a duration-only canvas is playing audio.
     * 
     * @return the accompanying canvas
     */
    public Canvas getAccompanyingCanvas() {
        return accompanyingCanvas;
    }

    /**
     * Sets the accompanying canvas.
     * 
     * @param accompanyingCanvas accompanying canvas to set. Must not be
     *                           {@code null}.
     */
    public void setAccompanyingCanvas(Canvas accompanyingCanvas) {
        assert accompanyingCanvas != null : "accompanyingCanvas must not be null";

        this.accompanyingCanvas = accompanyingCanvas;
    }

    /**
     * &#x1F511; Returns the identifier. A URI that identifies the ###.
     * 
     * @return the identifier
     */
    public URI getId() {
        return id;
    }

    /**
     * &#x1F511; Sets the identifier. A collection must have an id.
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
     * Returns the viewing direction. The direction in which a set of canvases
     * should be displayed to the user.
     * 
     * @return the viewing direction
     */
    public ViewingDirection getViewingDirection() {
        return viewingDirection;
    }

    /**
     * Sets the viewing direction.
     * 
     * @param viewingDirection viewing direction to set. Must not be {@code
     * null}                .
     */
    public void setViewingDirection(ViewingDirection viewingDirection) {
        assert viewingDirection != null : "viewingDirection must not be null";

        this.viewingDirection = viewingDirection;
    }

    /**
     * Returns the behavior. A set of user experience features that the publisher of
     * the content would prefer the client to use when presenting the resource.
     * Collections inherit behaviors from their referencing Collection.
     * 
     * @return the behavior
     */
    public Behavior getBehavior() {
        return behavior;
    }

    /**
     * Sets the behavior.
     * 
     * @param behavior behavior to set. Must not be {@code null}.
     */
    public void setBehavior(Behavior behavior) {
        assert behavior != null : "behavior must not be null";

        this.behavior = behavior;
    }

    /**
     * Returns the see also. Machine-readable resources that are related to the
     * current resource.
     * 
     * @return the see also
     */
    public List<ContentResource> getSeeAlso() {
        return seeAlso;
    }

    /**
     * Sets the see also.
     * 
     * @param seeAlso see also to set. Must not be {@code null}.
     */
    public void setSeeAlso(List<ContentResource> seeAlso) {
        assert seeAlso != null : "seeAlso must not be null";

        this.seeAlso = seeAlso;
    }

    /**
     * Returns the service. A service that the client might interact with and gain
     * additional information or functionality.
     * 
     * @return the service
     */
    public List<ContentResource> getService() {
        return service;
    }

    /**
     * Sets the service.
     * 
     * @param service service to set. Must not be {@code null}.
     */
    public void setService(List<ContentResource> service) {
        assert service != null : "service must not be null";

        this.service = service;
    }

    /**
     * Returns the homepage. A web page about the represented entity.
     * 
     * @return the homepage
     */
    public List<ContentResource> getHomepage() {
        return homepage;
    }

    /**
     * Sets the homepage.
     * 
     * @param homepage homepage to set. Must not be {@code null}.
     */
    public void setHomepage(List<ContentResource> homepage) {
        assert homepage != null : "homepage must not be null";

        this.homepage = homepage;
    }

    /**
     * Returns the rendering. A resource that is an alternative, non-IIIF
     * representation of the resource.
     * 
     * @return the rendering
     */
    public List<ContentResource> getRendering() {
        return rendering;
    }

    /**
     * Sets the rendering.
     * 
     * @param rendering rendering to set. Must not be {@code null}.
     */
    public void setRendering(List<ContentResource> rendering) {
        assert rendering != null : "rendering must not be null";

        this.rendering = rendering;
    }

    /**
     * Returns the part of. A containing resource that includes this resource.
     * 
     * @return the part of
     */
    public InternalLink getPartOf() {
        return partOf;
    }

    /**
     * Sets the part of.
     * 
     * @param partOf part of to set. Must not be {@code null}.
     */
    public void setPartOf(InternalLink partOf) {
        assert partOf != null : "partOf must not be null";

        this.partOf = partOf;
    }

    /**
     * Returns the services. A list of one or more services that are shared by more
     * than one contained resources.
     *
     * <P>
     * This allows these shared services to be collected in a single place, rather
     * than either having their information duplicated throughout the document.
     * 
     * @return the services
     */
    public List<ContentResource> getServices() {
        return services;
    }

    /**
     * Sets the services.
     * 
     * @param services services to set. Must not be {@code null}.
     */
    public void setServices(List<ContentResource> services) {
        assert services != null : "services must not be null";

        this.services = services;
    }

    /**
     * &#x1F511; Returns the items.
     * 
     * @return the items
     */
    public List<CollectionOrManifest> getItems() {
        return items;
    }

    /**
     * &#x1F511; Sets the items. A collection must have items.
     * 
     * @param items items to set. Must not be {@code null}.
     */
    public void setItems(List<CollectionOrManifest> items) {
        assert items != null : "items must not be null";

        this.items = items;
    }

    /**
     * Returns the annotations.
     * 
     * @return the annotations
     */
    public List<AnnotationPage> getAnnotations() {
        return annotations;
    }

    /**
     * Sets the annotations.
     * 
     * @param annotations annotations to set. Must not be {@code null}.
     */
    public void setAnnotations(List<AnnotationPage> annotations) {
        assert annotations != null : "annotations must not be null";

        this.annotations = annotations;
    }
}
