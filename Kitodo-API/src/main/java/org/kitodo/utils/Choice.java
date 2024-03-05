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

package org.kitodo.utils;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.*;
import java.util.function.Consumer;

public class Choice<T> {
    private T compared;
    boolean task = false;
    boolean complete = false;
    List<Consumer<T>> actions = new ArrayList<>();

    private Choice(T compared) {
        this.compared = compared;
    }

    public static <T> Choice<T> on(T compared) {
        return new Choice<T>(compared);
    }

    public Choice<T> inCase(T comparee) {
        if (complete) throw new IllegalStateException("inCase() must not follow orFail() or others()");
        task |= Objects.equals(comparee, compared);
        return this;
    }

    public Choice<T> execute(Consumer<T> action) {
        if (complete) throw new IllegalStateException("run() must not follow orFail() or others()");
        if (task)
            actions.add(action);
        task = false;
        return this;
    }

    public void others() {
        if (complete) throw new IllegalStateException("others() must not follow orFail() or others()");
        for (Consumer<T> action : actions) {
            action.accept(compared);
        }
        complete = true;
    }

    public void others(Consumer<T> action) {
        if (complete) throw new IllegalStateException("others() must not follow orFail() or others()");
        if (actions.isEmpty())
            actions.add(action);
        others();
    }

    public void orFail() {
        orFail(IllegalStateException.class, "complete choice");
    }

    public void orFail(Class<? extends RuntimeException> exceptionClass, String message) {
        if (complete)  throw new IllegalStateException("orFail() must not follow orFail() or others()");
        if (actions.isEmpty()) {
            RuntimeException execption;
            try {
                execption = message == null ? exceptionClass.getDeclaredConstructor().newInstance()
                        : exceptionClass.getDeclaredConstructor(String.class).newInstance(message);
            } catch (Exception e) {
                throw e instanceof RuntimeException ? (RuntimeException) e : new UndeclaredThrowableException(e);
            }
            throw execption;
        }
        others();
    }
}
