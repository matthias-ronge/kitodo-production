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
 * &#x1F511; A page. Canvases represent the pages to display.
 *
 * <P>
 * The canvas represents an individual page or view and acts
 * as a central point for assembling the different content resources that make
 * up the display.
 *
 * <P>
 * This class implements {@code RangeOrCanvas} because it must be mixable with
 * ranges in the {@code items} list of {@code Range}s. This class implements
 * {@code InternalLink} because it can internally be referenced.
 */
public class Canvas implements RangeOrCanvas, InternalLink {

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
    private String type = "Canvas";
    private Integer height;
    private Integer width;
    private Float duration;
    private Behavior behavior;
    private List<ContentResource> seeAlso;
    private List<ContentResource> service;
    private List<ContentResource> homepage;
    private List<ContentResource> rendering;
    private InternalLink partOf;
    private List<AnnotationPage> items;
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
     * &#x1F511; Sets the name. A canvas is recommended to have a label.
     * 
     * @param label name to set. Must not be {@code null}.
     */
    public void setLabel(MultiText label) {
        assert label != null : "label must not be null";

        this.label = label;
    }

    /**
     * Returns the metadata. An ordered list of descriptions.
     * 
     * @return the metadata
     */
    public List<MultiField> getMetadata() {
        return metadata;
    }

    /**
     * Sets the metadata.
     * 
     * @param metadata metadata to set. Must not be {@code null}.
     */
    public void setMetadata(List<MultiField> metadata) {
        assert metadata != null : "metadata must not be null";

        this.metadata = metadata;
    }

    /**
     * Returns the summary. A short textual summary to be conveyed to the user when
     * the metadata is not displayed.
     * 
     * @return the summary
     */
    public MultiText getSummary() {
        return summary;
    }

    /**
     * Sets the summary.
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
     * &#x1F511; Sets the provider. A canvas is recommended to have a provider.
     * 
     * @param provider provider to set. Must not be {@code null}.
     */
    public void setProvider(List<Agent> provider) {
        assert provider != null : "provider must not be null";

        this.provider = provider;
    }

    /**
     * Returns the thumbnail. A small image or short audio clip that represents the
     * resource.
     * 
     * @return the thumbnail
     */
    public List<ContentResource> getThumbnail() {
        return thumbnail;
    }

    /**
     * Sets the thumbnail. A canvas is recommended to have a thumbnail.
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
     * &#x1F511; Sets the identifier. A canvas must have an id.
     * 
     * @param id identifier to set. Must not be {@code null}. The URI must not have
     *           a fragment.
     */
    public void setId(URI id) {
        assert id != null : "id must not be null";
        assert id.getFragment() == null : "id must not have a fragment";

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
     * Returns the height. The the value does not have a unit. In combination with
     * the width, it conveys an aspect ratio for the space in which content
     * resources are located.
     * 
     * @return the height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * Sets the height.
     * 
     * @param height height to set. Must not be {@code null}. If a canvas has a
     *               height, it must also have a width.
     */
    public void setHeight(Integer height) {
        assert height != null : "height must not be null";
        assert height != null ? width != null : true : "height requires existence of width";

        this.height = height;
    }

    /**
     * Returns the width. The the value does not have a unit. In combination with
     * the height, it conveys an aspect ratio for the space in which content
     * resources are located.
     * 
     * @return the width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * Sets the width.
     * 
     * @param width width to set. Must not be {@code null}. If a canvas has a width,
     *              it must also have a height.
     */
    public void setWidth(Integer width) {
        assert width != null : "width must not be null";
        assert width != null ? height != null : true : "width requires existence of width";

        this.width = width;
    }

    /**
     * Returns the duration. The duration given in seconds.
     * 
     * @return the duration
     */
    public Float getDuration() {
        return duration;
    }

    /**
     * Sets the duration.
     * 
     * @param duration duration to set. Must not be {@code null}.
     */
    public void setDuration(Float duration) {
        assert duration != null : "duration must not be null";

        this.duration = duration;
    }

    /**
     * Returns the behavior. A set of user experience features that the publisher of
     * the content would prefer the client to use when presenting the resource.
     * Canvases inherit behaviors from their referencing manifest, but not from any
     * referencing ranges.
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
     * &#x1F511; Returns the items.
     * 
     * @return the items
     */
    public List<AnnotationPage> getItems() {
        return items;
    }

    /**
     * &#x1F511; Sets the items. Canvases are recommended to have items.
     * 
     * @param items items to set. Must not be {@code null}.
     */
    public void setItems(List<AnnotationPage> items) {
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
