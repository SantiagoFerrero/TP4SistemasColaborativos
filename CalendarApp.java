import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CalendarApp extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel calendarPanel;
    private JLabel monthLabel;
    private LocalDate currentDate;
    private Map<LocalDate, ArrayList<String>> events;

    public CalendarApp() {
        setTitle("Calendario Mensual");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        events = new HashMap<>();  // Mapa para almacenar los eventos por fecha
        currentDate = LocalDate.now();

        // Panel superior para cambiar de mes
        JPanel topPanel = new JPanel();
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        monthLabel = new JLabel();
        updateMonthLabel();

        prevButton.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar();
        });

        nextButton.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar();
        });

        topPanel.add(prevButton);
        topPanel.add(monthLabel);
        topPanel.add(nextButton);

        add(topPanel, BorderLayout.NORTH);

        // Panel de calendario con cuadrícula
        calendarPanel = new JPanel(new GridLayout(0, 7)); // 7 columnas para los días de la semana
        add(calendarPanel, BorderLayout.CENTER);

        updateCalendar();  // Mostrar el calendario inicial
    }

    // Método para actualizar el mes en la etiqueta
    private void updateMonthLabel() {
        monthLabel.setText(currentDate.getMonth() + " " + currentDate.getYear());
    }

    // Método para actualizar la cuadrícula del calendario
    private void updateCalendar() {
        calendarPanel.removeAll();  // Limpiar el panel antes de agregar los días

        YearMonth yearMonth = YearMonth.of(currentDate.getYear(), currentDate.getMonth());
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        int daysInMonth = yearMonth.lengthOfMonth();
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();

        // Añadir nombres de los días de la semana
        String[] days = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
        for (String day : days) {
            calendarPanel.add(new JLabel(day, SwingConstants.CENTER));
        }

        // Añadir celdas vacías hasta el primer día del mes
        for (int i = 1; i < dayOfWeek; i++) {
            calendarPanel.add(new JLabel(""));
        }

        // Añadir los días del mes con botones para agregar/ver eventos
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setComponentPopupMenu(createPopupMenu(date));
            dayButton.addActionListener(e -> addEvent(date));

            // Si hay eventos en el día, cambiar el color del botón o agregar un indicador
            if (events.containsKey(date)) {
                dayButton.setBackground(Color.CYAN);
            }

            calendarPanel.add(dayButton);
        }

        updateMonthLabel();
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    // Método para crear el menú contextual de cada día
    private JPopupMenu createPopupMenu(LocalDate date) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem viewItem = new JMenuItem("Ver eventos");
        viewItem.addActionListener(e -> viewEvents(date));
        popupMenu.add(viewItem);

        JMenuItem editItem = new JMenuItem("Editar evento");
        editItem.addActionListener(e -> editEvent(date));
        popupMenu.add(editItem);

        JMenuItem deleteItem = new JMenuItem("Eliminar evento");
        deleteItem.addActionListener(e -> deleteEvent(date));
        popupMenu.add(deleteItem);

        return popupMenu;
    }

    // Método para agregar un evento a una fecha específica
    private void addEvent(LocalDate date) {
        String eventText = JOptionPane.showInputDialog(this, "Agregar evento para " + date + ":");
        if (eventText != null && !eventText.isEmpty()) {
            events.computeIfAbsent(date, k -> new ArrayList<>()).add(eventText);
            updateCalendar();  // Actualizar la vista del calendario
        }
    }

    // Método para ver los eventos de una fecha específica
    private void viewEvents(LocalDate date) {
        ArrayList<String> dayEvents = events.get(date);
        if (dayEvents == null || dayEvents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay eventos para " + date);
        } else {
            JOptionPane.showMessageDialog(this, "Eventos para " + date + ":\n" + String.join("\n", dayEvents));
        }
    }

    // Método para editar el primer evento de una fecha específica
    private void editEvent(LocalDate date) {
        ArrayList<String> dayEvents = events.get(date);
        if (dayEvents == null || dayEvents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay eventos para " + date);
            return;
        }

        String currentEvent = dayEvents.get(0); // Editaremos el primer evento como ejemplo
        String newEventText = JOptionPane.showInputDialog(this, "Editar evento:", currentEvent);
        if (newEventText != null && !newEventText.isEmpty()) {
            dayEvents.set(0, newEventText); // Reemplaza el primer evento
            updateCalendar();  // Actualizar la vista del calendario
        }
    }

    // Método para eliminar un evento de una fecha específica
    private void deleteEvent(LocalDate date) {
        ArrayList<String> dayEvents = events.get(date);
        if (dayEvents == null || dayEvents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay eventos para " + date);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "¿Eliminar el primer evento para " + date + "?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            dayEvents.remove(0); // Elimina el primer evento como ejemplo
            if (dayEvents.isEmpty()) {
                events.remove(date); // Si no quedan eventos, remueve la fecha del mapa
            }
            updateCalendar();  // Actualizar la vista del calendario
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalendarApp frame = new CalendarApp();
            frame.setVisible(true);
        });
    }
}
