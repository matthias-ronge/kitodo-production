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

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Contains text in multiple languages. It makes the text contents
 * internationalizable.
 *
 * <P>
 * For each language, contains a list of text lines. When used for the
 * <I>summary</I>, it may contain the basic HTML tags &lt;a>, &lt;b>, &lt;br>,
 * &lt;i>, &lt;img>, &lt;p>, &lt;small>, &lt;span>, &lt;sub> and &lt;sup>.
 */
public class MultiText {

    /**
     * The undefined language (language code "und").
     */
    public static final Locale UNDEFINED_LANGUAGE = Locale.forLanguageTag("und");

    /**
     * Creates an empty MultiText instance.
     */
    public MultiText() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Creates a MultiText instance with text without language information. This is
     * a convenience constructor.
     *
     * <P>
     * The text is wrapped to a list at line breaks and stored with the undefined
     * language ("und").
     * 
     * @param text text to store. Must not be {@code null}. Must not be empty.
     */
    public MultiText(String text) {
        assert text != null : "text must not be null";
        assert !text.isEmpty() : "text must not be empty";

        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Creates a MultiText instance with text without language information. This is
     * a convenience constructor.
     *
     * <P>
     * The text is stored with the undefined language ("und").
     * 
     * @param text text to store. Must not be {@code null}. Must not be empty.
     */
    public MultiText(List<String> text) {
        assert text != null : "text must not be null";
        assert !text.isEmpty() : "text must not be empty";

        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Creates a MultiText instance with text in a defined language. This is a
     * convenience constructor.
     *
     * <P>
     * The text is wrapped to a list at line breaks.
     * 
     * @param language language of the text. Must not be {@code null}.
     * @param text     text to store. Must not be {@code null}. Must not be empty.
     */
    public MultiText(Locale language, String text) {
        assert language != null : "language must not be null";
        assert text != null : "text must not be null";
        assert !text.isEmpty() : "text must not be empty";

        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Creates a MultiText instance with text in a defined language. This is a
     * convenience constructor.
     * 
     * @param language language of the text. Must not be {@code null}.
     * @param text     text to store. Must not be {@code null}. Must not be empty.
     */
    public MultiText(Locale language, List<String> text) {
        assert language != null : "language must not be null";
        assert text != null : "text must not be null";
        assert !text.isEmpty() : "text must not be empty";

        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Adds a line of text without language information. The line is appended to the
     * lines of text for the undefined language ("und").
     * 
     * @param line line to append. Must not be {@code null}.
     */
    public String addLine(String line) {
        assert line != null : "line must not be null";

        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Adds a line of text for a given language. The line is appended to the lines
     * of text for the given language.
     * 
     * @param language language of the text. Must not be {@code null}.
     * @param line     line to append. Must not be {@code null}.
     */
    public Locale addLine(Locale language, String line) {
        assert language != null : "language must not be null";
        assert line != null : "line must not be null";

        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Adds lines of text without language information. The lines are appended to
     * the lines of text for the undefined language ("und").
     * 
     * @param lines lines to append. Must not be {@code null}.
     */
    public List<String> addLines(List<String> lines) {
        assert lines != null : "lines must not be null";

        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Adds lines of text for a given language. The lines are appended to the lines
     * of text for the given language.
     * 
     * @param language language of the text. Must not be {@code null}.
     * @param lines    lines to append. Must not be {@code null}.
     */
    public Locale addLines(Locale language, List<String> lines) {
        assert language != null : "language must not be null";
        assert lines != null : "lines must not be null";

        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Returns all locales for which texts are available.
     *
     * <P>
     * <B>API Note:</B><BR>
     * For an empty object, returns an empty set. Never returns null.
     *
     * <P>
     * <B>Implementation Note:</B><BR>
     * The set cannot be modified.
     * 
     * @return all languages
     */
    public Set<Locale> getLanguages() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Sets text without language information. The text is wrapped to a list at line
     * breaks and stored with the undefined language ("und"). An existing text with
     * undefined language is replaced.
     * 
     * @param text text to set. If {@code null}, an existing text with undefined
     *             language is deleted. Must not be empty.
     */
    public String setText(String text) {
        assert !text.isEmpty() : "text must not be empty";

        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Sets text without language information. The text is stored with the undefined
     * language ("und"). An existing text with undefined language is replaced.
     * 
     * @param text text to set. If {@code null}, an existing text with undefined
     *             language is deleted. Must not be empty.
     */
    public List<String> setText(List<String> text) {
        assert !text.isEmpty() : "text must not be empty";

        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Sets text for a given language. The text is wrapped to a list at line breaks.
     * An existing text for that language is replaced.
     * 
     * @param language language of the text. Must not be {@code null}.
     * @param text     text to set. If {@code null}, an existing text in the given
     *                 language is deleted. Must not be empty.
     */
    public Locale setText(Locale language, String text) {
        assert language != null : "language must not be null";
        assert !text.isEmpty() : "text must not be empty";

        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Sets text for a given language. The text is wrapped to a list at line breaks.
     * An existing text for that language is replaced.
     * 
     * @param language language of the text. Must not be {@code null}.
     * @param text     text to set. If {@code null}, an existing text in the given
     *                 language is deleted. Must not be empty.
     */
    public Locale setText(Locale language, List<String> text) {
        assert language != null : "language must not be null";
        assert !text.isEmpty() : "text must not be empty";

        throw new UnsupportedOperationException("not yet implemented");
    }
}
