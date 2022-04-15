import cli.InputManager;
import lib.scheduling.SchedulingManager;
import repository.MovieRepository;
import repository.RoomViewRepository;
import services.IdService;

// TODO: Fix static variable preservation
public class Application {
    public static void main(String[] args) {
        IdService idService = IdService.getInstance();
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
        idService.saveToDisk();
    }
}
