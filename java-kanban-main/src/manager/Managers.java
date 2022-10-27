package manager;

import java.net.URI;
import java.net.URISyntaxException;

public class Managers {
    public static TaskManager getDefault(URI url) throws URISyntaxException {
        return new HttpTaskManager(url);
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
