//@@author TomLBZ

package command.action;

import command.ParamNode;
import constants.Constants;
import data.Data;
import data.Item;
import data.SingleModule;
import data.jobs.Deadline;
import data.jobs.Event;
import data.jobs.Task;
import data.jobs.ToDo;
import exceptions.CommandException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class EditAction extends Action {

    private class Operation {

        public boolean isMod;
        public String modCode;
        public int itemIndex;
        public ArrayList<String> operations;
        public String operationResult;

        public void defaultOperation() {
            isMod = false;
            modCode = null;
            itemIndex = -1;
            operations = new ArrayList<>();
        }

        public void normalOperation(boolean isMod, String input) {
            this.isMod = isMod;
            boolean isNumeric = isNumeric(input);
            if (isNumeric) {
                modCode = null;
                itemIndex = Integer.parseInt(input) - 1;
                if (itemIndex < 0) {
                    itemIndex = -1;
                }
            } else {
                if (isMod) {
                    modCode = input.trim();
                }
            }
            operations = new ArrayList<>();
        }

        public Operation(boolean isMod, ArrayList<String> strings) {
            if (strings == null || strings.size() == 0) {
                this.defaultOperation();
            }
            assert strings != null;
            String input = strings.get(0);
            strings.remove(0);
            normalOperation(isMod, input);
            operations.addAll(strings);
        }

        private boolean isNumeric(String input) {
            if (input == null || input.trim().isEmpty()) {
                return false;
            }
            char[] chars = input.trim().toCharArray();
            boolean isNum = true;
            for (char c: chars) {
                isNum = Character.isDigit(c);
                if (!isNum) {
                    break;
                }
            }
            return isNum;
        }

        public Item operate(Item item) {
            if (isMod && item instanceof SingleModule) {
                return operateMod((SingleModule) item);
            } else {
                return operateTask((Task) item);
            }
        }

        private SingleModule operateMod(SingleModule mod) {
            StringBuilder builder = new StringBuilder();
            if (!mod.isCompleted) {
                for (String operation : operations) {
                    String op = operation.replace(Constants.LINE_UNIT, Constants.SPACE).trim();
                    boolean operated = true;
                    if (op.contains(Constants.EQUALS)) {
                        String[] split = op.split(Constants.EQUALS);
                        split[0] = split[0].toLowerCase();
                        if (Arrays.stream(Constants.GRADE_ALIAS).anyMatch(s -> s.equals(split[0]))) {
                            mod.grade = split[1].toUpperCase();
                            mod.isTaken = true; // must be taken in order to have a grade
                        } else if (Arrays.stream(Constants.SU_ALIAS).anyMatch(s -> s.equals(split[0]))) {
                            mod.moduleSU = split[1];
                        } else if (Arrays.stream(Constants.SELECTED_ALIAS).anyMatch(s -> s.equals(split[0]))) {
                            mod.isSelected = split[1].toLowerCase().contains("t");
                        } else if (Arrays.stream(Constants.TAKEN_ALIAS).anyMatch(s -> s.equals(split[0]))) {
                            mod.isTaken = split[1].toLowerCase().contains("t");
                        } else if (Arrays.stream(Constants.TASK_ALIAS).anyMatch(s -> s.equals(split[0]))) {
                            mod.immediateData = split[1];
                        } else {
                            operated = false;
                        }
                    } else {
                        operated = false;
                    }
                    if (operated) {
                        builder.append(op).append(Constants.CMD_END).append(Constants.SPACE);
                    }
                }
            }
            operationResult = builder.toString();
            if (operationResult.length() == 0) {
                operationResult = Constants.NO_OPERATION_POSSIBLE;
            }
            return mod;
        }

        private Task operateTask(Task task) {
            StringBuilder builder = new StringBuilder();
            for (String operation : operations) {
                String op = operation.replace(Constants.LINE_UNIT, Constants.SPACE).trim();
                boolean operated = true;
                if (op.contains(Constants.EQUALS)) {
                    String[] split = op.split(Constants.EQUALS);
                    split[0] = split[0].toLowerCase();
                    if (Arrays.stream(Constants.DESCRIPTION_ALIAS).anyMatch(s -> s.equals(split[0]))) {
                        task.setDescription(split[1]);
                    } else if (Arrays.stream(Constants.TYPE_ALIAS).anyMatch(s -> s.equals(split[0]))) {
                        String dateTime = "01 01 2021 00:00";
                        if (task.isDated) {
                            dateTime = task.getDateTimeString();
                        }
                        switch (split[1].toLowerCase()) {
                        case "deadline": // same as "ddl"
                        case "ddl": // same as "d"
                        case "d":
                            Deadline ddl = new Deadline(task.getDescription(), dateTime);
                            ddl.isSelected = task.isSelected;
                            ddl.isWeekly = task.isWeekly;
                            if (task.getIsDone()) {
                                ddl.markAsDone();
                            }
                            task = ddl;
                            break;
                        case "event": // same as "e"
                        case "e":
                            Event event = new Event(task.getDescription(), dateTime);
                            event.isSelected = task.isSelected;
                            event.isWeekly = task.isWeekly;
                            if (task.getIsDone()) {
                                event.markAsDone();
                            }
                            task = event;
                            break;
                        case "todo": // same as "t"
                        case "t":
                            ToDo todo = new ToDo(task.getDescription());
                            todo.isSelected = task.isSelected;
                            todo.isWeekly = task.isWeekly;
                            if (task.getIsDone()) {
                                todo.markAsDone();
                            }
                            task = todo;
                            break;
                        default:
                            operated = false;
                            break;
                        }
                    } else if (Arrays.stream(Constants.DATE_ALIAS).anyMatch(s -> s.equals(split[0]))) {
                        if (task.isDated) {
                            LocalDateTime localDateTime = Item.parseDateTime(split[1]);
                            if (localDateTime != null) {
                                task.updateDateTime(localDateTime);
                            } else {
                                operated = false;
                            }
                        } else {
                            operated = false;
                        }
                    } else if (Arrays.stream(Constants.SELECTED_ALIAS).anyMatch(s -> s.equals(split[0]))) {
                        task.isSelected = split[1].toLowerCase().contains("t");
                    } else if (Arrays.stream(Constants.WEEKLY_ALIAS).anyMatch(s -> s.equals(split[0]))) {
                        task.isWeekly = split[1].toLowerCase().contains("t");
                    } else if (Arrays.stream(Constants.DONE_ALIAS).anyMatch(s -> s.equals(split[0]))) {
                        if (split[1].toLowerCase().contains("t")) {
                            task.markAsDone();
                        } else {
                            task.markAsUndone();
                        }
                    } else {
                        operated = false;
                    }
                } else {
                    operated = false;
                }
                if (operated) {
                    builder.append(op).append(Constants.CMD_END).append(Constants.SPACE);
                }
            }
            operationResult = builder.toString();
            if (operationResult.length() == 0) {
                operationResult = Constants.NO_OPERATION_POSSIBLE;
            }
            return task;
        }
    }

    private ArrayList<Operation> operations;

    private Item findMod(ArrayList<Item> mods, ArrayList<Item> targets, int index, String code) {
        if (code != null) {
            for (Item item : mods) {
                if (item.getName().equals(code)) {
                    return item;
                }
            }
        } else {
            if (index < 0 || index >= targets.size()) {
                return null;
            }
            Item target = targets.get(index);
            if (target instanceof SingleModule) {
                return target;
            } else {
                return mods.get(index);
            }
        }
        return null;
    }

    private Item findTask(ArrayList<Item> targets, int index) {
        if (index < 0 || index >= targets.size()) {
            return null;
        }
        Item target = targets.get(index);
        if (!(target instanceof SingleModule)) {
            return target;
        }
        return null;
    }

    @Override
    public String act(Data data) throws Exception {
        String defaultResult = super.act(data);
        StringBuilder stringBuilder = new StringBuilder();
        if (operations == null || operations.size() == 0) {
            stringBuilder.append(Constants.NO_OPERATION_POSSIBLE);
        } else {
            ArrayList<Item> targets = data.getTarget();
            for (Operation operation : operations) {
                Item target;
                if (operation.isMod) {
                    target = findMod(data.mods, targets, operation.itemIndex, operation.modCode);
                } else {
                    target = findTask(targets, operation.itemIndex);
                }
                Item operatedTarget = operation.operate(target);
                if (operatedTarget instanceof SingleModule) {
                    String immediate = operatedTarget.immediateData;
                    ArrayList<Item> immediateList = loadImmediateList(immediate, data.target);
                }
                data.updateItem(target, operatedTarget);
                stringBuilder.append(operation.operationResult).append(Constants.WIN_NEWLINE);
            }
        }
        return defaultResult.replace(Constants.TEXT_PLACEHOLDER, stringBuilder.toString());
    }

    private ArrayList<Item> loadImmediateList(String immediate, ArrayList<Item> targets) {
        return targets;
    }

    private boolean isImmediateFormatted(String input) {
        if (input == null || input.length() == 0) {
            return false;
        }
        String[] splitted = input.split(Constants.COMMA);
        for (String fraction : splitted) {
            if (fraction.length() != 2) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void prepare(ParamNode args) throws Exception {
        operations = new ArrayList<>();
        super.prepare(args);
        for (ParamNode arg : flattenedArgs) {
            if (arg.thisData == null) {
                throw new CommandException();
            }
            if (arg.name.equals(Constants.MOD)) {
                String[] strings = arg.thisData.toFlatString().split(Constants.SPACE);
                operations.add(new Operation(true, new ArrayList<String>(Arrays.asList(strings))));
            } else if (arg.name.equals(Constants.TASK)) {
                String[] strings = arg.thisData.toFlatString().split(Constants.SPACE);
                operations.add(new Operation(false, new ArrayList<String>(Arrays.asList(strings))));
            } else {
                throw new CommandException();
            }
        }
    }
}
