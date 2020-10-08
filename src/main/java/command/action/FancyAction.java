package command.action;

import constants.Constants;
import data.Data;

/**
 * The type Fancy action.
 */
public class FancyAction extends Action {

    /**
     * Instantiates a new Fancy action.
     */
    public FancyAction() {
        super();
    }

    @Override
    public String act(Data data) throws Exception {
        return Constants.WELCOME;
    }

}
