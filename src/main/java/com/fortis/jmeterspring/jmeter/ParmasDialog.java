package com.fortis.jmeterspring.jmeter;

import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ParmasDialog extends JDialog implements ActionListener {

  private JButton okBtn ;
  private JButton cancelBtn;
  private JComboBox box;
  private JTextArea parmasText;

  private List<String> params;

  private int windowWidth;
  private int windowHeight;

  private ParmasDialogListener delegate;

  public ParmasDialog(JFrame parent, boolean modal, List<String> paramsList, int winWidth, int winHeight){
    super(parent,modal);
    params = paramsList;
    windowHeight= winHeight;
    windowWidth = winWidth;

    init();
  }

  private void init(){
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //得到屏幕的尺寸
    int screenWidth = screenSize.width;
    int screenHeight = screenSize.height;
    setTitle("test");
    setSize(windowWidth,windowHeight);
    setLayout(new BorderLayout());
    setResizable(false);
    setLocation((screenWidth - this.getWidth()) / 2, (screenHeight - this.getHeight())/2);

    JPanel settings = new VerticalPanel();

    JPanel parmPanel = new HorizontalPanel();
    box = new JComboBox<String>();
    box.setEditable(true);
    box.setLightWeightPopupEnabled(false);
    for (String item : params)
      box.addItem(item);
    JLabel boxLabl = new JLabel("参数类型：");
//    boxLabl.setLabelFor(box);
    parmPanel.add(boxLabl);
    parmPanel.add(box);
    settings.add(parmPanel);

    JPanel parmContentPanel = new HorizontalPanel();
    parmContentPanel.setPreferredSize(new Dimension(windowWidth,windowHeight/2));
    JLabel parmasTextLabl = new JLabel("参数内容：");
    parmasText = new JTextArea();
    JScrollPane jScrollPane = new JScrollPane(parmasText);

//    parmasTextLabl.setLabelFor(parmasText);
    parmContentPanel.add(parmasTextLabl);
    parmContentPanel.add(jScrollPane);
    settings.add(parmContentPanel);

    JPanel btnPanel = new HorizontalPanel();
    okBtn = new JButton("确定");
    cancelBtn = new JButton("取消");
    okBtn.setName("ok");
    cancelBtn.setName("cancel");
    okBtn.addActionListener(this);
    cancelBtn.addActionListener(this);
    btnPanel.add(okBtn);
    btnPanel.add(cancelBtn);
    settings.add(btnPanel);

    add(settings);

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Component[] cmp = getContentPane().getComponents();
    JButton btn = (JButton)e.getSource();
    String jtfKey = ""+btn.getName().replaceAll("btn", "");
    if(null != jtfKey && !jtfKey.equals("ok") && !jtfKey.equals("cancel") ){
      String btnText = btn.getText();
      for (Component c : cmp) {
        if(c instanceof JTextField){
          JTextField tf = (JTextField) c;
          if(c.getName().equals("jtf"+jtfKey)){

            int val = Integer.parseInt(tf.getText());
            if(btnText.equals("+")){
              if(val < 999){
                val = val + 1;
              }
            }else{
              val = val - 1;
              if(val <= 0){
                val = 1;
              }
            }
            tf.setText(String.valueOf(val));
          }
        }
      }
      return;
    }

    if(e.getSource()==okBtn){
      System.out.println("OK");
     if (delegate != null){
       delegate.tapOK(this.box.getSelectedItem().toString(),this.parmasText.getText());
     }
      dispose();
      return;
    }
    if(e.getSource()==cancelBtn){
      if (delegate != null){
        delegate.tapCancel();
      }
      dispose();
      return;
    }
  }

  public ParmasDialogListener getParmasDelegate() {
    return delegate;
  }

  public void setParmasDelegate(ParmasDialogListener delegate) {
    this.delegate = delegate;
  }
}
