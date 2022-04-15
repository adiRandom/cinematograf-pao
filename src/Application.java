import cli.InputManager;

public class Application {
    public static void main(String[] args) {
        System.out.println(System.getProperty("user.home"));
        InputManager inputManager = new InputManager();
        while(!inputManager.getIsDone()){
            inputManager.getInput();
        }
    }
}
