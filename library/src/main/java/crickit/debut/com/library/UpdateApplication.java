package crickit.debut.com.library;

/**
 * Created by android on 20/4/17.
 */

public interface UpdateApplication {
   public void newVersionFound(String latestversion);
   public void noUpdate(String message);
}
