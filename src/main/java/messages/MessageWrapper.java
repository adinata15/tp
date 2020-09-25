package messages;

import constants.Constants;

import java.util.Arrays;

public class MessageWrapper {
    private String lineBase;

    public MessageWrapper(int length, String baseUnit) {
        setLineBase(length, baseUnit);
    }

    public void setLineBase(int length, String baseUnit) {
        lineBase = baseUnit.repeat(length);
    }

    public String wrap(String input, MessageOptions[] options) {
        return wrap(new String[]{input}, options);
    }

    public String wrap(String[] inputs, MessageOptions[] options) {
        boolean isLineBefore = Arrays.asList(options).contains(
                MessageOptions.LINE_BEFORE);
        boolean isLineAfter = Arrays.asList(options).contains(
                MessageOptions.LINE_AFTER);
        boolean isAutoReturn = Arrays.asList(options).contains(
                MessageOptions.AUTO_RETURN);
        MessageOptions indexOption = getIndexOption(options);
        StringBuilder stringBuilder = new StringBuilder();
        int lineIndent = getLineIndent(options);
        int textIndent = getTextIndent(options);
        if (isLineBefore) {
            stringBuilder.append(buildLine(lineIndent, lineBase));
        }
        for (int i = 0; i < inputs.length; i++) {
            int index = i + 1;
            stringBuilder.append(buildMessage(
                    textIndent, index, inputs[i], isAutoReturn, indexOption));
        }
        if (isLineAfter) {
            stringBuilder.append(buildLine(lineIndent, lineBase));
        }
        return stringBuilder.toString();
    }

    public void show(String[] inputs, MessageOptions[] options) {
        System.out.print(wrap(inputs, options));
    }

    public void show(String input, MessageOptions[] options) {
        System.out.print(wrap(input, options));
    }

    private int getLineIndent(MessageOptions[] options) {
        if (Arrays.asList(options).contains(MessageOptions.LINE_INDENT_1)) {
            return Constants.INDENT_1;
        } else if (Arrays.asList(options).contains(MessageOptions.LINE_INDENT_2)) {
            return Constants.INDENT_2;
        } else if (Arrays.asList(options).contains(MessageOptions.LINE_INDENT_3)) {
            return Constants.INDENT_3;
        }
        return Constants.NO_INDENT;
    }

    private int getTextIndent(MessageOptions[] options) {
        if (Arrays.asList(options).contains(MessageOptions.INDENTED_1)) {
            return Constants.INDENT_1;
        } else if (Arrays.asList(options).contains(MessageOptions.INDENTED_2)) {
            return Constants.INDENT_2;
        } else if (Arrays.asList(options).contains(MessageOptions.INDENTED_3)) {
            return Constants.INDENT_3;
        }
        return Constants.NO_INDENT;
    }

    private MessageOptions getIndexOption(MessageOptions[] options) {
        if (Arrays.asList(options).contains(MessageOptions.INDEXED_NUM)) {
            return MessageOptions.INDEXED_NUM;
        } else if (Arrays.asList(options).contains(MessageOptions.INDEXED_ABC)) {
            return MessageOptions.INDEXED_ABC;
        }
        return MessageOptions.NOT_INDEXED;
    }

    private String buildLine(int lineIndent, String lineBase) {
        return Constants.TAB.repeat(lineIndent) +
                lineBase + Constants.WIN_NEWLINE;
    }

    private String buildMessage(int indent, int index, String message,
                                boolean isAutoReturn, MessageOptions indexOption) {
        String formattedMessage = Constants.TAB.repeat(indent);
        if (index > 0) {
            if (indexOption == MessageOptions.INDEXED_NUM) {
                formattedMessage += index + Constants.DOT;
            } else if (indexOption == MessageOptions.INDEXED_ABC) {
                formattedMessage += (char) (index +
                        Constants.LETTER_OFFSET) + Constants.DOT;
            }
        }
        formattedMessage += message;
        if (isAutoReturn) {
            formattedMessage += Constants.WIN_NEWLINE;
        }
        return formattedMessage;
    }
}
