package com.sirmasolutions.entity;

// This is not a persistence class.
// This class is used to display the projects of the employee pair that was found earlier in the calculations.
public class EmployeePair {
    private int empID1;
    private int empID2;
    private int projectID;
    // The number of days the employees worked together on the project.
    private long daysWorked;

    public int getEmpID1() {
        return empID1;
    }

    public void setEmpID1(int empID1) {
        this.empID1 = empID1;
    }

    public int getEmpID2() {
        return empID2;
    }

    public void setEmpID2(int empID2) {
        this.empID2 = empID2;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public long getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(long daysWorked) {
        this.daysWorked = daysWorked;
    }

    public EmployeePair() {}
}
