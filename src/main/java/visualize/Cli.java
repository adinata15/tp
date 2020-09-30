package visualize;

import constants.Constants;
import data.TaskList;
import messages.MessageFormat;
import messages.MessageOptions;
import messages.MessageWrapper;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The type Cli.
 */
public class Cli extends UI {

    /**
     * The Msg format.
     */
    protected MessageFormat msgFormat;
    /**
     * The Msg wrapper.
     */
    protected MessageWrapper msgWrapper;

    /**
     * Instantiates a new Cli.
     *
     * @param stream the stream
     * @param input  the input
     */
    public Cli(PrintStream stream, InputStream input) {
        super(stream, input);
        msgFormat = new MessageFormat(new MessageOptions[]{
            MessageOptions.LINE_INDENT_1,
            MessageOptions.LINE_BEFORE,
            MessageOptions.INDENTED_2,
            MessageOptions.AUTO_RETURN,
            MessageOptions.LINE_AFTER
        });
        msgWrapper = new MessageWrapper(Constants.LINE_REPETITION, Constants.LINE_UNIT);
    }

    @Override
    public void showWelcome() {
        msgWrapper.show(stream, Constants.WELCOME, msgFormat.getMessageOptions());
    }

    @Override
    public void showText(String input) {
        String[] lines = input.split(Constants.WIN_NEWLINE);
        msgWrapper.show(stream, lines, msgFormat.getMessageOptions());
    }

    /**
     * Show list text.
     *
     * @param input       the input
     * @param indexOption the index option
     */
    public void showListText(String input, MessageOptions indexOption) {
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(input.split(Constants.WIN_NEWLINE)));
        String head = lines.get(0);
        lines.remove(head);
        msgFormat.removeMessageOption(MessageOptions.LINE_AFTER);
        msgWrapper.show(stream, head, msgFormat.getMessageOptions());
        msgFormat.addMessageOption(MessageOptions.LINE_AFTER);
        msgFormat.removeMessageOption(MessageOptions.LINE_BEFORE);
        msgFormat.addMessageOption(indexOption);
        String[] strings = new String[0];
        strings = lines.toArray(strings);
        msgWrapper.show(stream, strings, msgFormat.getMessageOptions());
        msgFormat.addMessageOption(MessageOptions.LINE_BEFORE);
        msgFormat.removeMessageOption(indexOption);
    }

    @Override
    public void update(String input, TaskList tasks) {
        if (freshlySwitched) {
            String replay = tasks.lastInput;
            MessageOptions replayOption = tasks.lastIndexOption;
            if (replay == null || replay.equals(Constants.ZERO_LENGTH_STRING)) {
                showWelcome();
            } else {
                showListText(replay, replayOption);
            }
            freshlySwitched = false;
            return;
        }
        if (input == null || input.equals(Constants.ZERO_LENGTH_STRING)) {
            showText(Constants.ZERO_LENGTH_STRING);
        } else if (input.contains(Constants.BMP_LIST_SWITCH)
                || input.contains(Constants.BMP_SEL_SWITCH)) {
            if (!tasks.lastInput.equals(Constants.ZERO_LENGTH_STRING)) {
                showListText(tasks.lastInput, tasks.lastIndexOption);
            }
        } else {
            showListText(input, tasks.indexOption);
            tasks.lastInput = input;
            tasks.lastIndexOption = tasks.indexOption;
        }
        tasks.indexOption = MessageOptions.NOT_INDEXED;
    }

}