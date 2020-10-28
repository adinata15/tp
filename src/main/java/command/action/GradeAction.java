//@@author TomLBZ
package command.action;

import command.ParamNode;
import constants.Constants;
import data.Data;
import data.Item;
import data.SingleModule;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Grade input action.
 */
public class GradeAction extends TakeAction {

    private HashMap<String, ArrayList<String>> map;
    private boolean isShown;

    @Override
    public String act(Data data) throws Exception {
        if (isShown) {
            blindSearch = Constants.TAKEN;
        } else {
            blindSearch = Constants.SELECTED;
        }
        String output = super.act(data).replace("taken or untaken", "graded")
                .replace(Constants.MODIFY_FAILED, Constants.MOD_NOT_FOUND);
        if (isShown) {
            output = output.replace("selected", "taken");
        }
        return output;
    }

    @Override
    protected boolean modifyObject(Item item) {
        assert item instanceof SingleModule;
        for (String key : map.keySet()) {
            boolean notBlind = !isBlind
                    && (key.equals(((SingleModule) item).moduleCode) || key.equals(item.immediateData));
            boolean blind = isBlind && key.equals("default");
            if (notBlind || blind) {
                ArrayList<String> values = map.get(key);
                String grade = "";
                if (values == null || values.size() == 0) {
                    grade = null;
                } else {
                    grade = values.get(0);
                }
                ((SingleModule) item).grade = grade;
                ((SingleModule) item).isTaken = true;
                return true;
            }
        }
        item.immediateData = null;
        return isShown;
    }

    @Override
    protected String getObjectInfo(Item item) {
        return item.getName() + Constants.DETAILS_SIGNATURE + ((SingleModule)item).grade;
    }

    @Override
    protected void safetyCheck() {
        isBlind = true;
        isShown = true;
    }

    @Override
    public void prepare(ParamNode args) throws Exception {
        isShown = false;
        map = new HashMap<>();
        indices = new ArrayList<>();
        codes = new ArrayList<>();
        super.superPrepare(args);
        isBlind = false;
        if (args.thisData == null || flattenedArgs.length < 1) {
            safetyCheck();
            return;
        }
        String[] identifiers = args.thisData.toFlatString().split(Constants.SPACE);
        String lastKey = "default";
        for (String id : identifiers) {
            try {
                indices.add(Integer.parseInt(id) - 1);
                lastKey = id;
                map.put(id, new ArrayList<>());
            } catch (Exception e) {
                if (id.matches("([A-Z])+([0-9])+[A-Z]*")) {
                    codes.add(id);
                    lastKey = id;
                    map.put(id, new ArrayList<>());
                } else {
                    if (map.get(lastKey) == null) {
                        isBlind = true;
                        map.put(lastKey, new ArrayList<>());
                    }
                    map.get(lastKey).add(id);
                }
            }
        }
    }
}
