package main.java.org.serialization.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Department implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String location;
    private String description;
    
    private List<User> employees;
    
    private Company company;
    
    private Department parentDepartment;
    private List<Department> subDepartments;
    
    private transient int employeeCount;
    
    public Department(String name, String location) {
        this.name = name;
        this.location = location;
        this.employees = new ArrayList<>();
        this.subDepartments = new ArrayList<>();
    }
    
    public void addEmployee(User user) {
        if (!employees.contains(user)) {
            employees.add(user);
            if (user.getDepartment() != this) {
                user.setDepartment(this);
            }
        }
    }
    
    public void removeEmployee(User user) {
        employees.remove(user);
    }
    
    public void addSubDepartment(Department subDept) {
        if (!subDepartments.contains(subDept)) {
            subDepartments.add(subDept);
            subDept.setParentDepartment(this);
        }
    }
    
    public void setCompany(Company company) {
        this.company = company;
        if (!company.getDepartments().contains(this)) {
            company.addDepartment(this);
        }
    }
    
    public int getTotalEmployeeCount() {
        int count = employees.size();
        for (Department subDept : subDepartments) {
            count += subDept.getTotalEmployeeCount();
        }
        return count;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<User> getEmployees() {
        return employees;
    }
    
    public Company getCompany() {
        return company;
    }
    
    public Department getParentDepartment() {
        return parentDepartment;
    }
    
    public void setParentDepartment(Department parentDepartment) {
        this.parentDepartment = parentDepartment;
    }
    
    public List<Department> getSubDepartments() {
        return subDepartments;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(name, that.name) && 
               Objects.equals(location, that.location);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, location);
    }
    
    @Override
    public String toString() {
        return String.format("Department{name='%s', location='%s', employees=%d, subDepartments=%d}",
            name,
            location,
            employees.size(),
            subDepartments.size()
        );
    }
    
    public void printHierarchy(int level) {
        String indent = "  ".repeat(level);
        System.out.println(indent + "├─ " + name + " (" + employees.size() + " employees)");
        
        for (Department subDept : subDepartments) {
            subDept.printHierarchy(level + 1);
        }
    }
}