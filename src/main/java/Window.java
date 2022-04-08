import Logic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class Window {

    DBLogic dbLogic = new DBLogic();
    Logic logic = dbLogic.passConnectionL();
    StatisticAna statistic = dbLogic.passConnectionS();

    JFrame frame;

    public Window() throws ErrorException, SQLException{


        this.frame = new JFrame("Fanaliza");
        this.frame.setSize(800,600);
        this.frame.setLayout(new BorderLayout());


        //Tables for JList
        Integer[] YearsTable = new Integer[this.logic.sizes(2)];
        for (int y = 0; y < this.logic.sizes(2); y++) {
            YearsTable[y]= 1950+y;
        }
        JList<Integer> Years = new JList<>(YearsTable);

        ResultSet DriversRes = this.logic.resForCombo(1);
        String[] DriverTable = new String[this.logic.sizes(3)];
        int d=0;
        while (DriversRes.next()){
            DriverTable[d]=DriversRes.getString("Driver");
            d++;
        }
        JList<String> Drivers = new JList<>(DriverTable);

        ResultSet TeamsRes = this.logic.resForCombo(2);
        String[] TeamsTable = new String[this.logic.sizes(4)];
        int t = 0;
        while (TeamsRes.next()){
            TeamsTable[t]=TeamsRes.getString("Constructor");
            t++;
        }
        JList<String> Teams = new JList<>(TeamsTable);

        ResultSet ESupplierRes = this.logic.resForCombo(3);
        String[] ESupplierTable = new String[this.logic.sizes(5)];
        int eS=0;
        while (ESupplierRes.next()){
            ESupplierTable[eS]=ESupplierRes.getString("EngineSupplier");
            eS++;
        }
        JList<String> ESuppliers = new JList<>(ESupplierTable);



        JPanel MainPanel = new JPanel(new BorderLayout());
        JPanel statsPanel = new JPanel();
        JLabel statsResults = new JLabel();

        JScrollPane stats = new JScrollPane(statsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel optionPanel = new JPanel(new GridLayout(1,5));
        JLabel blank = new JLabel();
        JButton show = new JButton("Show");
        AtomicInteger shoWhi = new AtomicInteger();
        show.addActionListener(e->{
            System.out.println(shoWhi);
            switch(shoWhi.get()){
                case 1, 2 -> {
                    var year = Years.getSelectedValue();
                    try {
                        statsResults.setText(convertForLabel(shoWhi.get(),"",year,0));
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            statsPanel.add(statsResults);
            MainPanel.add(stats,BorderLayout.CENTER);
            MainPanel.repaint();
            MainPanel.revalidate();
        });



//        optionPanel.add(new JScrollPane(Years));
//        optionPanel.add(new JScrollPane(Drivers));
//        optionPanel.add(new JScrollPane(Teams));
//        optionPanel.add(new JScrollPane(ESuppliers));
//        optionPanel.add(show);



        JPanel sideButtonPanel = new JPanel(new GridLayout(50,1));

        //Hear add buttons
        JButton yearD = new JButton("Drivers Championship");
        yearD.addActionListener(e->{
            shoWhi.set(1);
            MainPanel.removeAll();
            optionPanel.removeAll();
            Years.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            optionPanel.add(new JScrollPane(Years));
            for (int i = 0; i < 3; i++)
                optionPanel.add(blank);
            optionPanel.add(show);
            MainPanel.add(optionPanel,BorderLayout.NORTH);
            MainPanel.repaint();
            MainPanel.revalidate();
            optionPanel.revalidate();
            optionPanel.repaint();
        });
        sideButtonPanel.add(yearD);

        JButton yearT = new JButton("Team Championship");
        yearT.addActionListener(e->{
            shoWhi.set(2);
            MainPanel.removeAll();
            optionPanel.removeAll();
            Years.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            optionPanel.add(new JScrollPane(Years));
            for (int i = 0; i < 3; i++)
                optionPanel.add(blank);
            optionPanel.add(show);
            MainPanel.add(optionPanel,BorderLayout.NORTH);
            MainPanel.repaint();
            MainPanel.revalidate();
            optionPanel.revalidate();
            optionPanel.repaint();
        });
        sideButtonPanel.add(yearT);


        JScrollPane navButton = new JScrollPane(sideButtonPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        frame.add(MainPanel,BorderLayout.CENTER);
        frame.add(navButton,BorderLayout.WEST);

        frame.setVisible(true);
        this.frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    dbLogic.close();
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
                System.exit(0);

            }
        });
    }
    public String convertForLabel(int whi, String DriverTeam, int fromYear, int toYear) throws SQLException {
        StringBuilder res;
        switch(whi){
            case 1 -> {
                ResultSet result = statistic.Driver(1,"",fromYear,0);
                res = new StringBuilder("<html><table><tr><th>Number</th><th>Rider</th><th>Points</th><th>Team</th></tr>");
                while (result.next()) {
                    res.append("<tr><th>").append(result.getString("Num")).append("</th><th>").append(result.getString("Driver")).append("</th><th>").append(result.getString("Total")).append("</th><th>").append(result.getString("Team")).append("</th></tr>");
                }
                res.append("</table></html>");
            }
            case 2 -> {
                ResultSet result = statistic.Constructor(1,"",fromYear,0);
                res = new StringBuilder("<html><table><tr><th>Team</th><th>Points</th></tr>");
                while (result.next()) {
                    res.append("<tr><th>").append(result.getString("Team")).append("</th><th>").append(result.getString("Pkt")).append("</th></tr>");
                }
                res.append("</table></html>");
            }
            default -> throw new IllegalStateException("Unexpected value: " + whi);
        }
        return  res.toString();
    }
}
