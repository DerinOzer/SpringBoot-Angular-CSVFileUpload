package com.sirmasolutions.controller;

import com.sirmasolutions.entity.EmployeePair;
import com.sirmasolutions.entity.ProjectHistory;
import com.sirmasolutions.repository.ProjectRepository;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST})
@RequestMapping("/sirma")
public class ProjectController {
    @Autowired
    ProjectRepository projectRepository;

    @PostMapping("/upload")
    public List<ProjectHistory> uploadFile(@RequestParam("file") MultipartFile file) throws Exception{
        // Since no information is kept on the database, we first check if anything is left and if so delete everything
        // before the user can upload another file.
        if(projectRepository.count()>0)
            projectRepository.deleteAll();
        // The empty list on which the parsed records will be added.
        List<ProjectHistory> projectHistoryList = new ArrayList<>();
        InputStream inputStream = file.getInputStream();
        CsvParserSettings csvSetting = new CsvParserSettings();
        csvSetting.setHeaderExtractionEnabled(true);
        CsvParser csvParser = new CsvParser(csvSetting);
        List<Record> allRecords = csvParser.parseAllRecords(inputStream);
        // Providing header names prevents users to choose another header name.
        for (Record record : allRecords) {
            ProjectHistory projectHistory = new ProjectHistory();
            projectHistory.setEmpID(Integer.parseInt(record.getString("EmpID")));
            projectHistory.setProjectID(Integer.parseInt(record.getString("ProjectID")));
            projectHistory.setDateFrom(projectHistory.parseDate(record.getString("DateFrom")));
            // If DateTo is NULL, it equals to today's date.
            if(record.getString("DateTo").equals("NULL")) {
                LocalDate todaysDate = LocalDate.now();
                projectHistory.setDateTo(projectHistory.parseDate(todaysDate.toString()));
            }
            else{
                projectHistory.setDateTo(projectHistory.parseDate(record.getString("DateTo")));
            }
            if(projectHistory.getDateTo().before(projectHistory.getDateFrom())){
                throw new Exception("DateTo field can't contain a date before the DateFrom field.");
            }
            // If no errors are found, the record is added to the list.
            projectHistoryList.add(projectHistory);
        }
        projectRepository.saveAll(projectHistoryList);
        return projectHistoryList;
    }

    @GetMapping("/employees")
    public List<EmployeePair> getEmployeePairs() throws Exception {
        // This list includes the pair of employees that worked together the longest and their projects together.
        // This is not a persistence class.
        List<EmployeePair> employeePairs = new ArrayList<>();
        // All parsed records that are saved to the database with the upload.
        List<ProjectHistory> allRecords = projectRepository.findAll();
        // First the time between the dates is calculated, and then it is converted to days.
        long timeBetween = 0;
        long numberOfDaysBetween = 0;
        // This variable allows us to find the pair of employees that worked together the longest.
        long maxDaysWorked = 0;
        // The IDs of the pair of employees that worked together the longest.
        int empID1 = 0, empID2 = 0;
        // First loop is for the first employee.
        for(ProjectHistory employee1 : allRecords){
            // The second loop is to find the second employee.
            // The second loop uses a sublist to increase performance.
            // Because 1-2 and 2-1 are the same and gives the same number of days.
            for(ProjectHistory employee2 :  allRecords.subList(allRecords.indexOf(employee1)+1, allRecords.size())){
                // We check if both employees are working on the same project. If not, this record is not necessary.
                if(employee1.getProjectID() == employee2.getProjectID()){
                    // This condition is added to make sure that the employees worked on the project together and not in different periods.
                    if(employee1.getDateFrom().before(employee2.getDateTo()) && employee2.getDateFrom().before(employee1.getDateTo())){
                        // DateFrom(to calculate the number of days worked together) is the latest of the two dates.
                        Date dateFrom = (employee1.getDateFrom().after(employee2.getDateFrom())) ? employee1.getDateFrom() : employee2.getDateFrom();
                        // DateTo(to calculate the number of days worked together) is the earliest of the two dates.
                        Date dateTo = (employee1.getDateTo().before(employee2.getDateTo())) ? employee1.getDateTo() : employee2.getDateTo();
                        timeBetween = Math.abs(dateTo.getTime() - dateFrom.getTime());
                        numberOfDaysBetween = TimeUnit.DAYS.convert(timeBetween, TimeUnit.MILLISECONDS);
                        // If the calculated number of days is superior to max days then the variable is updated as well as employee IDs.
                        if (numberOfDaysBetween > maxDaysWorked) {
                            maxDaysWorked = numberOfDaysBetween;
                            empID1 = employee1.getEmpID();
                            empID2 = employee2.getEmpID();
                        }
                    }
                }
            }
        }
        // If a pair of employee that worked together is found, we go on to find the projects on which they worked together.
        if(empID1 !=0 && empID2 != 0) {
            // Only the projects with the empID1 is concerned for this loop.
            for (ProjectHistory projectWithEmp1 : projectRepository.findByEmpID(empID1)) {
                // We then look if the second employee also worked on this project.
                if (projectRepository.existsByEmpIDAndAndProjectID(empID2, projectWithEmp1.getProjectID())) {
                    // A new employee pair is created two put the two employee IDs and the project ID.
                    EmployeePair employeePair = new EmployeePair();
                    employeePair.setEmpID1(empID1);
                    employeePair.setEmpID2(empID2);
                    employeePair.setProjectID(projectWithEmp1.getProjectID());
                    // We also find the record that includes the empID2 and the project ID.
                    ProjectHistory projectWithEmp2 = projectRepository.findByEmpIDAndProjectID(employeePair.getEmpID2(), projectWithEmp1.getProjectID());
                    // Same formula is used to calculate the number of days that the two employees worked together on this particular project.
                    Date dateFrom = (projectWithEmp1.getDateFrom().after(projectWithEmp2.getDateFrom())) ? projectWithEmp1.getDateFrom() : projectWithEmp2.getDateFrom();
                    Date dateTo = (projectWithEmp1.getDateTo().before(projectWithEmp2.getDateTo())) ? projectWithEmp1.getDateTo() : projectWithEmp2.getDateTo();
                    timeBetween = Math.abs(dateTo.getTime() - dateFrom.getTime());
                    numberOfDaysBetween = TimeUnit.DAYS.convert(timeBetween, TimeUnit.MILLISECONDS);
                    employeePair.setDaysWorked(numberOfDaysBetween);
                    employeePairs.add(employeePair);
                }
            }
        }
        return employeePairs;
    }

    @DeleteMapping("/restart")
    // This method is used to initialize the database and start the processing of a new .csv document.
    public ResponseEntity<Map<String, Boolean>> deleteAllProjects(){
        projectRepository.deleteAll();
        Map<String, Boolean> response = new HashMap<>();
        response.put("All records are deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}
