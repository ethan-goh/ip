import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
class Task {
    protected String description;
    protected boolean isComplete;

    protected String input;

    public Task (String description, String input) {
        this.description = description;
        this.isComplete = false;
        this.input = input;
    }

    public String getInput() {
        return this.input;
    }

    @Override
    public String toString() {
        String marked;
        if (isComplete) {
            marked = "[X] ";
        } else {
            marked = "[ ] ";
        }
        return marked + this.description;
    }

    public void mark() {
        this.isComplete = true;
    }
    public void unmark() {
        this.isComplete = false;
    }
}

class Todo extends Task {
    public Todo (String description, String input) {
        super(description, input);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}

class Deadline extends Task {
    protected LocalDate deadline;
    public Deadline (String description, LocalDate deadline, String input) {
        super(description, input);
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.deadline.format(DateTimeFormatter.ofPattern("MMM d yyyy")) + ")";
    }
}

class Event extends Task {
    protected LocalDate startTime;
    protected LocalDate endTime;
    public Event (String description, LocalDate startTime, LocalDate endTime, String input) {
        super(description, input);
        this.startTime = startTime;
        this.endTime = endTime;
    }
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.startTime.format(DateTimeFormatter.ofPattern("MMM d yyyy"))
                + " to: " + this.endTime.format(DateTimeFormatter.ofPattern("MMM d yyyy")) + ")";
    }
}

class TaskManager {
    protected ArrayList<Task> tasks;

    public TaskManager() {
        this.tasks = new ArrayList<>();
    }

    public ArrayList<Task> getTasks() {
        return this.tasks;
    }



    public void addTask(Task task, boolean silent) {
        tasks.add(task);
        if (!silent) {
            System.out.println("Got it. I've added this task:\n" + task.toString() + "\nNow you have " + tasks.size()
                    + " tasks in the list.");
        }
    }

    public void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks in the list.");
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    public void markTask(int index) {
        if (isValidIndex(index)) {
            tasks.get(index).mark();
            System.out.println("Nice! I've marked this task as done:\n" + tasks.get(index).description);
        }
    }

    public void unmarkTask(int index) {
        if (isValidIndex(index)) {
            tasks.get(index).unmark();
            System.out.println("Ok. I've marked this task as not done yet:\n" + tasks.get(index).description);
        }
    }

    public void deleteTask(int index) {
        if (isValidIndex(index)) {
            Task removedTask = tasks.remove(index);
            System.out.println("Noted. I have removed the following task: \n" + removedTask.toString() +
                    "\nNow you have " + tasks.size() + " tasks in the list.");
        }
    }

    private boolean isValidIndex(int index) {
        if (index < 0 || index >= tasks.size()) {
            System.out.println("No such task exists!");
            return false;
        }
        return true;
    }
}

class CommandParser {
    protected TaskManager taskManager;

    public CommandParser(TaskManager taskManager) {
        this.taskManager = taskManager;
        loadTasks();
    }
    public void loadTasks() {
        File f = new File("data/tasks.txt");
        if (f.exists()) {
            try (Scanner s = new Scanner(f)) {
                while (s.hasNext()) {
                    parseCommand(s.nextLine(), true);
                }

            } catch (IOException e) {
                System.out.println("Error loading tasks from file: " + e.getMessage());
            }
        } else {
            System.out.println("Error. Data file does not exist!");
        }
    }

    public void writeTasks() {
        try (FileWriter fw = new FileWriter("data/tasks.txt")) {
            ArrayList<Task> tasks = this.taskManager.tasks;
            for (Task task: tasks) {
                fw.write(task.getInput() + System.lineSeparator());
            }
            fw.close();

        } catch (IOException e) {
            System.out.println("File does not exist!");
        }

    }

    public void parseCommand(String input, boolean silent) {
        if (input.equalsIgnoreCase("bye")) {
            System.out.println("Bye. Hope to see you again soon!");
            System.exit(0);
        } else if (input.equalsIgnoreCase("list")) {
            taskManager.listTasks();
        } else if (input.startsWith("mark ")) {
            handleMark(input);
        } else if (input.startsWith("unmark ")) {
            handleUnmark(input);
        } else if (input.startsWith("delete ")) {
            handleDelete(input);
        } else if (input.startsWith("deadline ")) {
            handleDeadline(input, silent);
        } else if (input.startsWith("todo ")) {
            handleTodo(input, silent);
        } else if (input.startsWith("event ")) {
            handleEvent(input, silent);
        } else {
            System.out.println("Sorry, I am not sure what task this is! Please enter a valid task.");
        }
    }

    private void handleMark(String input) {
        try {
            int index = Integer.parseInt(input.substring(5)) - 1;
            taskManager.markTask(index);
            writeTasks();
        } catch (NumberFormatException e) {
            System.out.println("Invalid task number!");
        }
    }

    private void handleUnmark(String input) {
        try {
            int index = Integer.parseInt(input.substring(7)) - 1;
            taskManager.unmarkTask(index);
            writeTasks();
        } catch (NumberFormatException e) {
            System.out.println("Invalid task number!");
        }
    }

    private void handleDelete(String input) {
        try {
            int index = Integer.parseInt(input.substring(7)) - 1;
            taskManager.deleteTask(index);
            writeTasks();
        } catch (NumberFormatException e) {
            System.out.println("Invalid task number!");
        }
    }

    private void handleDeadline(String input, boolean silent) {
        if (!input.contains("/by ")) {
            System.out.println("You need a deadline to add this task!");
            return;
        }
        String[] parts = input.split("/by ");
        String taskName = parts[0].replaceFirst("deadline ", "").trim();
        if (taskName.isEmpty()) {
            System.out.println("You need a task description!");
            return;
        }
        String initial = parts[1].trim();
        LocalDate deadline = LocalDate.parse(initial);
        Deadline deadlineTask = new Deadline(taskName, deadline, input);
        taskManager.addTask(deadlineTask, silent);
        writeTasks();
    }

    private void handleTodo(String input, boolean silent) {
        String taskName = input.replaceFirst("todo ", "").trim();
        if (taskName.isEmpty()) {
            System.out.println("You need a task description!");
            return;
        }
        Todo todoTask = new Todo(taskName, input);
        taskManager.addTask(todoTask, silent);
        writeTasks();
    }

    private void handleEvent(String input, boolean silent) {
        if (!input.contains("/from ") || !input.contains("/to ")) {
            System.out.println("You need a starting and ending date to add this task!");
            return;
        }
        String[] parts = input.split("/from ");
        String[] dateParts = parts[1].split("/to ");
        String taskName = parts[0].replaceFirst("event ", "").trim();
        if (taskName.isEmpty()) {
            System.out.println("You need a task description!");
            return;
        }
        String initialStartDate = dateParts[0].trim();
        String initialEndDate = dateParts[1].trim();
        LocalDate startDate = LocalDate.parse(initialStartDate);
        LocalDate endDate = LocalDate.parse(initialEndDate);
        Event eventTask = new Event(taskName, startDate, endDate, input);
        taskManager.addTask(eventTask, silent);
        writeTasks();
    }
}

public class Genesis {

    public static void main(String[] args) {
        System.out.println("Hello! I'm Genesis!\nWhat can I do for you?\n");

        TaskManager taskManager = new TaskManager();
        CommandParser parser = new CommandParser(taskManager);

        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine();
            parser.parseCommand(input, false);
        }
    }
}
/*public class Genesis {

    public static void main(String[] args) {


        System.out.println("Hello! I'm Genesis!\n"
                + "What can I do for you?\n");
        Scanner sc = new Scanner(System.in);
        ArrayList<Task> items = new ArrayList<>();
        while (true) {
            String input = sc.nextLine();
            if (input.equalsIgnoreCase("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            } else if (input.equalsIgnoreCase("list")) {
                for (int i = 0; i < items.size(); i++) {
                    System.out.println(i + 1 + ". " + items.get(i));
                }
            } else if (input.startsWith("mark ")) {
                int index = Integer.parseInt(input.substring(5)) - 1;
                if (index >= items.size()) {
                    System.out.println("No such task exists!");
                    continue;
                }
                Task current = items.get(index);
                current.mark();
                System.out.println("Nice! I've marked this as done:\n" +
                                    current.description);
            } else if (input.startsWith("unmark ")) {
                int index = Integer.parseInt(input.substring(7)) - 1;
                if (index >= items.size()) {
                    System.out.println("No such task exists!");
                    continue;
                }
                Task current = items.get(index);
                current.unmark();
                System.out.println("Ok. I've marked this task as not done yet:\n" +
                        current.description);
            } else if (input.startsWith("delete ")) {
                int index = Integer.parseInt(input.substring(7)) - 1;
                if (index >= items.size()) {
                    System.out.println("No such task exists!");
                    continue;
                }
                Task current = items.get(index);
                items.remove(index);
                System.out.println("Noted. I have removed the following task: \n"
                                   + current.toString()
                                   + "\nNow you have " + items.size() + " items in the list.");
            } else if (input.startsWith("deadline ")){
                if (!input.contains("/by ")) {
                    System.out.println("You need a deadline to add this task!");
                    continue;
                }
                String[] parts = input.split("/by ");
                String taskName = parts[0].replaceFirst("deadline ", "").trim();
                if (taskName.equals("")) {
                    System.out.println("You need a task description!");
                    continue;
                }
                String deadline = parts[1].trim();
                Deadline current = new Deadline(taskName, deadline);
                items.add(current);
                System.out.println("Got it. I've added this task:\n" + current.toString()
                                    + "\nYou now have " + items.size() + " items in the list.");
            } else if (input.startsWith("todo ")) {
                String taskName = input.replaceFirst("todo ", "").trim();
                if (taskName.equals("")) {
                    System.out.println("You need a task description!");
                    continue;
                }
                Todo current = new Todo(taskName);
                items.add(current);
                System.out.println("Got it. I've added this task:\n" + current.toString()
                        + "\nYou now have " + items.size() + " items in the list.");
            } else if (input.startsWith("event ")) {
                if (!input.contains("/from ")) {
                    System.out.println("You need a starting date to add this task!");
                    continue;
                } else if (!input.contains("/ to")) {
                    System.out.println("You need an ending date to add this task!");
                }
                String[] parts = input.split("/from ");
                String[] parts2 = parts[1].split("/to ");
                String taskName = parts[0].replaceFirst("event ", "").trim();
                if (taskName.equals("")) {
                    System.out.println("You need a task description!");
                    continue;
                }
                String startDate = parts2[0].trim();
                String endDate = parts2[1].trim();
                Event current = new Event(taskName, startDate, endDate);
                items.add(current);
                System.out.println("Got it. I've added this task:\n" + current.toString()
                        + "\nYou now have " + items.size() + " items in the list.");
            } else {
                System.out.println("Sorry, I am not sure what task this is! Please enter a valid task.");
            }
        }

    }
}*/
