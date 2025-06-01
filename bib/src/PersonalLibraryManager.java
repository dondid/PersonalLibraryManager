// Aplicație: Sistem de Gestionare Bibliotecă Personală
// Autor: Programator Java Avansat
// Descriere: Aplicație modernă cu interfață grafică pentru gestionarea cărților personale

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.nio.file.*;

import static java.awt.SystemColor.text;

// Clasa principală pentru aplicația de bibliotecă
public class PersonalLibraryManager extends JFrame {
    private static final Icon ADD_ICON = UIManager.getIcon("OptionPane.informationIcon");
    private static final Icon EDIT_ICON = UIManager.getIcon("FileView.directoryIcon");
    private final List<Book> books;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter, statusFilter;
    private JLabel statsLabel;

    // Constante pentru design modern
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public PersonalLibraryManager() {
        books = new ArrayList<>();
        setIconImage(createLibraryIcon()); // Set logo personalizat
        initializeGUI();
        loadSampleData();
        updateTable();
        updateStatistics();
    }

    private void initializeGUI() {
        try {
            Font emojiFont = Font.createFont(Font.TRUETYPE_FONT,
                    new File("seguiemj.ttf")); // sau alt font cu suport emoji
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(emojiFont);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        } catch (Exception e) {
            System.err.println("Eroare la setarea fontului: " + e.getMessage());
        }

        setTitle("Biblioteca Mea Personala - Sistem Avansat de Gestionare");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Header cu titlu și statistici
        createHeaderPanel();

        // Panel principal cu tabel și controale
        createMainPanel();

        // Footer cu butoane de acțiune
        createFooterPanel();

        // Configurare fereastră
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Look and Feel modern
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.err.println("Eroare: " + e.getMessage());
        }
    }

    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Biblioteca Mea Personala");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        statsLabel = new JLabel();
        statsLabel.setFont(NORMAL_FONT);
        statsLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statsLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel de căutare și filtrare
        JPanel searchPanel = createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Tabel cu cărți
        createBooksTable();
        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2), "Lista Cartilor", TitledBorder.LEFT, TitledBorder.TOP, TITLE_FONT, PRIMARY_COLOR));

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(SUCCESS_COLOR, 2), "Cautare si Filtrare", TitledBorder.LEFT, TitledBorder.TOP, TITLE_FONT, SUCCESS_COLOR));

        // Câmp de căutare
        searchPanel.add(new JLabel("Căutare:"));
        searchField = new JTextField(20);
        searchField.setFont(NORMAL_FONT);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterBooks();
            }
        });
        searchPanel.add(searchField);

        // Filtru categorie
        searchPanel.add(new JLabel("Categorie:"));
        categoryFilter = new JComboBox<>(new String[]{"Toate", "Ficțiune", "Non-ficțiune", "Tehnică", "Istorie", "Știință", "Biografii"});
        categoryFilter.setFont(NORMAL_FONT);
        categoryFilter.addActionListener(e -> filterBooks());
        searchPanel.add(categoryFilter);

        // Filtru status
        searchPanel.add(new JLabel("Status:"));
        statusFilter = new JComboBox<>(new String[]{"Toate", "Citită", "În curs", "De citit"});
        statusFilter.setFont(NORMAL_FONT);
        statusFilter.addActionListener(e -> filterBooks());
        searchPanel.add(statusFilter);

        // Buton reset filtre
        JButton resetButton = createStyledButton("🔄 Reset", WARNING_COLOR);
        resetButton.addActionListener(e -> resetFilters());
        searchPanel.add(resetButton);

        JButton button = new JButton(String.valueOf(text));
        if (Boolean.parseBoolean(text.toString())) {
            button.setIcon(ADD_ICON);
            button.setText("Adaugă Carte");
        }

        return searchPanel;
    }

    private void createBooksTable() {
        String[] columns = {"ID", "Titlu", "Autor", "Categorie", "Status", "Rating", "Data Adăugare", "Pagini"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        booksTable = new JTable(tableModel);
        booksTable.setFont(NORMAL_FONT);
        booksTable.setRowHeight(30);
        booksTable.setGridColor(new Color(230, 230, 230));
        booksTable.setSelectionBackground(PRIMARY_COLOR.brighter());

        // Personalizare header
        JTableHeader header = booksTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR.darker());  // Schimbare culoare din PRIMARY_COLOR în PRIMARY_COLOR.darker()
        header.setForeground(Color.WHITE);

        // Setare pentru a preveni resetarea culorii de către L&F
        header.setOpaque(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(PRIMARY_COLOR.darker());
                setForeground(Color.WHITE);
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                return this;
            }
        });

        // Setare lățimi coloane
        booksTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        booksTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        booksTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        booksTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        booksTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        booksTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        booksTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        booksTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        // Context menu pentru tabel
        createTableContextMenu();
    }

    private void createTableContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem editItem = new JMenuItem("✏️ Editează");
        editItem.addActionListener(e -> editSelectedBook());
        contextMenu.add(editItem);

        JMenuItem deleteItem = new JMenuItem("🗑️ Șterge");
        deleteItem.addActionListener(e -> deleteSelectedBook());
        contextMenu.add(deleteItem);

        contextMenu.addSeparator();

        JMenuItem detailsItem = new JMenuItem("📄 Detalii");
        detailsItem.addActionListener(e -> showBookDetails());
        contextMenu.add(detailsItem);

        booksTable.setComponentPopupMenu(contextMenu);
    }

    private void createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        footerPanel.setBackground(BACKGROUND_COLOR);
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton addButton = createStyledButton("➕ Adauga Carte", SUCCESS_COLOR);
        addButton.addActionListener(e -> showAddBookDialog());

        JButton editButton = createStyledButton("✏️ Editeaza", PRIMARY_COLOR);
        editButton.addActionListener(e -> editSelectedBook());

        JButton deleteButton = createStyledButton("🗑️ Sterge", DANGER_COLOR);
        deleteButton.addActionListener(e -> deleteSelectedBook());

        JButton statsButton = createStyledButton("📊 Statistici", WARNING_COLOR);
        statsButton.addActionListener(e -> showStatistics());

        JButton exportButton = createStyledButton("💾 Export", new Color(156, 39, 176));
        exportButton.addActionListener(e -> exportData());

        footerPanel.add(addButton);
        footerPanel.add(editButton);
        footerPanel.add(deleteButton);
        footerPanel.add(statsButton);
        footerPanel.add(exportButton);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);

        // Setare culori pentru buton complet colorat
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        // Font pentru emoji-uri
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        // Efect hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void showAddBookDialog() {
        BookDialog dialog = new BookDialog(this, "Adaugă Carte Nouă", null);
        dialog.setVisible(true);

        if (dialog.getResult() != null) {
            books.add(dialog.getResult());
            updateTable();
            updateStatistics();
            showMessage("Cartea a fost adăugată cu succes!", "Succes", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Vă rugăm să selectați o carte pentru editare.", "Atenție", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = booksTable.convertRowIndexToModel(selectedRow);
        int bookId = (Integer) tableModel.getValueAt(modelRow, 0);
        Book book = findBookById(bookId);

        if (book != null) {
            BookDialog dialog = new BookDialog(this, "Editează Carte", book);
            dialog.setVisible(true);

            if (dialog.getResult() != null) {
                int index = books.indexOf(book);
                books.set(index, dialog.getResult());
                updateTable();
                updateStatistics();
                showMessage("Cartea a fost modificată cu succes!", "Succes", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void deleteSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Vă rugăm să selectați o carte pentru ștergere.", "Atenție", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "Sunteți sigur că doriți să ștergeți această carte?", "Confirmare ștergere", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            int modelRow = booksTable.convertRowIndexToModel(selectedRow);
            int bookId = (Integer) tableModel.getValueAt(modelRow, 0);
            Book book = findBookById(bookId);

            if (book != null) {
                books.remove(book);
                updateTable();
                updateStatistics();
                showMessage("Cartea a fost ștearsă cu succes!", "Succes", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void showBookDetails() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Vă rugăm să selectați o carte pentru a vedea detaliile.", "Atenție", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = booksTable.convertRowIndexToModel(selectedRow);
        int bookId = (Integer) tableModel.getValueAt(modelRow, 0);
        Book book = findBookById(bookId);

        if (book != null) {
            BookDetailsDialog detailsDialog = new BookDetailsDialog(this, book);
            detailsDialog.setVisible(true);
        }
    }

    private void showStatistics() {
        StatisticsDialog statsDialog = new StatisticsDialog(this, books);
        statsDialog.setVisible(true);
    }

    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvează biblioteca");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fișiere text (*.txt)", "txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".txt")) {
                    file = new File(file.getPath() + ".txt");
                }

                StringBuilder content = new StringBuilder();
                content.append("BIBLIOTECA MEA PERSONALĂ - EXPORT\n");
                content.append("================================\n\n");

                for (Book book : books) {
                    content.append("Titlu: ").append(book.getTitle()).append("\n");
                    content.append("Autor: ").append(book.getAuthor()).append("\n");
                    content.append("Categorie: ").append(book.getCategory()).append("\n");
                    content.append("Status: ").append(book.getStatus()).append("\n");
                    content.append("Rating: ").append(book.getRating()).append("/5\n");
                    content.append("Pagini: ").append(book.getPages()).append("\n");
                    content.append("Data adăugare: ").append(book.getDateAdded()).append("\n");
                    if (!book.getNotes().isEmpty()) {
                        content.append("Note: ").append(book.getNotes()).append("\n");
                    }
                    content.append("-".repeat(50)).append("\n\n");
                }

                Files.write(file.toPath(), content.toString().getBytes());
                showMessage("Datele au fost exportate cu succes!", "Export reușit", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                showMessage("Eroare la exportul datelor: " + e.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filterBooks() {
        String searchText = searchField.getText().toLowerCase();
        String categoryFilterText = (String) categoryFilter.getSelectedItem();
        String statusFilterText = (String) statusFilter.getSelectedItem();

        List<Book> filteredBooks = books.stream().filter(book -> {
            boolean matchesSearch = searchText.isEmpty() || book.getTitle().toLowerCase().contains(searchText) || book.getAuthor().toLowerCase().contains(searchText);

            boolean matchesCategory = "Toate".equals(categoryFilterText) || book.getCategory().equals(categoryFilterText);

            boolean matchesStatus = "Toate".equals(statusFilterText) || book.getStatus().equals(statusFilterText);

            return matchesSearch && matchesCategory && matchesStatus;
        }).toList();

        updateTableWithBooks(filteredBooks);
    }

    private void resetFilters() {
        searchField.setText("");
        categoryFilter.setSelectedIndex(0);
        statusFilter.setSelectedIndex(0);
        updateTable();
    }

    private void updateTable() {
        updateTableWithBooks(books);

    }

    private void updateTableWithBooks(List<Book> bookList) {
        tableModel.setRowCount(0);
        for (Book book : bookList) {
            String ratingDisplay = book.getRating() + "/5";
            tableModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getCategory(), book.getStatus(), ratingDisplay, book.getDateAdded(), book.getPages()});
        }
    }

    private void updateStatistics() {
        int totalBooks = books.size();
        long readBooks = books.stream().filter(book -> "Citită".equals(book.getStatus())).count();
        long inProgressBooks = books.stream().filter(book -> "În curs".equals(book.getStatus())).count();

        statsLabel.setText(String.format("Total: %d | Citite: %d | In curs: %d", totalBooks, readBooks, inProgressBooks));
    }

    private Book findBookById(int id) {
        return books.stream().filter(book -> book.getId() == id).findFirst().orElse(null);
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void loadSampleData() {
        books.add(new Book(1, "Java: The Complete Reference", "Herbert Schildt", "Tehnică", "Citită", 5, 1248, "Ghid complet pentru programarea Java"));
        books.add(new Book(2, "Clean Code", "Robert C. Martin", "Tehnică", "În curs", 4, 464, "Principii de cod curat"));
        books.add(new Book(3, "1984", "George Orwell", "Ficțiune", "Citită", 5, 328, "Clasic al literaturii distopice"));
        books.add(new Book(4, "Sapiens", "Yuval Noah Harari", "Istorie", "De citit", 0, 443, ""));
        books.add(new Book(5, "Effective Java", "Joshua Bloch", "Tehnică", "Citită", 5, 412, "Best practices în Java"));
    }

    // Creează iconul personalizat pentru bibliotecă
    private Image createLibraryIcon() {
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();

        // Activare antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fundal circular gradient
        GradientPaint gradient = new GradientPaint(0, 0, new Color(52, 152, 219), 64, 64, new Color(41, 128, 185));
        g2d.setPaint(gradient);
        g2d.fillOval(4, 4, 56, 56);

        // Cărți stilizate
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2f));

        // Prima carte
        g2d.fillRoundRect(18, 20, 12, 20, 2, 2);
        g2d.setColor(new Color(46, 204, 113));
        g2d.fillRoundRect(22, 18, 12, 20, 2, 2);

        // A doua carte
        g2d.setColor(new Color(231, 76, 60));
        g2d.fillRoundRect(30, 16, 12, 20, 2, 2);

        // Linii decorative pe cărți
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1f));
        for (int i = 0; i < 3; i++) {
            g2d.drawLine(24, 22 + i * 3, 32, 22 + i * 3);
            g2d.drawLine(32, 20 + i * 3, 40, 20 + i * 3);
        }

        g2d.dispose();
        return icon;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Eroare: " + e.getMessage());
            }

            // Afișare splash screen cu progress bar
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);

            // Simulare încărcare aplicație
            Timer timer = new Timer(50, null);
            final int[] progress = {0};

            timer.addActionListener(e -> {
                progress[0] += 2;
                splash.updateProgress(progress[0]);

                if (progress[0] >= 100) {
                    timer.stop();
                    splash.dispose();

                    // Lansare aplicație principală
                    PersonalLibraryManager app = new PersonalLibraryManager();
                    app.setVisible(true);
                }
            });

            timer.start();
        });
    }
}

// Clasa Book pentru reprezentarea unei cărți
class Book {
    private static int nextId = 1;
    private int id;
    private String title;
    private String author;
    private String category;
    private String status;
    private int rating;
    private int pages;
    private String notes;
    private String dateAdded;

    public Book(String title, String author, String category, String status, int rating, int pages, String notes) {
        this.id = nextId++;
        this.title = title;
        this.author = author;
        this.category = category;
        this.status = status;
        this.rating = rating;
        this.pages = pages;
        this.notes = notes;
        this.dateAdded = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public Book(int id, String title, String author, String category, String status, int rating, int pages, String notes) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.status = status;
        this.rating = rating;
        this.pages = pages;
        this.notes = notes;
        this.dateAdded = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (id >= nextId) nextId = id + 1;
    }

    // Getteri și setteri
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDateAdded() {
        return dateAdded;
    }
}

// Dialog pentru adăugarea/editarea cărților
class BookDialog extends JDialog {
    private Book result;
    private JTextField titleField, authorField, pagesField;
    private JComboBox<String> categoryCombo, statusCombo, ratingCombo;
    private JTextArea notesArea;

    public BookDialog(JFrame parent, String title, Book book) {
        super(parent, title, true);
        initComponents();
        if (book != null) {
            fillFields(book);
        }
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titlu
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        formPanel.add(new JLabel("Titlu:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        titleField = new JTextField(30);
        formPanel.add(titleField, gbc);

        // Autor
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Autor:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        authorField = new JTextField(30);
        formPanel.add(authorField, gbc);

        // Categorie
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Categorie:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        categoryCombo = new JComboBox<>(new String[]{"Ficțiune", "Non-ficțiune", "Tehnică", "Istorie", "Știință", "Biografii"});
        formPanel.add(categoryCombo, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        statusCombo = new JComboBox<>(new String[]{"De citit", "În curs", "Citită"});
        formPanel.add(statusCombo, gbc);

        // Rating
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Rating:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        ratingCombo = new JComboBox<>(new String[]{"0", "1", "2", "3", "4", "5"});
        formPanel.add(ratingCombo, gbc);

        // Pagini
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Pagini:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        pagesField = new JTextField(10);
        formPanel.add(pagesField, gbc);

        // Note
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Note:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        notesArea = new JTextArea(4, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(notesArea), gbc);

        add(formPanel, BorderLayout.CENTER);

        // Butoane
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("💾 Salvează");
        JButton cancelButton = new JButton("❌ Anulează");

        saveButton.addActionListener(e -> saveBook());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void fillFields(Book book) {
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        categoryCombo.setSelectedItem(book.getCategory());
        statusCombo.setSelectedItem(book.getStatus());
        ratingCombo.setSelectedIndex(book.getRating());
        pagesField.setText(String.valueOf(book.getPages()));
        notesArea.setText(book.getNotes());
    }

    private void saveBook() {
        if (titleField.getText().trim().isEmpty() || authorField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vă rugăm să completați titlul și autorul!", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int pages = Integer.parseInt(pagesField.getText().trim());
            result = new Book(titleField.getText().trim(), authorField.getText().trim(), (String) categoryCombo.getSelectedItem(), (String) statusCombo.getSelectedItem(), ratingCombo.getSelectedIndex(), pages, notesArea.getText().trim());
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Numărul de pagini trebuie să fie un număr valid!", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Book getResult() {
        return result;
    }
}

// Dialog pentru detaliile unei cărți
class BookDetailsDialog extends JDialog {
    public BookDetailsDialog(JFrame parent, Book book) {
        super(parent, "Detalii Carte", true);
        initComponents(book);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents(Book book) {
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titlu mare
        JLabel titleLabel = new JLabel("📖 " + book.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Informații principale
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informații"));

        infoPanel.add(new JLabel("👤 Autor:"));
        infoPanel.add(new JLabel(book.getAuthor()));

        infoPanel.add(new JLabel("📂 Categorie:"));
        infoPanel.add(new JLabel(book.getCategory()));

        infoPanel.add(new JLabel("📊 Status:"));
        infoPanel.add(new JLabel(book.getStatus()));

        infoPanel.add(new JLabel("⭐ Rating:"));
        infoPanel.add(new JLabel(book.getRating() + "/5 stele"));

        infoPanel.add(new JLabel("📄 Pagini:"));
        infoPanel.add(new JLabel(String.valueOf(book.getPages())));

        infoPanel.add(new JLabel("📅 Data adăugare:"));
        infoPanel.add(new JLabel(book.getDateAdded()));

        contentPanel.add(infoPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Note
        if (!book.getNotes().isEmpty()) {
            JPanel notesPanel = new JPanel(new BorderLayout());
            notesPanel.setBorder(BorderFactory.createTitledBorder("📝 Note"));

            JTextArea notesArea = new JTextArea(book.getNotes());
            notesArea.setEditable(false);
            notesArea.setLineWrap(true);
            notesArea.setWrapStyleWord(true);
            notesArea.setBackground(getBackground());

            notesPanel.add(new JScrollPane(notesArea), BorderLayout.CENTER);
            contentPanel.add(notesPanel);
        }

        add(contentPanel, BorderLayout.CENTER);

        // Buton închidere
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("✅ Închide");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(400, 300);
    }
}

// Dialog pentru statistici
class StatisticsDialog extends JDialog {
    public StatisticsDialog(JFrame parent, List<Book> books) {
        super(parent, "📊 Statistici Bibliotecă", true);
        initComponents(books);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents(List<Book> books) {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Statistici generale
        JPanel generalStats = new JPanel(new GridLayout(0, 2, 10, 5));
        generalStats.setBorder(BorderFactory.createTitledBorder("📈 Statistici Generale"));

        generalStats.add(new JLabel("📚 Total cărți:"));
        generalStats.add(new JLabel(String.valueOf(books.size())));

        long readBooks = books.stream().filter(b -> "Citită".equals(b.getStatus())).count();
        generalStats.add(new JLabel("✅ Cărți citite:"));
        generalStats.add(new JLabel(String.valueOf(readBooks)));

        long inProgressBooks = books.stream().filter(b -> "În curs".equals(b.getStatus())).count();
        generalStats.add(new JLabel("📖 Cărți în curs:"));
        generalStats.add(new JLabel(String.valueOf(inProgressBooks)));

        long toReadBooks = books.stream().filter(b -> "De citit".equals(b.getStatus())).count();
        generalStats.add(new JLabel("📋 De citit:"));
        generalStats.add(new JLabel(String.valueOf(toReadBooks)));

        int totalPages = books.stream().mapToInt(Book::getPages).sum();
        generalStats.add(new JLabel("📄 Total pagini:"));
        generalStats.add(new JLabel(String.format("%,d", totalPages)));

        double avgRating = books.stream().filter(b -> b.getRating() > 0).mapToInt(Book::getRating).average().orElse(0.0);
        generalStats.add(new JLabel("⭐ Rating mediu:"));
        generalStats.add(new JLabel(String.format("%.1f/5", avgRating)));

        mainPanel.add(generalStats);
        mainPanel.add(Box.createVerticalStrut(15));

        // Statistici pe categorii
        JPanel categoryStats = new JPanel(new BorderLayout());
        categoryStats.setBorder(BorderFactory.createTitledBorder("📂 Distribuție pe Categorii"));

        Map<String, Long> categoryCount = books.stream().collect(java.util.stream.Collectors.groupingBy(Book::getCategory, java.util.stream.Collectors.counting()));

        StringBuilder categoryText = new StringBuilder();
        categoryCount.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).forEach(entry -> {
            double percentage = (entry.getValue() * 100.0) / books.size();
            categoryText.append(String.format("• %s: %d cărți (%.1f%%)\n", entry.getKey(), entry.getValue(), percentage));
        });

        JTextArea categoryArea = new JTextArea(categoryText.toString());
        categoryArea.setEditable(false);
        categoryArea.setBackground(getBackground());
        categoryStats.add(categoryArea, BorderLayout.CENTER);

        mainPanel.add(categoryStats);
        mainPanel.add(Box.createVerticalStrut(15));

        // Top cărți (rating 5)
        List<Book> topBooks = books.stream().filter(b -> b.getRating() == 5).sorted((a, b) -> a.getTitle().compareTo(b.getTitle())).limit(10).toList();

        if (!topBooks.isEmpty()) {
            JPanel topBooksPanel = new JPanel(new BorderLayout());
            topBooksPanel.setBorder(BorderFactory.createTitledBorder("🏆 Cărți cu Rating Maxim (5★)"));

            StringBuilder topBooksText = new StringBuilder();
            topBooks.forEach(book -> topBooksText.append(String.format("• %s - %s\n", book.getTitle(), book.getAuthor())));

            JTextArea topBooksArea = new JTextArea(topBooksText.toString());
            topBooksArea.setEditable(false);
            topBooksArea.setBackground(getBackground());
            topBooksPanel.add(new JScrollPane(topBooksArea), BorderLayout.CENTER);

            mainPanel.add(topBooksPanel);
        }

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // Buton închidere
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("✅ Închide");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(500, 600);
    }
}

// Splash Screen animat cu progress bar
class SplashScreen extends JWindow {
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel logoLabel;
    private Timer animationTimer;
    private float logoAlpha = 0.0f;

    public SplashScreen() {
        initComponents();
        startLogoAnimation();
    }

    private void initComponents() {
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Panel principal cu gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Gradient de fundal
                GradientPaint gradient = new GradientPaint(0, 0, new Color(74, 144, 226), 0, getHeight(), new Color(34, 193, 195));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Efecte decorative - cercuri transparente
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillOval(-50, -50, 200, 200);
                g2d.fillOval(getWidth() - 150, getHeight() - 150, 200, 200);

                // Particule flotante
                g2d.setColor(new Color(255, 255, 255, 50));
                for (int i = 0; i < 20; i++) {
                    int x = (i * 47) % getWidth();
                    int y = (i * 31) % getHeight();
                    g2d.fillOval(x, y, 4, 4);
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Logo și titlu
        JPanel logoPanel = createLogoPanel();
        mainPanel.add(logoPanel, BorderLayout.CENTER);

        // Progress bar și status
        JPanel progressPanel = createProgressPanel();
        mainPanel.add(progressPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(new EmptyBorder(50, 50, 30, 50));

        // Logo mare animat
        logoLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Aplicare transparență animată
                AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, logoAlpha);
                g2d.setComposite(alpha);

                // Desenare logo mare
                drawLargeLogo(g2d, getWidth() / 2 - 60, 10);
            }
        };
        logoLabel.setPreferredSize(new Dimension(120, 120));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Titlu aplicație
        JLabel titleLabel = new JLabel("Biblioteca Mea Personală");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitlu
        JLabel subtitleLabel = new JLabel("Sistem Avansat de Gestionare");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Versiune
        JLabel versionLabel = new JLabel("v2.0 Professional Edition");
        versionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        versionLabel.setForeground(new Color(255, 255, 255, 150));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPanel.add(logoLabel);
        logoPanel.add(Box.createVerticalStrut(20));
        logoPanel.add(titleLabel);
        logoPanel.add(Box.createVerticalStrut(10));
        logoPanel.add(subtitleLabel);
        logoPanel.add(Box.createVerticalStrut(5));
        logoPanel.add(versionLabel);

        return logoPanel;
    }

    private JPanel createProgressPanel() {
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setOpaque(false);
        progressPanel.setBorder(new EmptyBorder(20, 50, 40, 50));

        // Progress bar personalizat
        progressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fundal progress bar
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Progress fill cu gradient
                if (getValue() > 0) {
                    int progressWidth = (int) ((double) getValue() / getMaximum() * getWidth());
                    GradientPaint progressGradient = new GradientPaint(0, 0, new Color(46, 204, 113), progressWidth, 0, new Color(39, 174, 96));
                    g2d.setPaint(progressGradient);
                    g2d.fillRoundRect(2, 2, progressWidth - 4, getHeight() - 4, 12, 12);

                    // Efect de strălucire
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.fillRoundRect(4, 4, progressWidth - 8, getHeight() / 2 - 2, 8, 8);
                }

                // Text progres
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String text = getValue() + "%";
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = (getHeight() + fm.getAscent()) / 2;
                g2d.drawString(text, textX, textY);
            }
        };
        progressBar.setPreferredSize(new Dimension(400, 25));
        progressBar.setStringPainted(false);
        progressBar.setBorderPainted(false);
        progressBar.setOpaque(false);

        // Status text
        statusLabel = new JLabel("Inițializare aplicație...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(255, 255, 255, 180));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(statusLabel, BorderLayout.SOUTH);

        return progressPanel;
    }

    private void drawLargeLogo(Graphics2D g2d, int x, int y) {
        // Logo mare pentru splash screen
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fundal circular cu shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillOval(x + 5, y + 5, 110, 110);

        // Fundal principal cu gradient
        GradientPaint bgGradient = new GradientPaint(x, y, new Color(52, 152, 219), x + 120, y + 120, new Color(41, 128, 185));
        g2d.setPaint(bgGradient);
        g2d.fillOval(x, y, 120, 120);

        // Highlight circular
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillOval(x + 10, y + 10, 30, 30);

        // Cărți stilizate mari
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(x + 25, y + 35, 20, 35, 4, 4);

        g2d.setColor(new Color(46, 204, 113));
        g2d.fillRoundRect(x + 35, y + 30, 20, 35, 4, 4);

        g2d.setColor(new Color(231, 76, 60));
        g2d.fillRoundRect(x + 45, y + 25, 20, 35, 4, 4);

        g2d.setColor(new Color(243, 156, 18));
        g2d.fillRoundRect(x + 55, y + 30, 20, 35, 4, 4);

        // Detalii decorative
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1.5f));

        // Linii pe cărți
        for (int i = 0; i < 4; i++) {
            g2d.drawLine(x + 37, y + 35 + i * 5, x + 53, y + 35 + i * 5);
            g2d.drawLine(x + 47, y + 30 + i * 5, x + 63, y + 30 + i * 5);
            g2d.drawLine(x + 57, y + 35 + i * 5, x + 73, y + 35 + i * 5);
        }

        // Stea mică decorativă
        g2d.setColor(new Color(241, 196, 15));
        drawStar(g2d, x + 85, y + 80, 8);
    }

    private void drawStar(Graphics2D g2d, int x, int y, int size) {
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];

        for (int i = 0; i < 10; i++) {
            double angle = i * Math.PI / 5;
            int radius = (i % 2 == 0) ? size : size / 2;
            xPoints[i] = x + (int) (radius * Math.cos(angle - Math.PI / 2));
            yPoints[i] = y + (int) (radius * Math.sin(angle - Math.PI / 2));
        }

        g2d.fillPolygon(xPoints, yPoints, 10);
    }

    private void startLogoAnimation() {
        animationTimer = new Timer(50, e -> {
            logoAlpha += 0.05f;
            if (logoAlpha >= 1.0f) {
                logoAlpha = 1.0f;
                animationTimer.stop();
            }
            logoLabel.repaint();
        });
        animationTimer.start();
    }

    public void updateProgress(int progress) {
        progressBar.setValue(progress);

        // Actualizare mesaje status
        if (progress < 20) {
            statusLabel.setText("Inițializare componente...");
        } else if (progress < 40) {
            statusLabel.setText("Încărcare interfață grafică...");
        } else if (progress < 60) {
            statusLabel.setText("Configurare baza de date...");
        } else if (progress < 80) {
            statusLabel.setText("Aplicare teme vizuale...");
        } else if (progress < 95) {
            statusLabel.setText("Finalizare configurări...");
        } else {
            statusLabel.setText("Gata! Lansare aplicație...");
        }

        // Animație progres bar
        progressBar.repaint();
    }
}

