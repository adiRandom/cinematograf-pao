import cli.InputManager;

public class Application {
    public static void main(String[] args) {
        InputManager inputManager = new InputManager();
        while(!inputManager.getIsDone()){
            inputManager.getInput();
        }
    }
}
