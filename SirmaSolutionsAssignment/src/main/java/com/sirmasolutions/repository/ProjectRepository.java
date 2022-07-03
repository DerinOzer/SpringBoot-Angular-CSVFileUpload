package com.sirmasolutions.repository;

import com.sirmasolutions.entity.ProjectHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectHistory,Integer> {
    public List<ProjectHistory> findByEmpID(int empID);
    public ProjectHistory findByEmpIDAndProjectID(int empID, int projectID);
    public boolean existsByEmpIDAndAndProjectID(int empID, int projectID);
}
