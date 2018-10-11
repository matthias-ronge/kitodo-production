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

package org.kitodo.security.password;

import com.ibm.icu.impl.locale.XCldrStub;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitodo.config.PasswordConfig;
import org.kitodo.helper.Helper;
import org.passay.CharacterRule;
import org.passay.DictionaryRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.MessageResolver;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.PropertiesMessageResolver;
import org.passay.Rule;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.passay.dictionary.ArrayWordList;
import org.passay.dictionary.WordListDictionary;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private static final Logger logger = LogManager.getLogger(PasswordConstraintValidator.class);

    @Override
    public void initialize(ValidPassword argument) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator validator;
        if (Objects.nonNull(getLocalizedMessages())) {
            validator = new PasswordValidator(getLocalizedMessages(), getRulesFromConfigFile());
        } else {
            validator = new PasswordValidator(getRulesFromConfigFile());
        }

        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                XCldrStub.Joiner.on(",").join(validator.getMessages(result)))
                .addConstraintViolation();
        return false;
    }

    private List<Rule> getRulesFromConfigFile() {
        List<Rule> rules = new ArrayList<>();

        rules.add(new LengthRule(PasswordConfig.getLengthMin(), PasswordConfig.getLengthMax()));
        rules.add(new CharacterRule(EnglishCharacterData.Digit,PasswordConfig.getNumberOfDigitCharacters()));
        rules.add(new CharacterRule(EnglishCharacterData.Special,PasswordConfig.getNumberOfSpecialCharacters()));
        rules.add(new CharacterRule(EnglishCharacterData.UpperCase,PasswordConfig.getNumberOfUppercaseCharacters()));
        rules.add(new DictionaryRule(new WordListDictionary(new ArrayWordList(PasswordConfig.getNotAllowedWords()))));

        if (!PasswordConfig.isWhitespaceAllowed()) {
            rules.add(new WhitespaceRule());
        }

        return rules;
    }

    private MessageResolver getLocalizedMessages() {
        String messageFile = "password_en.properties";

        if (FacesContext.getCurrentInstance() != null) {
            Locale desiredLanguage = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            if (desiredLanguage != null && desiredLanguage.equals(Locale.GERMAN)) {
                messageFile = "password_de.properties";
            }
        }

        Properties properties = new Properties();

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classloader.getResourceAsStream("messages/" + messageFile)) {
            properties.load(inputStream);
            return new PropertiesMessageResolver(properties);
        } catch (IOException e) {
            Helper.setErrorMessage("Problem with messages loading!", logger, e);
            return null;
        }
    }
}
