package command;

import com.sun.nio.sctp.AbstractNotificationHandler;
import command.action.UnknownAction;
import command.action.Action;
import constants.Constants;
import constants.HelpText;
import data.Data;
import exceptions.InvalidCommandException;
import exceptions.ModuleNotFoundException;

/**
 * The type Command.
 */
public class Command implements Help {

    /**
     * The Args.
     */
    public ParamNode args;
    /**
     * The Flattened args.
     */
    public ParamNode[] flattenedArgs;
    /**
     * The Name.
     */
    public String name;
    private HelpText helpText;
    /**
     * The Action.
     */
    public Action action;
    /**
     * The Result.
     */
    public String result = "";

    /**
     * Instantiates a new Command.
     *
     * @param args the args
     */
    public Command(ParamNode args) {
        this.args = args;
        name = args.name;
        flattenedArgs = new ParamNode[0];
        if (args.thisData != null) {
            flattenedArgs = args.thisData.flatten().toArray(flattenedArgs);
        }
        helpText = Constants.helpMap.getOrDefault(name, HelpText.UNKNOWN); // later change to help.
        initiateAction();
    }

    private boolean isArgsValid() {
        String targetArg = Constants.paramMap.get(name);
        if (targetArg == null) {
            return true; // does not need any parameter
        } else {
            for (ParamNode node : flattenedArgs) {
                if (targetArg.equals(node.name)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void initiateAction() {
        action = Constants.actionMap.getOrDefault(name, new UnknownAction());
    }

    /**
     * Execute.
     *
     * @param data the data
     */
    public void execute(Data data) {
        try {
            if (isArgsValid()) {
                action.prepare(args);
                action.checkError(args,data);
                result = action.act(data);
            } else {
                throw new InvalidCommandException();
            }
        } catch (ModuleNotFoundException me) {
            StringBuilder builder = new StringBuilder(Constants.NO_MODULE);
            result = builder.toString();
        } catch (Exception e) {
            StringBuilder builder = new StringBuilder(Constants.INVALID);
            String[] syntax = getSyntax();
            for (int i = 0; i < syntax.length; i++) {
                builder.append(syntax[i]);
                if (i < syntax.length - 1) {
                    builder.append(Constants.SYNTAX_OR);
                }
            }
            result = builder.toString();
        }
    }

    /**
     * Is exit boolean.
     *
     * @return the boolean
     */
    public boolean isBye() {
        return result.equals(Constants.messageMap.get(Constants.BYE));
    }

    /**
     * Is fancy boolean.
     *
     * @return the boolean
     */
    public boolean isFancy() {
        return result.equals(Constants.messageMap.get(Constants.FANCY));
    }

    /**
     * Is plain boolean.
     *
     * @return the boolean
     */
    public boolean isPlain() {
        return result.equals(Constants.messageMap.get(Constants.PLAIN));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return helpText.description;
    }

    @Override
    public String[] getSyntax() {
        return helpText.syntax;
    }

    @Override
    public String[] getUsages() {
        return helpText.usages;
    }

    @Override
    public String getHelp() {
        return helpText.toString();
    }

    @Override
    public String toString() {
        return args.toString();
    }

}