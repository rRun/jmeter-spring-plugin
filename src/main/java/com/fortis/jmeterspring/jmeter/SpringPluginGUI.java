package com.fortis.jmeterspring.jmeter;

import com.fortis.jmeterspring.spring.SpringMethodParams;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;

public class SpringPluginGUI extends AbstractSamplerGui implements ParmasDialogListener {

  private static final long serialVersionUID = 12312213;

  private static final Logger logger = LoggerFactory.getLogger(SpringPluginGUI.class);

  private Container titleGUI;
  private Container settingsGUI;
  private Container requestGUI;

  private JTextField interfaceText;//接口全称
  private JTextField methodText;//方法全称
  private JTable paramsTable;
  private DefaultTableModel paramsTableModel;//参数model
  private static String[] columnNames = {"参数类型","参数内容"};

  private JList<String> fileList;
  private DefaultListModel<String> filesListModel;//参数model
  private boolean hasPopMenu;

  public SpringPluginGUI(){
    super();
    this.init();
  }

  /**
   * 初始化界面
   */
  private void init(){
    logger.info("创建dubbo 插件");
    //设置布局
    this.setLayout(new VFlowLayout());
    this.setBorder(this.makeBorder());

    //创建页面
    this.titleGUI = createTitlePanel();
    this.settingsGUI = createSpringXmlSettings();
    this.requestGUI = createRequestPanel();

    //部署页面
    this.add(this.titleGUI);
    this.add(this.settingsGUI);
    this.add(this.requestGUI);
  }

  /**
   * 创建标题模块
   * @return 标题模块
   */
  private Container createTitlePanel(){
    return makeTitlePanel();
  }

  /**
   * 通过spring xml配置
   * @return spring配置面板
   */
  private JPanel createSpringXmlSettings(){
    JPanel dubboSettingsPanel = new VerticalPanel();
    dubboSettingsPanel.setBorder(BorderFactory.createTitledBorder("xml配置"));

    //表格panel
    //xml地址列表
    filesListModel = new DefaultListModel<String>();
    fileList = new JList<String>(filesListModel);

    //添加按钮
    JButton addBtn = new JButton("选择文件");
    addBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        saveFile();
      }
    });

    JButton delBtn = new JButton("删除文件");
    delBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int rowIndex = fileList.getSelectedIndex();
        if(rowIndex != -1)
          filesListModel.remove(rowIndex);
      }
    });

    JScrollPane scrollpane = new JScrollPane(fileList);
    scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollpane.setPreferredSize(new Dimension(300,80));
    dubboSettingsPanel.add(scrollpane);
    dubboSettingsPanel.add(addBtn);
    dubboSettingsPanel.add(delBtn);

    return dubboSettingsPanel;
  }

  public void saveFile() {
    JFileChooser jf = new JFileChooser();
    jf.showOpenDialog(this);//显示打开的文件对话框

    File f =  jf.getSelectedFile();//使用文件类获取选择器选择的文件
    if (f.isFile())
      filesListModel.addElement("file://"+f.getAbsolutePath());
  }
  /**
   * 创建请求模块
   * @return 请求模块
   */
  private JPanel createRequestPanel(){
    JPanel interfaceSettings = new VerticalPanel();
    interfaceSettings.setBorder(BorderFactory.createTitledBorder("Request 配置"));

    //接口配置
    JPanel interfacePanel = new HorizontalPanel();
    JLabel interfaceLable = new JLabel("接口类:", SwingConstants.RIGHT);
    interfaceText = new JTextField(10);
    interfaceLable.setLabelFor(interfaceText);
    interfacePanel.add(interfaceLable);
    interfacePanel.add(interfaceText);
    interfaceSettings.add(interfacePanel);

    //方法配置
    JPanel methodPanel = new HorizontalPanel();
    JLabel methodLable = new JLabel("方法:", SwingConstants.RIGHT);
    methodText = new JTextField(10);
    methodLable.setLabelFor(methodText);
    methodPanel.add(methodLable);
    methodPanel.add(methodText);
    interfaceSettings.add(methodPanel);

    //表格panel
    JPanel tablePanel = new HorizontalPanel();
    //参数配置
    paramsTableModel = new DefaultTableModel();
    paramsTableModel.setDataVector(null, columnNames);
    paramsTable = new JTable(paramsTableModel);
    paramsTable.setPreferredSize(new Dimension(300,160));

    paramsTable.setRowHeight(30);

//    setUpSportColumn(paramsTable.getColumnModel().getColumn(0));

    //添加按钮
    JButton addBtn = new JButton("增加参数");
    final SpringPluginGUI tempThis = this;
    addBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub

//        paramsTableModel.addRow(tmpRow);

        ArrayList<String> lists = new ArrayList<>();

        lists.add("byte");
        lists.add("short");
        lists.add("int");
        lists.add("long");
        lists.add("float");
        lists.add("double");
        lists.add("boolean");
        lists.add("char");
        lists.add("String");
        lists.add("Map");
        lists.add("List");

        ParmasDialog d =new ParmasDialog(null, true,lists,360,320);
        d.setParmasDelegate(tempThis);
        d.setVisible(true);
      }


    });

    JButton delBtn = new JButton("删除参数");
    delBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
        int rowIndex = paramsTable.getSelectedRow();
        if(rowIndex != -1)
          paramsTableModel.removeRow(rowIndex);
      }
    });

    //表格滚动条
    JScrollPane scrollpane = JTable.createScrollPaneForTable(paramsTable);
    scrollpane.setPreferredSize(new Dimension(300,160));

    tablePanel.add(scrollpane);
    tablePanel.add(addBtn);
    tablePanel.add(delBtn);
    interfaceSettings.add(tablePanel);


    return interfaceSettings;
  }


  public void setUpSportColumn(TableColumn sportColumn) {

    JComboBox<String> box = new JComboBox<String>();
    box.setEditable(true);
    box.setLightWeightPopupEnabled(false);
    box.addItem("byte");
    box.addItem("short");
    box.addItem("int");
    box.addItem("long");
    box.addItem("float");
    box.addItem("double");
    box.addItem("boolean");
    box.addItem("char");
    box.addItem("String");
    box.addItem("Map");
    box.addItem("List");
    sportColumn.setCellEditor(new DefaultCellEditor(box));
    final JPopupMenu jPopupMenu = new JPopupMenu("选择属性");
    jPopupMenu.add("1");
    jPopupMenu.add("2");
    sportColumn.getCellEditor().addCellEditorListener(new CellEditorListener() {
      @Override
      public void editingStopped(ChangeEvent e) {
        hasPopMenu = !hasPopMenu;
        if (hasPopMenu){
          jPopupMenu.show(paramsTable,5,5);
        } else{

        }
      }

      @Override
      public void editingCanceled(ChangeEvent e) {
        System.out.println("editingCanceled");
      }
    });
  }
  /**
   * 返回标题
   * @return 标题
   */
  @Override
  public String getStaticLabel() {
    return "spring request";
  }
  @Override
  public String getLabelResource() {
    return this.getClass().getSimpleName();
//    return "spring request";
  }

  /**
   * 这个方法将Smapler的数据设置到gui中
   */
  @Override
  public void configure(TestElement element) {
    super.configure(element);
    logger.info("sample赋值给gui");
    SpringPluginSampler sample = (SpringPluginSampler) element;

    interfaceText.setText(sample.getSinterface());
    methodText.setText(sample.getSmethod());
    Vector<String> columnNames = new Vector<String>();
    columnNames.add("参数类型");
    columnNames.add("参数内容");
    paramsTableModel.setDataVector(paserMethodArgsData(sample.getSmethodParams()), columnNames);

    if (sample.getSpring_files()!=null && sample.getSpring_files().size()>0){
      filesListModel.clear();
      for (String file : sample.getSpring_files()){
        filesListModel.addElement(file);
      }
    }
  }

  /**
   * 创建新的sampler。并且将它传给你创建的modifyTestElement(TestElement)方法。
   * @return 返回测试样本对象
   */
  @Override
  public TestElement createTestElement() {
    logger.info("创建dubbo测试样本 "+Thread.currentThread().getId());
    //创建sample对象
    SpringPluginSampler sample = new SpringPluginSampler();
    modifyTestElement(sample);
    return sample;
  }

  /**
   *  这个方法是用来将数据从你的gui传到TestElement
   *  给sample赋值
   * @param element 测试样本对象
   */
  @Override
  public void modifyTestElement(TestElement element) {
    logger.info("为dubbo测试样本赋值"+ Thread.currentThread().getId());
    SpringPluginSampler sample = (SpringPluginSampler) element;
    sample.clear();
    configureTestElement(sample);
    List<String> springFiles = new ArrayList<String>();

    for (int i = 0;i<filesListModel.getSize();i++){
      String file = filesListModel.get(i);
      if (!file.isEmpty()) {
        springFiles.add(file);
      }
    }
    if (springFiles.size()>0)
      sample.setSpring_files(springFiles);

    sample.setSinterface(interfaceText.getText());
    sample.setSmethod(methodText.getText());

    List<SpringMethodParams> params = new ArrayList<SpringMethodParams>();
    if (!paramsTableModel.getDataVector().isEmpty()) {
      //处理参数
      Iterator<Vector<String>> it = paramsTableModel.getDataVector().iterator();
      while(it.hasNext()) {
        Vector<String> param = it.next();
        if (!param.isEmpty()) {
          params.add(new SpringMethodParams(param.get(0), param.get(1)));
        }
      }
    }
    sample.setSmethodParams(params);
  }


  private Vector<Vector<String>> paserMethodArgsData(List<SpringMethodParams> list) {
    Vector<Vector<String>> res = new Vector<Vector<String>>();
    for (SpringMethodParams params : list) {
      Vector<String> v = new Vector<String>();
      v.add(params.getParameterType());
      v.add(params.getParameterValue());
      res.add(v);
    }
    return res;
  }

  @Override
  public void tapOK(String paramType, String paramValue) {
    logger.info("input params:" + paramType,paramValue);
    String[] tmpRow = {paramType,paramValue};
    paramsTableModel.addRow(tmpRow);
  }

  @Override
  public void tapCancel() {

  }


  /**
   * 垂直布局
   */
  private class VFlowLayout extends FlowLayout{
    /**
     * 指定顶部间隔的距离
     */
    public static final int TOP = 0;

    /**
     * 指定中间间隔的距离
     */
    public static final int MIDDLE = 1;

    /**
     * 指定底部间隔的距离
     */
    public static final int BOTTOM = 2;

    int hgap;
    int vgap;
    boolean hfill;
    boolean vfill;

    public VFlowLayout()
    {
      this(TOP, 5, 5, true, false);
    }

    public VFlowLayout(boolean hfill, boolean vfill)
    {
      this(TOP, 5, 5, hfill, vfill);
    }

    public VFlowLayout(int align)
    {
      this(align, 5, 5, true, false);
    }

    public VFlowLayout(int align, boolean hfill, boolean vfill)
    {
      this(align, 5, 5, hfill, vfill);
    }

    public VFlowLayout(int align, int hgap, int vgap, boolean hfill, boolean vfill)
    {
      setAlignment(align);
      this.hgap = hgap;
      this.vgap = vgap;
      this.hfill = hfill;
      this.vfill = vfill;
    }

    public Dimension preferredLayoutSize(Container target)
    {
      Dimension tarsiz = new Dimension(0, 0);

      for (int i = 0; i < target.getComponentCount(); i++)
      {
        Component m = target.getComponent(i);

        if (m.isVisible())
        {
          Dimension d = m.getPreferredSize();
          tarsiz.width = Math.max(tarsiz.width, d.width);

          if (i > 0)
          {
            tarsiz.height += vgap;
          }

          tarsiz.height += d.height;
        }
      }

      Insets insets = target.getInsets();
      tarsiz.width += insets.left + insets.right + hgap * 2;
      tarsiz.height += insets.top + insets.bottom + vgap * 2;

      return tarsiz;
    }

    public Dimension minimumLayoutSize(Container target)
    {
      Dimension tarsiz = new Dimension(0, 0);

      for (int i = 0; i < target.getComponentCount(); i++)
      {
        Component m = target.getComponent(i);

        if (m.isVisible())
        {
          Dimension d = m.getMinimumSize();
          tarsiz.width = Math.max(tarsiz.width, d.width);

          if (i > 0)
          {
            tarsiz.height += vgap;
          }

          tarsiz.height += d.height;
        }
      }

      Insets insets = target.getInsets();
      tarsiz.width += insets.left + insets.right + hgap * 2;
      tarsiz.height += insets.top + insets.bottom + vgap * 2;

      return tarsiz;
    }

    public void setVerticalFill(boolean vfill)
    {
      this.vfill = vfill;
    }

    public boolean getVerticalFill()
    {
      return vfill;
    }


    public void setHorizontalFill(boolean hfill)
    {
      this.hfill = hfill;
    }

    public boolean getHorizontalFill()
    {
      return hfill;
    }

    private void placethem(Container target, int x, int y, int width, int height, int first, int last)
    {
      int align = getAlignment();

      if (align == MIDDLE)
      {
        y += height / 2;
      }

      if (align == BOTTOM)
      {
        y += height;
      }

      for (int i = first; i < last; i++)
      {
        Component m = target.getComponent(i);
        Dimension md = m.getSize();

        if (m.isVisible())
        {
          int px = x + (width - md.width) / 2;
          m.setLocation(px, y);
          y += vgap + md.height;
        }
      }
    }

    public void layoutContainer(Container target)
    {
      Insets insets = target.getInsets();
      int maxheight = target.getSize().height - (insets.top + insets.bottom + vgap * 2);
      int maxwidth = target.getSize().width - (insets.left + insets.right + hgap * 2);
      int numcomp = target.getComponentCount();
      int x = insets.left + hgap, y = 0;
      int colw = 0, start = 0;

      for (int i = 0; i < numcomp; i++)
      {
        Component m = target.getComponent(i);

        if (m.isVisible())
        {
          Dimension d = m.getPreferredSize();

          // fit last component to remaining height
          if ((this.vfill) && (i == (numcomp - 1)))
          {
            d.height = Math.max((maxheight - y), m.getPreferredSize().height);
          }

          // fit component size to container width
          if (this.hfill)
          {
            m.setSize(maxwidth, d.height);
            d.width = maxwidth;
          }
          else
          {
            m.setSize(d.width, d.height);
          }

          if (y + d.height > maxheight)
          {
            placethem(target, x, insets.top + vgap, colw, maxheight - y, start, i);
            y = d.height;
            x += hgap + colw;
            colw = d.width;
            start = i;
          }
          else
          {
            if (y > 0)
            {
              y += vgap;
            }

            y += d.height;
            colw = Math.max(colw, d.width);
          }
        }
      }

      placethem(target, x, insets.top + vgap, colw, maxheight - y, start, numcomp);
    }
  }


  public static void main(String[] args){
    SpringPluginGUI dubboPluginGUI = new SpringPluginGUI();

    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(800,800));
    frame.add(dubboPluginGUI);
    frame.pack();
    frame.setVisible(true);
  }
}
