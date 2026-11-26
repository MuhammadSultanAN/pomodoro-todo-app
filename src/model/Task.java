package model;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
public class Task implements Serializable{
  private static final long serialVersionUID = 1L;
  private String id;
  private String title;
  private String description;
  private boolean isDone = false;
  private LocalDateTime deadline;
 
  // Constructor
  public Task(String title, LocalDateTime deadline){
    this(title, "", deadline);
  }

  public Task(String title, String description, LocalDateTime deadline){
    this.id = UUID.randomUUID().toString();
    this.title = title;
    this.description = description;
    this.deadline = deadline;
  }

  // Getter
  public String getId(){
    return id;
  }

  public String getTitle(){
    return title;
  }

  // Tidak pernah null (data lama yang belum punya deskripsi dianggap kosong)
  public String getDescription(){
    return description == null ? "" : description;
  }

  public LocalDateTime getDeadline(){
    return deadline;
  }

  public boolean getIsDone(){
    return isDone;
  }

  // Setter
  public void setTitle(String title){
    this.title = title;
  }

  public void setDescription(String description){
    this.description = description;
  }

  public void setDeadline(LocalDateTime deadline){
    this.deadline = deadline;
  }

  public void setIsDone(boolean isDone){
    this.isDone = isDone;
  }
}
