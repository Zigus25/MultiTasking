package Logic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class DBLogic {
    Connection connection, linksConnection;
    Statement statement, linkStatement;

    int ready = 0;

    //Connection with database
    public DBLogic() throws ErrorException {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:Fanaliza.db");
            statement = this.connection.createStatement();
            statement.setQueryTimeout(5);
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Races(ID INT NOT NULL,Year STRING NOT NULL,Place STRING NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Results(Position INT NOT NULL,Driver STRING NOT NULL,Num INT NOT NULL,Poi INT NOT NULL,Team STRING NOT NULL,ID INT NOT NULL,Year INT NOT NULL,Constructor String,EngineSupplier String)");

            this.linksConnection = DriverManager.getConnection("jdbc:sqlite:Links.db");
            linkStatement = this.linksConnection.createStatement();
            linkStatement.executeUpdate("CREATE TABLE IF NOT EXISTS Links(Link String,StateOf int)");
        } catch (SQLException e) {
            throw new ErrorException("Data base error: " + e.getMessage());
        }
    }

    //Passing connection
    public StatisticAna passConnectionS() {
        return new StatisticAna(this.connection);
    }
    public Logic passConnectionL() {
        return new Logic(this.connection, this.linksConnection);
    }

    //Getting list of links
    public void Links() throws SQLException, IOException {
        if (ready == 0) {
            for (int i = 0; i < 73; i++) {
                var rok = 1950 + i;
                Document t = Jsoup.connect("https://www.formula1.com/en/results/jcr:content/resultsarchive.html/" + rok + "/races.html").get();
                Elements races = t.select(".resultsarchive-table a");
                for (Element race : races) {
                    var link = race.absUrl("href");
                    ResultSet res = linkStatement.executeQuery("SELECT COUNT(*) AS iNull FROM Links WHERE Link = '" + link + "'");
                    if (res.getInt("iNull") == 0) {
                        PreparedStatement links = this.linksConnection.prepareStatement("INSERT INTO Links(Link,StateOf) VALUES (?,?)");
                        links.setString(1, link);
                        links.setInt(2, 0);
                        links.executeUpdate();
                    }
                }
            }
            ready = 1;
        }
    }

    //Reading data from website
    public void GetData(String link) throws IOException {
        String[] SplitLink = link.split("/");
        Elements[] DataFromWebsite = new Elements[6];
        Document doc = Jsoup.connect(link).get();
        DataFromWebsite[0] = doc.select(".resultsarchive-table tbody td:nth-child(2)");//pos
        DataFromWebsite[1] = doc.select(".resultsarchive-table tbody td:nth-child(3)");//no
        DataFromWebsite[2] = doc.select(".resultsarchive-table tbody td:nth-child(8)");//poi
        DataFromWebsite[3] = doc.select(".resultsarchive-table tbody td:nth-child(4) span:nth-child(1)");//d1
        DataFromWebsite[4] = doc.select(".resultsarchive-table tbody td:nth-child(4) span:nth-child(2)");//d2
        DataFromWebsite[5] = doc.select(".resultsarchive-table tbody td:nth-child(5)");//tea
        AddData(DataFromWebsite, SplitLink);
    }

    //Adding data to DB
    public void AddData(Elements[] DataSource, String[] SplitLink) {
        try (
                PreparedStatement RacersToDB = this.connection.prepareStatement("INSERT INTO Results (Position,Driver,Num,Poi,Team,ID,Year) VALUES (?,?,?,?,?,?,?)");
        ) {
            for (int i = 0; i < Objects.requireNonNull(DataSource[0]).size(); i++) {
                RacersToDB.setString(1, DataSource[0].get(i).text());
                RacersToDB.setString(2, DataSource[3].get(i).text() + " " + DataSource[4].get(i).text());
                RacersToDB.setString(3, DataSource[1].get(i).text());
                RacersToDB.setString(4, DataSource[2].get(i).text());
                RacersToDB.setString(5, DataSource[5].get(i).text());
                RacersToDB.setString(6, SplitLink[7]);
                RacersToDB.setString(7, SplitLink[5]);
                RacersToDB.addBatch();
            }
            RacersToDB.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (
                PreparedStatement RaceToDB = this.connection.prepareStatement("INSERT INTO Races (ID,Year,Place) VALUES (?,?,?)");
        ) {

            RaceToDB.setString(1, SplitLink[7]);
            RaceToDB.setString(2, SplitLink[5]);
            RaceToDB.setString(3, SplitLink[8]);
            RaceToDB.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Getting link from DB
    public String GetLink(int whi) throws SQLException {
        String query = "SELECT Link FROM Links WHERE StateOf = 0";
        String link = linkStatement.executeQuery(query).getString("Link");
        linkStatement.executeUpdate("UPDATE Links SET StateOf = 1 WHERE Link = '"+link+"'");
        return link;
    }

    //Closing connection with database
    public void close() throws SQLException {
        this.connection.close();
    }
}