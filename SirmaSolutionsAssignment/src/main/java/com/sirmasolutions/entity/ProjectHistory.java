package com.sirmasolutions.entity;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table
public class ProjectHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int empID;
    private int projectID;
    private Date dateFrom;
    private Date dateTo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        id = id;
    }

    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empID) {
        this.empID = empID;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public ProjectHistory() {}

    public Date parseDate(String dateString) throws Exception{
        List<SimpleDateFormat> knownPatterns = new ArrayList<>();
        knownPatterns.add(new SimpleDateFormat("yyyy-MM-dd"));

        for (SimpleDateFormat pattern : knownPatterns) {
            pattern.setLenient(false);
            // There's a conversion between a Date and a formatted Date string.
            // During the formatting because the time is not considered, the day before the real date is taken into account.
            // That's why we add a day after this operation.
            Date date = new Date(pattern.parse(dateString).getTime() + (1000 * 60 * 60 * 24));
            return date;
        }
        return null;
    }
}
