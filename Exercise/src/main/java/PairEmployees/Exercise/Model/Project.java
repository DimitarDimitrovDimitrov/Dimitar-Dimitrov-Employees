package PairEmployees.Exercise.Model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Project {
    private int empId;
    private int id;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public Project(int empId, int id, LocalDate dateFrom, LocalDate dateTo) {
        this.empId = empId;
        this.id = id;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }
}
