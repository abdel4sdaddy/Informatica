import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Contatto {
    private String nome;
    private String telefono;

    public Contatto(String nome, String telefono) {
        this.nome = nome;
        this.telefono = telefono;
    }

    public String getNome() { return nome; }
    public String getTelefono() { return telefono; }

    public String toCSV() {
        return nome + "," + telefono;
    }
}

class GestoreDati {
    private static final String FILE_PATH = "contatti.csv";

    public static void salva(List<Contatto> contatti) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Contatto c : contatti) {
                bw.write(c.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Contatto> carica() {
        List<Contatto> contatti = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return contatti;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String riga;
            while ((riga = br.readLine()) != null) {
                String[] parti = riga.split(",");
                if (parti.length == 2) {
                    contatti.add(new Contatto(parti[0], parti[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contatti;
    }
}

public class ContattiApp extends JFrame {
    private DefaultTableModel tableModel;
    private JTextField txtNome, txtTelefono;
    private List<Contatto> listaContatti;

    public ContattiApp() {
        setTitle("Gestione Contatti");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        listaContatti = GestoreDati.carica();

        tableModel = new DefaultTableModel(new Object[]{"Nome", "Telefono"}, 0);
        JTable tabella = new JTable(tableModel);
        aggiornaTabella();

        JPanel panelInput = new JPanel(new FlowLayout());
        txtNome = new JTextField(10);
        txtTelefono = new JTextField(10);
        JButton btnAggiungi = new JButton("Aggiungi");
        
        panelInput.add(new JLabel("Nome:"));
        panelInput.add(txtNome);
        panelInput.add(new JLabel("Tel:"));
        panelInput.add(txtTelefono);
        panelInput.add(btnAggiungi);

        JButton btnSalva = new JButton("Salva CSV");

        add(panelInput, BorderLayout.NORTH);
        add(new JScrollPane(tabella), BorderLayout.CENTER);
        add(btnSalva, BorderLayout.SOUTH);

        btnAggiungi.addActionListener(e -> {
            String n = txtNome.getText().trim();
            String t = txtTelefono.getText().trim();
            if (!n.isEmpty() && !t.isEmpty()) {
                listaContatti.add(new Contatto(n, t));
                aggiornaTabella();
                txtNome.setText("");
                txtTelefono.setText("");
            }
        });

        btnSalva.addActionListener(e -> {
            GestoreDati.salva(listaContatti);
            JOptionPane.showMessageDialog(this, "Dati salvati!");
        });
    }

    private void aggiornaTabella() {
        tableModel.setRowCount(0);
        for (Contatto c : listaContatti) {
            tableModel.addRow(new Object[]{c.getNome(), c.getTelefono()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ContattiApp().setVisible(true));
    }
}
