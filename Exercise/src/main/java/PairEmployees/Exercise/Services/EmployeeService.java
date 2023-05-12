package PairEmployees.Exercise.Services;

import PairEmployees.Exercise.Model.Project;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {
    public Map<String, Integer> getEmployeePairs() {
        String csvFile = "D:/TestExercise/EmployeePairs/src/main/resources/Data.csv";
        String line = "";
        String cvsSplitBy = ",";
        Map<Integer, List<Project>> employeeProjects = new HashMap<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter dateFormatter3 = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                try {
                    String[] data = line.split(cvsSplitBy);

                    if (data.length != 4) {
                        System.err.println("Invalid data: " + line);
                        continue;
                    }

                    if (!data[0].trim().matches("\\d+") || !data[1].trim().matches("\\d+")) {
                        System.err.println("Invalid employee ID or project ID: " + line);
                        continue;
                    }

                    int empId = Integer.parseInt(data[0].trim());
                    int projectId = Integer.parseInt(data[1].trim());

                    LocalDate dateFrom = null;
                    LocalDate dateTo = null;

                    try {
                        dateFrom = LocalDate.parse(data[2].trim(), dateFormatter);
                    } catch (DateTimeParseException e1) {
                        try {
                            dateFrom = LocalDate.parse(data[2].trim(), dateFormatter2);
                        } catch (DateTimeParseException e2) {
                            try {
                                dateFrom = LocalDate.parse(data[2].trim(), dateFormatter3);
                            } catch (DateTimeParseException e3) {
                                throw new IllegalArgumentException("Invalid date format: " + data[2]);
                            }
                        }
                    }

                    if (data[3].trim().equalsIgnoreCase("null")) {
                        dateTo = LocalDate.now();
                    } else {
                        try {
                            dateTo = LocalDate.parse(data[3].trim(), dateFormatter);
                        } catch (DateTimeParseException e1) {
                            try {
                                dateTo = LocalDate.parse(data[3].trim(), dateFormatter2);
                            } catch (DateTimeParseException e2) {
                                try {
                                    dateTo = LocalDate.parse(data[3].trim(), dateFormatter3);
                                } catch (DateTimeParseException e3) {
                                    throw new IllegalArgumentException("Invalid date format: " + data[3]);
                                }
                            }
                        }
                    }

                    if (dateTo.isBefore(dateFrom)) {
                        throw new IllegalArgumentException("Invalid date range: " + line);
                    }

                    Project project = new Project(empId, projectId, dateFrom, dateTo);
                    employeeProjects.computeIfAbsent(empId, k -> new ArrayList<>()).add(project);

                } catch (NumberFormatException e) {
                    System.err.println("Invalid input: " + line);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<Integer, List<Project>> projectEmployees = new HashMap<>();
        for (Map.Entry<Integer, List<Project>> entry : employeeProjects.entrySet()) {
            int empId = entry.getKey();
            List<Project> projects = entry.getValue();
            for (Project project : projects) {
                projectEmployees.computeIfAbsent(project.getId(), k -> new ArrayList<>()).add(project);
            }
        }

        Map<String, Integer> employeePairs = new HashMap<>();
        for (Map.Entry<Integer, List<Project>> entry : projectEmployees.entrySet()) {
            List<Project> projects = entry.getValue();
            for (int i = 0; i < projects.size(); i++) {
                for (int j = i + 1; j < projects.size(); j++) {
                    Project project1 = projects.get(i);
                    Project project2 = projects.get(j);
                    int daysWorkedTogether = (int) ChronoUnit.DAYS.between(
                            project1.getDateFrom().isAfter(project2.getDateFrom()) ? project1.getDateFrom() : project2.getDateFrom(),
                            project1.getDateTo().isBefore(project2.getDateTo()) ? project1.getDateTo() : project2.getDateTo());
                    if (daysWorkedTogether > 0) {
                        String employeePair = project1.getEmpId() < project2.getEmpId() ?
                                project1.getEmpId() + "," + project2.getEmpId() :
                                project2.getEmpId() + "," + project1.getEmpId();
                        employeePairs.merge(employeePair, daysWorkedTogether, Integer::sum);
                    }
                }
            }
        }
        return employeePairs;
    }
}