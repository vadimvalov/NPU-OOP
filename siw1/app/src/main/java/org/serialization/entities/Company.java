package org.serialization.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Company implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String country;
    private String industry;
    private LocalDate foundedDate;
    
    private List<Department> departments;
    private User ceo; 

    private double revenue;
    private double marketCap;
    
    private transient boolean isPublic;
    
    public Company(String name, String country, String industry) {
        this.name = name;
        this.country = country;
        this.industry = industry;
        this.foundedDate = LocalDate.now();
        this.departments = new ArrayList<>();
    }
    
    public void addDepartment(Department department) {
        if (!departments.contains(department)) {
            departments.add(department);
            if (department.getCompany() != this) {
                department.setCompany(this);
            }
        }
    }
    
    public void removeDepartment(Department department) {
        departments.remove(department);
    }
    
    public void setCEO(User ceo) {
        this.ceo = ceo;
    }
    
    public List<User> getAllEmployees() {
        List<User> allEmployees = new ArrayList<>();
        
        for (Department dept : departments) {
            allEmployees.addAll(dept.getEmployees());
        }
        
        return allEmployees;
    }
    
    public int getTotalEmployeeCount() {
        int count = 0;
        for (Department dept : departments) {
            count += dept.getTotalEmployeeCount();
        }
        return count;
    }
    
    public Department findDepartment(String name) {
        return departments.stream()
            .filter(dept -> dept.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getIndustry() {
        return industry;
    }
    
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public LocalDate getFoundedDate() {
        return foundedDate;
    }
    
    public void setFoundedDate(LocalDate foundedDate) {
        this.foundedDate = foundedDate;
    }
    
    public List<Department> getDepartments() {
        return departments;
    }
    
    public User getCeo() {
        return ceo;
    }
    
    public double getRevenue() {
        return revenue;
    }
    
    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
    
    public double getMarketCap() {
        return marketCap;
    }
    
    public void setMarketCap(double marketCap) {
        this.marketCap = marketCap;
    }
    
    public boolean isPublic() {
        return isPublic;
    }
    
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(name, company.name) && 
               Objects.equals(country, company.country);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, country);
    }
    
    @Override
    public String toString() {
        return String.format("Company{name='%s', country='%s', industry='%s', departments=%d, employees=%d, CEO=%s}",
            name,
            country,
            industry,
            departments.size(),
            getTotalEmployeeCount(),
            ceo != null ? ceo.getUsername() : "none"
        );
    }

    public void printStructure() {
        System.out.println("=".repeat(60));
        System.out.println("Company: " + name);
        System.out.println("Country: " + country);
        System.out.println("Industry: " + industry);
        System.out.println("CEO: " + (ceo != null ? ceo.getUsername() : "none"));
        System.out.println("Total Employees: " + getTotalEmployeeCount());
        System.out.println("Departments:");
        
        for (Department dept : departments) {
            dept.printHierarchy(1);
        }
        
        System.out.println("=".repeat(60));
    }
}
