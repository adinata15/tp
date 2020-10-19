package command.action;

import constants.Constants;
import org.junit.jupiter.api.Test;
import seedu.duke.Duke;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;


/**
 * Module Credits Test.
 */
public class McActionTest {

    private String[] testCommand = {"mc", "mc -d","mc -p","mc -d -p"};

    @Test
    public void mcAction_NormalInput_PrintTotalMcAndDetailMC() {
        Duke d = new Duke(false, System.out, System.in, Constants.PATH, Constants.TEST_FILENAME);
        assertAll("McActionTest", () -> assertTrue(d.testSut(testCommand[0], false, true)
                .contains("619")),
            () -> assertTrue(d.testSut(testCommand[1], false, true).contains("12"))
        );
    }

    @Test
    public void mcAction_ExceptionInput_PrintInvalidCommand() {
        Duke d = new Duke(false, System.out, System.in, Constants.PATH, Constants.TEST_FILENAME);
        assertTrue(() -> d.testSut(testCommand[2],false,true).contains(Constants.INVALID));
        assertTrue(() -> d.testSut(testCommand[3],false,true).contains(Constants.INVALID));
    }

}
