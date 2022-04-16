package cli.handlers;


import utils.Pair;

import javax.naming.OperationNotSupportedException;
import java.util.Date;

public interface CommandHandler {
    void handleCommand() throws OperationNotSupportedException;
}
