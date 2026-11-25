package controller;
import java.util.ArrayList;
import java.time.LocalDateTime;
import model.Task;

public class TaskManager{
  private ArrayList<Task> taskList;
  
  public TaskManager(){
    taskList = new ArrayList<Task>();
  }
  
  public ArrayList<Task>getAllTasks(){
    return this.taskList;
  }

  public void addTask(String title, LocalDateTime deadline){
    taskList.add(new Task(title, deadline));
  }
 
  public Task findTask(String id){
    for(Task task : taskList){
      if(task.getId().equals(id)){
      return task;
      }
    }
    return null;
  }
  public void taskDone(String id){
    Task targetTask = findTask(id);
    if(targetTask != null) {
      targetTask.setIsDone(true);
    }
  }

  public void renameTask(String id, String title){
    Task targetTask = findTask(id);
    if(targetTask != null) {
      targetTask.setTitle(title);
    }
  }

  public void changeDeadline(String id, LocalDateTime deadline){
    Task targetTask = findTask(id);
    if(targetTask != null) {
      targetTask.setDeadline(deadline);
    }
  }

  public void removeTask(String id){
    Task targetTask = findTask(id);
    if(targetTask != null) {
      taskList.remove(targetTask);
    }
  }
}
