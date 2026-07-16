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
import java.util.Locale;

/**
 * An external web resource. Points to the content (images, OCR data, …) by its
 * URL.
 *
 * <P>
 * Content resources link to content that must be obtained in a separate HTTP
 * request.
 *
 * <P>
 * This class implements {@code InternalLink} because it can internally be
 * referenced.
 */
public class ContentResource implements InternalLink {

    private MultiText label;
    private List<MultiField> metadata;
    private MultiText summary;
    private MultiField requiredStatement;
    private URI rights;
    private List<Locale> language;
    private List<Agent> provider;
    private List<ContentResource> thumbnail;
    private URI id;
    private String type;
    private String format;
    private String profile;
    private Integer height;
    private Integer width;
    private Float duration;
    private Behavior behavior;
    private List<ContentResource> seeAlso;
    private List<ContentResource> service;
    private List<ContentResource> homepage;
    private List<ContentResource> rendering;
    private InternalLink partOf;
    private List<AnnotationPage> annotations;

    /**
     * Returns the name. A human readable label, name or title.
     * 
     * @return the name
     */
    public MultiText getLabel() {
        return label;
    }

    /**
     * Sets the name.
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
     * &#x1F511; Returns the languages.
     * 
     * @return the languages
     */
    public List<Locale> getLanguage() {
        return language;
    }

    /**
     * &#x1F511; Sets the languages. Content resources are recommended to have
     * language information.
     * 
     * @param language languages to set. Must not be {@code null}.
     */
    public void setLanguage(List<Locale> language) {
        assert language != null : "language must not be null";

        this.language = language;
    }

    /**
     * Returns the provider. An organization or person that contributed to providing
     * the content of the resource.
     * 
     * @return the provider
     */
    public List<Agent> getProvider() {
        return provider;
    }

    /**
     * Sets the provider.
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
     * Sets the thumbnail.
     * 
     * @param thumbnail thumbnail to set. Must not be {@code null}.
     */
    public void setThumbnail(List<ContentResource> thumbnail) {
        assert thumbnail != null : "thumbnail must not be null";

        this.thumbnail = thumbnail;
    }

    /**
     * &#x1F511; Returns the identifier. A URI that identifies the content resource.
     * 
     * @return the identifier
     */
    public URI getId() {
        return id;
    }

    /**
     * &#x1F511; Sets the identifier. Content resources must have an id.
     * 
     * @param id identifier to set. Must not be {@code null}.
     */
    public void setId(URI id) {
        assert id != null : "id must not be null";

        this.id = id;
    }

    /**
     * &#x1F511; Returns the type class. The value of type is drawn from other
     * specifications. Recommendations for common content types are the strings
     * “{@code Dataset}”, “{@code Image}”, “{@code Model}”, “{@code
     * Sound}”, “{@code Text}” and “{@code Video}”. “Dataset” indicates data not
     * intended to be rendered to humans directly, “Model” in turn a three (or more)
     * dimensional model intended to be interacted with by humans.
     * 
     * @return the type class
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the MIME type.
     *
     * <P>
     * This is used for distinguishing different formats of the same resource, such
     * as distinguishing text in XML from plain text.
     * 
     * @return the MIME type
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the MIME type.
     * 
     * @param format MIME type to set. Must not be {@code null}. Must be a mime
     *               type.
     */
    public void setFormat(String format) {
        assert format != null : "format must not be null";
        assert format.matches("\\w+/[-.\\w]+(?:\\+[-.\\w]+)?") : "format must be a mime type";

        this.format = format;
    }

    /**
     * Returns the profile. A schema or named set of functionality available from
     * the resource.
     * 
     * @return the profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sets the profile. The value must be a string from the profiles registry or a
     * URI.
     * 
     * @param profile profile to set. Must not be {@code null}.
     */
    public void setProfile(String profile) {
        assert profile != null : "profile must not be null";

        this.profile = profile;
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
     * Sets the height. The value must be given in pixels.
     * 
     * @param height height to set. Must not be {@code null}.
     */
    public void setHeight(Integer height) {
        assert height != null : "height must not be null";

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
     * Sets the width. The value must be given in pixels.
     * 
     * @param width width to set. Must not be {@code null}.
     */
    public void setWidth(Integer width) {
        assert width != null : "width must not be null";

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
