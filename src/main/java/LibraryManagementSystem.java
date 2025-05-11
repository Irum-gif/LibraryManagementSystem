import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import static java.lang.System.err;

public class LibraryManagementSystem extends JFrame {

    private JTextField readerIdField, firstNameField, lastNameField, addressField, phoneNumberField, readerLimitField, isbnField, titleField, authorsField, publisherField, publicationDateField, typeField;
    private JTextField  isbnField1, titleField1, authorsField1, publisherField1, editionNumberField1, publicationDateField1, typeField1;
    private JComboBox<String> sortByComboBox;
    private JTable booksTable,booksManageTable,readersTable;
    private DefaultTableModel tableModel;
    private DefaultTableModel recordsTableModel;
    private DefaultTableModel readersTableModel;
    private Connection connection;

    private final JPanel mainPanel;
    private JPanel bookSearchAndBorrowPanel;
    private JPanel bookManagementPanel;
    private JPanel readerManagementPanel;

    //配置文件
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;
    //配置文件静态代码块
    static {
        //读取外界的配置文件
        InputStream asStream= LibraryManagementSystem.class.getResourceAsStream("/account.properties");
        //解析流
        Properties properties=new Properties();
        try {
            //读取流
            properties.load(asStream);
            //获取配置文件中的值
            URL=properties.getProperty("url");
            USER=properties.getProperty("user");
            PASSWORD=properties.getProperty("password");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LibraryManagementSystem() {
        // 设置窗口标题、大小、关闭操作和位置
        setTitle("简易图书管理系统");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 尝试连接到MySQL数据库
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD); // 建立数据库连接
        } catch (SQLException e) {
            e.printStackTrace(err);
            System.exit(1); // 终止程序
        }

        // 创建主面板并设置布局管理器
        mainPanel = new JPanel(new BorderLayout());

        // 创建标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("图书管理系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 38));
        titlePanel.add(titleLabel);

        // 添加标题面板到主面板的北部区域
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 20)); // 一行三列，间距为20像素
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // 上下左右各50像素的空白边距

        JButton readerButton = new JButton("读者管理");
        JButton searchButton = new JButton("图书搜索与借还");
        JButton manageButton = new JButton("图书管理");

        readerButton.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        searchButton.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        manageButton.setFont(new Font("微软雅黑", Font.PLAIN, 18));

        readerButton.addActionListener(e -> showPanel(readerManagementPanel));
        searchButton.addActionListener(e -> showPanel(bookSearchAndBorrowPanel));
        manageButton.addActionListener(e -> showPanel(bookManagementPanel));

        buttonPanel.add(readerButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(manageButton);

        // 添加按钮面板到主面板的北部区域
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // 创建各个子面板
        readerManagementPanel = createReaderManagementPanel();
        bookManagementPanel = createBookManagementPanel();
        bookSearchAndBorrowPanel = createBooksPanel();


        // 设置主面板为主窗口的内容面板
        setContentPane(mainPanel);

    }

    private void showPanel(JPanel panel) {
        mainPanel.removeAll(); // 清空主面板的所有组件
        mainPanel.setLayout(new BorderLayout()); // 重置布局管理器

        if (panel != mainPanel) {
            // 如果不是主面板，则添加返回按钮
            JButton backButton = new JButton("返回主菜单");
            backButton.setFont(new Font("微软雅黑", Font.PLAIN, 18));
            backButton.addActionListener(e -> showMainPanel());
            mainPanel.add(backButton, BorderLayout.SOUTH);
        }

        mainPanel.add(panel, BorderLayout.CENTER); // 添加新的面板到中部区域
        revalidate(); // 刷新布局
        repaint(); // 重绘组件
    }

    private void showMainPanel() {
        // 显示主面板
        mainPanel.removeAll(); // 清空主面板的所有组件
        mainPanel.setLayout(new BorderLayout()); // 重置布局管理器

        // 创建标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("图书管理系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 38));
        titlePanel.add(titleLabel);

        // 添加标题面板到主面板的北部区域
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 20)); // 一行三列，间距为20像素
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // 上下左右各50像素的空白边距

        JButton readerButton = new JButton("读者管理");
        JButton searchButton = new JButton("图书搜索与借还");
        JButton manageButton = new JButton("图书管理");

        readerButton.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        searchButton.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        manageButton.setFont(new Font("微软雅黑", Font.PLAIN, 18));

        readerButton.addActionListener(e -> showPanel(readerManagementPanel));
        searchButton.addActionListener(e -> showPanel(bookSearchAndBorrowPanel));
        manageButton.addActionListener(e -> showPanel(bookManagementPanel));

        buttonPanel.add(readerButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(manageButton);

        // 添加按钮面板到主面板的中部区域
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        revalidate(); // 刷新布局
        repaint(); // 重绘组件
    }

    //创建书籍面板
    private JPanel createBooksPanel(){
        JPanel panel = new JPanel(new BorderLayout());

        // 创建搜索面板并设置布局管理器、边框和背景颜色
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("搜索条件"));
        searchPanel.setBackground(Color.WHITE);

        // 使用GridBagConstraints来控制组件的布局
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST; // 文本标签左对齐
        gbc.insets = new Insets(5, 5, 5, 5); // 设置组件之间的间距

        // 添加标签和文本框到搜索面板
        addLabelAndField(searchPanel, gbc, "ISBN:", isbnField = new JTextField(20));
        addLabelAndField(searchPanel, gbc, "Title:", titleField = new JTextField());
        addLabelAndField(searchPanel, gbc, "Authors:", authorsField = new JTextField());
        addLabelAndField(searchPanel, gbc, "Publisher:", publisherField = new JTextField());
        addLabelAndField(searchPanel, gbc, "Publication Date:", publicationDateField = new JTextField());
        addLabelAndField(searchPanel, gbc, "Type:", typeField = new JTextField());

        // 添加排序选项
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        searchPanel.add(new JLabel("sort by:"), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        String[] sortOptions = {"ISBN", "Title", "Authors", "Publisher", "PublicationDate", "Type"};
        sortByComboBox = new JComboBox<>(sortOptions);
        searchPanel.add(sortByComboBox, gbc);

        // 添加搜索按钮
        gbc.gridx = 1;
        JButton searchButton = new JButton("搜索");
        searchButton.setToolTipText("Search for books based on criteria."); // 按钮提示信息
        searchButton.addActionListener(this::actionPerformed);
        searchPanel.add(searchButton, gbc);

        // 将搜索面板添加到窗口的北部区域
        panel.add(searchPanel, BorderLayout.NORTH);

        // 创建书籍表格模型和表格，并将其放入滚动窗格中
        String[] columnNames = {"ISBN", "Title", "Authors", "Publisher", "Edition Number", "Publication Date", "Type"};
        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 默认情况下所有单元格都不可编辑
            }
        };
        booksTable = new JTable(tableModel);
        JScrollPane booksScrollPane = new JScrollPane(booksTable);
        panel.add(booksScrollPane, BorderLayout.CENTER);

        // 创建借阅记录表格模型和表格，并将其放入滚动窗格中
        String[] recordsColumnNames = {"Record ID", "ISBN", "Reader ID", "Borrowing Date", "Return Date"};
        recordsTableModel = new DefaultTableModel(recordsColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 默认情况下所有单元格都不可编辑
            }
        };
        JTable recordsTable = new JTable(recordsTableModel);
        JScrollPane recordsScrollPane = new JScrollPane(recordsTable);

        // 使用JSplitPane将书籍目录和借阅记录分开
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, booksScrollPane, recordsScrollPane);
        splitPane.setDividerLocation(0.5); // 初始分隔比例为50%
        splitPane.setResizeWeight(0.5); // 允许用户调整分隔条位置时保持相对比例
        // 将splitPane添加到窗口的中心区域
        panel.add(splitPane, BorderLayout.CENTER);

        // 创建借书和还书面板并设置布局管理器、背景颜色和边框
        JPanel borrowReturnPanel = new JPanel(new FlowLayout());
        borrowReturnPanel.setBackground(Color.LIGHT_GRAY);
        borrowReturnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 添加借书按钮
        JButton borrowButton = new JButton("借书");
        borrowButton.setToolTipText("请选择一本书并输入您的借阅者证号来借出它"); // 按钮提示信息
        // 重写父类或接口中的方法，表示这个方法是实现自父类或接口的
        borrowButton.addActionListener(_ -> {
            // 实现ActionListener接口中的actionPerformed方法，该方法在按钮等组件被触发时调用
            borrowBook();//调用借书方法，执行借书的相关逻辑
        });
        borrowReturnPanel.add(borrowButton);

        // 添加还书按钮
        JButton returnButton = new JButton("还书");
        returnButton.setToolTipText("请选择一本书并输入您的借阅者证号来还它"); // 按钮提示信息
        returnButton.addActionListener(_ -> {
            returnBook(); // 调用还书方法
        });
        borrowReturnPanel.add(returnButton);

        // 添加查看借阅记录按钮
        JButton viewRecordsButton = new JButton("查看借阅记录");
        viewRecordsButton.setToolTipText("查看所有的借阅记录");
        viewRecordsButton.addActionListener(_ -> {
            viewBorrowRecords(); // 调用查看借阅记录方法
        });
        borrowReturnPanel.add(viewRecordsButton);

        // 将借书和还书面板添加到窗口的南部区域
        panel.add(borrowReturnPanel, BorderLayout.SOUTH);

        return panel;
    }
    //创建书籍管理面板
    private JPanel createBookManagementPanel() {
        // 创建主面板，使用BorderLayout布局
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 创建表单面板，使用GridBagLayout布局
        JPanel formPanel = new JPanel(new GridBagLayout());
        // 设置表单面板的边框和背景色
        formPanel.setBorder(BorderFactory.createTitledBorder("书籍管理"));
        formPanel.setBackground(Color.WHITE);

        // 创建GridBagConstraints对象，用于设置组件的布局约束
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST; // 组件对齐方式为西（左对齐）
        gbc.insets = new Insets(5, 5, 5, 5); // 组件之间的间距

        // 添加标签和文本字段到表单面板
        addLabelAndField(formPanel, gbc, "ISBN:", isbnField1 = new JTextField(20));
        addLabelAndField(formPanel, gbc, "Title:", titleField1 = new JTextField());
        addLabelAndField(formPanel, gbc, "Authors:", authorsField1 = new JTextField());
        addLabelAndField(formPanel, gbc, "Publisher:", publisherField1 = new JTextField());
        addLabelAndField(formPanel, gbc, "Publication Date:", publicationDateField1 = new JTextField());
        addLabelAndField(formPanel, gbc, "Type:", typeField1 = new JTextField());
        addLabelAndField(formPanel, gbc, "Edition Number:", editionNumberField1 = new JTextField());

        // 设置添加按钮的布局约束
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 创建添加按钮，并添加事件监听器
        JButton addButton = new JButton("添加书籍");
        addButton.addActionListener(_ -> addOrUpdateBook(false));
        formPanel.add(addButton, gbc);

        // 设置组件的网格位置
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // 创建更新按钮，并添加事件监听器
        JButton updateButton = new JButton("修改书籍");
        updateButton.addActionListener(_ -> addOrUpdateBook(true));
        formPanel.add(updateButton, gbc);

        // 设置删除按钮的布局约束
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 创建删除按钮，并添加事件监听器
        JButton deleteButton = new JButton("删除书籍");
        deleteButton.addActionListener(_ -> deleteBook());
        formPanel.add(deleteButton, gbc);

        // 将表单面板添加到主面板的北部
        mainPanel.add(formPanel, BorderLayout.NORTH);


        // 定义表格的列名
        String[] columns = {"ISBN", "Title", "Authors", "Publisher", "Edition Number", "Publication Date", "Type"};
        // 创建表格模型，并设置单元格不可编辑
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        // 创建表格并设置滚动面板
        booksManageTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(booksManageTable);

        // 将滚动面板添加到主面板的中心部
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 加载书籍数据
        loadBooksData();

        // 返回主面板
        return mainPanel;
    }
    //创建读者管理面板
    private JPanel createReaderManagementPanel() {
        // 创建主面板，并设置布局为BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());


        // 创建表单面板，并设置布局为GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        // 设置表单面板的边框，标题为“读者管理”
        formPanel.setBorder(BorderFactory.createTitledBorder("读者管理"));
        // 设置表单面板的背景颜色为白色
        formPanel.setBackground(Color.WHITE);

        // 创建GridBagConstraints对象，用于设置组件的约束
        GridBagConstraints gbc = new GridBagConstraints();
        // 设置组件的对齐方式为靠左
        gbc.anchor = GridBagConstraints.WEST;
        // 设置组件的边距
        gbc.insets = new Insets(5, 5, 5, 5);

        // 添加标签和文本字段到表单面板
        addLabelAndField(formPanel, gbc, "Reader ID:", readerIdField = new JTextField(10));
        addLabelAndField(formPanel, gbc, "First Name:", firstNameField = new JTextField(20));
        addLabelAndField(formPanel, gbc, "Last Name:", lastNameField = new JTextField(20));
        addLabelAndField(formPanel, gbc, "Address:", addressField = new JTextField(30));
        addLabelAndField(formPanel, gbc, "Phone Number:", phoneNumberField = new JTextField(15));
        addLabelAndField(formPanel, gbc, "Limits:", readerLimitField = new JTextField(5));

        // 设置组件的网格位置
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 创建“添加读者”按钮，并添加事件监听器
        JButton addButton = new JButton("添加读者");
        addButton.addActionListener(_ -> addOrUpdateReader(false));
        // 将按钮添加到表单面板
        formPanel.add(addButton, gbc);

        // 设置组件的网格位置
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // 创建“更新读者”按钮，并添加事件监听器
        JButton updateButton = new JButton("更新读者");
        updateButton.addActionListener(_ -> addOrUpdateReader(true));
        // 将按钮添加到表单面板
        formPanel.add(updateButton, gbc);

        // 设置组件的网格位置
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 创建“删除读者”按钮，并添加事件监听器
        JButton deleteButton = new JButton("删除读者");
        deleteButton.addActionListener(_ -> deleteReader());
        // 将按钮添加到表单面板
        formPanel.add(deleteButton, gbc);

        // 将表单面板添加到主面板的北部
        mainPanel.add(formPanel, BorderLayout.NORTH);


        // 定义表格的列名
        String[] columns = {"Reader ID", "First Name", "Last Name", "Address", "Phone Number", "Limits"};
        // 创建表格模型，并设置列名，且表格单元格不可编辑
        readersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        // 创建表格，并设置模型
        readersTable = new JTable(readersTableModel);
        // 创建滚动面板，并将表格添加到滚动面板中
        JScrollPane scrollPane = new JScrollPane(readersTable);

        // 将滚动面板添加到主面板的中心部
        mainPanel.add(scrollPane, BorderLayout.CENTER);


        loadReadersData();

        return mainPanel;
    }
    /**
     * 辅助方法：将标签和文本框添加到指定的面板中
     *
     * @param panel 面板对象
     * @param gbc   GridBagConstraints对象
     * @param label 标签文本
     * @param field 文本框对象
     */
    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String label, JTextField field) {
        gbc.gridx = 0; // 设置标签的x坐标
        gbc.gridy++; // 设置标签的y坐标
        panel.add(new JLabel(label), gbc); // 添加标签到面板

        gbc.gridx = 1; // 设置文本框的x坐标
        gbc.fill = GridBagConstraints.HORIZONTAL; // 设置文本框填充方式
        panel.add(field, gbc); // 添加文本框到面板
        gbc.fill = GridBagConstraints.NONE; // 重置填充方式
    }
    //定义一个私有方法，用于处理动作事件（ActionEvent）
    private void actionPerformed(ActionEvent e) {
        // 调用searchBooks方法，执行书籍搜索操作
        searchBooks();
    }
    //搜索书籍的方法
    private void searchBooks() {
        // 获取用户输入的搜索条件
        String isbn = isbnField.getText();
        String title = titleField.getText();
        String authors = authorsField.getText();
        String publisher = publisherField.getText();
        String publicationDate = publicationDateField.getText();
        String type = typeField.getText();
        String sortBy = (String) sortByComboBox.getSelectedItem();
        // 构建SQL查询语句
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Books WHERE 1=1");
        if (!isbn.isEmpty()) queryBuilder.append(" AND ISBN LIKE ? ");
        if (!title.isEmpty()) queryBuilder.append(" AND Title LIKE ? ");
        if (!authors.isEmpty()) queryBuilder.append(" AND Authors LIKE ? ");
        if (!publisher.isEmpty()) queryBuilder.append(" AND Publisher LIKE ? ");
        if (!publicationDate.isEmpty()) queryBuilder.append(" AND PublicationDate LIKE ? ");
        if (!type.isEmpty()) queryBuilder.append(" AND Type LIKE ? ");

        queryBuilder.append(" ORDER BY ").append(sortBy);

        try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
            int parameterIndex = 1; // 参数索引从1开始
            if (!isbn.isEmpty()) statement.setString(parameterIndex++, "%" + isbn + "%");
            if (!title.isEmpty()) statement.setString(parameterIndex++, "%" + title + "%");
            if (!authors.isEmpty()) statement.setString(parameterIndex++, "%" + authors + "%");
            if (!publisher.isEmpty()) statement.setString(parameterIndex++, "%" + publisher + "%");
            if (!publicationDate.isEmpty()) statement.setString(parameterIndex++, "%" + publicationDate + "%");
            if (!type.isEmpty()) statement.setString(parameterIndex++, "%" + type + "%");

            ResultSet resultSet = statement.executeQuery(); // 执行查询
            tableModel.setRowCount(0); // 清空表格数据
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getString("ISBN"));
                row.add(resultSet.getString("Title"));
                row.add(resultSet.getString("Authors"));
                row.add(resultSet.getString("Publisher"));
                row.add(resultSet.getInt("EditionNumber"));
                row.add(resultSet.getDate("PublicationDate"));
                row.add(resultSet.getString("Type"));
                tableModel.addRow(row); // 将查询结果添加到表格中

            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "拿取数据错误 " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // 显示错误消息框
        }

    }
    //借书
    private void borrowBook() {
        // 获取选中的书籍行索引
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择您要借阅的书籍", "Warning", JOptionPane.WARNING_MESSAGE); // 提示选择书籍
            return;
        }

        // 获取所选书籍的ISBN
        String isbn = (String) booksTable.getValueAt(selectedRow, 0);

        // 获取读者ID输入
        String readerIdInput = JOptionPane.showInputDialog(this, "请输入您的借阅者证号");
        if(readerIdInput == null){
            return;
        }
        if (readerIdInput.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "借阅者证号不能为空", "Warning", JOptionPane.WARNING_MESSAGE); // 提示输入借阅者证号
            return;
        }
        int readerId;
        try {
            readerId = Integer.parseInt(readerIdInput); // 将输入转换为整数
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "无效格式", "Error", JOptionPane.ERROR_MESSAGE); // 提示无效格式
            return;
        }

        try {
            // 检查书籍是否已被借出
            PreparedStatement checkBorrowedStatement = connection.prepareStatement(
                    "SELECT ReturnDate FROM Record WHERE ISBN = ? AND ReturnDate IS NOT Null"
            );
            checkBorrowedStatement.setString(1, isbn);
            ResultSet borrowedResult = checkBorrowedStatement.executeQuery();
            if (borrowedResult.next()) {
                JOptionPane.showMessageDialog(this, "该书已被借出，归还时间 " + borrowedResult.getDate("ReturnDate"), "Information", JOptionPane.INFORMATION_MESSAGE); // 提示归还日期
                return;
            }

            // 检查读者是否可以借更多书籍
            PreparedStatement checkLimitsStatement = connection.prepareStatement(
                    "SELECT COUNT(*) AS Count, Limits FROM Reader JOIN Record ON Reader.ReaderID = Record.ReaderID WHERE Reader.ReaderID = ? AND Record.ReturnDate IS NOT NULL GROUP BY Reader.ReaderID"
            );
            checkLimitsStatement.setInt(1, readerId);
            ResultSet limitsResult = checkLimitsStatement.executeQuery();
            boolean canBorrow = true;
            if (limitsResult.next()) {
                int count = limitsResult.getInt("Count");
                int limit = limitsResult.getInt("Limits");
                if (count >= limit) {
                    canBorrow = false;
                }
            }
            if (!canBorrow) {
                JOptionPane.showMessageDialog(this, "您已经超过您的最大借阅数", "Warning", JOptionPane.WARNING_MESSAGE); // 提示超出借阅限制
                return;
            }

            // 计算归还日期（假设借阅期限为14天）
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date()); // 设置当前日期
            calendar.add(Calendar.DAY_OF_MONTH, 14); // 增加14天
            Date dueDate = calendar.getTime();

            // 插入新的借阅记录
            PreparedStatement insertRecordStatement = connection.prepareStatement(
                    "INSERT INTO Record (ISBN, ReaderID, BorrowingDate, ReturnDate) VALUES (?, ?, CURDATE(), ?)"
            );
            insertRecordStatement.setString(1, isbn);
            insertRecordStatement.setInt(2, readerId);
            insertRecordStatement.setDate(3, new java.sql.Date(dueDate.getTime()));
            insertRecordStatement.executeUpdate();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.format(dueDate);

            JOptionPane.showMessageDialog(this, "借书成功", "Success", JOptionPane.INFORMATION_MESSAGE); // 提示借书成功
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "借书失败" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // 显示错误消息框
        }
    }
    //还书
    private void returnBook() {
        // 获取选中的书籍行索引
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择您要归还的书籍", "Warning", JOptionPane.WARNING_MESSAGE); // 提示选择书籍
            return;
        }

        // 获取所选书籍的ISBN
        String isbn = (String) booksTable.getValueAt(selectedRow, 0);

        // 获取读者ID输入
        String readerIdInput = JOptionPane.showInputDialog(this, "请输入您的借阅者证号");
        if(readerIdInput == null){
            return;
        }
        if (readerIdInput.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "借阅者证号不能为空", "Warning", JOptionPane.WARNING_MESSAGE); // 提示输入读者ID
            return;
        }
        int readerId;
        try {
            readerId = Integer.parseInt(readerIdInput); // 将输入转换为整数
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "无效格式", "Error", JOptionPane.ERROR_MESSAGE); // 提示无效格式
            return;
        }

        try {
            // 删除借阅记录
            PreparedStatement deleteRecordStatement = connection.prepareStatement(
                    "DELETE FROM Record WHERE ISBN = ? AND ReaderID = ? AND ReturnDate IS NOT NULL LIMIT 1"
            );
            deleteRecordStatement.setString(1, isbn);
            deleteRecordStatement.setInt(2, readerId);
            int rowsAffected = deleteRecordStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "还书成功", "Success", JOptionPane.INFORMATION_MESSAGE); // 提示还书成功
            } else {
                JOptionPane.showMessageDialog(this, "没有找到您这本书的借阅记录", "Information", JOptionPane.INFORMATION_MESSAGE); // 提示没有匹配的借阅记录
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "还书失败" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // 显示错误消息框
        }
    }
    //查看借阅记录的方法
    private void viewBorrowRecords() {
        try {
            // 查询所有借阅记录
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT RecordID, ISBN, ReaderID, BorrowingDate, ReturnDate FROM Record"
            );

            ResultSet resultSet = statement.executeQuery(); // 执行查询
            recordsTableModel.setRowCount(0); // 清空表格数据
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getInt("RecordID"));
                row.add(resultSet.getString("ISBN"));
                row.add(resultSet.getInt("ReaderID"));
                row.add(resultSet.getDate("BorrowingDate"));
                row.add(resultSet.getDate("ReturnDate"));
                recordsTableModel.addRow(row); // 将查询结果添加到表格中
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch borrow records: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // 显示错误消息框
        }
    }
    //加载书籍数据
    private void loadBooksData() {
        try {
            //准备查询语句
            PreparedStatement statement = connection.prepareStatement("SELECT ISBN, Title, Authors, Publisher, EditionNumber, PublicationDate, Type FROM Books");
            //执行查询语句
            ResultSet resultSet = statement.executeQuery();
            //清空表格
            tableModel.setRowCount(0);
            //遍历查询结果
            while (resultSet.next()) {
                //创建一个行向量
                Vector<Object> row = new Vector<>();
                //将查询结果添加到行向量中
                row.add(resultSet.getString("ISBN"));
                row.add(resultSet.getString("Title"));
                row.add(resultSet.getString("Authors"));
                row.add(resultSet.getString("Publisher"));
                row.add(resultSet.getInt("EditionNumber"));
                row.add(resultSet.getDate("PublicationDate"));
                row.add(resultSet.getString("Type"));
                //将行向量添加到表格模型中
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            //如果出现异常，弹出错误提示框
            JOptionPane.showMessageDialog(this, "错误书籍数据: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    //增改书籍数据
    private void addOrUpdateBook(boolean isUpdate) {
        String isbn = isbnField1.getText();
        String title = titleField1.getText();
        String authors = authorsField1.getText();
        String publisher = publisherField1.getText();
        String publicationDateStr = publicationDateField1.getText();
        String type = typeField1.getText();
        String editionNumberStr = editionNumberField1.getText();

        if (isbn.isEmpty() || title.isEmpty() || authors.isEmpty() || publisher.isEmpty() || publicationDateStr.isEmpty() || type.isEmpty() || editionNumberStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有信息都需填写", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int editionNumber;
        try {
            editionNumber = Integer.parseInt(editionNumberStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "错误编辑数字", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.sql.Date publicationDate;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = sdf.parse(publicationDateStr);
            publicationDate = new java.sql.Date(parsedDate.getTime());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "错误日期出版格式，请用yyyy-MM-dd格式.", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (isUpdate) {
                PreparedStatement updateStatement = connection.prepareStatement(
                        "UPDATE Books SET Title = ?, Authors = ?, Publisher = ?, EditionNumber = ?, PublicationDate = ?, Type = ? WHERE ISBN = ?"
                );
                updateStatement.setString(1, title);
                updateStatement.setString(2, authors);
                updateStatement.setString(3, publisher);
                updateStatement.setInt(4, editionNumber);
                updateStatement.setDate(5, publicationDate);
                updateStatement.setString(6, type);
                updateStatement.setString(7, isbn);
                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "书籍更新成功!（重进刷新）", "成功", JOptionPane.INFORMATION_MESSAGE);
                    loadBooksData();
                } else {
                    JOptionPane.showMessageDialog(this, "没有匹配到书籍", "信息", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO Books (ISBN, Title, Authors, Publisher, EditionNumber, PublicationDate, Type) VALUES (?, ?, ?, ?, ?, ?, ?)"
                );
                insertStatement.setString(1, isbn);
                insertStatement.setString(2, title);
                insertStatement.setString(3, authors);
                insertStatement.setString(4, publisher);
                insertStatement.setInt(5, editionNumber);
                insertStatement.setDate(6, publicationDate);
                insertStatement.setString(7, type);
                insertStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "书籍添加成功!（重进刷新）", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadBooksData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "操作失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    //删除书籍
    private void deleteBook() {
        int selectedRow = booksManageTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择一个书籍删除.", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String isbn = (String) booksManageTable.getValueAt(selectedRow, 0);

        try {
            PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM Books WHERE ISBN = ?"
            );
            deleteStatement.setString(1, isbn);
            int rowsAffected = deleteStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "书籍删除成功!（重进刷新）", "成功", JOptionPane.INFORMATION_MESSAGE);
                revalidate(); // 刷新布局
                repaint(); // 重绘组件
                loadBooksData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败.", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "删除失败（已被借出）: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    //加载读者数据
    private void loadReadersData() {
        // 尝试从数据库中加载读者数据
        try {
            // 创建一个预编译的SQL语句，用于从Reader表中查询读者信息
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT ReaderID, FirstName, LastName, Address, PhoneNumber, Limits FROM Reader"
            );

            // 执行查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 清空当前表格模型中的所有数据
            readersTableModel.setRowCount(0);
            // 遍历结果集，将每一行数据添加到表格模型中
            while (resultSet.next()) {
                // 创建一个向量用于存储当前行的数据
                Vector<Object> row = new Vector<>();
                // 从结果集中获取ReaderID并添加到向量中
                row.add(resultSet.getInt("ReaderID"));
                // 从结果集中获取FirstName并添加到向量中
                row.add(resultSet.getString("FirstName"));
                // 从结果集中获取LastName并添加到向量中
                row.add(resultSet.getString("LastName"));
                // 从结果集中获取Address并添加到向量中
                row.add(resultSet.getString("Address"));
                // 从结果集中获取PhoneNumber并添加到向量中
                row.add(resultSet.getString("PhoneNumber"));
                // 从结果集中获取Limits并添加到向量中
                row.add(resultSet.getInt("Limits"));
                // 将当前行的数据添加到表格模型中
                readersTableModel.addRow(row);
            }
        } catch (SQLException e) {
            // 如果发生SQL异常，显示错误消息对话框
            JOptionPane.showMessageDialog(this, "错误读者书籍: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    //增改读者
    private void addOrUpdateReader(boolean isUpdate) {
        String idStr = readerIdField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String address = addressField.getText();
        String phoneNumber = phoneNumberField.getText();
        String limitStr = readerLimitField.getText();

        if (idStr.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() || limitStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有信息都需要.", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int readerId;
        int limit;
        try {
            readerId = Integer.parseInt(idStr);
            limit = Integer.parseInt(limitStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "错误输入格式.", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (isUpdate) {
                PreparedStatement updateStatement = connection.prepareStatement(
                        "UPDATE Reader SET FirstName = ?, LastName = ?, Address = ?, PhoneNumber = ?, Limits = ? WHERE ReaderID = ?"
                );
                updateStatement.setString(1, firstName);
                updateStatement.setString(2, lastName);
                updateStatement.setString(3, address);
                updateStatement.setString(4, phoneNumber);
                updateStatement.setInt(5, limit);
                updateStatement.setInt(6, readerId);
                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "读者更新成功!", "成功", JOptionPane.INFORMATION_MESSAGE);
                    loadReadersData();
                } else {
                    JOptionPane.showMessageDialog(this, "没有匹配到读者.", "信息", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO Reader (ReaderID, FirstName, LastName, Address, PhoneNumber, Limits) VALUES (?, ?, ?, ?, ?, ?)"
                );
                insertStatement.setInt(1, readerId);
                insertStatement.setString(2, firstName);
                insertStatement.setString(3, lastName);
                insertStatement.setString(4, address);
                insertStatement.setString(5, phoneNumber);
                insertStatement.setInt(6, limit);
                insertStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "读者添加成功!", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadReadersData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "操作失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    //删除读者
    private void deleteReader() {
        int selectedRow = readersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择一个读者删除.", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int readerId = (int) readersTable.getValueAt(selectedRow, 0);

        try {
            PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM Reader WHERE ReaderID = ?"
            );
            deleteStatement.setInt(1, readerId);
            int rowsAffected = deleteStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "读者删除成功!", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadReadersData();
            } else {
                JOptionPane.showMessageDialog(this, "删除读者失败.", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "删除读者失败（有书籍未归还）: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }


}



