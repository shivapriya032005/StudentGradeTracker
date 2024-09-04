import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GradeTracker extends JFrame {
    private ArrayList<Subject> subjects;
    private JPanel subjectPanel, gradePanel, resultPanel;
    private JButton addSubjectButton, calculateButton, renameButton;
    private JTextArea resultArea;
    private JTextField[] subjectFields;
    private JTextField[] totalMarksFields;

    public GradeTracker() {
        subjects = new ArrayList<>();

        // Initialize default subjects
        String[] defaultSubjects = {"Math", "Science", "History", "English", "Computer Science"};
        for (String name : defaultSubjects) {
            subjects.add(new Subject(name));
        }

        setTitle("Student Grade Tracker");
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Subject Management Panel
        subjectPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        add(subjectPanel, BorderLayout.NORTH);

        // Add labels
        gbc.gridx = 0;
        gbc.gridy = 0;
        subjectPanel.add(new JLabel("Subject"), gbc);

        gbc.gridx = 1;
        subjectPanel.add(new JLabel("Obtained Marks"), gbc);

        gbc.gridx = 2;
        subjectPanel.add(new JLabel("Total Marks"), gbc);

        // Initialize subject fields with default subjects
        subjectFields = new JTextField[defaultSubjects.length];
        totalMarksFields = new JTextField[defaultSubjects.length];
        for (int i = 0; i < defaultSubjects.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            subjectPanel.add(new JLabel(defaultSubjects[i]), gbc);

            gbc.gridx = 1;
            JTextField gradeField = new JTextField();
            gradeField.setPreferredSize(new Dimension(100, 25));
            subjectFields[i] = gradeField;
            subjectPanel.add(gradeField, gbc);

            gbc.gridx = 2;
            JTextField totalMarksField = new JTextField();
            totalMarksField.setPreferredSize(new Dimension(100, 25));
            totalMarksFields[i] = totalMarksField;
            subjectPanel.add(totalMarksField, gbc);
        }

        gbc.gridx = 0;
        gbc.gridy = defaultSubjects.length + 1;
        renameButton = new JButton("Rename Subjects");
        renameButton.addActionListener(new RenameListener());
        subjectPanel.add(renameButton, gbc);

        gbc.gridx = 1;
        addSubjectButton = new JButton("Add Subject");
        addSubjectButton.addActionListener(new AddSubjectListener());
        subjectPanel.add(addSubjectButton, gbc);

        // Grade Input Panel
        gradePanel = new JPanel(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(gradePanel, BorderLayout.CENTER);

        // Result Display Panel
        resultPanel = new JPanel(new BorderLayout());

        calculateButton = new JButton("Calculate Grades");
        calculateButton.addActionListener(new CalculateListener());
        resultPanel.add(calculateButton, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        add(resultPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private class AddSubjectListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String subjectName = JOptionPane.showInputDialog("Enter New Subject Name:");
            if (subjectName != null && !subjectName.trim().isEmpty()) {
                Subject subject = new Subject(subjectName);
                subjects.add(subject);

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.gridx = 0;
                gbc.gridy = subjects.size();

                subjectPanel.add(new JLabel(subjectName), gbc);

                gbc.gridx = 1;
                JTextField gradeField = new JTextField();
                gradeField.setPreferredSize(new Dimension(100, 25));
                subject.setGradeField(gradeField);
                subjectPanel.add(gradeField, gbc);

                gbc.gridx = 2;
                JTextField totalMarksField = new JTextField();
                totalMarksField.setPreferredSize(new Dimension(100, 25));
                subject.setTotalMarksField(totalMarksField);
                subjectPanel.add(totalMarksField, gbc);

                subjectPanel.revalidate();
                subjectPanel.repaint();
            }
        }
    }

    private class RenameListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < subjects.size(); i++) {
                String newName = JOptionPane.showInputDialog("Enter new name for " + subjects.get(i).getName() + ":", subjects.get(i).getName());
                if (newName != null && !newName.trim().isEmpty()) {
                    subjects.get(i).setName(newName);
                    subjectPanel.getComponent(i * 3).setName(newName);
                }
            }
        }
    }

    private class CalculateListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            double totalObtainedMarks = 0;
            double totalPossibleMarks = 0;
            int totalSubjects = subjects.size();

            StringBuilder result = new StringBuilder();

            for (Subject subject : subjects) {
                try {
                    double obtainedMarks = Double.parseDouble(subject.getGradeField().getText());
                    double totalMarks = Double.parseDouble(subject.getTotalMarksField().getText());

                    if (totalMarks <= 0) {
                        result.append(subject.getName()).append(": Invalid total marks\n");
                        continue;
                    }

                    double percentage = (obtainedMarks / totalMarks) * 100;
                    totalObtainedMarks += obtainedMarks;
                    totalPossibleMarks += totalMarks;

                    result.append(subject.getName()).append(": ").append(obtainedMarks)
                          .append(" / ").append(totalMarks).append(" (")
                          .append(String.format("%.2f", percentage)).append("%)\n");

                } catch (NumberFormatException ex) {
                    result.append(subject.getName()).append(": Invalid marks\n");
                }
            }

            double overallPercentage = (totalObtainedMarks / totalPossibleMarks) * 100;
            String letterGrade = calculateLetterGrade(overallPercentage);

            result.append("\nOverall Percentage: ").append(String.format("%.2f", overallPercentage)).append("%");
            result.append("\nLetter Grade: ").append(letterGrade);

            resultArea.setText(result.toString());
        }
    }

    private String calculateLetterGrade(double percentage) {
        if (percentage >= 90) {
            return "A+";
        } else if (percentage >= 80) {
            return "A";
        } else if (percentage >= 70) {
            return "B";
        } else if (percentage >= 60) {
            return "C";
        } else if (percentage >= 50) {
            return "D";
        } else {
            return "F";
        }
    }

    public static void main(String[] args) {
        new GradeTracker();
    }
}

class Subject {
    private String name;
    private JTextField gradeField;
    private JTextField totalMarksField;

    public Subject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JTextField getGradeField() {
        return gradeField;
    }

    public void setGradeField(JTextField gradeField) {
        this.gradeField = gradeField;
    }

    public JTextField getTotalMarksField() {
        return totalMarksField;
    }

    public void setTotalMarksField(JTextField totalMarksField) {
        this.totalMarksField = totalMarksField;
    }
}
