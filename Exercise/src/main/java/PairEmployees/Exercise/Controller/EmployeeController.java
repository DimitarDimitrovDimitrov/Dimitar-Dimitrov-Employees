package PairEmployees.Exercise.Controller;

import PairEmployees.Exercise.Services.EmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("projects")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employee-pairs")
    public Map<String, Integer> getEmployeePairs() {
        return employeeService.getEmployeePairs();
    }
}