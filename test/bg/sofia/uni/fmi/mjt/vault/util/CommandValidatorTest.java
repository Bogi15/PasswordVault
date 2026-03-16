package bg.sofia.uni.fmi.mjt.vault.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommandValidatorTest {

    @Test
    void testCommandValidatorRegisterCorrectInputCount() {
        String input = "register username password123 password123";

        ParsedCommand parsed = CommandParser.parse(input);
        String result = CommandValidator.validate(parsed);

        assertNull(result, "Valid register command should return null");
    }

    @Test
    void testCommandValidateRegisterDifferentPasswords() {
        String input = "register username password123 differentPassword";

        ParsedCommand parsed = CommandParser.parse(input);
        String result = CommandValidator.validate(parsed);

        assertEquals("Passwords do not match.", result);
    }


    @Test
    void testCommandValidatorRegisterFewArg() {
        String input = "register username password123";

        ParsedCommand parsed = CommandParser.parse(input);
        String result = CommandValidator.validate(parsed);

        assertEquals("Usage: register <user> <password> <password-repeat>", result);
    }

    @Test
    void testCommandValidatorLoginCorrectInput() {
        String input = "login username password123";

        ParsedCommand parsed = CommandParser.parse(input);
        String result = CommandValidator.validate(parsed);

        assertNull(result, "Valid login command should return null");
    }

    @Test
    void testCommandValidatorLoginFewArg() {
        String input = "login username";

        ParsedCommand parsed = CommandParser.parse(input);
        String result = CommandValidator.validate(parsed);

        assertEquals("Usage: login <user> <password>", result);
    }

    @Test
    void testCommandValidatorRetrieveCredentialsCorrectInput() {
        String input = "retrieve-credentials instagram.com user@mail.com";

        ParsedCommand parsed = CommandParser.parse(input);
        String result = CommandValidator.validate(parsed);

        assertNull(result, "Valid retrieve-credentials command should return null");
    }

    @Test
    void testCommandValidatorRetrieveCredentialsFewArg() {
        String input = "retrieve-credentials instagram.com";

        ParsedCommand parsed = CommandParser.parse(input);
        String result = CommandValidator.validate(parsed);

        assertEquals("Usage: retrieve-credentials <website> <user>", result);
    }

    @Test
    void testCommandValidatorGeneratePasswordCorrectInput() {
        String input = "generate-password instagram.com user@mail.com";

        ParsedCommand parsed = CommandParser.parse(input);
        String result = CommandValidator.validate(parsed);

        assertNull(result, "Valid generate-password command should return null");
    }

    @Test
    void testCommandValidatorGeneratePasswordFewArg() {
        String input = "generate-password instagram.com";

        ParsedCommand parsed = CommandParser.parse(input);
        String result = CommandValidator.validate(parsed);

        assertEquals("Usage: generate-password <website> <user>", result);
    }

    @Test
    void testCommandValidatorAddPasswordCorrectInput() {
        String input = "add-password instagram.com user@mail.com password";

        ParsedCommand parsed = CommandParser.parse(input);
        String result = CommandValidator.validate(parsed);

        assertNull(result, "Valid add-password command should return null");
    }

    @Test
    void testCommandValidateAddPasswordFewArg() {
        String input = "add-password instagram.com user@mail.com";

        ParsedCommand parsed = CommandParser.parse(input);
        String result = CommandValidator.validate(parsed);

        assertEquals("Usage: add-password <website> <user> <password>", result);
    }

    @Test
    void testCommandValidateRemovePasswordCorrectInput() {
        String input = "remove-password instagram.com user@mail.com";

        ParsedCommand parsed = CommandParser.parse(input);
        String result = CommandValidator.validate(parsed);

        assertNull(result, "Valid remove-password command should return null");
    }

    @Test
    void testCommandValidateRemovePasswordFewArg() {
        String input = "remove-password instagram.com";

        ParsedCommand parsed = CommandParser.parse(input);
        String result = CommandValidator.validate(parsed);

        assertEquals("Usage: remove-password <website> <user>", result);
    }

    @Test
    void testCommandValidateLogoutCorrectInput() {
        String input = "logout";

        ParsedCommand parsed = CommandParser.parse(input);
        String result = CommandValidator.validate(parsed);

        assertNull(result, "Valid logout command should return null");
    }

    @Test
    void testCommandValidateLogoutTooManyArg() {
        String input = "logout extra";

        ParsedCommand parsed = CommandParser.parse(input);
        String result = CommandValidator.validate(parsed);

        assertEquals("Invalid usage.", result);
    }
}