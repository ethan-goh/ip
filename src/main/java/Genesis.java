import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
class Task {
    protected String description;
    protected boolean isComplete;

    public Task (String description) {
        this.description = description;
        this.isComplete = false;
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
    public Todo (String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}

class Deadline extends Task {
    protected String deadline;
    public Deadline (String description, String deadline) {
        super(description);
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.deadline + ")";
    }
}

class Event extends Task {
    protected String startTime;
    protected String endTime;
    public Event (String description, String startTime, String endTime) {
        super(description);
        this.startTime = startTime;
        this.endTime = endTime;
    }
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.startTime + " to: " + this.endTime + ")";
    }
}
public class Genesis {

    public static void main(String[] args) {

        /*String logo = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";*/
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
}
