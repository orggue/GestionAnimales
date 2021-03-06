package org.jorgechato.fightclub;

import com.toedter.calendar.JDateChooser;
import org.jorgechato.fightclub.base.Boxer;
import org.jorgechato.fightclub.base.Fight;
import org.jorgechato.fightclub.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by jorge on 10/02/15.
 */
public class FightIssue  extends JDialog implements ActionListener {
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JButton aceptarButton;
    private JButton cancelarButton;
    private JPanel panel1;
    private JDateChooser dateInnauguration;
    private JList list1;
    private Window window;
    private String nameFight;
    private Fight query;
    private DefaultListModel<Boxer> model;

    public FightIssue(Window window,String nameFight) {
        dateInnauguration.setPreferredSize(new Dimension(250, 29));
        setContentPane(panel1);
        pack();
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setModal(true);
        setLocationRelativeTo(null);

        this.window = window;
        this.nameFight = nameFight;

        model = new DefaultListModel<Boxer>();
        list1.setModel(model);

        init();
    }

    private void init() {
        cancelarButton.addActionListener(this);
        aceptarButton.addActionListener(this);

        model.clear();
        for (Boxer boxer : window.getListBoxer()) {
            model.addElement(boxer);
        }

        if (nameFight != null){
            query = window.searchFightByName(nameFight).get(0);
            textField1.setText(query.getName());
            textField2.setText(query.getStreet());
            dateInnauguration.setDate(new Date(query.getDay().getTime()));

            ArrayList<Integer> array = new ArrayList();

            for (int i = 0 ; i <  model.size() ; i++){
                for (int j = 0 ; j < query.getBoxers().size(); j++){
                    if (query.getBoxers().get(j).equals(model.get(i))){
                        array.add(i);
                    }
                }
            }

            int vector [] = new int[array.size()];
            for (int i = 0 ; i < array.size() ; i++){
                vector[i] = array.get(i);
            }

            list1.setSelectedIndices(vector);
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == cancelarButton){
            setVisible(false);
            return;
        }
        if(actionEvent.getSource() == aceptarButton){
            if (nameFight == null){
                Fight fight = new Fight();
                fight.setName(textField1.getText());
                fight.setStreet(textField2.getText());
                fight.setDay(new Date(dateInnauguration.getDate().getTime()));

                fight.setBoxers(list1.getSelectedValuesList());

                Util.db.store(fight);
                Util.db.commit();
            }else {
                query.setName(textField1.getText());
                query.setStreet(textField2.getText());
                query.setDay(new Date(dateInnauguration.getDate().getTime()));

                query.setBoxers(list1.getSelectedValuesList());

                Util.db.store(query);
                Util.db.commit();
            }
            window.reloadFightTable(Util.db.query(Fight.class));
            setVisible(false);
            return;
        }
    }
}
