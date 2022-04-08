package Logic;

import java.io.IOException;
import java.sql.SQLException;

public class UpdateRaces {
    public UpdateRaces() throws ErrorException, SQLException, IOException, InterruptedException{
        DBLogic con = new DBLogic();
        Logic logic = con.passConnectionL();
        con.Links();
        for (int i = 1; i <= logic.sizes(1); i++) {
            MultiTask multiTask = new MultiTask();
            multiTask.starting(i);
            Thread.sleep(200);
        }
        con.close();
    }
}
