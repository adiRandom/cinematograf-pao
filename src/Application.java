import cli.InputManager;
import lib.scheduling.SchedulingManager;
import repository.MovieRepository;
import repository.RoomViewRepository;
import utils.SerializeUtils;

public class Application {
    public static void main(String[] args) {
        RoomViewRepository roomViewRepository = RoomViewRepository.getInstance();
        MovieRepository movieRepository = MovieRepository.getInstance();
        SchedulingManager schedulingManager = SchedulingManager.getInstance();
        InputManager inputManager = new InputManager();

        while (!inputManager.getIsDone()) {
            inputManager.getInput();
        }

        roomViewRepository.saveToDisk();
        movieRepository.saveToDisk();
        schedulingManager.saveToDisk();
    }
}
